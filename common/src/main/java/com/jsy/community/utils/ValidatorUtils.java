package com.jsy.community.utils;

import com.jsy.community.exception.JSYException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

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
}