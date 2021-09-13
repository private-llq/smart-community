package com.jsy.community.util.excel.impl;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.IoUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author niexh
 * @description
 * @description 泛微协同商务系统, 版权所有.
 * @date 2021/1/4 18:27
 */
public class CustomImageCellWriteHandler implements CellWriteHandler {
    /**
     * 图片字段在excel中的列索引
     */
    private List<Integer> imageColumnIndexs = new ArrayList<>();

    public CustomImageCellWriteHandler(List<Integer> imageColumnIndexs) {
        this.imageColumnIndexs = imageColumnIndexs;
    }

    private List<String> repeats = new ArrayList<>();
    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

    }

    @Override
    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, CellData cellData, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            return;
        }
        if(imageColumnIndexs.contains(cell.getColumnIndex())){
            cellData.setType(CellDataTypeEnum.EMPTY);
        }
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        Sheet sheet = cell.getSheet();
        if (isHead||cellDataList==null) {
            return;
        }
        if(!imageColumnIndexs.contains(cell.getColumnIndex())){
            return;
        }
        String key = cell.getRowIndex()+"_"+cell.getColumnIndex();
        if (repeats.contains(key)){//afterCellDispose好像会被重复调用
            return;
        }
        repeats.add(key);
        CellData cellData = cellDataList.get(0);
        String fieldids = cellData.getStringValue();
        if("".equals(fieldids)){
            return;
        }
        String[] fieldidArr = fieldids.split(",");
        sheet.getRow(cell.getRowIndex()).setHeight((short) 900);
        sheet.setColumnWidth(cell.getColumnIndex(),240*8*fieldidArr.length);
        for (int i = 0; i < fieldidArr.length; i++) {
            this.insertImage(sheet,cell,fieldidArr[i],i);
        }
    }

    private void insertImage(Sheet sheet,Cell cell,String fieldid,int i){
        //导出每张图片要求固定大小60*60px,所以这里偏移量也是60px
        int picWidth = Units.pixelToEMU(60);
        int index = sheet.getWorkbook().addPicture(getImage(fieldid), HSSFWorkbook.PICTURE_TYPE_PNG);
        Drawing drawing = sheet.getDrawingPatriarch();
        if (drawing == null) {
            drawing = sheet.createDrawingPatriarch();
        }
        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        // 设置图片坐标
        anchor.setDx1(picWidth*i);
        anchor.setDx2(picWidth+picWidth*i);
        anchor.setDy1(0);
        anchor.setDy2(0);
        //设置图片位置
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex());
        anchor.setRow1(cell.getRowIndex());
        anchor.setRow2(cell.getRowIndex() + 1);
        // 设置图片可以随着单元格移动
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        drawing.createPicture(anchor, index);
    }

    private byte[] getImage(String fieldid){
        InputStream inputStream = null;
        byte[] bytes=null;
        try {
            //测试数据,随便写死的的,实际业务中可以根据fieldid或者是图片地址去取等等...
            URL url =new URL(fieldid);
            inputStream = url.openStream();
            bytes= IoUtils.toByteArray(inputStream);
            return bytes;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }
}