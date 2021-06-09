package com.jsy.community.util.excel.impl;

import com.jsy.community.entity.property.PropertyFinanceOrderEntity;
import com.jsy.community.entity.property.PropertyFinanceReceiptEntity;
import com.jsy.community.util.FinanceExcelHandler;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.vo.StatementOrderVO;
import com.jsy.community.vo.StatementVO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 财务Excel助手实现
 * @Date: 2021/4/24 15:10
 * @Version: 1.0
 **/
@Service
public class FinanceExcelImpl implements FinanceExcelHandler {

    // 结算单表.xlsx 字段 如果增加字段  需要改变实现类逻辑
    public static final String[] STATEMENT_TITLE_FIELD = {"结算单号", "结算时间段", "状态", "结算金额", "开户名称", "开户银行", "开户支行", "银行账号", "创建日期", "驳回原因"};

    // 结算单关联账单表.xlsx 字段 如果增加字段  需要改变实现类逻辑
    public static final String[] ORDER_TITLE_FIELD = {"账单号", "账单日期", "账单类型", "收款金额", "收款时间", "收款渠道", "支付渠道单号"};

    // 账单表字段 如果增加字段  需要改变实现类逻辑
    public static final String[] MASTER_ORDER_TITLE_FIELD = {"账单号", "状态", "账单日期", "账单类型", "应收金额", "房屋/车牌号", "业主姓名", "收款单号", "收款时间", "收款渠道", "支付渠道单号", "结算单号", "结算状态"};

    // 收款单表.xlsx 字段 如果增加字段  需要改变实现类逻辑
    public static final String[] RECEIPT_TITLE_FIELD = {"收款单号", "收款金额", "收款时间", "收款渠道", "收款渠道单号"};

