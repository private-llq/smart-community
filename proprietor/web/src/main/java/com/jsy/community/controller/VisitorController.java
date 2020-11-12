package com.jsy.community.controller;

import com.jsy.community.api.visitor.ITVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.visitor.VisitorEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.visitor.VisitorQO;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
public class VisitorController {

    @DubboReference(version = Const.version, group = Const.group)
    private ITVisitorService iTVisitorService;

    /**
    * @Description: 访客登记 新增
     * @Param: [tVisitor]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2020/11/11
    **/
    @PostMapping("")
    public CommonResult save(@RequestBody VisitorEntity visitorEntity){
        boolean saveResult = iTVisitorService.save(visitorEntity);
        return saveResult ? CommonResult.ok("") : CommonResult.error(JSYError.INTERNAL.getCode(),"新增访客登记失败");
    }
    
    /**
    * @Description: 访客登记 逻辑删除
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @DeleteMapping("/{id}")
    public CommonResult delete(@PathVariable("id")Long id){
        boolean delResult = iTVisitorService.removeById(id);
        return delResult ? CommonResult.ok("") : CommonResult.error(JSYError.INTERNAL.getCode(),"删除失败");
    }
    
    /**
     * @Description: 访客登记 修改/审核
     * @Param: [visitorEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @PutMapping("")
    public CommonResult update(@RequestBody VisitorEntity visitorEntity){
        boolean updateResult = iTVisitorService.updateById(visitorEntity);
        return updateResult ? CommonResult.ok("") : CommonResult.error(-1,"访客登记申请修改失败");
    }
    
    /**
    * @Description: 分页查询
     * @Param: [baseQO<VisitorQO>]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @GetMapping("")
    public CommonResult query(@RequestBody BaseQO<VisitorQO> baseQO){
        return CommonResult.ok(iTVisitorService.queryByPage(baseQO).getRecords());
    }
    
}
