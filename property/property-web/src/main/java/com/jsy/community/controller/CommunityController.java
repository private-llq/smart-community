package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IPropertyCompanyService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.PropertyCommunityListVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社区 前端控制器
 * </p>
 *
 * @author qq459799974
 * @since 2020-11-25
 */
@Api(tags = "社区控制器")
@RestController
// @ApiJSYController
@RequestMapping("/community")
public class CommunityController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityService communityService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyCompanyService propertyCompanyService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${propertyLoginExpireHour}")
    private long loginExpireHour = 12;

    /**
     * @return com.jsy.community.vo.CommonResult<java.util.List < com.jsy.community.vo.BannerVO>>
     * @Author lihao
     * @Description 测试分布式事物  ==========先别删，有点用  要删的时候  我来================
     * @Date 2020/12/23 15:47
     * @Param [bannerQO]
     **/
    @ApiOperation("添加社区")
    @GetMapping("/addCommunityEntity")
    @Permit("community:property:community:addCommunityEntity")
    public CommonResult addCommunityEntity() {
        communityService.addCommunityEntity();
        return CommonResult.ok();
    }

    @ApiOperation("获取社区电子地图")
    @GetMapping
    @Permit("community:property:community")
    public CommonResult getElectronicMap() {
        Long communityId = UserUtils.getAdminInfo().getCommunityId();
        Map<String, Object> map = communityService.getElectronicMap(communityId);
        return CommonResult.ok(map);
    }

    /**
     * @Description: id单查详情
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021-07-29
     **/
    @ApiOperation("id单查详情")
    @GetMapping("/details")
    @Permit("community:property:community:details")
    public CommonResult queryDetails(@RequestParam Long communityId) {
        return CommonResult.ok(communityService.queryDetails(communityId));
    }

    /**
     * @param communityEntity:
     * @author: Pipi
     * @description: 物业端添加社区
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/7/22 9:37
     **/
    @PostMapping("/addCommunity")
    @businessLog(operation = "新增", content = "新增了【物业社区】")
    @Permit("community:property:community:addCommunity")
    public CommonResult addCommunity(@RequestBody CommunityEntity communityEntity) {
        ValidatorUtils.validateEntity(communityEntity, CommunityEntity.ProperyuAddValidatedGroup.class);
        communityEntity.setPropertyId(UserUtils.getAdminCompanyId());
        // 设置默认的社区房屋层级模式
        communityEntity.setHouseLevelMode(1);
        // 新增数据
        Long communityId = communityService.addCommunity(communityEntity, UserUtils.getUserId());
        // 获取登录用户数据
        AdminInfoVo adminUserInfo = UserUtils.getAdminInfo();
        adminUserInfo.getCommunityIdList().add(String.valueOf(communityId));
        String token = UserUtils.getUserToken();
        // 根据token,更新Redis数据
        UserUtils.updateRedisByToken("Admin:Login", JSON.toJSONString(adminUserInfo), token, loginExpireHour);
        return CommonResult.ok("添加成功!");
    }

    /**
     * @param baseQO:
     * @author: Pipi
     * @description: 物业端分页查询小区列表
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/7/22 11:41
     **/
    @PostMapping("/queryCommunityList")
    @Permit("community:property:community:queryCommunityList")
    public CommonResult communityList(@RequestBody BaseQO<CommunityEntity> baseQO) {
        PageInfo<PropertyCommunityListVO> communityListVOPageInfo = communityService.queryPropertyCommunityList(baseQO, UserUtils.getAdminInfo().getCommunityIdList());
        return CommonResult.ok(communityListVOPageInfo);
    }

    /**
     * @param communityEntity:
     * @author: Pipi
     * @description: 物业端更新社区信息
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/7/22 18:03
     **/
    @PutMapping("/updateCommunity")
    @businessLog(operation = "编辑", content = "更新了【物业社区】")
    @Permit("community:property:community:updateCommunity")
    public CommonResult updateCommunity(@RequestBody CommunityEntity communityEntity) {
        if (communityEntity.getId() == null) {
            throw new JSYException(400, "社区ID不能为空!");
        }
        // 需要判定用户有权限的社区是否包含该社区
        AdminInfoVo adminUserInfo = UserUtils.getAdminInfo();
        if (!adminUserInfo.getCommunityIdList().contains(communityEntity.getId())) {
            throw new JSYException(400, "你没有该社区的操作权限!");
        }
        ValidatorUtils.validateEntity(communityEntity, CommunityEntity.ProperyuAddValidatedGroup.class);
        // 设置默认的社区房屋层级模式
        communityEntity.setHouseLevelMode(1);
        return communityService.updateCommunity(communityEntity) > 0 ? CommonResult.ok("更新成功") : CommonResult.error("更新失败");
    }

    /**
     * @param month:
     * @author: DKS
     * @description: 获取小区概况
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/24 11:52
     **/
    @GetMapping("/getCommunitySurvey")
    @Permit("community:property:community:getCommunitySurvey")
    public CommonResult getCommunitySurvey(Integer month) {
        if (month == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "缺少查询类型");
        }
        Long adminCommunityId = UserUtils.getAdminCommunityId();
        return CommonResult.ok(communityService.getCommunitySurvey(month, adminCommunityId));
    }

    /**
     * @author: DKS
     * @description: 获取物业控制台
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/25 13:45
     **/
    @GetMapping("/getPropertySurvey")
    @Permit("community:property:community:getPropertySurvey")
    public CommonResult getPropertySurvey() {
        Long companyId = UserUtils.getAdminCompanyId();
        List<String> communityIdList = UserUtils.getAdminCommunityIdList();
        return CommonResult.ok(communityService.getPropertySurvey(companyId, communityIdList));
    }

    /**
     * @param communityId:
     * @author: DKS
     * @description: 获取物业控制台里的收费统计
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/31 17:09
     **/
    @GetMapping("/getPropertySurvey/order/from")
    @Permit("community:property:community:getPropertySurvey:order:from")
    public CommonResult getPropertySurvey(Integer year, Long communityId) {
        if (communityId == null || year == null) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "缺少查询类型");
        }
        return CommonResult.ok(communityService.getPropertySurveyOrderFrom(year, communityId));
    }

    /**
     * @author: DKS
     * @description: 获取物业通用顶部
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/26 11:49
     **/
    @ApiOperation("获取物业通用顶部")
    @GetMapping("/property/top/details")
    @Permit("community:property:community:property:top:details")
    public CommonResult getCompanyNameByCompanyId() {
        return CommonResult.ok(propertyCompanyService.getCompanyNameByCompanyId(UserUtils.getAdminCompanyId()));
    }

    /**
     * @author: DKS
     * @description: 物业端-系统设置-短信群发
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/30 17:22
     **/
    @GetMapping("/group/send/sms")
    @Permit("community:property:community:group:send:sms")
    public CommonResult groupSendSMS(@RequestParam(value = "content")String content, @RequestParam(value = "isDistinct")boolean isDistinct,
                                     @RequestParam(value = "taskTime")String taskTime, @RequestParam(value = "number")int number, @RequestParam(value = "communityIdList")List<String> communityIdList) {
        Long adminCompanyId = UserUtils.getAdminCompanyId();
        if (content == null) {
            throw new PropertyException(JSYError.REQUEST_PARAM.getCode(), "缺少查询类型");
        }
        return communityService.groupSendSMS(communityIdList, content, isDistinct, taskTime, number, adminCompanyId) ? CommonResult.ok("发送成功") : CommonResult.error("发送失败");
    }

    /**
     * @author: DKS
     * @description: 物业端-系统设置-短信配置
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/1 11:50
     **/
    @PostMapping("/update/sms/config")
    @Permit("community:property:community:update:sms:config")
    public CommonResult updateSMSConfig(@RequestBody PropertyCompanyEntity propertyCompanyEntity) {
        Long companyId = UserUtils.getAdminCompanyId();
        ValidatorUtils.validateEntity(propertyCompanyEntity);
        propertyCompanyEntity.setId(companyId);
        return propertyCompanyService.updateSMSConfig(propertyCompanyEntity) ? CommonResult.ok("更新成功") : CommonResult.error("更新失败");
    }

    /**
     * @author: DKS
     * @description: 查询物业公司详情
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/2 09:51
     **/
    @GetMapping("/select/company")
    @Permit("community:property:community:select:company")
    public CommonResult selectCompany() {
        Long companyId = UserUtils.getAdminCompanyId();
        return CommonResult.ok(propertyCompanyService.selectCompany(companyId), "查询成功");
    }
}

