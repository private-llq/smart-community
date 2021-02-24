package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.ISignatureService;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.signature.SignatureUserDTO;
import com.jsy.community.utils.MapBeanUtil;
import com.jsy.community.utils.MyHttpUtils;
import com.jsy.community.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;


/**
 * @author chq459799974
 * @description 签章相关 实现类
 * @since 2021-02-23 17:38
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class SignatureServiceImpl implements ISignatureService {
	
	private static final String protocolType = "http://";
		private static final String host = "192.168.12.37";
//	private static final String host = "222.178.212.29";
	private static final String port = "10005";
	
	//POST 新增用户信息
	@Override
	public boolean insertUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(1, signatureUserDTO);
	}
	
	//POST 批量新增用户信息(物业端批量导入时)
	@Override
	public boolean batchInsertUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(2,signatureUserDTO);
	}
	
	//PUT 实名认证后修改签章用户信息
	@Override
	public boolean realNameUpdateUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(3,signatureUserDTO);
	}
	
	//PUT 修改用户普通信息
	@Override
	public boolean updateUser(SignatureUserDTO signatureUserDTO){
		return sendRedbagByHttp(4,signatureUserDTO);
	}
	
	/**
	 * http调用签章接口
	 */
	private boolean sendRedbagByHttp(int type, SignatureUserDTO signatureUserDTO){
		HttpPost httpPost = null;
		HttpPut httpPut = null;
		String url = "";
		long id = SnowFlake.nextId(); //远程服务调用id
		switch(type){
			case 1:
				url = protocolType + host + ":" + port + "/user/insertUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_INSERT_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
			case 2:
				url = protocolType + host + ":" + port + "/user/batchInsertUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_BATCH_INSERT_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
			case 3:
				url = protocolType + host + ":" + port + "/user/RealNameUpdateUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_REALNAME_UPDATE_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
			case 4:
				url = protocolType + host + ":" + port + "/user/updateUser";
				log.info("ID：" + id + "签章服务 - 准备调用：" + SignatureBehaveEnum.BEHAVE_UPDATE_USER.getName());
				log.info("用户：" + signatureUserDTO.getUuid());
				break;
		}
		//获取加密对象
//		OpenParam openParam = AESUtil.returnOpenParam(redbagQO);
		//组装请求body
//		Map<String, Object> bodyMap = JSONObject.parseObject(JSON.toJSONString(openParam), Map.class);
		if(type == 1 || type == 2){
			httpPost = MyHttpUtils.httpPostWithoutParams(url, MapBeanUtil.object2Map(signatureUserDTO));
		}else if(type == 3 || type == 4){
			httpPut = MyHttpUtils.httpPutWithoutParams(url, MapBeanUtil.object2Map(signatureUserDTO));
		}
		//设置默认header
		MyHttpUtils.setDefaultHeader(httpPost != null ? httpPost : httpPut);
		//设置默认配置
		MyHttpUtils.setRequestConfig(httpPost != null ? httpPost : httpPut);
		JSONObject result = null;
		String httpResult = null;
		try{
			//执行请求，解析结果
			httpResult = (String)MyHttpUtils.exec(httpPost != null ? httpPost : httpPut,MyHttpUtils.ANALYZE_TYPE_STR);
			result = JSONObject.parseObject(httpResult);
			if(result == null || result.getIntValue("code") != 200){
				log.error("ID：" + id + "签章用户远程服务 - 调用返回code非200：\n" + httpResult);
				return false;
			}
		}catch (Exception e) {
			log.error("ID：" + id + "签章用户远程服务 - 调用或解析出错，调用返回：\n" + httpResult);
			return false;
		}
		log.error("ID：" + id + "签章用户远程服务 - 调用成功：\n" + httpResult);
		return true;
	}
	
}

/**
 * @Description: 签章接口枚举
 * @Author: chq459799974
 * @Date: 2020/2/24
 **/
enum SignatureBehaveEnum {
	BEHAVE_INSERT_USER("新增用户", 1),
	BEHAVE_BATCH_INSERT_USER("批量新增用户", 2),
	BEHAVE_REALNAME_UPDATE_USER("更新用户实名认证信息",3),
	BEHAVE_UPDATE_USER("更新用户普通信息",4);
	private String name;
	private Integer code;
	SignatureBehaveEnum(String name, Integer code) {
		this.name = name;
		this.code = code;
	}
	public String getName() {
		return name;
	}
}
