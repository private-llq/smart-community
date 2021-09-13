package com.jsy.community.config;
import com.alibaba.excel.EasyExcel;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.util.excel.impl.CustomImageCellWriteHandler;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
 
    public static void exportModule(String filename, HttpServletResponse response,Class c, List data, Integer type) throws IOException {
        String formFileName=filename;
        String types=".xls";
        ServletOutputStream out = response.getOutputStream();
        switch (type){
            case 1:
                types=".xls";
                break;
            case 2:
                types=".xlsx";
                break;
          }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename+types, StandardCharsets.UTF_8));
        //response.setContentType("multipart/form-data");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        List<Integer> imageColumnIndexs = new ArrayList<>();
        imageColumnIndexs.add(0);
        imageColumnIndexs.add(4);
        imageColumnIndexs.add(8);
        EasyExcel.write(out, c).sheet(filename).registerWriteHandler(new CustomImageCellWriteHandler(imageColumnIndexs))
                .doWrite(data);
        out.flush();
        out.close();
    }
 
}