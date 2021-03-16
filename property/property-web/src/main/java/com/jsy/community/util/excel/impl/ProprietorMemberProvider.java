package com.jsy.community.util.excel.impl;

import cn.hutool.core.util.StrUtil;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.ExcelHandler;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author YuLF
 * @since 2020-12-07 15:21
 * 业主家属成员.xlsx 下载模板 、信息解析类
 */
@Slf4j
public class ProprietorMemberProvider implements ExcelHandler {



    /**
     * 导入业主家属信息录入表excel
     * @param excel 业主家属信息表.xlsx
     * @return      返回解析好的数据
     */
    @Override
    public List<UserEntity> importProprietorExcel(MultipartFile excel, Map<String,Object> map) {
        List<UserEntity> userEntityList = new ArrayList<>();
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(excel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = ProprietorExcelCommander.MEMBER_TITLE_FIELD;
            //效验excel标题行
            ProprietorExcelCommander.validExcelField(sheetAt, titleField);
            //获取该社区的
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                Row dataRow = sheetAt.getRow(j);
                if (ProprietorExcelCommander.cellOneIsNotNull(dataRow)) {
                    //如果这行数据不为空 创建一个 实体接收 信息
                    UserEntity userEntity = new UserEntity();
                    userEntity.setHouseEntity(new HouseEntity());
                    //遍历列
                    for (int z = 0; z < titleField.length; z++) {
                        Cell cell = dataRow.getCell(z);
                        String cellValue = ProprietorExcelCommander.getCellValForType(cell).toString();
                        //列字段效验
                        switch (z) {
                            // 1列 通过业主姓名获取业主的Uid  因为家属需要和业主关联
                            case 0:
                                if (RegexUtils.isRealName(cellValue)) {
                                    String uid = String.valueOf(map.get(cellValue));
                                    if(ProprietorExcelCommander.isEmpty(uid)){
                                        throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 在当前社区没有这个业主!");
                                    }
                                    //当前家属所属业主uid
                                    userEntity.setUid(uid);
                                } else {
                                    //因为 第一行 和第二行 是标题 和字段 所以需要+1        列下标是按0开始的 需要+1
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 不是一个正确的中国姓名!");
                                }
                                break;
                            //第2列 验证业主家属关系
                            case 1:
                                //获得家属关系code
                                int relationCode = getRelationCode(cellValue);
                                if (relationCode != 0) {
                                    userEntity.setRelationCode(relationCode);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 不是一个正确的家属关系!请选择正确的家属关系");
                                }
                                break;
                            //第3列 性别
                            case 2:
                                if ("男".equals(cellValue)) {
                                    userEntity.setSex(1);
                                } else if ("女".equals(cellValue)) {
                                    userEntity.setSex(2);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 不是一个正确的性别!请选择正确的性别");
                                }
                                break;
                            //第4列 所属房屋
                            case 3:
                                if(StringUtils.isBlank(cellValue)){
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 未选择家属所属房屋!");
                                }
                                userEntity.getHouseEntity().setAddress(cellValue);
                                break;
                            //第5列 家属姓名
                            case 4:
                                if (RegexUtils.isRealName(cellValue)) {
                                    userEntity.setRealName(cellValue);
                                } else {
                                    //因为 第一行 和第二行 是标题 和字段 所以需要+1        列下标是按0开始的 需要+1
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 不是一个正确的中国姓名!");
                                }
                                break;
                            //第6列 家属身份证号码
                            case 5:
                                if (RegexUtils.isIdCard(cellValue)) {
                                    userEntity.setIdCard(cellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 不是一个正确的身份证号码!");
                                }
                                break;
                            //第7列 家属手机号
                            case 6:
                                //小孩的手机号码可以为空
                                if( cellValue == null || "".equals(cellValue)){
                                    break;
                                }
                                if (RegexUtils.isMobile(cellValue)) {
                                    userEntity.setMobile(cellValue);
                                } else {
                                    throw new JSYException(1, "：第" + (j + 1) + "行,第" + (z + 1) + "列 '" + cellValue + "' 不是一个正确的电话号码 电信|联通|移动!");
                                }
                                break;
                            default:
                                break;
                        } //switch-end
                    }
                    //保证传进来的泛型是相同的
                    userEntityList.add(userEntity);
                }
            }
        } catch (IOException e) {
            log.error("com.jsy.community.controller.ProprietorController.readProprietorExcel：{}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
        return userEntityList;
    }

    /**
     * 【获取关系名称的code】如果传进来的值在 关系常量里面存在 则返回他的code 否则返回0
     * @param relationName      关系名称：父子、母子
     */
    private int getRelationCode(String relationName){
        for (Map.Entry<Integer, String> next : BusinessEnum.RelationshipEnum.relationshipMap.entrySet()) {
            if (relationName.equals(next.getValue())) {
                return next.getKey();
            }
        }
        return 0;
    }

    /**
     * 录入数据excel下载模板
     * @param entityList 实体List、存在该社区所有业主姓名，该社区小区名
     * @param res        为实现类其他需要的字段值存取
     * @return           返回创建好的工作薄excel
     */
    @Override
    public Workbook exportProprietorExcel(List<?> entityList, Map<String, Object> res) {
        //工作表名称
        String titleName = String.valueOf(res.get("name"));
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        //3.创建约束数据隐藏表 避免数据过大下拉框不显示问题
        XSSFSheet hiddenSheet = (XSSFSheet) workbook.createSheet("hiddenSheet");
        String[] titleField = ProprietorExcelCommander.MEMBER_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ProprietorExcelCommander.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ProprietorExcelCommander.createExcelField(workbook, sheet, titleField);
        //添加需要约束数据的列下标   "所属业主",  "与业主关系", "家属性别", "所属房屋"
        int[] arrIndex = new int[]{0, 1, 2, 3};
        //取出所有的房屋信息
        Object obj = res.get("communityHouseAddr");
        List<String> communityHouseAddr = castStrList(obj);
        //表明验证约束 结束行
        int endRow = ProprietorExcelCommander.MEMBER_CONSTRAINT_ROW;
        for (int index : arrIndex) {
            //通过传过来的 约束列constraintColIndex 获得List中的约束数据
            //其中 业主姓名 和 业主uid是关联的 所以需要一起创建对应的Map数据
            String[] constraintData = getConstraintSet(entityList, index, communityHouseAddr);
            //创建业主信息登记表与隐藏表的约束字段
            ProprietorExcelCommander.createProprietorConstraintRef(workbook, hiddenSheet, constraintData, endRow, index);
            //绑定验证
            sheet.addValidationData(ProprietorExcelCommander.setBox(sheet, endRow, index));
        }
        //隐藏 隐藏表  下标1 就是隐藏表
        workbook.setSheetHidden(1, true);
        return workbook;
    }

    /**
     * 把指定的Obj对象转换为对应的List<String>对象
     * @param obj           List<String>的Object对象
     *
     */
    private List<String> castStrList(Object obj)
    {
        List<String> result = new ArrayList<>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add((String) o);
            }
            return result;
        }
        return null;
    }

