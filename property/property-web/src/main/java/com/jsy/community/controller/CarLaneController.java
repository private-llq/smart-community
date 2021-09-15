package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarLaneService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarLaneEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@Api(tags = "车道管理")
@RestController
@RequestMapping("carLane")
@ApiJSYController
public class CarLaneController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    public ICarLaneService carLaneService;


    /**
     * 新增
     * @param CarLaneEntity
     * @return
     */
    @Login
    @PostMapping("SaveCarLane")
    @businessLog(operation = "新增",content = "新增了【车道管理】")
    public CommonResult SaveCarLane(@RequestBody CarLaneEntity CarLaneEntity) {
        carLaneService.SaveCarLane(CarLaneEntity, UserUtils.getAdminCommunityId());
        return CommonResult.ok();
    }

    /**
     * 修改
     * @param carLaneEntity
     * @return
     */
    @PutMapping("UpdateCarLane")
    @businessLog(operation = "编辑",content = "更新了【车道管理】")
    public CommonResult UpdateCarLane(@RequestBody CarLaneEntity carLaneEntity) {
        carLaneService.UpdateCarLane(carLaneEntity);
        return CommonResult.ok();
    }


    /**
     * 删除
     * @param uid
     * @return
     */
    @DeleteMapping("DelCarLane")
    @businessLog(operation = "删除",content = "删除了【车道管理】")
    public CommonResult DelCarLane(@RequestParam("uid") String uid) {
        carLaneService.DelCarLane(uid);
        return CommonResult.ok();
    }


    /**
     *分页查询
     * @param baseQO query:"车道名称"
     * @return
     */
    @Login
    @PostMapping("FindByLaneNamePage")
    public CommonResult<PageInfo> FindByLaneNamePage(@RequestBody BaseQO baseQO) {
        PageInfo pageInfo = carLaneService.FindByLaneNamePage(baseQO,UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }



}
