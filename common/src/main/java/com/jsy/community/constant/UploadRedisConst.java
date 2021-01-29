package com.jsy.community.constant;

/**
 * @ClassName：UploudRedisConst
 * @Description：TODO
 * @author：lihao
 * @date：2021/1/29 18:10
 * @version：1.0
 */
public interface UploadRedisConst {
	
	/**
	 * @Author lihao
	 * @Description 报修只上传图片时存的redis
	 **/
	String REPAIR_IMG_PART = "repair_img_part";
	
	/**
	 * @Author lihao
	 * @Description 报修整体上传时存的redis
	 **/
	String REPAIR_IMG_ALL = "repair_img_all";
	
	/**
	 * @Author lihao
	 * @Description  报修评价只上传图片时存的redis
	 **/
	String REPAIR_COMMENT_IMG_PART = "repair_comment_img_part";
	
	/**
	 * @Author lihao
	 * @Description  报修评价整体上传时存的redis
	 **/
	String REPAIR_COMMENT_IMG_ALL = "repair_comment_img_all";
	
	/**
	 * @Author lihao
	 * @Description app菜单只上传图片时存的redis
	 **/
	String APP_MENU_IMG_PART = "app_menu_img_part";
	
	/**
	 * @Author lihao
	 * @Description app菜单整体上传时存的redis
	 **/
	String APP_MENU_IMG_ALL = "app_menu_img_all";
	
	/**
	 * @Author lihao
	 * @Description 商铺租赁只上传图片时存的redis
	 **/
	String SHOP_IMG_PART = "shop_img_part";
	
	/**
	 * @Author lihao
	 * @Description 商铺整体上传时存的redis
	 **/
	String SHOP_IMG_ALL = "shop_img_all";
}
