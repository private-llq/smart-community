package com.jsy.community.config.service;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动设置创建时间、更新时间
 *
 * @author ling
 * @since 2020-11-11 15:47
 */
@Component
@Slf4j
@ConditionalOnProperty(value = "jsy.service.enable", havingValue = "true")
public class MyMetaObjectHandler implements MetaObjectHandler {
	@Override
	public void insertFill(MetaObject metaObject) {
		setFieldValByName("createTime", LocalDateTime.now(), metaObject);
		setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
	}
	
	@Override
	public void updateFill(MetaObject metaObject) {
		setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
	}
}