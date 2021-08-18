package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPatternEntity;
import com.jsy.community.entity.property.CarProprietorEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api("业主车辆")
@RestController
@RequestMapping("/carProprietor")
@ApiJSYController
public class CarProprietorController {

    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarProprietorService carProprietorService;

    @Login
    @PostMapping("/listAll")
    public CommonResult listAll(){
       List<CarProprietorEntity> listAll = carProprietorService.listAll(UserUtils.getAdminCommunityId());
        return CommonResult.ok(listAll,"查询成功");
    }

    /**
     * @Description: 分页查询
     * @Param: [baseQO, phone]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/11-15:08
     **/
    @Login
    @PostMapping("/listPage")
    public CommonResult listPage(@RequestBody BaseQO<CarProprietorEntity> baseQO,Long phone){
        Page<CarProprietorEntity> listPage = carProprietorService.listPage(baseQO,UserUtils.getAdminCommunityId(),phone);
        return CommonResult.ok(listPage,"查询成功");
    }

    /**添加
     * @Description:
     * @Param: [carProprietorEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/11-15:09
     **/
    @Login
    @PostMapping("/addProprietor")
    public CommonResult addProprietor(@RequestBody CarProprietorEntity carProprietorEntity){
        boolean b = carProprietorService.addProprietor(carProprietorEntity,UserUtils.getAdminCommunityId());
        return CommonResult.ok("添加成功");
    }

    /**
     * @Description: 修改
     * @Param: [carProprietorEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/11-15:09
     **/
    @Login
    @PostMapping("/updateProprietor")
    public CommonResult updateProprietor(@RequestBody CarProprietorEntity carProprietorEntity){
        boolean b = carProprietorService.updateProprietor(carProprietorEntity);
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 删除
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/11-15:09
     **/
    @Login
    @DeleteMapping("/deleteProprietor")
    public CommonResult deleteProprietor(@RequestParam Long id){
        boolean b = carProprietorService.deleteProprietor(id);
        return CommonResult.ok("删除成功");
    }
}
