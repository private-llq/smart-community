package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.PeopleHistoryEntity;
import com.jsy.community.entity.VisitorStrangerEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PicUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @author chq459799974
 * @description 物业端访客Controller
 * @since 2021-04-12 13:45
 **/
@RequestMapping("visitor")
@Api(tags = "访客控制器")
@RestController
@Login
@ApiJSYController
public class VisitorController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IVisitorService visitorService;
	
	/**
	* @Description: 访客记录 分页查询(现在主表数据是t_visitor,以后会改为t_people_history)
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/12
	**/
	@PostMapping("page")
	public CommonResult queryVisitorPage(@RequestBody BaseQO<PeopleHistoryEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new PeopleHistoryEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(visitorService.queryVisitorPage(baseQO),"查询成功");
	}

	/**
	 * @author: Pipi
	 * @description: 访客管理分页查询
	 * @param baseQO: 分页参数
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/13 17:25
	 **/
	@PostMapping("/visitorPage")
	public CommonResult visitorPage(@RequestBody BaseQO<VisitorEntity> baseQO) {
		return CommonResult.ok(visitorService.visitorPage(baseQO));
	}
	
	/**
	* @Description: 查询单次访客邀请的随行人员列表
	 * @Param: [visitorId]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021/4/15
	**/
	@GetMapping("follow")
	public CommonResult queryFollowPersonListByVisitorId(@RequestParam Long visitorId){
		return CommonResult.ok(visitorService.queryFollowPersonListByVisitorId(visitorId),"查询成功");
	}
	
	/**
	* @Description: 陌生人记录 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-08-02
	**/
	@PostMapping("stranger/page")
	public CommonResult queryStrangerPage(@RequestBody BaseQO<VisitorStrangerEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new VisitorStrangerEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(visitorService.queryStrangerPage(baseQO));
	}

	/**
	 * @author: Pipi
	 * @description: 物业端添加访客邀请
	 * @param visitorEntity: 访客表实体
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/13 10:47
	 **/
	@PostMapping("/addPropertyVisitor")
	@businessLog(operation = "新增",content = "新增了【访客邀请】")
	public CommonResult addVisitor(@RequestBody VisitorEntity visitorEntity) {
		ValidatorUtils.validateEntity(visitorEntity, VisitorEntity.addVisitorValidate.class);
		visitorEntity.setCommunityId(UserUtils.getAdminCommunityId());
		// 如果上传了人脸,则是人脸识别通行
		if (!StringUtils.isEmpty(visitorEntity.getFaceUrl())) {
			// 人脸识别通行
			visitorEntity.setIsCommunityAccess(2);
		} else {
			// 否则,二维码通行
			visitorEntity.setIsCommunityAccess(1);
		}
		// 默认楼栋门禁为无
		visitorEntity.setIsBuildingAccess(0);
		// 审核方式默认为物业审核
		visitorEntity.setCheckType(2);
		visitorEntity.setCheckTime(LocalDateTime.now());
		// 默认入园状态为待入园
		visitorEntity.setStatus(1);
		return visitorService.addVisitor(visitorEntity) > 0 ? CommonResult.ok("邀请成功!") : CommonResult.error("邀请失败!");
	}

	/**
	 * @author: Pipi
	 * @description: 访客人脸上传
	 * @param file:
	 * @return: com.jsy.community.vo.CommonResult
	 * @date: 2021/8/17 14:25
	 **/
	@Login
	@PostMapping("/v2/uploadVisitorFace")
	public CommonResult uploadVisitorFace(MultipartFile file) {
		PicUtil.imageQualified(file);
		String url = MinioUtils.upload(file, "visitor-face");
		if(!StringUtils.isEmpty(url)){
			return CommonResult.ok(url, "上传成功!");
		}
		return CommonResult.error("上传失败!");
	}
	
}
