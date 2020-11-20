package com.jsy.community.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.qo.BaseQO;

/**
 * 查询参数
 */
public class Query<T> {
	
	public IPage<T> getPage(BaseQO<?> baseQO) {
		return this.getPage(baseQO, "id", false);
	}
	
	public IPage<T> getPage(BaseQO<?> qo, String defaultOrderField, boolean isAsc) {
		//分页参数
		long curPage = 1;
		long size = 10;
		
		if (qo.getPage() != null) {
			curPage = qo.getPage();
		}
		if (qo.getSize() != null) {
			size = qo.getSize();
		}
		
		//分页对象
		Page<T> page = new Page<>(curPage, size);
		
		//没有排序字段，则不排序
		if (StrUtil.isBlank(defaultOrderField)) {
			return page;
		}
		
		//默认排序
		if (isAsc) {
			page.addOrder(OrderItem.asc(defaultOrderField));
		} else {
			page.addOrder(OrderItem.desc(defaultOrderField));
		}
		
		return page;
	}
}
