package com.jsy.community.controller;

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
import org.springframework.web.bind.annotation.*;

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
    public CommonResult<List<CommunityInformEntity>> queryInform(@RequestBody BaseQO<CommunityInformEntity> communityEntity){
        //1.分页查询参数非空数字效验
        ValidatorUtils.validatePageParam(communityEntity);
        List<CommunityInformEntity> records = communityInformService.queryCommunityInform(communityEntity).getRecords();
        return CommonResult.ok(records);
    }

    /**
     * 修改通知消息 [管理端]
     * @param communityEntity  修改参数实体接收类
     * @return                 返回修改结果
     */
    @PutMapping(produces = "application/json;charset=utf-8")
    @ApiOperation("修改社区通知消息")
    public CommonResult<Boolean> updateInform(@RequestBody CommunityInformEntity communityEntity){
        //1.参数效验
        ValidatorUtils.validateEntity(CommunityInformEntity.updateCommunityInformValidate.class);
        //2.修改操作
        Boolean isUpdateSuccess = communityInformService.updateCommunityInform(communityEntity);
        return isUpdateSuccess ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     * 删除通知消息 [管理员]
     * @param id   消息id
     * @return     返回修改成功值
     */
    @DeleteMapping("{id}")
    @ApiOperation("删除社区通知消息")
    public CommonResult<Boolean> deleteInform(@PathVariable("id")Long id){
        return communityInformService.delCommunityInform(id) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    /**
     * 新增通知消息 [管理员]
     * @param communityInformEntity     新增消息参数实体
     * @return              返回是否新增成功
     */
    @PostMapping()
    @ApiOperation("添加社区通知消息")
    public CommonResult<Boolean> addInform(@RequestBody CommunityInformEntity communityInformEntity){
        //1.效验用户是否是一个管理员
        //2.验证参数实体效验
        ValidatorUtils.validateEntity(CommunityInformEntity.addCommunityInformValidate.class);
        //3.添加当前社区新消息
        Boolean isAddSuccess = communityInformService.addCommunityInform(communityInformEntity);
        return isAddSuccess ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


}
