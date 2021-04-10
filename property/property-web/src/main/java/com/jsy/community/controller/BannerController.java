package com.jsy.community.controller;


import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IBannerService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.BannerEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.BannerQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.BannerVO;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.admin.AdminInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @author chq459799974
 * @since 2020-11-18 16:19
 **/
@Api(tags = "轮播图控制器")
@RestController
@RequestMapping("banner")
@ApiJSYController
@Login
public class BannerController {

	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IBannerService bannerService;
	
	private static final String BUCKETNAME = "bannner-img"; //暂时写死  后面改到配置文件中  BUCKETNAME命名规范：只能小写，数字，-
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	
	/**
	 * @Description: 轮播图 分页查询
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
	@ApiOperation("【轮播图】分页查询")
	@PostMapping("page")
	public CommonResult<PageInfo<BannerEntity>> list(@RequestBody BaseQO<BannerEntity> baseQO){
		BannerEntity query = baseQO.getQuery();
		if(query == null || (query.getId() == null && query.getPublishType() == null)){
			throw new JSYException(JSYError.REQUEST_PARAM.getCode(),"请确定操作 查询草稿/已发布/详情");
		}
		query.setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(bannerService.queryBannerPage(baseQO),"查询成功");
	}

	/**
	* @Description: 轮播图 新增
	 * @Param: [file, bannerEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@ApiOperation("【轮播图基本信息】新增")
	@PostMapping("")
	public CommonResult upload(@RequestBody BannerEntity bannerEntity){
		ValidatorUtils.validateEntity(bannerEntity, BannerEntity.addBannerValidatedGroup.class);
		AdminInfoVo adminUserInfo = UserUtils.getAdminUserInfo();
		bannerEntity.setCommunityId(adminUserInfo.getCommunityId());
		bannerEntity.setCreateBy(adminUserInfo.getUid());
		//写库
		boolean b = bannerService.addBanner(bannerEntity);
		String filePath = bannerEntity.getUrl();
		if (!StringUtils.isEmpty(filePath)) {
			redisTemplate.opsForSet().add("banner_img_all",filePath);
		}
		if(b){
			return CommonResult.ok("操作成功");
		}
		return CommonResult.error(JSYError.INTERNAL.getCode(),"操作失败");
	}
	
	/**
	 * @return com.jsy.community.vo.CommonResult
	 * @Author lihao
	 * @Description 轮播图图片上传
	 * @Date 2020/12/4 18:04
	 * @Param [file]
	 **/
	@ApiOperation("【轮播图图片】上传")
	@PostMapping("uploadImg")
	public CommonResult uploadImg(@RequestParam("file") MultipartFile file){
		String filePath = MinioUtils.upload(file, BUCKETNAME);
		redisTemplate.opsForSet().add("banner_img_part",filePath);
		return CommonResult.ok(filePath);
	}

	/**
	* @Description: 轮播图 批量删除
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	**/
	@ApiOperation("【轮播图】批量删除")
	@DeleteMapping("")
	public CommonResult deleteBanner(@RequestBody Long[] ids){
		if(ids.length == 0){
			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(),"缺少ID");
		}
		boolean result = bannerService.deleteBannerBatch(ids);
		return result ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"轮播图删除失败");
	}
	
	/**
	* @Description: 修改跳转路径和描述
	 * @Param: [bannerQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/2/2
	**/
	@ApiOperation("【轮播图】修改跳转路径和描述")
	@PutMapping("")
	public CommonResult updateBanner(@RequestBody BannerQO bannerQO){
		if(bannerQO.getId() == null){
			return CommonResult.error(JSYError.REQUEST_PARAM.getCode(),"缺少ID");
		}
		boolean b = bannerService.updateBanner(bannerQO);
		return b ? CommonResult.ok() : CommonResult.error(JSYError.INTERNAL.getCode(),"修改轮播图信息失败");
	}
	
}

