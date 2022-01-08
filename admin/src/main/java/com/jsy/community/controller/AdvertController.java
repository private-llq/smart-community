package com.jsy.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jsy.community.dto.advert.FileUrlDto;
import com.jsy.community.entity.admin.AdvertEntity;
import com.jsy.community.qo.admin.AddAdvertQO;
import com.jsy.community.qo.admin.AdvertIdList;
import com.jsy.community.qo.admin.AdvertQO;
import com.jsy.community.qo.admin.Id;
import com.jsy.community.service.AdvertService;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.FileVo;
import com.zhsj.baseweb.annotation.Permit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告控制层
 * @date 2021/12/25 11:25
 */
@RestController
@RequestMapping("/advert")
public class AdvertController {

    @Autowired
    private AdvertService advertService;

    /**
     * 分页条件查询广告列表
     * @param qo 查询条件
     * @return 广告列表
     */
    @PostMapping("/page")
    @Permit("community:admin:advert:page")
    public CommonResult page(@RequestBody AdvertQO qo) {
        return CommonResult.ok(advertService.toPage(qo));
    }

    /**
     * 根据id查询一条广告信息
     * @param qo id
     * @return 广告信息
     */
    @PostMapping("/getOneById")
    @Permit("community:admin:advert:getOneById")
    public CommonResult getOne (@RequestBody AdvertQO qo) {
        return CommonResult.ok(advertService.getOne(new LambdaQueryWrapper<AdvertEntity>().eq(AdvertEntity::getAdvertId, qo.getAdvertId())));
    }

    /**
     * 新增一条广告
     * @param qo 新增信息
     * @return boolean
     */
    @PostMapping("/insert")
    @Permit("community:admin:advert:insert")
    public CommonResult insert(@RequestBody AddAdvertQO qo) {
        return CommonResult.ok(advertService.insertAdvert(qo) ? "添加成功" : "添加失败");
    }

    /**
     * 修改广告信息
     * @param entity 广告对象
     * @return boolean
     */
    @PostMapping("/update")
    @Permit("community:admin:advert:update")
    public CommonResult update(@RequestBody AdvertEntity entity) {
        return CommonResult.ok(advertService.updateAdvert(entity) ? "修改成功" : "修改失败");
    }

    /**
     * 查询所有广告列表
     * @return 广告列表
     */
    @PostMapping("/getList")
    @Permit("community:admin:advert:getList")
    public CommonResult getAll() {
        return CommonResult.ok(advertService.list());
    }

    /**
     * 根据ID删除广告
     * @param id 广告id
     * @return
     */
    @PostMapping("/deleteById")
    @Permit("community:admin:advert:deleteById")
    public CommonResult delete(@RequestBody Id id) {
        return CommonResult.ok(advertService.removeById(id.getId()) ? "删除成功" : "删除失败");
    }

    /**
     * 根据id删除多个广告
     * @param ids
     * @return
     */
    @PostMapping("/deleteList")
    @Permit("community:admin:advert:deleteList")
    public CommonResult deleteList(@RequestBody AdvertIdList ids) {
        return CommonResult.ok(advertService.removeByIds(ids.getIds()) ? "删除成功" : "删除失败");
    }

    @PostMapping("/fileUpload")
    public CommonResult fileUpload (@NotNull MultipartFile file) {
        return CommonResult.ok(advertService.fileUpload(file), "上传成功");
    }
}
