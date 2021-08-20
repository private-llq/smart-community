package com.jsy.community.util.excel.impl;

import com.jsy.community.entity.property.*;
import com.jsy.community.util.FinanceExcelHandler;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.vo.StatementOrderVO;
import com.jsy.community.vo.StatementVO;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    protected static final String[] STATEMENT_TITLE_FIELD = {"结算单号", "结算时间段", "状态", "结算金额", "开户名称", "开户银行", "开户支行", "银行账号", "创建日期", "驳回原因"};

    // 结算单关联账单表.xlsx 字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] ORDER_TITLE_FIELD = {"账单号", "账单日期", "账单类型", "收款金额", "收款时间", "收款渠道", "支付渠道单号"};

    // 账单表字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] MASTER_ORDER_TITLE_FIELD = {"账单号", "状态", "账单日期", "账单类型", "应收金额", "房屋/车牌号", "业主姓名", "收款单号", "收款时间", "收款渠道", "支付渠道单号", "结算单号", "结算状态"};

    // 收款单表.xlsx 字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] RECEIPT_TITLE_FIELD = {"收款单号", "收款金额", "收款时间", "收款渠道", "收款渠道单号"};

    // 收款单关联账单表.xlsx 字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] RECEIPT_ORDER_TITLE_FIELD = {"账单号", "账单日期", "账单类型", "应收金额"};

    protected static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    protected static final String TIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    
    // 财务报表-小区收入字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] FINANCE_FORM_TITLE_FIELD = {"类型", "线上收费", "线下收费", "退款/提现", "合计收入"};
    
    // 财务报表-小区收费字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] CHARGE_TITLE_FIELD = {"收费项目名称", "本月应收", "往月欠收", "优惠金额", "滞纳应收", "滞纳实收", "线上收款", "线下收款", "合计应收", "合计实收", "预存款抵扣", "合计欠收"};
    
    // 财务报表-小区收费字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] COLLECTION_FORM_TITLE_FIELD = {"收费项目名称", "微信支付", "支付宝支付", "余额支付", "现金支付", "银联刷卡", "银行代扣", "合计"};
    
    // 财务报表-小区收费字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] COLLECTION_FORM_ORDER_TITLE_FIELD = {"楼宇", "应收", "实收", "欠缴"};
    
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
                            } else if (entity.getOrderStatus() == 1) {
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
                        if (entity.getReceiptEntity() != null && entity.getReceiptEntity().getTransactionType() != null) {
                            if (entity.getReceiptEntity().getTransactionType() == 1) {
                                cell.setCellValue("支付宝");
                            } else if (entity.getReceiptEntity().getTransactionType() == 2) {
                                cell.setCellValue("微信");
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
    
    /**
     *@Author: DKS
     *@Description: 导出财务报表-小区收入
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/19 15:46
     **/
    @Override
    public Workbook exportFinanceForm(List<?> entityList) {
        //工作表名称
        String titleName = "财务报表-小区收入";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = FINANCE_FORM_TITLE_FIELD;
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
        for (int index = 0; index < 4; index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < FINANCE_FORM_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyFinanceFormEntity entity = (PropertyFinanceFormEntity) entityList.get(0);
                switch (j) {
                    case 0:
                        // 类型
                        if (index == 0) {
                            cell.setCellValue("预存款充值");
                        } else if (index == 1) {
                            cell.setCellValue("小区收费");
                        } else if (index == 2) {
                            cell.setCellValue("押金");
                        } else if (index == 3) {
                            cell.setCellValue("合计");
                        }
                        break;
                    case 1:
                        // 线上收费
                        if (index == 0) {
                            if (entity.getAdvanceDepositOnlineCharging() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getAdvanceDepositOnlineCharging()));
                            }
                        } else if (index == 1) {
                            if (entity.getCommunityOnlineCharging() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getCommunityOnlineCharging()));
                            }
                        } else if (index == 2) {
                            if (entity.getDepositOnlineCharging() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getDepositOnlineCharging()));
                            }
                        } else if (index == 3) {
                            if (entity.getOnlineChargingSum() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getOnlineChargingSum()));
                            }
                        }
                        break;
                    case 2:
                        // 线下收费
                        if (index == 0) {
                            if (entity.getAdvanceDepositOfflineCharging() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getAdvanceDepositOfflineCharging()));
                            }
                        } else if (index == 1) {
                            if (entity.getCommunityOfflineCharging() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getCommunityOfflineCharging()));
                            }
                        } else if (index == 2) {
                            if (entity.getDepositOfflineCharging() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getDepositOfflineCharging()));
                            }
                        } else if (index == 3) {
                            if (entity.getOfflineChargingSum() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getOfflineChargingSum()));
                            }
                        }
                        break;
                    case 3:
                        // 退款/提现
                        if (index == 0) {
                            if (entity.getAdvanceDepositWithdrawal() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getAdvanceDepositWithdrawal()));
                            }
                        } else if (index == 1) {
                            cell.setCellValue(String.valueOf(new BigDecimal("0.00")));
                        } else if (index == 2) {
                            if (entity.getDepositRefund() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getDepositRefund()));
                            }
                        } else if (index == 3) {
                            if (entity.getRefundOrWithdrawalSum() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getRefundOrWithdrawalSum()));
                            }
                        }
                        break;
                    case 4:
                        // 合计收入
                        if (index == 0) {
                            if (entity.getAdvanceDepositTotal() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getAdvanceDepositTotal()));
                            }
                        } else if (index == 1) {
                            if (entity.getCommunityTotal() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getCommunityTotal()));
                            }
                        } else if (index == 2) {
                            if (entity.getDepositTotal() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getDepositTotal()));
                            }
                        } else if (index == 3) {
                            if (entity.getTotalSum() == null) {
                                cell.setCellValue("0.00");
                            } else {
                                cell.setCellValue(String.valueOf(entity.getTotalSum()));
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
     *@Author: DKS
     *@Description: 导出财务报表-小区收费报表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/20 10:31
     **/
    @Override
    public Workbook exportCharge(List<?> entityList) {
        //工作表名称
        String titleName = "财务报表-小区收费";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = CHARGE_TITLE_FIELD;
        //4.创建excel标题行头(最大的那个标题)
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, titleField.length);
        //5.创建excel 字段列  (表示具体的数据列字段)
        ExcelUtil.createExcelField(workbook, sheet, titleField);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        // 设置列宽
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(9, 3000);
        sheet.setColumnWidth(10, 3000);
        sheet.setColumnWidth(11, 3000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < CHARGE_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyFinanceFormChargeEntity entity = (PropertyFinanceFormChargeEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 收费项目名称
                        cell.setCellValue(entity.getFeeRuleName());
                        break;
                    case 1:
                        // 本月应收
                        if (entity.getTotalMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getTotalMoney()));
                        }
                        break;
                    case 2:
                        // 往月欠收
                        if (entity.getArrearsMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getArrearsMoney()));
                        }
                        break;
                    case 3:
                        // 优惠金额
                        if (entity.getCouponMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getCouponMoney()));
                        }
                        break;
                    case 4:
                        // 滞纳应收
                        if (entity.getReceivablePenalMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getReceivablePenalMoney()));
                        }
                        break;
                    case 5:
                        // 滞纳实收
                        if (entity.getCollectPenalMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getCollectPenalMoney()));
                        }
                        break;
                    case 6:
                        // 线上收款
                        if (entity.getCommunityOnlineCharging() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getCommunityOnlineCharging()));
                        }
                        break;
                    case 7:
                        // 线下收款
                        if (entity.getCommunityOfflineCharging() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getCommunityOfflineCharging()));
                        }
                        break;
                    case 8:
                        // 合计应收
                        if (entity.getTotalMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getTotalMoney()));
                        }
                        break;
                    case 9:
                        // 合计实收
                        if (entity.getCommunityOnlineCharging() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getCommunityOnlineCharging()));
                        }
                        break;
                    case 10:
                        // 预存款抵扣
                        if (entity.getDeductionMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getDeductionMoney()));
                        }
                        break;
                    case 11:
                        // 合计欠收
                        if (entity.getArrearsMoneySum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getArrearsMoneySum()));
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
     *@Author: DKS
     *@Description: 导出收款报表-收款报表
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/20 10:59
     **/
    @Override
    public Workbook exportCollectionForm(List<?> entityList) {
        //工作表名称
        String titleName = "收款报表-收款报表";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = COLLECTION_FORM_TITLE_FIELD;
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
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < COLLECTION_FORM_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyCollectionFormEntity entity = (PropertyCollectionFormEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 收费项目名称
                        cell.setCellValue(entity.getFeeRuleName());
                        break;
                    case 1:
                        // 微信支付
                        if (entity.getWeChatPaySum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getWeChatPaySum()));
                        }
                        break;
                    case 2:
                        // 支付宝支付
                        if (entity.getAliPaySum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getAliPaySum()));
                        }
                        break;
                    case 3:
                        // 余额支付
                        if (entity.getBalancePaySum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getBalancePaySum()));
                        }
                        break;
                    case 4:
                        // 现金支付
                        if (entity.getCashPaySum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getCashPaySum()));
                        }
                        break;
                    case 5:
                        // 银联刷卡
                        if (entity.getUnionPaySum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getUnionPaySum()));
                        }
                        break;
                    case 6:
                        // 银行代扣
                        if (entity.getBankPaySum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getBankPaySum()));
                        }
                        break;
                    case 7:
                        // 合计
                        if (entity.getTotalSum() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getTotalSum()));
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
     *@Author: DKS
     *@Description: 导出收款报表-账单统计
     *@Param: entityList:
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/8/20 10:59
     **/
    @Override
    public Workbook exportCollectionFormOrder(List<?> entityList) {
        //工作表名称
        String titleName = "收款报表-账单统计";
        //1.创建excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        String[] titleField = COLLECTION_FORM_ORDER_TITLE_FIELD;
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
        for (int index = 0; index < entityList.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < COLLECTION_FORM_ORDER_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                PropertyCollectionFormEntity entity = (PropertyCollectionFormEntity) entityList.get(index);
                switch (j) {
                    case 0:
                        // 楼宇
                        cell.setCellValue(entity.getTargetIdName());
                        break;
                    case 1:
                        // 应收
                        if (entity.getStatementReceivableMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getStatementReceivableMoney()));
                        }
                        break;
                    case 2:
                        // 实收
                        if (entity.getStatementCollectMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getStatementCollectMoney()));
                        }
                        break;
                    case 3:
                        // 欠缴
                        if (entity.getStatementArrearsMoney() == null) {
                            cell.setCellValue("0.00");
                        } else {
                            cell.setCellValue(String.valueOf(entity.getStatementArrearsMoney()));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return workbook;
    }
}
