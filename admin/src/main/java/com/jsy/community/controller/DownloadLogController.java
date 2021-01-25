package com.jsy.community.controller;

import com.jsy.community.exception.JSYException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 从服务器上面下载日志
 *
 * @author YuLF
 * @since 2021-01-25 14:05
 */
@Controller
@Slf4j
public class DownloadLogController {

    //日志的绝对路径 本地
    //private final String LogPath = "D:" + File.separator + "mnt" + File.separator + "db" + File.separator + "smart-community" + File.separator + "logs" + File.separator;

    //linux 日志logs目录的绝对路径
    private final String LogPath = File.separator+"mnt"+File.separator+"db"+File.separator+"smart-community"+File.separator+"logs" + File.separator;

    @GetMapping("/downloadLog")
    public ResponseEntity<byte[]> downloadLog(@RequestParam(value = "isDirectory", required = false, defaultValue = "true") Boolean isDirectory,
                                              @RequestParam(value = "date", required = false) String date,
                                              @RequestParam(value = "moduleName") String moduleName, HttpServletRequest request) {
        MultiValueMap<String, String> header = new HttpHeaders();
        byte[] bytes;
        if (isDirectory) {
            bytes = downloadDirectory(LogPath, moduleName, header, request);
        } else {
            if (date == null) {
                throw new JSYException(400, "下载文件名不能为空!");
            }
            bytes = downloadFile(LogPath, moduleName, date, header, request);
        }
        if (bytes == null) {
            throw new JSYException(400, "文件或目录不存在!");
        }
        return new ResponseEntity<>(bytes, header, HttpStatus.OK);
    }

    private byte[] downloadFile(String logPath, String moduleName, String date, MultiValueMap<String, String> header, HttpServletRequest request) {
        byte[] in;
        FileInputStream fileInputStream = null;
        //获取文件名
        List<String> toDayAbsoluteName = getToDayAbsoluteName(logPath + moduleName, date);
        //如果当天的日志文件有多个 则 使用压缩包
        if (toDayAbsoluteName.size() > 1) {
            return downloadMultipartFile(logPath, moduleName, date, header, request);
        }
        //当天的日志只有一个文件
        String filePath = logPath + moduleName + File.separator + toDayAbsoluteName.get(0);
        try {
            //读取文件
            fileInputStream = new FileInputStream(filePath);
            in = new byte[fileInputStream.available()];
            int read = fileInputStream.read(in);
            header.set("Content-Disposition", "attachment;filename=" + filePath.substring(filePath.lastIndexOf(File.separator) + 1));
            header.set("Content-type", request.getServletContext().getMimeType(filePath));
            return in;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(fileInputStream);
        }
    }

