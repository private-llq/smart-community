package com.jsy.community.annotation;

import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全文搜索 es 导入注解
 * @author YuLF
 * @since 2021-03-04 09:24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EsImport {

    /**
     * 操作符 表明 是对ES增删改哪一种操作
     */
    Operation operation();

    /**
     * 操作类型（租赁、消息、趣事...）、用于标识该数据属于哪一类型，方便其他地方进行数据操作
     */
    RecordFlag recordFlag();

    /**
     * 业务方法请求参数对象、仅应用于 ES 增 改 操作
     * 参数对象类型，用于获取请求参数对象
     * 获取对象 数据 作为 导入es的数据
     */
    Class<?> parameterType() default Object.class;

    /**
     * 仅用于 ES 删除 操作 删除需要用这个id的值
     * 表明 方法 形参 中 业务ID 名称
     */
    String deletedId() default "id";

    /**
     * parameterType中需要导入es的作为数据的字段列
     * 这个字段作为 需要导入 至 es 的数据，如果你的对象中的某些字段数据需要在 搜索出来的时候前端会取
     */
    String[] importField() default "";

    /**
     * parameterType中需要作为es搜索的字段列
     * 这个字段 表示 你的 qo 中 那些字段的内容需要作为 全文搜索
     */
    String[] searchField() default "";

    /**
     * 仅用于 es 增 改 操作
     * 标识业务数据 唯一 id 名称
     * parameterType 对象 中 作为 数据id 的主键名称
     */
    String idKey() default "id";

}
