package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IPatrolService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PatrolEquipEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.TestUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * @author chq459799974
 * @description 巡检接口
 * @since 2021-07-23 09:15
 **/
@ApiJSYController
@RestController
@Login
@RequestMapping("/patrol")
public class PatrolController {
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IPatrolService patrolService;
	
	//======================= 设备start ===========================
	/**
	* @Description: 添加巡检设备
	 * @Param: [PatrolEquipEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@PostMapping("/equip")
	public CommonResult addEquip(@RequestBody PatrolEquipEntity PatrolEquipEntity){
		ValidatorUtils.validateEntity(PatrolEquipEntity);
		PatrolEquipEntity.setCommunityId(UserUtils.getAdminCommunityId());
		boolean b = patrolService.addEquip(PatrolEquipEntity);
		return b ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
	}
	
	/**
	* @Description: 巡检设备 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@PostMapping("/equip/page")
	public CommonResult queryEquipPage(@RequestBody BaseQO<PatrolEquipEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new PatrolEquipEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(patrolService.queryEquipPage(baseQO),"查询成功");
	}
	
	/**
	* @Description: 修改巡检设备
	 * @Param: [patrolEquipEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@PutMapping("/equip")
	public CommonResult updateEquip(@RequestBody PatrolEquipEntity patrolEquipEntity){
		if(patrolEquipEntity.getId() == null){
			return CommonResult.error("缺少id");
		}
		patrolEquipEntity.setCommunityId(UserUtils.getAdminCommunityId());
		boolean b = patrolService.updateEquip(patrolEquipEntity);
		return b ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	/**
	* @Description: 删除巡检设备
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@DeleteMapping("/equip")
	public CommonResult deleteEquip(@RequestParam Long id){
		patrolService.deleteEquip(id,UserUtils.getAdminCommunityId());
		return CommonResult.ok("操作成功");
	}
	//======================= 设备end ===========================
	
	
	
	//==================== 接收硬件数据 =========================
	@Login(allowAnonymous = true)
	@RequestMapping("/record/add")
	public String test123(HttpServletRequest req) throws IOException {
		System.out.println("进来了");
		InputStream in = req.getInputStream();
		
		ByteArrayOutputStream out=new ByteArrayOutputStream(1024);
		
		byte[] temp=new byte[1024];
		int size=0;
		while((size=in.read(temp))!=-1)
		{
			out.write(temp,0,size);
		}
		in.close();
		byte[] bytes=out.toByteArray();
		System.out.println("bytes size got is:"+bytes.length);
		System.out.println(TestUtil.bytes2HexString(bytes));
		
		return "OK";
	}
}
