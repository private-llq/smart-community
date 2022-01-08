package com.jsy.community.service.impl;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CebBankService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserLivingExpensesAccountService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.entity.UserLivingExpensesBillEntity;
import com.jsy.community.entity.UserLivingExpensesGroupEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.UserLivingExpensesAccountMapper;
import com.jsy.community.mapper.UserLivingExpensesBillMapper;
import com.jsy.community.mapper.UserLivingExpensesGroupMapper;
import com.jsy.community.qo.cebbank.CebQueryBillInfoQO;
import com.jsy.community.qo.cebbank.CebQueryMobileBillQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.cebbank.CebQueryBillInfoVO;
import com.jsy.community.vo.cebbank.CebQueryMobileBillVO;
import com.jsy.community.vo.cebbank.test.CebBillQueryResultDataModelVO;
import com.jsy.community.vo.cebbank.test.CebCreatePaymentBillParamsModelVO;
import com.zhsj.basecommon.exception.BaseException;
import jodd.util.StringUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

/**
 * @Author: Pipi
 * @Description:
 * @Date: 2021/12/2 16:55
 * @Version: 1.0
 **/
@DubboService(version = Const.version, group = Const.group_proprietor)
public class UserLivingExpensesAccountServiceImpl extends ServiceImpl<UserLivingExpensesAccountMapper, UserLivingExpensesAccountEntity> implements UserLivingExpensesAccountService {
    @Autowired
    private UserLivingExpensesAccountMapper accountMapper;

    @Autowired
    private UserLivingExpensesBillMapper billMapper;

    @Autowired
    private UserLivingExpensesGroupMapper groupMapper;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private CebBankService cebBankService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    /**
     * @param accountEntity :
     * @author: Pipi
     * @description: 绑定户号
     * @return: {@link Integer}
     * @date: 2021/12/3 16:57
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAccount(UserLivingExpensesAccountEntity accountEntity) {
        accountEntity.setId(SnowFlake.nextId());
        if (accountEntity.getBusinessFlow() == 1) {
            // 查询缴费信息,无返回值,目的是验证填写信息是否正确,不正确会抛出异常,不往下走
            directBillInfo(accountEntity);
        } else {
            // 查询用户信息和账单,并添加
            CebQueryBillInfoVO cebQueryBillInfoVO = queryBillInfo(accountEntity);
            addBill(accountEntity, cebQueryBillInfoVO);
        }
        // 如果没有选择分组,则分配到默认分组
        if (StringUtil.isBlank(accountEntity.getGroupId())) {
            // 查询是否存在分组
            QueryWrapper<UserLivingExpensesGroupEntity> groupEntityQueryWrapper = new QueryWrapper<>();
            groupEntityQueryWrapper.eq("uid", accountEntity.getUid());
            groupEntityQueryWrapper.last("limit 1");
            UserLivingExpensesGroupEntity userLivingExpensesGroupEntity = groupMapper.selectOne(groupEntityQueryWrapper);
            if (userLivingExpensesGroupEntity != null) {
                accountEntity.setGroupId(userLivingExpensesGroupEntity.getId().toString());
            } else {
                // 新增默认分组
                UserLivingExpensesGroupEntity userLivingExpensesGroupEntity1 = new UserLivingExpensesGroupEntity();
                userLivingExpensesGroupEntity1.setUid(accountEntity.getUid());
                userLivingExpensesGroupEntity1.setGroupName("默认分组");
                userLivingExpensesGroupEntity1.setIsDefault(1);
                userLivingExpensesGroupEntity1.setId(SnowFlake.nextId());
                groupMapper.insert(userLivingExpensesGroupEntity1);
                accountEntity.setGroupId(userLivingExpensesGroupEntity1.getId().toString());
            }
        }
        int insert = accountMapper.insert(accountEntity);
        return insert == 1 ? accountEntity.getId() : null;
    }

    /**
     * @param accountEntity :
     * @author: Pipi
     * @description: 根据account和uid查询账户信息
     * @return: {@link UserLivingExpensesAccountEntity}
     * @date: 2021/12/7 10:30
     **/
    @Override
    public UserLivingExpensesAccountEntity queryAccount(UserLivingExpensesAccountEntity accountEntity) {
        QueryWrapper<UserLivingExpensesAccountEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", accountEntity.getAccount());
        queryWrapper.eq("uid", accountEntity.getUid());
        return accountMapper.selectOne(queryWrapper);
    }

