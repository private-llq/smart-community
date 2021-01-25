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
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 从服务器上面下载日志
 * @author YuLF
 * @since 2021-01-25 14:05
 */
@Controller
@Slf4j
public class DownloadLogController {

    //日志的绝对路径 本地
    private final String LogPath = "D:"+ File.separator+"mnt"+File.separator+"db"+File.separator+"smart-community"+File.separator+"logs" + File.separator;

    //linux 日志logs目录的绝对路径
    //private final String LogPath = ""

    @GetMapping("/downloadLog")
    public ResponseEntity<byte[]> downloadLog(@RequestParam(value = "isDirectory", required = false, defaultValue = "true")Boolean isDirectory,
                                              @RequestParam(value = "date", required = false)String date,
                                              @RequestParam(value = "moduleName")String moduleName, HttpServletRequest request)
    {
        MultiValueMap<String, String> header = new HttpHeaders();
        byte[] bytes;
        if( isDirectory ){
            bytes = downloadDirectory(LogPath, moduleName, header, request);
        } else {
            if(date == null){
                throw new JSYException(400, "下载文件名不能为空!");
            }
            bytes = downloadFile(LogPath, moduleName, date, header, request);
        }
        if(bytes == null){
            throw new JSYException(400, "文件或目录不存在!");
        }
        return new ResponseEntity<>(bytes, header, HttpStatus.OK);
    }

    private byte[] downloadFile(String logPath,  String moduleName, String date, MultiValueMap<String, String> header,  HttpServletRequest request) {
        byte[] in;
        FileInputStream fileInputStream = null;
        String filePath = logPath + moduleName + File.separator + date + ".0.log";
        try {
            //读取文件
            fileInputStream = new FileInputStream( filePath );
            in	= new byte[fileInputStream.available()];
            int read = fileInputStream.read(in);
            header.set( "Content-Disposition", "attachment;filename=" + filePath.substring(filePath.lastIndexOf(File.separator) + 1) );
            header.set( "Content-type", request.getServletContext().getMimeType( filePath ) );
            return in;
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }  finally {
            close(fileInputStream);
        }
    }

    private byte[] downloadDirectory(String directoryPath, String moduleName, MultiValueMap<String, String> header, HttpServletRequest request){
        byte[] fileStream;
        FileInputStream fileInputStream = null;
        String modulePath = directoryPath + moduleName;
        String moduleZipName = moduleName + ".zip";
        try {
            String moduleZipPath = new File("").getCanonicalPath() + File.separator + moduleZipName;
            compress(modulePath, moduleZipName);
            //读取文件
            fileInputStream = new FileInputStream( moduleZipPath  );
            fileStream	= new byte[fileInputStream.available()];
            int read = fileInputStream.read(fileStream);
            header.set( "Content-Disposition", "attachment;filename=" + moduleZipName );
            header.set( "Content-type", request.getServletContext().getMimeType( moduleZipPath ) );
            close(fileInputStream);
            //读取完之后删掉
            delete(moduleZipPath );
            return fileStream;
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        } finally {
            close(fileInputStream);
        }
    }

    private static void close(InputStream inputStream){
        if(inputStream != null){
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
     * 压缩文件或目录
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

    private static void compressDirectory(File dir, ZipOutputStream zipOut, String baseDir) throws IOException {
        File[] files = dir.listFiles();
        if (files != null && ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                compress(file, zipOut, baseDir + dir.getName() + File.separator);
            }
        }
    }

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
