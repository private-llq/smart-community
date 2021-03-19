package com.jsy.community.utils;


import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PageInfo<T> implements Serializable {
	private static final long serialVersionUID = 8545991263226528798L;
	
	@ApiModelProperty(value = "每页记录数")
	private List<T> records;
	
	@ApiModelProperty(value = "总记录数")
	private long total;
	
	@ApiModelProperty(value = "每页记录数")
	private long size;
	
	@ApiModelProperty(value = "当前页")
	private long current;
	
	
	
	public PageInfo() {
		this.records = Collections.emptyList();
		this.total = 0L;
		this.size = 10L;
		this.current = 1L;
		
	}
	
	public PageInfo(long current, long size) {
		this(current, size, 0L);
	}
	
	public PageInfo(long current, long size, long total) {
		this(current, size, total, true);
	}
	
	public PageInfo(long current, long size, boolean isSearchCount) {
		this(current, size, 0L, isSearchCount);
	}
	
	public PageInfo(long current, long size, long total, boolean isSearchCount) {
		this.records = Collections.emptyList();
		this.total = 0L;
		this.size = 10L;
		this.current = 1L;
		if (current > 1L) {
			this.current = current;
		}
		
		this.size = size;
		this.total = total;
	}
	
	
	public List<T> getRecords() {
		return this.records;
	}
	
	public PageInfo<T> setRecords(List<T> records) {
		this.records = records;
		return this;
	}
	
	public long getTotal() {
		return this.total;
	}
	
	public PageInfo<T> setTotal(long total) {
		this.total = total;
		return this;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public PageInfo<T> setSize(long size) {
		this.size = size;
		return this;
	}
	
	public long getCurrent() {
		return this.current;
	}
	
	public PageInfo<T> setCurrent(long current) {
		this.current = current;
		return this;
	}
}
