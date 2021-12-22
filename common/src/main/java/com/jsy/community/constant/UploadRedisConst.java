package com.jsy.community.constant;

/**
 * @ClassName：UploudRedisConst
 * @Description：
 * 解决无效图片只针对用户操作，因为用户多  出现无效图片的情况较多。      该功能可有可无，只是针对项目做一个优化，减少服务器的存储压力,不去存储那些没用图片。
 * 该功能带来的效果是减少存储压力，缺点是要走一次redis，要开销性能。
 * @author：lihao
 * @date：2021/1/29 18:10
 * @version：1.0
 */
public interface UploadRedisConst {
	
	/**
	 * @Author lihao
	 * @Description 发起报修
	 **/
	String REPAIR_IMG_PART = "repair_img_part";
	String REPAIR_IMG_ALL = "repair_img_all";
	
	/**
	 * @Author lihao
	 * @Description  报修评价
	 **/
	String REPAIR_COMMENT_IMG_PART = "repair_comment_img_part";
	String REPAIR_COMMENT_IMG_ALL = "repair_comment_img_all";
	
	/**
	 * @Author lihao
	 * @Description 商铺租赁
	 **/
	String SHOP_HEAD_IMG_PART = "shop_head_img_part";
	String SHOP_HEAD_IMG_ALL = "shop_head_img_all";
	
	String SHOP_MIDDLE_IMG_PART = "shop_middle_img_part";
	String SHOP_MIDDLE_IMG_ALL = "shop_middle_img_all";
	
	String SHOP_OTHER_IMG_PART = "shop_other_img_part";
	String SHOP_OTHER_IMG_ALL = "shop_other_img_all";
	
	/**
	 * @Author lihao
	 * @Description 房屋租赁
	 **/
	String HOUSE_IMG_PART = "house_img_part";
	String HOUSE_IMG_ALL = "house_img_all";
	
	
}
