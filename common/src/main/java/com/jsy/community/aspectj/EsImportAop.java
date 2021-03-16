package com.jsy.community.aspectj;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.EsImport;
import com.jsy.community.exception.JSYException;
import com.jsy.community.utils.JsonUtils;
import com.jsy.community.utils.es.ElasticSearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author YuLF
 * @since 2021-03-04 09:30
 */
@Aspect
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EsImportAop extends BaseAop {

    @Pointcut("@annotation(com.jsy.community.annotation.EsImport)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object desensitization(ProceedingJoinPoint point) throws Throwable {
        //执行方法
        Object result = point.proceed();
        //获得业务执行方法
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();
        Method method = getMethod(targetClass, signature);
        //拿到注解参数
        EsImport annotation = method.getAnnotation(EsImport.class);
        Operation operation = annotation.operation();
        RecordFlag recordFlag = annotation.recordFlag();
        String idKey = annotation.idKey();
        String[] importFields = annotation.importField();
        String[] searchFields = annotation.searchField();
        Class<?> parameterType = annotation.parameterType();
        String deletedId = annotation.deletedId();
        Object[] args = point.getArgs();
        switch (operation) {
            //es删除操作
            case DELETE:
                //拿到所有的参数名称
                String[] parameterNames = ((CodeSignature) point.getSignature()).getParameterNames();
                //es 删除操作 业务方法 形参 下标位置
                int businessIdIndex = 0;
                //通过用户指定的业务id名称 拿到 业务方法请求参数值
                for (String name : parameterNames) {
                    if (name.equals(deletedId)) {
                        break;
                    }
                    businessIdIndex++;
                }
                //取出业务方法 形参 值 此时businessId 为 业务数据id值
                Object businessId = args[businessIdIndex];
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("esId", businessId);
                //标记这条数据的操作 删 类型
                jsonObject.put("operation", operation);
                ElasticSearchImportProvider.elasticOperation(jsonObject);
                break;
            case INSERT:
            case UPDATE:
                // 如果 在es 操作更新 注解中的 导入列 和 搜索列 为空，抛出异常
                if (StringUtils.isEmpty(importFields) || StringUtils.isEmpty(searchFields)) {
                    throw new JSYException("Annotation @EsImport parameter importFields or searchFields be not is null!");
                }
                //在做增改操作后  按返回值 来判断业务方法是否执行成功，  返回值 判断类型： void Integer Boolean  如果业务方法增/改操作  不返回这三种类型 那就不导入es 不做任何操作
                //1.业务方法返回值为 void
                if( Objects.nonNull(result) ){
                    //2.业务方法返回值为Integer/int 返回sql 执行结果行数 == 0  如果是更新在没有异常的情况下 sql 执行结果行数是可能为 0的 增改操作
                    if (result instanceof Integer) {
                        Integer executeResRow = (Integer) result;
                        if (executeResRow == 0) {
                            return result;
                        }
                    }
                    //3.业务方法返回值为布尔
                    if (!Boolean.parseBoolean(result.toString())) {
                        return result;
                    }
                }

                //获得参数对象
                Object obj = null;
                //通过类型 拿到 请求参数
                for (Object arg : args) {
                    Class<?> aClass = arg.getClass();
                    if (aClass.isAssignableFrom(parameterType)) {
                        obj = arg;
                        break;
                    }
                }
                if (Objects.isNull(obj)) {
                    return result;
                }
                //插入 和 更新 都做同样的操作
                JSONObject insertObj = getJsonObj(annotation, obj);
                ElasticSearchImportProvider.elasticOperation(insertObj);
                break;
            default:
                break;
        }
        return result;
    }


    /**
     * 从 参数对象 中 取出调用方 指定的 需要导入es的字段列 组成JSONObject
     *
     * @param esImport 注解
     * @param o        参数对象
     */
    private JSONObject getJsonObj(EsImport esImport, Object o) {
        //把 idKey 添加 到 导入field 数组里面 组成一个新的数组
        String[] strArrays = addArrays(esImport.importField(), esImport.idKey());
        JSONObject jsonObject = JsonUtils.toJsonObject(o, strArrays);
        //为json对象添加 es 搜索字段
        jsonObject.put("searchTitle", mergeField(jsonObject, esImport.searchField()));
        //标记这条数据的操作 增or改 类型
        jsonObject.put("operation", esImport.operation());
        //标记这条数据 是属于 哪一个类型（如 社区消息、房屋租赁、社区趣事..） 用于 后续的数据操作
        jsonObject.put("flag", esImport.recordFlag());
        //对象数据业务id 作为 es id 存入 该对象 id 的名称
        jsonObject.put("esId", jsonObject.getString(esImport.idKey()));
        return jsonObject;
    }

    /**
     * 把对象中 需要搜索的内容 以逗号分割 添加至 jsonObject ->searchTitle  最终导入es
     *
     * @param searchFields 调用方指定 对象中 哪一些字段列作为es搜索条件
     * @param jsonObject   请求对象  取值的json对象
     */
    private String mergeField(JSONObject jsonObject, String[] searchFields) {
        StringBuilder sb = new StringBuilder();
        for (String field : searchFields) {
            sb.append(jsonObject.getString(field)).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }


    /**
     * 把 param 添加到 original[]
     *
     * @return 返回新数组
     * @author YuLF
     * @Param param            需要添加到original数组的参数
     * @Param original         旧数组
     * @since 2021/3/4 15:42
     */
    private static String[] addArrays(String[] original, String... param) {
        String[] newArray = new String[original.length + param.length];
        int paramIndex = 0;
        for (int i = 0; i < newArray.length; i++) {
            if (i >= original.length) {
                newArray[i] = param[paramIndex];
                paramIndex++;
                continue;
            }
            newArray[i] = original[i];
        }
        return newArray;
    }



}