    // 收款单关联账单表.xlsx 字段 如果增加字段  需要改变实现类逻辑
    public static final String[] RECEIPT_ORDER_TITLE_FIELD = {"账单号", "账单日期", "账单类型", "应收金额"};

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    public static final String TIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    /**
     * @Author: Pipi
     * @Description: 导出结算单主表
     * @Param: entityList: 实体List
     * @Param: res: 存放实现类需要传递的数据
     * @Return: org.apache.poi.ss.usermodel.Workbook 返回生成好的工作簿
     * @Date: 2021/4/24 15:07
     */
    @Override
    public Workbook exportMaterStatement(List<?> entityList) {
        //工作表名称
        String titleName = "结算单表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = STATEMENT_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        // 设置列宽
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 8000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 8000);
        sheet.setColumnWidth(7, 7000);
        sheet.setColumnWidth(8, 4000);
        sheet.setColumnWidth(9, 9000);
        //往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < STATEMENT_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                StatementVO statementVO = (StatementVO)entityList.get(index);
                switch (j) {
                    case 0:
                        // 结算单号
                        cell.setCellValue(statementVO.getStatementNum());
                        break;
                    case 1:
                        // 结算时间段
                        String startDate = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementVO.getStartDate());
                        String endDate = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementVO.getEndDate());
                        cell.setCellValue(startDate + "至" + endDate);
                        break;
                    case 2:
                        // 状态
                        if (statementVO.getStatementStatus() == 1) {
                            cell.setCellValue("待审核");
                        } else if (statementVO.getStatementStatus() == 2) {
                            cell.setCellValue("结算中");
                        } else if (statementVO.getStatementStatus() == 3) {
                            cell.setCellValue("已结算");
                        } else if (statementVO.getStatementStatus() == 4) {
                            cell.setCellValue("驳回");
                        }
                        break;
                    case 3:
                        // 结算金额
                        cell.setCellValue(statementVO.getTotalMoney().setScale(2).toString());
                        break;
                    case 4:
                        // 开户名称
                        cell.setCellValue(statementVO.getAccountName());
                        break;
                    case 5:
                        // 开户银行
                        cell.setCellValue(statementVO.getBankName());
                        break;
                    case 6:
                        // 开户支行
                        cell.setCellValue(statementVO.getBankBranchName());
                        break;
                    case 7:
                        // 银行账号
                        cell.setCellValue(statementVO.getBankNo());
                        break;
                    case 8:
                        // 创建日期
                        cell.setCellValue(DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementVO.getCreateTime()));
                        break;
                    case 9:
                        // 驳回原因
                        cell.setCellValue(statementVO.getRejectReason());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }

    /**
     * @Author: Pipi
     * @Description: 导出结算单主表和从表
     * @Param: entityList: 实体List
     * @Param: res: 存放实现类需要传递的数据
     * @Return: org.apache.poi.ss.usermodel.Workbook 返回生成好的工作簿
     * @Date: 2021/4/24 15:09
     */
    @Override
    public Workbook exportMasterSlaveStatement(List<?> entityList) {
        //工作表名称
        String titleName = "结算单表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = STATEMENT_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        // 设置列宽
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 8000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 8000);
        sheet.setColumnWidth(7, 7000);
        sheet.setColumnWidth(8, 4000);
        sheet.setColumnWidth(9, 9000);
        //往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < STATEMENT_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                StatementVO statementVO = (StatementVO)entityList.get(index);
                switch (j) {
                    case 0:
                        // 结算单号
                        String statementSheetName = statementVO.getStatementNum();
                        cell.setCellValue(statementVO.getStatementNum());
                        createStatementSubOrderExcel(workbook, statementVO.getOrderVOList(), statementSheetName);
                        cell.setCellFormula("HYPERLINK(\"#" + statementSheetName +"!A1\",\"" + statementSheetName + "\")");
                        XSSFCellStyle fieldCellStyle = ExcelUtil.hyperlinkStyle(workbook);
                        // 设置单元格字体颜色
                        cell.setCellStyle(fieldCellStyle);
                        break;
                    case 1:
                        // 结算时间段
                        String startDate = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementVO.getStartDate());
                        String endDate = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementVO.getEndDate());
                        cell.setCellValue(startDate + "至" + endDate);
                        break;
                    case 2:
                        // 状态
                        if (statementVO.getStatementStatus() == 1) {
                            cell.setCellValue("待审核");
                        } else if (statementVO.getStatementStatus() == 2) {
                            cell.setCellValue("结算中");
                        } else if (statementVO.getStatementStatus() == 3) {
                            cell.setCellValue("已结算");
                        } else if (statementVO.getStatementStatus() == 4) {
                            cell.setCellValue("驳回");
                        }
                        break;
                    case 3:
                        // 结算金额
                        cell.setCellValue(statementVO.getTotalMoney().setScale(2).toString());
                        break;
                    case 4:
                        // 开户名称
                        cell.setCellValue(statementVO.getAccountName());
                        break;
                    case 5:
                        // 开户银行
                        cell.setCellValue(statementVO.getBankName());
                        break;
                    case 6:
                        // 开户支行
                        cell.setCellValue(statementVO.getBankBranchName());
                        break;
                    case 7:
                        // 银行账号
                        cell.setCellValue(statementVO.getBankNo());
                        break;
                    case 8:
                        // 创建日期
                        cell.setCellValue(DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementVO.getCreateTime()));
                        break;
                    case 9:
                        // 驳回原因
                        cell.setCellValue(statementVO.getRejectReason());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }

    /**
     *@Author: Pipi
     *@Description: 导出账单表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/4/25 15:46
     **/
    @Override
    public Workbook exportMaterOrder(List<?> entityList) {
        //工作表名称
        String titleName = "账单表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = MASTER_ORDER_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        // 设置列宽
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 9000);
        sheet.setColumnWidth(6, 4000);
        sheet.setColumnWidth(7, 7000);
        sheet.setColumnWidth(8, 6000);
        sheet.setColumnWidth(9, 4000);
        sheet.setColumnWidth(10, 10000);
        sheet.setColumnWidth(11, 6000);
        sheet.setColumnWidth(12, 4000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < MASTER_ORDER_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyFinanceOrderEntity entity = (PropertyFinanceOrderEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 账单号
                        cell.setCellValue(entity.getOrderNum());
                        break;
                    case 1:
                        // 状态
                        if (entity.getOrderStatus() != null) {
                            if (entity.getOrderStatus() == 0) {
                                cell.setCellValue("待收款");
                            } else if (entity.getOrderStatus() == 0) {
                                cell.setCellValue("已收款");
                            }
                        }
                        break;
                    case 2:
                        // 账单日期
                        cell.setCellValue(DateTimeFormatter.ofPattern("yyyy-MM").format(entity.getOrderTime()));
                        break;
                    case 3:
                        // 账单类型
                        cell.setCellValue("物业费");
                        break;
                    case 4:
                        // 应收金额
                        cell.setCellValue(entity.getTotalMoney().setScale(2).toString());
                        break;
                    case 5:
                        // 房屋/车牌号
                        cell.setCellValue(entity.getAddress());
                        break;
                    case 6:
                        // 业主姓名
                        cell.setCellValue(entity.getRealName());
                        break;
                    case 7:
                        // 收款单号
                        if (entity.getReceiptEntity() != null) {
                            cell.setCellValue(entity.getReceiptEntity().getReceiptNum());
                        }
                        break;
                    case 8:
                        // 收款时间
                        if (entity.getReceiptEntity() != null) {
                            cell.setCellValue(DateTimeFormatter.ofPattern(TIME_FORMAT_STRING).format(entity.getReceiptEntity().getCreateTime()));
                        }
                        break;
                    case 9:
                        // 收款渠道
                        if (entity.getReceiptEntity() != null) {
                            if (entity.getReceiptEntity().getTransactionType() != null) {
                                if (entity.getReceiptEntity().getTransactionType() == 1) {
                                    cell.setCellValue("支付宝");
                                } else if (entity.getReceiptEntity().getTransactionType() == 2) {
                                    cell.setCellValue("微信");
                                }
                            }
                        }
                        break;
                    case 10:
                        // 支付渠道单号
                        if (entity.getReceiptEntity() != null) {
                            cell.setCellValue(entity.getReceiptEntity().getTransactionNo());
                        }
                        break;
                    case 11:
                        // 结算单号
                        cell.setCellValue(entity.getStatementNum());
                        break;
                    case 12:
                        // 结算状态
                        if (entity.getStatementStatus() != null) {
                            if (entity.getStatementStatus() == 0) {
                                cell.setCellValue("待结算");
                            } else if (entity.getStatementStatus() == 1) {
                                cell.setCellValue("待审核");
                            } else if (entity.getStatementStatus() == 2) {
                                cell.setCellValue("结算中");
                            } else if (entity.getStatementStatus() == 3) {
                                cell.setCellValue("已结算");
                            } else if (entity.getStatementStatus() == 4) {
                                cell.setCellValue("驳回");
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }

    /**
     *@Author: Pipi
     *@Description: 导出收款单主表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/4/26 10:16
     **/
    @Override
    public Workbook exportMasterReceipt(List<?> entityList) {
        //工作表名称
        String titleName = "收款单表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = RECEIPT_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 9000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < RECEIPT_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyFinanceReceiptEntity entity = (PropertyFinanceReceiptEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 收款单号
                        cell.setCellValue(entity.getReceiptNum());
                        break;
                    case 1:
                        // 收款金额
                        cell.setCellValue(entity.getReceiptMoney().setScale(2).toString());
                        break;
                    case 2:
                        // 收款时间
                        cell.setCellValue(DateTimeFormatter.ofPattern(TIME_FORMAT_STRING).format(entity.getCreateTime()));
                        break;
                    case 3:
                        // 收款渠道
                        if (entity.getTransactionType() != null) {
                            if (entity.getTransactionType() == 1) {
                                cell.setCellValue("支付宝");
                            } else if (entity.getTransactionType() == 2) {
                                cell.setCellValue("微信");
                            }
                        }
                        break;
                    case 4:
                        // 收款渠道单号
                        cell.setCellValue(entity.getTransactionNo());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }

    /**
     *@Author: Pipi
     *@Description: 导出收款单主从表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/4/26 11:26
     **/
    @Override
    public Workbook exportMasterSlaveReceipt(List<?> entityList) {
        //工作表名称
        String titleName = "收款单表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = RECEIPT_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 9000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < RECEIPT_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyFinanceReceiptEntity entity = (PropertyFinanceReceiptEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 收款单号
                        String orderSheetName = entity.getReceiptNum();
                        cell.setCellValue(entity.getReceiptNum());
                        createReceiptSubOrderExcel(workbook, entity.getOrderList(), orderSheetName);
                        cell.setCellFormula("HYPERLINK(\"#" + orderSheetName +"!A1\",\"" + orderSheetName + "\")");
                        XSSFCellStyle fieldCellStyle = ExcelUtil.hyperlinkStyle(workbook);
                        // 设置单元格字体颜色
                        cell.setCellStyle(fieldCellStyle);
                        break;
                    case 1:
                        // 收款金额
                        cell.setCellValue(entity.getReceiptMoney().setScale(2).toString());
                        break;
                    case 2:
                        // 收款时间
                        cell.setCellValue(DateTimeFormatter.ofPattern(TIME_FORMAT_STRING).format(entity.getCreateTime()));
                        break;
                    case 3:
                        // 收款渠道
                        if (entity.getTransactionType() != null) {
                            if (entity.getTransactionType() == 1) {
                                cell.setCellValue("支付宝");
                            } else if (entity.getTransactionType() == 2) {
                                cell.setCellValue("微信");
                            }
                        }
                        break;
                    case 4:
                        // 收款渠道单号
                        cell.setCellValue(entity.getTransactionNo());
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }

    /**
     *@Author: Pipi
     *@Description:
     *@Param: workbook: 创建多sheet,用于关联收款单的账单表
     *@Param: entityList:
     *@Param: titleName:
     *@Return: void
     *@Date: 2021/4/26 11:29
     **/
    private void createReceiptSubOrderExcel(Workbook workbook, List<?> entityList, String titleName) {
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = RECEIPT_ORDER_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createLinkExcelTitle(workbook, sheet, titleName + "账单表", 530, "宋体", 20, titleField.length, "收款单表");
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        //往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < RECEIPT_ORDER_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyFinanceOrderEntity orderEntity = (PropertyFinanceOrderEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 账单号
                        cell.setCellValue(orderEntity.getOrderNum());
                        break;
                    case 1:
                        // 账单日期
                        cell.setCellValue(DateTimeFormatter.ofPattern("yyyy-MM").format(orderEntity.getOrderTime()));
                        break;
                    case 2:
                        // 账单类型
                        cell.setCellValue("物业费");
                        break;
                    case 3:
                        // 应收金额
                        cell.setCellValue(orderEntity.getTotalMoney().setScale(2).toString());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     *@Author: Pipi
     *@Description: 创建多sheet,用于关联结算单的账单表
     *@Param: entityList:
     *@Param: titleName:
     *@Return: void
     *@Date: 2021/4/25 11:05
     **/
    private void createStatementSubOrderExcel(Workbook workbook, List<?> entityList, String titleName) {
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = ORDER_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createLinkExcelTitle(workbook, sheet, titleName + "账单表", 530, "宋体", 20, titleField.length, "结算单表");
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 7000);
        //往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < ORDER_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                StatementOrderVO statementOrderVO = (StatementOrderVO)entityList.get(index);
                switch (j) {
                    case 0:
                        // 账单号
                        cell.setCellValue(statementOrderVO.getOrderNum());
                        break;
                    case 1:
                        // 账单日期
                        cell.setCellValue(DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementOrderVO.getOrderTime()));
                        break;
                    case 2:
                        // 账单类型
                        cell.setCellValue(statementOrderVO.getOrderType());
                        break;
                    case 3:
                        // 收款金额
                        cell.setCellValue(statementOrderVO.getTotalMoney().setScale(2).toString());
                        break;
                    case 4:
                        // 收款时间
                        if (statementOrderVO.getPayTime() != null) {
                            cell.setCellValue(DateTimeFormatter.ofPattern(DATE_FORMAT_STRING).format(statementOrderVO.getPayTime()));
                        }
                        break;
                    case 5:
                        // 收款渠道
                        if (statementOrderVO.getTransactionType() != null) {
                            if (statementOrderVO.getTransactionType() == 1) {
                                cell.setCellValue("支付宝");
                            } else if (statementOrderVO.getTransactionType() == 2) {
                                cell.setCellValue("微信");
                            }
                        }
                        break;
                    case 6:
                        // 支付渠道单号
                        cell.setCellValue(statementOrderVO.getTransactionNo());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
