package com.jsy.community.util;

import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.util.excel.impl.ProprietorInfoProvider;
import com.jsy.community.util.excel.impl.ProprietorMemberProvider;
import com.jsy.community.vo.property.ProprietorVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * excel中转站
 * @author YuLF
 * @since 2020-11-26 15:54
 * 业主 excel 下载 导入 指定使用具体某个子类的方法  ，这里是控制器类和excel具体实现类的中枢
 * 业主 excel 指挥者[提供零件]  负责提供 excel 所需要的各个组件方法  和具体组装好的成品对象
 */
@Slf4j
public class ProprietorExcelCommander {

    /**
     * 错误信息Excel 在 Minio 中存在的bucket名称
     */
    public static final String BUCKET_NAME = "proprietor-excel";

    /**
     * 业主信息录入表.xlsx 字段 如果增加字段  需要改变实现类逻辑
     */
    public static final String[] PROPRIETOR_TITLE_FIELD = {"姓名", "身份证号", "联系电话", "房屋编号", "微信", "QQ", "电子邮箱","备注"};

    /**
     * 业主Sheet名称
     */
    public static final String PROPRIETOR_SHEET_NAME = "通讯录";

    /**
     * 业主excel标题名称
     */
    public static final String PROPRIETOR_TITLE_NAME = "业主信息";

    /**
     * 业主家属信息录入表.xlsx 字段 如果增加字段  需要改变实现类逻辑
     */
    public static final String[] MEMBER_TITLE_FIELD = {"所属业主", "与业主关系", "家属性别", "所属房屋", "家属姓名", "家属身份证号", "家属手机号"};

    /**
     * 业主家属信息模板下载 需要增加约束的 总行数量
     * 如果为1000 表明业主家属信息excel 最多同时为1000名家属录入信息
     * 当然也可以更多 只是超过1000行的单元格 没有和社区 直接关联的数据 直接选择
     */
    public static final Integer MEMBER_CONSTRAINT_ROW = 1000;


    /**
     * 【业主信息录入excel 模板 下载方法】 控制方法
     * [扩展]避免直接改动excel导出功能实现方法,
     * 这里如果需要改变使用另一种下载excel模板的方式， 新建类实现JSYExcelAbstract 的exportProprietorExcel方法 然后在这里替换new ProprietorExcelProvider()
     * @return 返回生成好的excel工作簿 好让控制器直接转换为数据流响应给客户端 下载
     * @author YuLF
     * @since 2020/11/26 16:00
     */
    public static Workbook exportProprietorInfo() {
        String[] fields = Arrays.copyOf(ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD, ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD.length - 1);
        return new ProprietorInfoProvider().exportProprietorExcel( fields );
    }


    public static Workbook exportProprietorDefaultInfo(){
        return new ProprietorInfoProvider().exportProprietorExcel( PROPRIETOR_TITLE_FIELD );
    }

    /**
     * 【业主信息导入】解析导入的业主信息 excel文件 返回 信息实体列表 控制方法
     *
     * @return 返回解析好的数据
     * @author YuLF
     * @Param proprietorExcel      excel文件
     * @Param errorVos             解析错误信息集合
     * @since 2020/11/26 16:55
     */
    public static List<ProprietorEntity> importProprietorExcel(MultipartFile proprietorExcel, List<ProprietorVO> errorVos) {
        return new ProprietorInfoProvider().importProprietorExcel(proprietorExcel, errorVos);
    }

    /**
     * 【业主家属信息导入】解析导入的业主信息 excel文件 返回 信息实体列表 控制方法
     *
     * @return 返回解析好的数据
     * @author YuLF
     * @Param proprietorExcel      excel文件
     * @since 2020/11/26 16:55
     */
    public static List<UserEntity> importMemberExcel(MultipartFile proprietorExcel, Map<String, Object> map) {
        return new ProprietorMemberProvider().importProprietorExcel(proprietorExcel, map);
    }

    
    /**
     * 【业主家属成员excel模板导出】下载业主家庭成员Excel控制方法
     * @author YuLF
     * @since  2020/12/7 14:57
     * @Param  userEntityList       社区名称、社区住户名、社区住户uid信息
     * @Param  解析数据需要携带的数据
     * @return 返回创建好的Workbook
     */
    public static Workbook exportProprietorMember(List<UserEntity> userEntityList, Map<String, Object> res) {
        //调用导出excel 创建模板
        return new ProprietorMemberProvider().exportProprietorExcel(userEntityList,res);
    }

    /**
     * 【提供红色背景样式】
     * @param workbook  工作薄
     * @return          返回样式对象
     */
    public static XSSFCellStyle provideBackground(Workbook workbook){
        XSSFCellStyle fieldCellStyle = (XSSFCellStyle) workbook.createCellStyle();
        fieldCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        fieldCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return fieldCellStyle;
    }

    public static boolean isEmpty(String str){
        return str == null || "".equals(str.trim()) || "null".equals(str) || "undefined".equals(str);
    }

}
