package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarEquipmentManageService;
import com.jsy.community.api.ICarLocationService;
import com.jsy.community.api.ICarPatternService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarEquipmentManageEntity;
import com.jsy.community.entity.property.CarLocationEntity;
import com.jsy.community.entity.property.CarPatternEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.CarEquipMentQO;
import com.jsy.community.qo.property.CarEquipmentManageQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Api("车禁模块-设备管理")
@RestController
@RequestMapping("/carEquipmentManage")
@ApiJSYController
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
    @Login
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
    @Login
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
    @Login
    public CommonResult addEquipment(@RequestBody CarEquipMentQO carEquipMentQO){
     boolean b =  equipmentManageService.addEquipment(carEquipMentQO,UserUtils.getAdminCommunityId(),UserUtils.getUserId());
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
    @Login
    public CommonResult updateEquipment(@RequestBody CarEquipMentQO carEquipMentQO){
        boolean b =  equipmentManageService.updateEquipment(carEquipMentQO,UserUtils.getAdminCommunityId(),UserUtils.getUserId());
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 删除设备位置
     * @Param: [patternId]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/9-11:24
     **/
    @Login
    @DeleteMapping("/deleteEquipment")
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

    @Login
    @GetMapping("/listPattern")
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
    @Login
    @PostMapping("/addPattern")
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
    @Login
    @PostMapping("/updatePattern")
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
    @Login
    @DeleteMapping("/deletePattern")
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
    @Login
    @PostMapping("/listLocation")
    public  CommonResult listLocation(@RequestBody  BaseQO<CarLocationEntity> baseQO){
        Page<CarLocationEntity> locationEntityList = locationService.listLocation(baseQO,UserUtils.getAdminCommunityId());
        System.out.println(UserUtils.getAdminCommunityId());
        return CommonResult.ok(locationEntityList,"查询成功");
    }

    @Login
    @GetMapping("/selectList")
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
    @Login
    @PostMapping("/addLocation")
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
    @Login
    @PostMapping("/updateLocation")
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
    @Login
    @DeleteMapping("/deleteLocation")
    public  CommonResult deleteLocation(@RequestParam("location_id")String locationId){
        boolean b = locationService.deleteLocation(locationId,UserUtils.getAdminCommunityId());
        return CommonResult.ok("修改成功");
    }
}
