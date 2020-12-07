package com.jsy.community.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.util.excel.impl.ProprietorInfoProvider;
import com.jsy.community.util.excel.impl.ProprietorMemberProvider;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YuLF
 * @since 2020-11-26 15:54
 * 业主 excel 下载 导入 指定使用具体某个子类的方法  扩展类  业主相关的 调用方法改动只用改动这里，这里是控制器类和excel具体实现类的中枢
 * 业主 excel 指挥者
 */
public class ProprietorExcelCommander {

    /**
     * 业主信息录入表.xlsx 字段
     */
    public static final String[] TITLE_FIELD = {"姓名", "性别", "楼栋", "单元", "楼层", "门牌", "身份证", "联系方式", "详细地址"};


    public static final String[] SUPPORT_EXCEL_EXTENSION = {"xls", "xlsx"};

    /**
     * excel 模板 下载方法
     * [扩展]避免直接改动excel导出功能实现方法,
     * 这里如果需要改变使用另一种下载excel模板的方式， 新建类实现JSYExcelAbstract 的exportProprietorExcel方法 然后在这里替换new ProprietorExcelProvider()
     *
     * @return 返回生成好的excel工作簿 好让控制器直接转换为数据流响应给客户端 下载
     * @author YuLF
     * @Param list    生成模板 需要用到的数据库数据List，用于excel模板给单元格增加约束，限制单元格只能选择数据库的数据，如录入单元时，让excel录入者只能选择当前社区在数据库已有的单元
     * @since 2020/11/26 16:00
     */
    public static Workbook exportProprietorInfo(List<?> list, Map<String, Object> res) {
        return new ProprietorInfoProvider().exportProprietorExcel(list, res);
    }

    /**
     * 解析导入 的excel文件 返回 信息实体列表
     *
     * @return 返回解析好的数据
     * @author YuLF
     * @Param proprietorExcel      excel文件
     * @since 2020/11/26 16:55
     */
    public static Object importProprietorExcel(MultipartFile proprietorExcel) {
        return new ProprietorInfoProvider().importProprietorExcel(proprietorExcel);
    }
    
    
    /**
     * 下载业主家庭成员Excel控制方法
     * @author YuLF
     * @since  2020/12/7 14:57
     * @Param  userEntityList       社区名称、社区住户名、社区住户uid信息
     * @return 返回创建好的Workbook
     */
    public static Workbook exportProprietorMember(List<UserEntity> userEntityList) {
        return new ProprietorMemberProvider().exportProprietorExcel(userEntityList,new HashMap<>(1));
    }

}
