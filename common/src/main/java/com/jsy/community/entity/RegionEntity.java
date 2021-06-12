package com.jsy.community.entity;

import java.io.Serializable;
import java.util.List;
/**
 * 省市区划
 * @author qq459799974
 * @since 2020-11-23
 */
public class RegionEntity implements Serializable {
	private Integer id;//编号
	private String name;//全称
	private String sname;//简称
	private Character ssname;//首字简称
	private Integer pid;//父级编号
	private Integer level;//层级
	private String pinyin;//拼音
	private String initials;//首字母
	private Float lng;//经度
	private Float lat;//纬度
	private List<RegionEntity> children;//子级封装
	public RegionEntity() {
		super();
	}
	
	public RegionEntity(Integer id, String name, Integer pid, Integer level, String initials, String pinyin, Float lng, Float lat) {
		this.id = id;
		this.name = name;
		this.pid = pid;
		this.level = level;
		this.initials = initials;
		this.pinyin = pinyin;
		this.lng = lng;
		this.lat = lat;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public Character getSsname() {
		return ssname;
	}
	public void setSsname(Character ssname) {
		this.ssname = ssname;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public List<RegionEntity> getChildren() {
		return children;
	}
	public void setChildren(List<RegionEntity> children) {
		this.children = children;
	}
	public String getInitials() {
		return initials;
	}
	public Float getLng() {
		return lng;
	}
	public void setLng(Float lng) {
		this.lng = lng;
	}
	public Float getLat() {
		return lat;
	}
	public void setLat(Float lat) {
		this.lat = lat;
	}
	
	public void setInitials(String initials) {
		this.initials = initials;
	}
	
	@Override
	public String toString() {
		return "RegionEntity{" +
			"id=" + id +
			", name='" + name + '\'' +
			", sname='" + sname + '\'' +
			", ssname=" + ssname +
			", pid=" + pid +
			", level=" + level +
			", pinyin='" + pinyin + '\'' +
			", initials='" + initials + '\'' +
			", lng=" + lng +
			", lat=" + lat +
			", children=" + children +
			'}';
	}
}
