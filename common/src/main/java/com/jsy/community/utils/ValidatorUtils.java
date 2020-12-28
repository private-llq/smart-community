package com.jsy.community.utils;

import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 参数验证
 */
@Slf4j
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
     *
     * @param param 字符串
     * @return 返回这个字符串是否是正整数的布尔值
     */
    public static Boolean isInteger(Object param) {
        if (param == null) {
            return false;
        }
        String str = param.toString();
        boolean matches = Pattern.compile("^[1-9]\\d*$").matcher(str).matches();
        if (!matches) {
            return false;
        }
        //避免出现同位 2147483647 更大的值 如 2147483648
        return str.length() <= String.valueOf(Integer.MAX_VALUE).length() && Long.parseLong(str) <= Integer.MAX_VALUE;
    }

    /**
     * 前端分页查询参数[page,pageSize]边界效验，非空效验 如果不合法 则设置默认的分页参数值
     *
     * @param baseQO 控制层接收参数的实体类
     */
    public static void validatePageParam(BaseQO<?> baseQO) {
        if (!isInteger(baseQO.getPage())) {
            baseQO.setPage(1L);
        }
        if (!isInteger(baseQO.getSize())) {
            baseQO.setSize(10L);
        }
    }

    /**
     * 针对数据库常量验证，在QO中 无法设定字段的 正确取值范围
     * 验证客户端传的参数 clientVal 是否在 List 中 field字段中值是否存在
     *
     * @author YuLF
     * @Param list                查询出来的服务端可用限定数据
     * @Param field            List中对象的字段属性
     * @Param clientVal        客户端需要验证的客户端参数
     * @since 2020/12/11 14:48
     */
    public static void validFieldVal(Collection<?> list, String field, Object clientVal) {
    	boolean exist = false;
        try {
            //获取List中对象属性 的 get方法 方便下面取值
            String FieldMethod = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
            for (Object t : list) {
                //拿到当前List中这个对象 并调用他的 get方法 拿到值
                Class<?> aClass = t.getClass();
                //如果客户端传递的参数在可用参数列表中存在 则提前结束验证
                if (Objects.equals(String.valueOf(aClass.getDeclaredMethod(FieldMethod).invoke(t)), clientVal)) {
					exist = true;
                }
            }
        } catch (Exception e) {
            log.error("com.jsy.community.utils.ValidatorUtils.validFieldVal: {}", e.getMessage());
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), "验证数据失败：" + e.getMessage());
        }
        if (!exist) {
            throw new JSYException(JSYError.BAD_REQUEST.getCode(), "在可用范围数据内. 没有找到：" + clientVal);
        }
    }

    /**
     * 验证图片地址格式是否正确
     * @author YuLF
     * @since  2020/12/28 15:16
     * @Param
     */
    public static void validateImages(String[] images){
        for( String image : images ){
            if( !RegexUtils.isUrl(image) ){
                throw new JSYException("'" + image + "' 不是一个正确的访问地址!");
            }
        }
    }


}