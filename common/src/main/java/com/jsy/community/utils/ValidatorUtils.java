package com.jsy.community.utils;

import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 参数验证
 */
public class ValidatorUtils {
	private static final Validator validator;
	
	static {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}
	
	/**
	 * 校验对象
	 *
	 * @param object 待校验对象
	 * @param groups 待校验的组
	 * @throws JSYException 校验不通过，则抛异常
	 */
	public static void validateEntity(Object object, Class<?>... groups)
		throws JSYException {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			StringBuilder msg = new StringBuilder();
			for (ConstraintViolation<Object> constraint : constraintViolations) {
				msg.append(constraint.getMessage()).append(",");
			}
			throw new JSYException(400, msg.substring(0, msg.length() - 1));
		}
	}
	/**
	 * 判断字符串是否是一个正整数
	 * @param param 字符串
	 * @return   	返回这个字符串是否是正整数的布尔值
	 */
	public static Boolean isInteger(Object param) {
		if( param == null ){
			return false;
		}
		String str = param.toString();
		boolean matches = Pattern.compile("^[1-9]\\d+$").matcher(str).matches();
		if(!matches) {
			return false;
		}
		//避免出现同位 2147483647 更大的值 如 2147483648
		return str.length() <= String.valueOf(Integer.MAX_VALUE).length() && Long.parseLong(str) <= Integer.MAX_VALUE;
	}
	/**
	 * 前端分页查询参数[page,pageSize]边界效验，非空效验 如果不合法 则设置默认的分页参数值
	 * @param baseQO 	控制层接收参数的实体类
	 */
	public static void validatePageParam(BaseQO<?> baseQO){
		if ( !isInteger(baseQO.getPage()) ){
			baseQO.setPage(1L);
		}
		if ( !isInteger(baseQO.getSize()) ){
			baseQO.setSize(10L);
		}
	}


}