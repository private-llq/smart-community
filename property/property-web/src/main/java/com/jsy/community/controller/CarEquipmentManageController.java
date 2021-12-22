package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarEquipmentManageService;
import com.jsy.community.api.ICarLocationService;
import com.jsy.community.api.ICarPatternService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarEquipmentManageEntity;
import com.jsy.community.entity.property.CarLocationEntity;
import com.jsy.community.entity.property.CarPatternEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEquipMentQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Api("车禁模块-设备管理")
@RestController
@RequestMapping("/carEquipmentManage")
// @ApiJSYController
public class CarEquipmentManageController {

    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarEquipmentManageService equipmentManageService;

    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarPatternService patternService;

    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarLocationService locationService;

    /**
     * @Description: 分页查询设备管理
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:22
     **/
    @PostMapping("/equipmentPage")
    @Permit("community:property:carEquipmentManage:equipmentPage")
    public CommonResult equipmentPage(@RequestBody BaseQO<CarEquipmentManageEntity> baseQO){
        Long communityId = UserUtils.getAdminCommunityId();
        if (baseQO.getQuery() == null){
            baseQO.setQuery(new CarEquipmentManageEntity());
        }

        Map<String, Object> map =  equipmentManageService.equipmentPage(baseQO, communityId);
        return CommonResult.ok(map,"查询成功");
    }
    /**
     * @Description: 查询所有设备管理
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:22
     **/
    @GetMapping("/equipmentList")
    @Permit("community:property:carEquipmentManage:equipmentList")
    public CommonResult equipmentList(){
        Long communityId = UserUtils.getAdminCommunityId();
        List<CarEquipmentManageEntity> list = equipmentManageService.equipmentList(communityId);
        return CommonResult.ok(list,"查询成功");
    }


