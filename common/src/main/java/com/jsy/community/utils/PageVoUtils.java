//package com.jsy.community.utils;
//
//import java.util.List;
//
///**
// * @author lihao
// * @ClassName PageVoUtils
// * @Date 2020/11/26  11:14
// * @Description TODO
// * @Version 1.0
// **/
//public class PageVoUtils {
//
//	public static PageInfo page(Long current, Long total, Long size, List list) {
//
//		int start = (int) ((current - 1) * size);// 每页起始索引
//		if (start > list.size()) {
//			return null;
//		}
//
//		int end = (int) (current * size);// 每页终止索引
//		if (end > list.size()) {
//			end = list.size();
//		}
//
//		PageInfo pageInfo = new PageInfo(current, size, total);
//		pageInfo.setStart(start);
//		pageInfo.setEnd(end);
//		List subList = list.subList(start, end);
//		pageInfo.setRecords(subList);
//		return pageInfo;
//	}
//
//}
