package com.jsy.community.util;

import com.jsy.community.entity.UserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author YuLF
 * @since 2020-11-26 15:17
 * 中间抽象层，用于具体的子类实现类 继承  选择性某些方法实现
 * 避免功能子类实现类 直接实现 接口 需要重写接口所有的方法
 */
public abstract class JSYExcelAbstract implements JSYExcel {

    @Override
    public <T> List<UserEntity> importProprietorExcel(MultipartFile multipartFile) {
        return null;
    }

    @Override
    public Workbook exportProprietorExcel(List<?> entityList, String workSheetName) {
        return null;
    }
}