    /**
     * @Description: 添加设备管理
     * @Param: [carEquipMentQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:23
     **/
    @PostMapping("/addEquipment")
    @businessLog(operation = "新增",content = "新增了【设备管理】")
    @CarOperation(operation = "新增了【设备管理】")
    @Permit("community:property:carEquipmentManage:addEquipment")
    public CommonResult addEquipment(@RequestBody CarEquipMentQO carEquipMentQO){
     boolean b =  equipmentManageService.addEquipment(carEquipMentQO,UserUtils.getAdminCommunityId(),UserUtils.getId());
     return CommonResult.ok("添加成功");
    }
    /**
     * @Description: 修改设备管理
     * @Param: [carEquipMentQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/19-9:31
     **/
    @PostMapping("/updateEquipment")
    @businessLog(operation = "编辑",content = "更新了【设备管理】")
    @CarOperation(operation = "编辑了【设备管理】")
    @Permit("community:property:carEquipmentManage:updateEquipment")
    public CommonResult updateEquipment(@RequestBody CarEquipMentQO carEquipMentQO){
        boolean b =  equipmentManageService.updateEquipment(carEquipMentQO,UserUtils.getAdminCommunityId(),UserUtils.getId());
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 删除设备位置
     * @Param: [patternId]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @DeleteMapping("/deleteEquipment")
    @businessLog(operation = "删除",content = "删除了【设备管理】")
    @CarOperation(operation = "删除了【设备管理】")
    @Permit("community:property:carEquipmentManage:deleteEquipment")
    public  CommonResult deleteEquipment(@RequestParam("id")Long id){
        boolean b = equipmentManageService.deleteEquipment(id,UserUtils.getAdminCommunityId());
        return CommonResult.ok("删除成功");
    }



    /**
     * @Description: 查询临时车模式
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:23
     **/
    @GetMapping("/listPattern")
    @Permit("community:property:carEquipmentManage:listPattern")
    public  CommonResult listPattern(){
        List<CarPatternEntity> patternEntityList = patternService.listPattern(UserUtils.getAdminCommunityId());
        return CommonResult.ok(patternEntityList,"查询成功");
    }


    /**
     * @Description: 添加临时车模式
     * @Param: [locationPattern]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @PostMapping("/addPattern")
    @businessLog(operation = "新增",content = "新增了【临时车模式】")
    @CarOperation(operation = "删除了【设备管理】")
    @Permit("community:property:carEquipmentManage:addPattern")
    public  CommonResult addPattern(@RequestParam("location_pattern")String locationPattern){

        boolean b = patternService.addPattern(locationPattern,UserUtils.getAdminCommunityId());
        return CommonResult.ok("添加成功");
    }

    /**
     * @Description: 修改临时车模式
     * @Param: [locationPattern, patternId]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @PostMapping("/updatePattern")
    @businessLog(operation = "编辑",content = "更新了【临时车模式】")
    @CarOperation(operation = "删除了【设备管理】")
    @Permit("community:property:carEquipmentManage:updatePattern")
    public  CommonResult updatePattern(@RequestParam("location_pattern")String locationPattern,@RequestParam("pattern_id")String patternId){

        boolean b = patternService.updatePattern(locationPattern,patternId,UserUtils.getAdminCommunityId());
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 删除临时车模式
     * @Param: [patternId]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @DeleteMapping("/deletePattern")
    @CarOperation(operation = "删除了【设备管理】")
    @businessLog(operation = "删除",content = "删除了【临时车模式】")
    @Permit("community:property:carEquipmentManage:deletePattern")
    public  CommonResult deletePattern(@RequestParam("pattern_id")String patternId){
        boolean b = patternService.deletePattern(patternId,UserUtils.getAdminCommunityId());
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 分页查询设备位置
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:23
     **/
    @PostMapping("/listLocation")
    @Permit("community:property:carEquipmentManage:listLocation")
    public  CommonResult listLocation(@RequestBody BaseQO<CarLocationEntity> baseQO){

        Page<CarLocationEntity> locationEntityList = locationService.listLocation(baseQO,UserUtils.getAdminCommunityId());
        System.out.println(UserUtils.getAdminCommunityId());
        return CommonResult.ok(locationEntityList,"查询成功");
    }

    @GetMapping("/selectList")
    @Permit("community:property:carEquipmentManage:selectList")
    public  CommonResult selectList(){
        List<CarLocationEntity> locationEntityList = locationService.selectList(UserUtils.getAdminCommunityId());

        return CommonResult.ok(locationEntityList,"查询成功");
    }


    /**
     * @Description: 添加设备位置
     * @Param: [locationPattern]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @PostMapping("/addLocation")
    @businessLog(operation = "新增",content = "新增了【设备位置】")
    @CarOperation(operation = "新增【设备管理】")
    @Permit("community:property:carEquipmentManage:addLocation")
    public  CommonResult addLocation(@RequestParam("equipment_location")String equipmentLocation){

        boolean b = locationService.addLocation(equipmentLocation,UserUtils.getAdminCommunityId());
        return CommonResult.ok("添加成功");
    }

    /**
     * @Description: 修改设备位置
     * @Param: [locationPattern, patternId]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @PostMapping("/updateLocation")
    @businessLog(operation = "编辑",content = "更新了【设备位置】")
    @CarOperation(operation = "编辑【设备管理】")
    @Permit("community:property:carEquipmentManage:updateLocation")
    public  CommonResult updateLocation(@RequestParam("equipment_location")String equipmentLocation,@RequestParam("location_id")String locationId){

        boolean b = locationService.updateLocation(equipmentLocation,locationId,UserUtils.getAdminCommunityId());
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 删除设备位置
     * @Param: [patternId]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @DeleteMapping("/deleteLocation")
    @CarOperation(operation = "删除【设备管理】")
    @businessLog(operation = "删除",content = "删除了【设备位置】")
    @Permit("community:property:carEquipmentManage:deleteLocation")
    public  CommonResult deleteLocation(@RequestParam("location_id")String locationId){
        boolean b = locationService.deleteLocation(locationId,UserUtils.getAdminCommunityId());
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 通过序列号查询设备管理
     * @Param: [baseQO]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:22
     **/
    @GetMapping("/equipmentOne")
    @Permit("community:property:carEquipmentManage:equipmentOne")
    public CommonResult equipmentOne(@RequestParam("camId")String camId){
        Long communityId = UserUtils.getAdminCommunityId();
        CarEquipmentManageEntity carEquipmentManageEntity = equipmentManageService.equipmentOne(camId);


        return CommonResult.ok(carEquipmentManageEntity,"查询成功");
    }


}
