package com.jsy.community.util.excel.impl;

import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.entity.property.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.util.FinanceExcelHandler;
import com.jsy.community.utils.DateCalculateUtil;
import com.jsy.community.utils.ExcelUtil;
import com.jsy.community.vo.StatementOrderVO;
import com.jsy.community.vo.StatementVO;
import com.jsy.community.vo.property.FinanceImportErrorVO;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    
    // 历史账单字段 如果增加字段  需要改变实现类逻辑
    protected static final String[] EXPORT_FINANCE_TEMPLATE = {"姓名", "手机号码", "主体类型", "账单主体", "收费项目", "起始时间", "截止时间", "物业费"};
    protected static final String[] EXPORT_FINANCE_ERROR_INFO = {"姓名", "手机号码", "主体类型", "账单主体", "收费项目", "起始时间", "截止时间", "物业费", "错误提示"};
    protected static final String[] FINANCE_TITLE_FIELD = {"关联目标", "关联类型", "收费项目", "开始时间", "结束时间", "状态", "账单金额", "优惠金额", "预存款抵扣", "滞纳金", "实付金额", "支付方式", "账单状态", "生成时间", "支付时间", "房屋备注"};
    
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
                            cell.setCellValue("物业押金");
                        } else if (index == 1) {
                            cell.setCellValue("预存款充值");
                        } else if (index == 2) {
                            cell.setCellValue("小区收费");
                        } else if (index == 3) {
                            cell.setCellValue("合计");
                        }
                        break;
                    case 1:
                        // 线上收费
                        cell.setCellValue(String.valueOf(entity.getOnlineCharging()));
                        break;
                    case 2:
                        // 线下收费
                        cell.setCellValue(String.valueOf(entity.getOfflineCharging()));
                        break;
                    case 3:
                        // 退款/提现
                        cell.setCellValue(String.valueOf(entity.getRefundOrWithdrawal()));
                        break;
                    case 4:
                        // 合计收入
                        cell.setCellValue(String.valueOf(entity.getTotal()));
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
    
    /**
     *@Author: DKS
     *@Description: 获取历史账单模板
     *@Param: :
     *@Return: org.apache.poi.ss.usermodel.Workbook
     *@Date: 2021/9/7 9:32
     **/
    @Override
    public Workbook exportFinanceTemplate() {
        // 表名称
        String titleName = "账单导入";
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
        ExcelUtil.createExcelTitle(workbook, sheet, titleName, 530, "宋体", 20, EXPORT_FINANCE_TEMPLATE.length);
        // 创建Excel字段列
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_FINANCE_TEMPLATE);
        //添加需要约束数据的列下标   "主体类型","收费项目"
        int[] arrIndex = new int[]{2,4};
        // 创建约束数据隐藏表 避免数据过大下拉框不显示问题
        XSSFSheet hiddenSheet = (XSSFSheet) workbook.createSheet("hiddenSheet");
        HashMap<Integer, String> constraintMap = new HashMap<>();
         constraintMap.put(2, "房屋,车位");
         StringBuffer bf = new StringBuffer();
	    for (BusinessEnum.FeeRuleNameEnum value : BusinessEnum.FeeRuleNameEnum.values()) {
            bf.append(value + ",");
	    }
        bf.replace(bf.length() - 2, bf.length() - 1, "");
	    constraintMap.put(4, String.valueOf(bf).replaceAll("\\d{0,2}_", ""));
        //表明验证约束 结束行
        int endRow = 1000;
        // 添加约束
        for (int index : arrIndex) {
            String[] constraintData = constraintMap.get(index).split(",");
            //创建业主信息登记表与隐藏表的约束字段
            ExcelUtil.createProprietorConstraintRef(workbook, hiddenSheet, constraintData, endRow, index);
            //绑定验证
            sheet.addValidationData(ExcelUtil.setBox(sheet, endRow, index));
        }
        //隐藏 隐藏表  下标1 就是隐藏表
        workbook.setSheetHidden(1, true);
        return workbook;
    }
    
    /**
     * @Author: DKS
     * @Description: 解析、常规格式效验Excel数据
     * @Param: excel:
     * @Param: errorVos:
     * @Return: java.util.List<com.jsy.community.entity.HouseEntity>
     * @Date: 2021/9/7 10:00
     */
    @Override
    public List<PropertyFinanceOrderEntity> importFinanceExcel(MultipartFile excel, List<FinanceImportErrorVO> errorVos) {
        List<PropertyFinanceOrderEntity> propertyFinanceOrderEntities = new ArrayList<>();
        //把文件流转换为工作簿
        try {
            //把文件流转换为工作簿
            Workbook workbook = WorkbookFactory.create(excel.getInputStream());
            //从工作簿中读取工作表
            Sheet sheetAt = workbook.getSheetAt(0);
            //excel 字段列
            String[] titleField = Arrays.copyOf(EXPORT_FINANCE_TEMPLATE, EXPORT_FINANCE_TEMPLATE.length);
            //效验excel标题行
            ExcelUtil.validExcelField(sheetAt, titleField);
            //每一列对象 值
            String cellValue;
            //列对象
            Cell cell;
            //行对象
            Row dataRow;
            //每一行的数据对象
            PropertyFinanceOrderEntity propertyFinanceOrderEntity;
            //标识当前行 如果有错误信息 读取到的数据就作废 不导入数据库
            boolean hasError;
            //开始读入excel数据 跳过标题和字段 从真正的数据行开始读取
            for (int j = 2; j <= sheetAt.getLastRowNum(); j++) {
                dataRow = sheetAt.getRow(j);
                hasError = false;
                //如果这行数据不为空 创建一个 实体接收 信息
                propertyFinanceOrderEntity = new PropertyFinanceOrderEntity();
                for (int z = 0; z < titleField.length; z++) {
                    cell = dataRow.getCell(z);
                    cellValue = ExcelUtil.getCellValForType(cell).toString();
                    //列字段校验
                    switch (z) {
                        case 0:
                            // 姓名
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setRealName(cellValue);
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的姓名!");
                                hasError = true;
                            }
                            break;
                        case 1:
                            // 手机号码
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setMobile(cellValue);
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的手机号码!");
                                hasError = true;
                            }
                            break;
                        case 2:
                            // 主体类型
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setAssociatedType(cellValue.equals("房屋") ? 1 : 2);
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的主体类型!");
                                hasError = true;
                            }
                            break;
                        case 3:
                            // 账单主体
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setFinanceTarget(cellValue);
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的账单主体!");
                                hasError = true;
                            }
                            break;
                        case 4:
                            // 收费项目
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setFeeRuleName(cellValue);
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的收费项目!");
                                hasError = true;
                            }
                            break;
                        case 5:
                            // 起始时间
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setBeginTime(DateCalculateUtil.gtmToLocalDate(cellValue));
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的起始时间!");
                                hasError = true;
                            }
                            break;
                        case 6:
                            // 截止时间
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setOverTime(DateCalculateUtil.gtmToLocalDate(cellValue));
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的截止时间!");
                                hasError = true;
                            }
                            break;
                        case 7:
                            // 物业费
                            if (StringUtils.isNotBlank(cellValue)) {
                                propertyFinanceOrderEntity.setPropertyFee(new BigDecimal(cellValue));
                            } else {
                                addFinanceResolverError(errorVos, dataRow, "请填写正确的物业费!");
                                hasError = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if(!hasError){
                    propertyFinanceOrderEntities.add(propertyFinanceOrderEntity);
                }
            }
            return propertyFinanceOrderEntities;
        } catch (IOException e) {
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), e.getMessage());
        }
    }
    
    /**
     * 把解析验证异常的数据添加至 错误集合
     *
     * @param errorList 错误集合
     * @param dataRow   数据行
     * @param errorMsg  错误备注消息
     */
    private static void addFinanceResolverError(@NonNull List<FinanceImportErrorVO> errorList, @NonNull Row dataRow, String errorMsg) {
        //获取单元格
        XSSFCell valueCell = (XSSFCell) dataRow.getCell(0);
        //设置单元格类型
        valueCell.setCellType(CellType.STRING);
        String number = valueCell.getStringCellValue();
        //如果在错误集合里面已经存在这个编号的信息了，那备注信息就直接追加的形式 直接返回集合该对象 否则 新建对象
        FinanceImportErrorVO vo = setAdvanceDepositVo(errorList, number, errorMsg);
        //每一列对象
        Cell cell;
        //每一列对象值
        String stringCellValue;
        for (int cellIndex = 0; cellIndex < dataRow.getLastCellNum(); cellIndex++) {
            cell = dataRow.getCell(cellIndex);
            stringCellValue = String.valueOf(ExcelUtil.getCellValForType(cell));
            switch (cellIndex) {
                case 0:
                    vo.setRealName(stringCellValue);
                    break;
                case 1:
                    vo.setMobile(stringCellValue);
                    break;
                case 2:
                    vo.setTargetType(stringCellValue);
                    break;
                case 3:
                    vo.setFinanceTarget(stringCellValue);
                    break;
                case 4:
                    vo.setFeeRuleName(stringCellValue);
                    break;
                case 5:
                    vo.setBeginTime(LocalDate.parse(stringCellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    break;
                case 6:
                    vo.setOverTime(LocalDate.parse(stringCellValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    break;
                case 7:
                    vo.setPropertyFee(new BigDecimal(stringCellValue));
                default:
                    break;
            }
        }
        errorList.add(vo);
    }
    
    /**
     * 为错误对象设置错误msg 便于excel回显
     * 使用realName作为属性字段查找是否有这个对象 如果 有直接返回 没有则创建一个对象返回
     * @param errorList 查找的列表
     * @param number  真实名称
     * @param errorMsg  错误信息
     * @return 返回列表对象
     */
    public static FinanceImportErrorVO setAdvanceDepositVo(List<FinanceImportErrorVO> errorList, String number, String errorMsg) {
        FinanceImportErrorVO resVo = null;
        //如果根据名称在错误信息列表里面找到了 那就返回这个对象 找不到则新创建一个对象返回
        FinanceImportErrorVO vo = Optional.ofNullable(resVo).orElseGet(FinanceImportErrorVO::new);
        //为该对象设置错误信息 多个以，分割 便于物业人员查看原因
        vo.setRemark(vo.getRemark() == null ? errorMsg :  vo.getRemark() + "，" + errorMsg );
        return vo;
    }
    
    /**
     *@Author: DKS
     *@Description: 写入充值余额导入错误信息 和 把错误信息excel文件上传至文件服务器
     *@Param: errorVos:
     *@Return: java.lang.String:  返回excel文件下载地址
     *@Date: 2021/9/7 16:12
     **/
    @Override
    public Workbook exportFinanceOrderErrorExcel(List<FinanceImportErrorVO> errorVos) {
        //创建excel 工作簿对象
        Workbook workbook = new XSSFWorkbook();
        //创建 一个工作表
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("账单导入错误收集");
        //创建excel标题行头
        ExcelUtil.createExcelTitle(workbook, sheet, "账单导入", 380, "宋体", 15, EXPORT_FINANCE_ERROR_INFO.length);
        //创建excel列字段
        ExcelUtil.createExcelField(workbook, sheet, EXPORT_FINANCE_ERROR_INFO);
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 4000);
        sheet.setColumnWidth(7, 4000);
        sheet.setColumnWidth(8, 5000);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        //2.往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < errorVos.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < EXPORT_FINANCE_ERROR_INFO.length; j++) {
                cell = row.createCell(j);
                FinanceImportErrorVO vo = errorVos.get(index);
                switch (j) {
                    case 0:
                        // 姓名
                        cell.setCellValue(vo.getRealName());
                        break;
                    case 1:
                        // 手机
                        cell.setCellValue(vo.getMobile());
                        break;
                    case 2:
                        // 主体类型
                        cell.setCellValue(vo.getTargetType());
                        break;
                    case 3:
                        // 账单主体
                        cell.setCellValue(vo.getFinanceTarget());
                        break;
                    case 4:
                        // 收费项目
                        cell.setCellValue(vo.getFeeRuleName());
                        break;
                    case 5:
                        // 起始时间
                        cell.setCellValue(String.valueOf(vo.getBeginTime()));
                        break;
                    case 6:
                        // 截止时间
                        cell.setCellValue(String.valueOf(vo.getOverTime()));
                        break;
                    case 7:
                        // 物业费
                        cell.setCellValue(String.valueOf(vo.getPropertyFee()));
                        break;
                    case 8:
                        // 错误提示
                        cell.setCellValue(vo.getRemark());
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
	 *@Description: 导出历史账单表
	 *@Param: :
	 *@Return: org.apache.poi.ss.usermodel.Workbook
	 *@Date: 2021/9/8 13:41
	 **/
	@Override
	public Workbook exportFinance(List<?> entityList) {
		//工作表名称
		String titleName = "账单导出表";
		//1.创建excel 工作簿
		Workbook workbook = new XSSFWorkbook();
		//2.创建工作表
		XSSFSheet sheet = (XSSFSheet) workbook.createSheet(titleName);
		String[] titleField = FINANCE_TITLE_FIELD;
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
		sheet.setColumnWidth(1, 1500);
		sheet.setColumnWidth(2, 3500);
		sheet.setColumnWidth(3, 3000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 1200);
		sheet.setColumnWidth(6, 2500);
		sheet.setColumnWidth(7, 2500);
		sheet.setColumnWidth(8, 2500);
		sheet.setColumnWidth(9, 2500);
		sheet.setColumnWidth(10, 2500);
		sheet.setColumnWidth(11, 3000);
		sheet.setColumnWidth(12, 2000);
		sheet.setColumnWidth(13, 5200);
		sheet.setColumnWidth(14, 5200);
		sheet.setColumnWidth(15, 2500);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		for (int index = 0; index < entityList.size(); index++) {
			row = sheet.createRow(index + 2);
			//创建列
			for (int j = 0; j < FINANCE_TITLE_FIELD.length; j++) {
				cell = row.createCell(j);
				PropertyFinanceOrderEntity entity = (PropertyFinanceOrderEntity) entityList.get(index);
				switch (j) {
					case 0:
						// 关联目标
						cell.setCellValue(entity.getAddress());
						break;
					case 1:
						// 关联类型
						cell.setCellValue(entity.getAssociatedType() == 1 ? "房屋" : "车位");
						break;
					case 2:
						// 收费项目
						cell.setCellValue(entity.getFeeRuleName());
						break;
					case 3:
						// 开始时间
						cell.setCellValue(String.valueOf(entity.getBeginTime()));
						break;
					case 4:
						// 结束时间
						cell.setCellValue(String.valueOf(entity.getOverTime()));
						break;
					case 5:
						// 状态
						cell.setCellValue(entity.getHide() == 1 ? "显示" : entity.getHide() == 2 ? "隐藏" : "");
						break;
					case 6:
						// 账单金额
						cell.setCellValue(String.valueOf(entity.getPropertyFee()));
						break;
					case 7:
						// 优惠金额
						cell.setCellValue(String.valueOf(entity.getCoupon()));
						break;
					case 8:
						// 预存款抵扣
						cell.setCellValue(String.valueOf(entity.getDeduction()));
						break;
					case 9:
						// 滞纳金
						cell.setCellValue(String.valueOf(entity.getPenalSum()));
						break;
					case 10:
						// 实付金额
						cell.setCellValue(String.valueOf(entity.getTotalMoney()));
						break;
                    case 11:
                        // 支付方式
                        if (entity.getPayType() != null) {
                            cell.setCellValue(entity.getPayType() == 1 ? "微信" : entity.getPayType() == 2 ? "支付宝" : entity.getPayType() == 3 ? "余额" : entity.getPayType() == 4 ? "现金" :
                                entity.getPayType() == 5 ? "银联刷卡" : entity.getPayType() == 6 ? "银行代扣" : entity.getPayType() == 7 ? "预存款抵扣" : "");
                        }
                        break;
                    case 12:
                        // 账单状态
                        cell.setCellValue(entity.getOrderStatus() == 1 ? "已收款" : "待收款");
                        break;
                    case 13:
                        // 生成时间
                        cell.setCellValue(df.format(entity.getCreateTime()));
                        break;
                    case 14:
                        // 支付时间
                        if (entity.getPayTime() == null) {
                            cell.setCellValue("");
                        } else {
                            cell.setCellValue(df.format(entity.getPayTime()));
                        }
                        break;
                    case 15:
                        // 房屋备注
                        cell.setCellValue(entity.getBuildType() == 1 ? "系统生成" : entity.getBuildType() == 2 ? "临时收费" : entity.getBuildType() == 3 ? "手动导入" : "");
                        break;
					default:
						break;
				}
			}
		}
		return workbook;
	}
}
