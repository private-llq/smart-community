package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommunityInformService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityInformEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * @author YuLF
 * @date 2020/11/16 10:56
 */
@Api(tags = "社区消息控制器")
@RestController
@RequestMapping("/community/inform")
@Slf4j
@Login(allowAnonymous = true)
@ApiJSYController
public class CommunityInformController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityInformService communityInformService;


    /**
     * 查询 通知消息
     * @param communityEntity   查询参数
     * @return                  返回查询结果
     */
    @PostMapping( value = "/page", produces = "application/json;charset=utf-8")
    @ApiOperation("查询社区通知消息")
    public CommonResult<?> queryInform(@RequestBody BaseQO<CommunityInformEntity> communityEntity){
        //1.查询分页参数非空数字效验
        ValidatorUtils.validatePageParam(communityEntity);
        if(communityEntity.getQuery() == null){
            return CommonResult.error(JSYError.BAD_REQUEST);
        }
        List<CommunityInformEntity> records = communityInformService.queryCommunityInform(communityEntity).getRecords();
        return CommonResult.ok(records);
    }

    /**
     * 修改通知消息 [管理端]
     * @param communityInformEntity  修改参数实体接收类
     * @return                       返回修改结果
     */
    @PutMapping(produces = "application/json;charset=utf-8")
    @ApiOperation("修改社区通知消息")
    public CommonResult<Boolean> updateInform(@RequestBody CommunityInformEntity communityInformEntity){
        //1.参数效验
        ValidatorUtils.validateEntity(communityInformEntity, CommunityInformEntity.updateCommunityInformValidate.class);
        //2.修改操作
        Boolean isUpdateSuccess = communityInformService.updateCommunityInform(communityInformEntity);
        return isUpdateSuccess ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     * 删除通知消息 [管理员]
     * @param id   消息id
     * @return     返回修改成功值
     */
    @DeleteMapping()
    @ApiOperation("删除社区通知消息")
    public CommonResult<Boolean> deleteInform(@RequestParam Long id){
        return communityInformService.delCommunityInform(id) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     * 新增通知消息 [管理员]
     * @param communityInformEntity     新增消息参数实体
     * @return              返回是否新增成功
     */
    @PostMapping()
    @ApiOperation("添加社区通知消息")
    public CommonResult<Boolean> addInform(@RequestBody CommunityInformEntity communityInformEntity) throws DuplicateKeyException, SQLIntegrityConstraintViolationException  {
        //1.效验用户是否是一个管理员
        //2.验证参数实体效验
        ValidatorUtils.validateEntity(communityInformEntity, CommunityInformEntity.addCommunityInformValidate.class);
        //3.添加当前社区新消息
        try{
            communityInformService.addCommunityInform(communityInformEntity);
            return CommonResult.ok();
        }catch (Exception e){
            log.error("com.jsy.community.controller.CommunityInformController.addInform：{}",e.getMessage());
            return CommonResult.error("添加失败!");
        }
    }


}