    /**
     * 对List中的对象字段去重 返回String数组
     * @param houseMembers          解析好的用户家属List数据
     * @param colIndex              列类型：表明家属性别、业主姓名、与业主关系 对应列的下标
     * @Param communityHouseAddr    当前社区所有已登记的房屋信息 如：帆云小区2栋2单元1层1-10
     * @return 返回去重好的字段 String数组
     */
    private static String[] getConstraintSet(List<?> houseMembers, int colIndex, List<String> communityHouseAddr) {
        Set<String> set = new HashSet<>(houseMembers.size());
        for (Object object : houseMembers) {
            UserEntity userEntity = (UserEntity) object;
            switch (colIndex) {
                //TITLE_FIELD 索引0为 业主姓名
                case 0:
                    String userRealName = userEntity.getRealName();
                    if (StrUtil.isNotEmpty(userRealName)) {
                        set.add(userRealName);
                    }
                    break;
                //TITLE_FIELD 索引1为家属与业主关系
                case 1:
                    //拿到家属关系    map key = code 家属id  value = name 家属关系名称：父子 母子 ...
                    Map<Integer, String> relationshipList = BusinessEnum.RelationshipEnum.relationshipMap;
                    set.addAll(relationshipList.values());
                    break;
                //TITLE_FIELD 索引2为性别
                case 2:
                    set.add("男");
                    set.add("女");
                    break;
                //TITLE_FIELD 索引3为家属所属房屋
                case 3:
                    set.addAll(communityHouseAddr);
                    break;
                default:
                    break;
            }
        }
        return set.toArray(new String[0]);
    }


}
