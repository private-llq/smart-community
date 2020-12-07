package com.jsy.community.util.excel.impl;

import com.jsy.community.entity.UserEntity;
import com.jsy.community.util.JSYExcel;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author YuLF
 * @since 2020-12-07 15:21
 * 业主家属成员.xlsx 下载模板 、信息解析类
 */
public class ProprietorMemberProvider implements JSYExcel {


    @Override
    public List<?> importProprietorExcel(MultipartFile excel) {
        return null;
    }

    @Override
    public Workbook exportProprietorExcel(List<?> entityList, Map<String, Object> res) {



        return null;
    }
}
