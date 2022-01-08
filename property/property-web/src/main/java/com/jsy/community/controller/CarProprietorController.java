package com.jsy.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.ICarProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarProprietorEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("业主车辆")
@RestController
@RequestMapping("/carProprietor")
// @ApiJSYController
public class CarProprietorController {

    @DubboReference(version = Const.version,group = Const.group_property,check = false)
    private ICarProprietorService carProprietorService;

    @PostMapping("/listAll")
    @Permit("community:property:carProprietor:listAll")
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
    @PostMapping("/listPage")
    @Permit("community:property:carProprietor:listPage")
    public CommonResult listPage(@RequestBody BaseQO<CarProprietorEntity> baseQO){
        if (baseQO.getQuery()==null){
            baseQO.setQuery(new CarProprietorEntity());
        }
        Long communityId = UserUtils.getAdminCommunityId();
        Page<CarProprietorEntity> listPage = carProprietorService.listPage(baseQO,communityId);
        return CommonResult.ok(listPage,"查询成功");
    }

    /**添加
     * @Description:
     * @Param: [carProprietorEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/11-15:09
     **/
    @PostMapping("/addProprietor")
    @businessLog(operation = "新增",content = "新增了【业主车辆】")
    @CarOperation(operation = "新增【业主车辆】")
    @Permit("community:property:carProprietor:addProprietor")
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
    @PostMapping("/updateProprietor")
    @businessLog(operation = "编辑",content = "更新了【业主车辆】")
    @CarOperation(operation = "编辑【业主车辆】")
    @Permit("community:property:carProprietor:updateProprietor")
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
    @DeleteMapping("/deleteProprietor")
    @CarOperation(operation = "删除【业主车辆】")
    @businessLog(operation = "删除",content = "删除了【业主车辆】")
    @Permit("community:property:carProprietor:deleteProprietor")
    public CommonResult deleteProprietor(@RequestParam Long id){
        boolean b = carProprietorService.deleteProprietor(id);
        return CommonResult.ok("删除成功");
    }
}