    /**
     * @param accountEntity :
     * @author: Pipi
     * @description: 根据id查询账户信息
     * @return: {@link UserLivingExpensesAccountEntity}
     * @date: 2021/12/10 18:49
     **/
    @Override
    public UserLivingExpensesAccountEntity queryAccountById(UserLivingExpensesAccountEntity accountEntity) {
        QueryWrapper<UserLivingExpensesAccountEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", accountEntity.getId());
        queryWrapper.eq("uid", accountEntity.getUid());
        return accountMapper.selectOne(queryWrapper);
    }

    /**
     * @param accountEntity : 账户信息实体
     * @author: Pipi
     * @description: 修改账户信息
     * @return: {@link Boolean}
     * @date: 2021/12/28 17:41
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyAccount(UserLivingExpensesAccountEntity accountEntity) {
        UserLivingExpensesAccountEntity recordAccountEntity = queryAccountById(accountEntity);
        if (recordAccountEntity == null) {
            throw new ProprietorException(JSYError.DATA_LOST);
        }
        if (!accountEntity.getAccount().equals(recordAccountEntity.getAccount()) || !accountEntity.getItemCode().equals(recordAccountEntity.getItemCode())) {
            // 如果户号或者项目有改变,需要重新查询账单信息,同时删除原有未缴费账单
            // 删除该用户的该户号的账单
            QueryWrapper<UserLivingExpensesBillEntity> billEntityQueryWrapper = new QueryWrapper<>();
            billEntityQueryWrapper.eq("uid", accountEntity.getUid());
            if (!accountEntity.getItemCode().equals(recordAccountEntity.getItemCode())) {
                // 修改了项目
                billEntityQueryWrapper.eq("item_code", recordAccountEntity.getItemCode());
            }
            if (accountEntity.getAccount().equals(recordAccountEntity.getAccount())) {
                // 修改了户号
                billEntityQueryWrapper.eq("bill_key", recordAccountEntity.getAccount());
            }
            billEntityQueryWrapper.eq("bill_status", 0);
            billMapper.delete(billEntityQueryWrapper);
            if (accountEntity.getBusinessFlow() == 1) {
                // 查询缴费信息,无返回值,目的是验证填写信息是否正确,不正确会抛出异常,不往下走
                directBillInfo(accountEntity);
            } else {
                // 查询用户信息和账单,并添加
                CebQueryBillInfoVO cebQueryBillInfoVO = queryBillInfo(accountEntity);
                addBill(accountEntity, cebQueryBillInfoVO);
            }
        } else {
            // 修改了分组
            recordAccountEntity.setGroupId(accountEntity.getGroupId());
            accountMapper.updateById(recordAccountEntity);
        }
        return true;
    }

    /**
     * @param accountEntity :
     * @author: Pipi
     * @description: 删除户号
     * @return: {@link Boolean}
     * @date: 2022/1/4 18:13
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAccount(UserLivingExpensesAccountEntity accountEntity) {
        QueryWrapper<UserLivingExpensesAccountEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", accountEntity.getUid());
        queryWrapper.eq("id", accountEntity.getId());
        UserLivingExpensesAccountEntity userLivingExpensesAccountEntity = accountMapper.selectOne(queryWrapper);
        if (userLivingExpensesAccountEntity == null) {
            return false;
        }
        QueryWrapper<UserLivingExpensesBillEntity> billEntityQueryWrapper = new QueryWrapper<>();
        billEntityQueryWrapper.eq("uid", accountEntity.getUid());
        billEntityQueryWrapper.eq("bill_status", 0);
        billEntityQueryWrapper.eq("bill_key", userLivingExpensesAccountEntity.getAccount());
        billMapper.delete(billEntityQueryWrapper);
        accountMapper.delete(queryWrapper);
        return true;
    }

    /**
     * @author: Pipi
     * @description: 查询查缴类型缴费账单信息
     * @param accountEntity:
     * @return: {@link CebQueryBillInfoVO}
     * @date: 2021/12/28 18:14
     **/
    protected CebQueryBillInfoVO queryBillInfo(UserLivingExpensesAccountEntity accountEntity) {
        CebQueryBillInfoQO billInfoQO = new CebQueryBillInfoQO();
        billInfoQO.setSessionId(cebBankService.getCebBankSessionId(accountEntity.getMobile(), accountEntity.getDeviceType()));
        billInfoQO.setType(accountEntity.getCategoryId());
        billInfoQO.setCityName(accountEntity.getCityName());
        billInfoQO.setItemCode(accountEntity.getItemId());
        billInfoQO.setItemId(accountEntity.getItemCode());
        billInfoQO.setBillKey(accountEntity.getAccount());
        billInfoQO.setFlag("1");
        billInfoQO.setPollingTimes("1");
        billInfoQO.setDeviceType(accountEntity.getDeviceType());
        billInfoQO.setBusinessFlow(accountEntity.getBusinessFlow());
        return cebBankService.queryBillInfo(billInfoQO);
    }

