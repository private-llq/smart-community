package com.jsy.community.controller;

import com.jsy.community.api.ProprietorException;
import com.jsy.community.api.UserLivingExpensesAccountService;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstError;
import com.jsy.community.entity.UserLivingExpensesAccountEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Pipi
 * @Description: 生活缴费账户控制器
 * @Date: 2021/12/3 16:49
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/livingExpensesAccount")
public class UserLivingExpensesAccountController {
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private UserLivingExpensesAccountService accountService;

    /**
     * @author: Pipi
     * @description: 添加户号
     * @param accountEntity:
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/3 16:54
     **/
    @PostMapping("/v2/addAccount")
    public CommonResult<?> addAccount(@RequestBody UserLivingExpensesAccountEntity accountEntity) {
        if (accountEntity.getBusinessFlow() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "业务流程不能为空");
        }
        if (accountEntity.getBusinessFlow() == 1) {
            ValidatorUtils.validateEntity(accountEntity, UserLivingExpensesAccountEntity.AddDirectAccountValidateGroup.class);
        } else {
            ValidatorUtils.validateEntity(accountEntity, UserLivingExpensesAccountEntity.AddQueryAccountValidateGroup.class);
        }
        accountEntity.setUid(UserUtils.getUserId());
        accountEntity.setMobile(UserUtils.getUserInfo().getMobile());
        UserLivingExpensesAccountEntity userLivingExpensesAccountEntity = accountService.queryAccount(accountEntity);
        if (userLivingExpensesAccountEntity != null) {
            throw new JSYException(JSYError.DUPLICATE_KEY);
        }
        Long id = accountService.addAccount(accountEntity);
        return id == null ? CommonResult.error("添加失败") : CommonResult.ok(id.toString(), "添加成功");
    }

    /**
     * @author: Pipi
     * @description: 费种图标上传
     * @param files:
     * @return: {@link CommonResult<?>}
     * @date: 2022/1/8 10:17
     **/
    @LoginIgnore
    @PostMapping("/v2/iconUpload")
    public CommonResult<?> iconUpload(@RequestParam("files") MultipartFile[] files) {
        for (MultipartFile file : files) {
            //获取文件名
            String originalFilename = file.getOriginalFilename();

        }
        //返回文件上传地址
        String[] upload = MinioUtils.uploadForBatch(files, "cost-icon", true);
        return CommonResult.ok("上传成功");
    }

    /**
     * @author: Pipi
     * @description: 修改户号
     * @param accountEntity:
     * @return: {@link CommonResult<?>}
     * @date: 2021/12/10 18:41
     **/
    @PostMapping("/v2/modifyAccount")
    public CommonResult<?> modifyAccount(@RequestBody UserLivingExpensesAccountEntity accountEntity) {
        if (accountEntity.getBusinessFlow() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM);
        }
        if (accountEntity.getBusinessFlow() == 1) {
            ValidatorUtils.validateEntity(accountEntity, UserLivingExpensesAccountEntity.AddDirectAccountValidateGroup.class);
        } else {
            ValidatorUtils.validateEntity(accountEntity, UserLivingExpensesAccountEntity.AddQueryAccountValidateGroup.class);
        }
        if (StringUtils.isBlank(accountEntity.getGroupId())) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "分组ID不能为空");
        }
        if (accountEntity.getId() == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "账号ID不能为空");
        }
        accountEntity.setUid(UserUtils.getUserId());
        accountService.modifyAccount(accountEntity);
        return CommonResult.ok();
    }

    /**
     * @author: Pipi
     * @description: 删除户号
     * @param accountEntity:
     * @return: {@link CommonResult<?>}
     * @date: 2022/1/4 18:12
     **/
    @PostMapping("/v2/deleteAccount")
    public CommonResult<?> deleteAccount(@RequestBody UserLivingExpensesAccountEntity accountEntity) {
        if (accountEntity.getId() == null) {
            throw new JSYException("账号ID不能为空");
        }
        accountEntity.setUid(UserUtils.getUserId());
        return accountService.deleteAccount(accountEntity) ? CommonResult.ok("删除成功!") : CommonResult.error("删除失败!");
    }
}
