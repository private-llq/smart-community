package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IPatrolService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PatrolEquipEntity;
import com.jsy.community.entity.property.PatrolLineEntity;
import com.jsy.community.entity.property.PatrolPointEntity;
import com.jsy.community.entity.property.PatrolRecordEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.NumberFormatUtil;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
	
	@DubboReference(version = Const.version, group = Const.group_property, check = false)
	private IHouseService houseService;
	
	//======================= 设备start ===========================
	/**
	* @Description: 添加巡检设备
	 * @Param: [PatrolEquipEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-23
	**/
	@PostMapping("/equip")
	public CommonResult addEquip(@RequestBody PatrolEquipEntity patrolEquipEntity){
		ValidatorUtils.validateEntity(patrolEquipEntity);
		patrolEquipEntity.setCommunityId(UserUtils.getAdminCommunityId());
		return patrolService.addEquip(patrolEquipEntity) ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
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
		return patrolService.updateEquip(patrolEquipEntity) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
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
		return patrolService.deleteEquip(id,UserUtils.getAdminCommunityId()) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	//======================= 设备end ===========================
	
	//======================= 点位start ===========================
	/**
	* @Description: 添加巡检点位
	 * @Param: [patrolPointEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@PostMapping("/point")
	public CommonResult addEquip(@RequestBody PatrolPointEntity patrolPointEntity){
		ValidatorUtils.validateEntity(patrolPointEntity);
		if(patrolPointEntity.getBuildingId() == null && patrolPointEntity.getUnitId() == null){
			return CommonResult.error("请至少选择楼栋或单元");
		}
		Long communityId = UserUtils.getAdminCommunityId();
		//检查楼栋单元
		houseService.checkBuildingAndUnit(patrolPointEntity.getBuildingId(),patrolPointEntity.getUnitId(),communityId);
		patrolPointEntity.setCommunityId(communityId);
		return patrolService.addPoint(patrolPointEntity) ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
	}
	
	/**
	* @Description: 巡检点位 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@PostMapping("/point/page")
	public CommonResult queryPointPage(@RequestBody BaseQO<PatrolPointEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new PatrolPointEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(patrolService.queryPointPage(baseQO),"查询成功");
	}
	
	/**
	* @Description: 修改巡检点位
	 * @Param: [patrolPointEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@PutMapping("/point")
	public CommonResult updateEquip(@RequestBody PatrolPointEntity patrolPointEntity){
		if(patrolPointEntity.getId() == null){
			return CommonResult.error("缺少id");
		}
		Long communityId = UserUtils.getAdminCommunityId();
		//检查楼栋单元
		houseService.checkBuildingAndUnit(patrolPointEntity.getBuildingId(),patrolPointEntity.getUnitId(),communityId);
		patrolPointEntity.setCommunityId(communityId);
		return patrolService.updatePoint(patrolPointEntity) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	/**
	* @Description: 删除巡检点位
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-24
	**/
	@DeleteMapping("/point")
	public CommonResult deletePoint(@RequestParam Long id){
		return patrolService.deletePoint(id,UserUtils.getAdminCommunityId()) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	//======================= 点位end ===========================
	
	//======================= 线路start ===========================
	/**
	* @Description: 新增巡检线路
	 * @Param: [patrolLineEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@PostMapping("/line")
	public CommonResult addLine(@RequestBody PatrolLineEntity patrolLineEntity){
		ValidatorUtils.validateEntity(patrolLineEntity);
		patrolLineEntity.setCommunityId(UserUtils.getAdminCommunityId());
		return patrolService.addLine(patrolLineEntity) ? CommonResult.ok("添加成功") : CommonResult.error("添加失败");
	}
	
	/**
	* @Description: 巡检线路 分页查询
	 * @Param: [baseQO]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@PostMapping("/line/page")
	public CommonResult queryLinePage(@RequestBody BaseQO<PatrolLineEntity> baseQO){
		if(baseQO.getQuery() == null){
			baseQO.setQuery(new PatrolLineEntity());
		}
		baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
		return CommonResult.ok(patrolService.queryLinePage(baseQO),"查询成功");
	}
	
	/**
	* @Description: 修改巡检线路
	 * @Param: [patrolLineEntity]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@PutMapping("/line")
	public CommonResult updateLine(@RequestBody PatrolLineEntity patrolLineEntity){
		if(patrolLineEntity.getId() == null){
			return CommonResult.error("缺少id");
		}
		patrolLineEntity.setCommunityId(UserUtils.getAdminCommunityId());
		return patrolService.updateLine(patrolLineEntity) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	
	/**
	* @Description: 删除巡检线路
	 * @Param: [id]
	 * @Return: com.jsy.community.vo.CommonResult
	 * @Author: chq459799974
	 * @Date: 2021-07-26
	**/
	@DeleteMapping("/line")
	public CommonResult deleteLine(@RequestParam Long id){
		return patrolService.deleteLine(id,UserUtils.getAdminCommunityId()) ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
	}
	//======================= 线路end ===========================
	
	
	//==================== 接收硬件数据 =========================
	/**
	* @Description: 解析硬件上传的数据并添加记录
	 * @Param: [req]
	 * @Return: java.lang.String
	 * @Author: chq459799974
	 * @Date: 2021-07-27
	**/
	@Login(allowAnonymous = true)
	@RequestMapping("/record")
	public String addRecord(HttpServletRequest req) throws IOException {
		//读参数
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
		String str = NumberFormatUtil.bytes2HexString(bytes);
		System.out.println(str);
		//处理数据
		System.out.println("数据总长" + str.length());
		String data = str.substring(4, str.length() - 12);
		System.out.println("截取有用数据总长" + data.length());
		System.out.println(data);
		String equipNumber = data.substring(data.length() - 8);
		System.out.println("设备编号：" + equipNumber);
		String patrolRecord = data.substring(0,data.length() - 8);
		System.out.println("打卡记录数量：" + patrolRecord.length()/16);
		System.out.println("打卡记录：" + patrolRecord);
		String equipNumberStr = NumberFormatUtil.hexStrToStr(equipNumber);
		System.out.println("10进制设备编号：" + equipNumberStr);
		ArrayList<PatrolRecordEntity> recordList = new ArrayList<>();
		for(int i=0;i<patrolRecord.length()/16;i++){
			String r = patrolRecord.substring(i*16,(i+1)*16); //单条记录
			System.out.println("单条记录：" + r);
			String num = r.substring(0,8);
			String time = r.substring(8,16);
			System.out.println("钮号：" + num);
			System.out.println("time：" + time);
			LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(NumberFormatUtil.hexStrToLong(time) * 1000), ZoneId.of("GMT+8"));
			System.out.println("时间：" + localDateTime);
			PatrolRecordEntity record = new PatrolRecordEntity();
			record.setEquipNumber(equipNumberStr);
			record.setPointNumber(num);
			record.setPatrolTime(localDateTime);
			recordList.add(record);
		}
		
		//调用接口
		//TODO 硬件品牌暂时固定为1
		return patrolService.addRecord(recordList,1L,equipNumberStr);
	}
	
}