    /**
     * @author: Pipi
     * @description: 查询直缴类型缴费账单信息
     * @param accountEntity:
     * @return: {@link CebQueryBillInfoVO}
     * @date: 2022/1/8 16:22
     **/
    protected void directBillInfo(UserLivingExpensesAccountEntity accountEntity) {
        CebQueryMobileBillQO cebQueryMobileBillQO = new CebQueryMobileBillQO();
        cebQueryMobileBillQO.setSessionId(cebBankService.getCebBankSessionId(accountEntity.getMobile(), accountEntity.getDeviceType()));
        cebQueryMobileBillQO.setCategoryType(accountEntity.getTypeId());
        cebQueryMobileBillQO.setMobile(accountEntity.getAccount());
        cebQueryMobileBillQO.setDeviceType(accountEntity.getDeviceType());
        cebBankService.queryMobileBill(cebQueryMobileBillQO);
    }

    /**
     * @author: Pipi
     * @description: 增加账单
     * @param cebQueryBillInfoVO:
     * @return: {@link Boolean}
     * @date: 2021/12/28 18:16
     **/
    protected Boolean addBill(UserLivingExpensesAccountEntity accountEntity, CebQueryBillInfoVO cebQueryBillInfoVO) {
        if (cebQueryBillInfoVO != null) {
            if (!CollectionUtils.isEmpty(cebQueryBillInfoVO.getBillQueryResultModel().getBillQueryResultDataModelList())) {
                CebBillQueryResultDataModelVO resultDataModelVO = cebQueryBillInfoVO.getBillQueryResultModel().getBillQueryResultDataModelList().get(0);
                String originalCustomerName = resultDataModelVO.getOriginalCustomerName();
                accountEntity.setHouseholder(StringUtil.isNotBlank(originalCustomerName) ? originalCustomerName : resultDataModelVO.getCustomerName());
                accountEntity.setAddress(cebQueryBillInfoVO.getBillQueryResultModel().getItem7());

                UserLivingExpensesBillEntity billEntity = new UserLivingExpensesBillEntity();
                billEntity.setUid(accountEntity.getUid());
                billEntity.setTypeId(accountEntity.getTypeId());
                billEntity.setItemId(accountEntity.getItemId());
                billEntity.setItemCode(accountEntity.getItemCode());
                billEntity.setBillKey(accountEntity.getAccount());
                billEntity.setBillAmount(resultDataModelVO.getPayAmount());
                billEntity.setQueryAcqSsn(cebQueryBillInfoVO.getQryAcqSsn());
                billEntity.setCustomerName(accountEntity.getHouseholder());
                billEntity.setContactNo(resultDataModelVO.getContractNo());
                billEntity.setBalance(resultDataModelVO.getBalance());
                billEntity.setBeginDate(resultDataModelVO.getBeginDate());
                billEntity.setEndDate(resultDataModelVO.getEndDate());
                billEntity.setFieldA(resultDataModelVO.getFiled1());
                billEntity.setFieldB(resultDataModelVO.getFiled2());
                billEntity.setFieldC(resultDataModelVO.getFiled3());
                billEntity.setFieldD(resultDataModelVO.getFiled4());
                billEntity.setFieldE(resultDataModelVO.getFiled5());
                billEntity.setBillStatus(0);
                CebCreatePaymentBillParamsModelVO createPaymentBillParamsModelVO = cebQueryBillInfoVO.getBillQueryResultModel().getCreatePaymentBillParamsModel();
                if (createPaymentBillParamsModelVO != null) {
                    billEntity.setRangLimit(createPaymentBillParamsModelVO.getRangLimit());
                    billEntity.setChooseAmount(createPaymentBillParamsModelVO.getChooseAmount());
                }
                billEntity.setId(SnowFlake.nextId());
                billMapper.insert(billEntity);
                return true;
            }
        } else {
            throw new ProprietorException(6001, "缴费信息查询失败");
        }
        return false;
    }
}