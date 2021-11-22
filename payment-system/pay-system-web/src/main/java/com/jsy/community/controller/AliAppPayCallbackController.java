package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.AliAppPayCallbackService;
import com.jsy.community.constant.Const;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ApiJSYController
@RestController
@RequestMapping("callBack")
public class AliAppPayCallbackController {
	
	@DubboReference(version = Const.version, group = Const.group_lease, check = false)
	private AliAppPayCallbackService aliAppPayCallbackService;
	
	@CrossOrigin
	@RequestMapping("pay")
	@Transactional(rollbackFor=Exception.class,timeout=3)
	@LoginIgnore
	@Permit("community:payment:callBack:pay")
	public String test(HttpServletRequest req){
		Map<String, String> paramsMap = new HashMap<>();
		Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements())
        {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = req.getParameterValues(paramName);
            if (paramValues.length == 1)
            {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0)
                {
                	paramsMap.put(paramName, paramValue);
                }
            }
        }
        log.error("==========收到回调参数Map==============");
        log.error(JSON.toJSONString(paramsMap));
		log.error("==========收到回调参数Map==============");
		return aliAppPayCallbackService.dealCallBack(paramsMap);
	}
	
}
