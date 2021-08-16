package com.jsy.community.controller;



import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICarPositionService;
import com.jsy.community.config.ExcelListener;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarPositionEntity;
import com.jsy.community.qo.property.SelectCarPositionPagingQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 车位 前端控制器
 * </p>
 *
 * @author Arli
 * @since 2021-08-03
 */

@ApiJSYController
@RequestMapping("/car-position")
@RestController
@Api(tags = "车位模块")
public class CarPositionController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarPositionService iCarPositionService;


    @ApiOperation("分页查询车位信息")
    @Login
    @RequestMapping(value = "/selectCarPositionPaging", method = RequestMethod.POST)
    public CommonResult<PageVO> selectCarPositionPaging(@RequestBody SelectCarPositionPagingQO qo) {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id

            Page<CarPositionEntity> ss= iCarPositionService.selectCarPositionPaging(qo,adminCommunityId);
            PageVO<CarPositionEntity> pageVO=new PageVO();
            pageVO.setPages(ss.getPages());
            pageVO.setRecords(ss.getRecords());
            pageVO.setCurrent(ss.getCurrent());
            pageVO.setPages(ss.getPages());
            pageVO.setTotal(ss.getTotal());
            return CommonResult.ok(pageVO,"查询成功") ;
    }
   /* @ApiOperation("条件查询车位信息")
    @Login
    @RequestMapping(value = "/selectCarPosition", method = RequestMethod.POST)
    public CommonResult<List<CarPositionEntity>> selectCarPositionPaging(@RequestBody CarPositionEntity qo) {
        List<CarPositionEntity> carPositionEntity= iCarPositionService.selectCarPosition(qo);



        return CommonResult.ok(carPositionEntity,"查询成功") ;
    }*/



    @ApiOperation("导入车位信息")
    @PostMapping("/carPositionUpload")
    @ResponseBody
    @Login
    public String upload(MultipartFile file) throws IOException {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
        EasyExcel.read(file.getInputStream(), //文件流
                CarPositionEntity.class, //实体类class
                new ExcelListener(iCarPositionService,adminCommunityId))//将调用的server用构造方法传进来
                .sheet(0)//工作表下标从0开始
                .doRead();
        return "success";

    }

    @ApiOperation("查询所有车位信息")
    @Login
    @RequestMapping(value = "/selectAllCarPosition", method = RequestMethod.POST)
    public CommonResult<List<CarPositionEntity>> selectCarPositionPaging() {
        Long adminCommunityId = UserUtils.getAdminCommunityId();//小区id
     List<CarPositionEntity> list=  iCarPositionService.getAll(adminCommunityId);

        return CommonResult.ok(list,"查询成功") ;
    }

}

