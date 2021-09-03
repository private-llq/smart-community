package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.CommunityHardWareService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityHardWareEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Pipi
 * @Description: 社区扫描设备(扫脸机)控制器
 * @Date: 2021/8/18 10:20
 * @Version: 1.0
 **/
@RestController
@ApiJSYController
@RequestMapping("/hardWare")
public class CommunityHardWareController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private CommunityHardWareService communityHardWareService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @author: Pipi
     * @description: 物业端添加扫描设备(扫脸机)
     * @param communityHardWareEntity: 扫描设备(扫脸机)实体
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/18 10:25
     **/
    @Login
    @PostMapping("/v2/addHardWare")
    public CommonResult addHardWare(@RequestBody CommunityHardWareEntity communityHardWareEntity) {
        ValidatorUtils.validateEntity(communityHardWareEntity, CommunityHardWareEntity.addHardWareValidate.class);
        communityHardWareEntity.setHardwareType(1);
        communityHardWareEntity.setCommunityId(UserUtils.getAdminCommunityId());
        communityHardWareEntity.setIsConnectData(2);
        communityHardWareEntity.setOnlineStatus(2);
        communityHardWareService.addHardWare(communityHardWareEntity);
        return CommonResult.ok("添加成功!");
    }

    /**
     * @author: Pipi
     * @description: 物业端修改扫描设备(扫脸机)信息
     * @param communityHardWareEntity: 扫描设备(扫脸机)实体
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/19 10:43
     **/
    @Login
    @PutMapping("/v2/updateHardWare")
    public CommonResult updateHardWare(@RequestBody CommunityHardWareEntity communityHardWareEntity) {
        if (communityHardWareEntity.getId() == null) {
            throw new JSYException("需要更新的ID为空");
        }
        ValidatorUtils.validateEntity(communityHardWareEntity, CommunityHardWareEntity.updateHardWareValidate.class);
        communityHardWareEntity.setCommunityId(UserUtils.getAdminCommunityId());
        return communityHardWareService.updateHardWare(communityHardWareEntity) > 0 ? CommonResult.ok("修改成功!") : CommonResult.ok("修改失败!");
    }

    /**
     * @author: Pipi
     * @description: 扫脸一体机人脸同步
     * @param id: 扫脸一体机ID
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/19 14:33
     **/
    @Login
    @PostMapping("/v2/syncFaceUrl")
    public CommonResult syncFaceUrl(@RequestParam("id") Long id) {
        Object syncFaceUrlRecord = redisTemplate.opsForValue().get("syncFaceUrl:" + id);
        if (syncFaceUrlRecord != null) {
            return CommonResult.error("请不要频繁操作!");
        }
        redisTemplate.opsForValue().set("syncFaceUrl:" + id, "", 2, TimeUnit.MINUTES);
        Integer resultNum = communityHardWareService.syncFaceUrl(id, UserUtils.getAdminCommunityId());
        return CommonResult.ok("同步完成;一共同步" + resultNum + "条数据。");
    }

    /**
     * @author: Pipi
     * @description: 分页查询设备列表
     * @param baseQO: 分页查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/3 14:59
     **/
    @Login
    @PostMapping("/v2/hardWarePageList")
    public CommonResult hardWarePageList(@RequestBody BaseQO<CommunityHardWareEntity> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new CommunityHardWareEntity());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(communityHardWareService.hardWarePageList(baseQO));
    }
}