    /**
     * 当天日志文件有多个，下载使用压缩包下载
     *
     * @param logPath    日志目录路径
     * @param moduleName 模块名称
     * @param date       日期文件名
     * @param header     响应头
     * @param request    请求头
     * @return 返回压缩后的文件流
     */
    private byte[] downloadMultipartFile(String logPath, String moduleName, String date, MultiValueMap<String, String> header, HttpServletRequest request) {
        String modulePath = logPath + moduleName;
        String moduleZipName = moduleName + ".zip";
        String moduleZipPath = modulePath + File.separator + moduleZipName;
        List<String> toDayAbsoluteName = getToDayAbsoluteName(modulePath, date);
        //压缩文件
        try {
            compressMultipartFile(toDayAbsoluteName, modulePath, moduleZipName );
            return readFile(moduleZipName, header, request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取当天的日志文件
     *
     * @param directoryPath 模块目录绝对路径
     * @param fileName      文件名称
     * @return 返回日志文件集合
     */
    private static List<String> getToDayAbsoluteName(String directoryPath, String fileName) {
        File moduleDirectory = new File(directoryPath);
        if (!moduleDirectory.exists()) {
            throw new JSYException(400, "目录不存在!");
        }
        List<String> fileNameList = new ArrayList<>();
        File[] files = moduleDirectory.listFiles();
        if (files == null) {
            throw new JSYException(405, "当前模块目录为空!");
        }
        for (File file : files) {
            if (file.getName().contains(fileName)) {
                fileNameList.add(file.getName());
            }
        }
        if (fileNameList.size() == 0) {
            throw new JSYException(404, "该目录不存在此文件" + fileName);
        }
        return fileNameList;
    }

    /**
     * 下载日志文件目录
     *
     * @param directoryPath 日志文件目录绝对路径
     * @param moduleName    模块名称
     * @param header        响应头
     * @param request       请求头
     * @return 返回读取的字节流
     */
    private byte[] downloadDirectory(String directoryPath, String moduleName, MultiValueMap<String, String> header, HttpServletRequest request) {
        byte[] fileStream;
        FileInputStream fileInputStream = null;
        String modulePath = directoryPath + moduleName;
        String moduleZipName = moduleName + ".zip";
        try {
            compress(modulePath, moduleZipName);
            return readFile( moduleZipName, header, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] readFile(String moduleZipName, MultiValueMap<String, String> header, HttpServletRequest request){
        byte[] fileStream;
        FileInputStream fileInputStream = null;
        try {
            String moduleZipPath = new File("").getCanonicalPath() + File.separator + moduleZipName;
            fileInputStream = new FileInputStream(moduleZipPath);
            fileStream = new byte[fileInputStream.available()];
            int read = fileInputStream.read(fileStream);
            header.set("Content-Disposition", "attachment;filename=" + moduleZipName);
            header.set("Content-type", request.getServletContext().getMimeType(moduleZipPath));
            close(fileInputStream);
            //读取完之后删掉
            delete(moduleZipPath);
            return fileStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(fileInputStream);
        }
    }

    private static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 递归删除文件或目录
     *
     * @param filePath 文件或目录
     */
    public static void delete(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                Arrays.stream(files).forEach(f -> delete(f.getPath()));
            }
        }
        try {
            Files.delete(Paths.get(filePath));
        } catch (IOException e) {
            log.error("文件删除失败", e);
        }
    }

    /**
     * 压缩指定名称的多文件
     * @param listFileNames 压缩文件名称集合
     * @param fromPath      待压缩文件或路径
     * @param toPath        压缩文件，如 xx.zip
     */
    public static void compressMultipartFile(List<String> listFileNames, String fromPath, String toPath) throws IOException {
        File toFile = new File(toPath);
        try (
                FileOutputStream outputStream = new FileOutputStream(toFile);
                CheckedOutputStream checkedOutputStream = new CheckedOutputStream(outputStream, new CRC32());
                ZipOutputStream zipOutputStream = new ZipOutputStream(checkedOutputStream)
        ) {
            listFileNames.forEach( filename -> {
                File fromFile = new File(fromPath + File.separator + filename);
                try {
                    compressFile(fromFile, zipOutputStream, fromPath.substring(fromPath.lastIndexOf(File.separator) + 1) + File.separator);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    /**
     * 压缩单文件或目录
     *
     * @param fromPath 待压缩文件或路径
     * @param toPath   压缩文件，如 xx.zip
     */
    public static void compress(String fromPath, String toPath) throws IOException {
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        if (!fromFile.exists()) {
            throw new JSYException(fromPath + "不存在！");
        }
        try (
                FileOutputStream outputStream = new FileOutputStream(toFile);
                CheckedOutputStream checkedOutputStream = new CheckedOutputStream(outputStream, new CRC32());
                ZipOutputStream zipOutputStream = new ZipOutputStream(checkedOutputStream)
        ) {
            String baseDir = "";
            compress(fromFile, zipOutputStream, baseDir);
        }
    }

    private static void compress(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (file.isDirectory()) {
            compressDirectory(file, zipOut, baseDir);
        } else {
            compressFile(file, zipOut, baseDir);
        }
    }

    /**
     * 压缩目录
     *
     * @param dir     目录路径
     * @param zipOut  zip输出流
     * @param baseDir 目录上层绝对路径
     */
    private static void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null && ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                compress(file, zipOut, baseDir + dir.getName() + File.separator);
            }
        }
    }

    /**
     * 压缩文件
     *
     * @param file    文件
     * @param zipOut  zip输出流
     * @param baseDir 文件上层目录绝对路径
     * @throws IOException 可能抛出异常
     */
    private static void compressFile(File file, ZipOutputStream zipOut, String baseDir) throws IOException {
        if (!file.exists()) {
            return;
        }
        int BUFFER = 2048;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zipOut.putNextEntry(entry);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }
        }
    }


}
