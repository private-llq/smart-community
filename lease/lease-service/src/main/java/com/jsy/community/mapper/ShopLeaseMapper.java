package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author jsy
 * @since 2020-12-17
 */
public interface ShopLeaseMapper extends BaseMapper<ShopLeaseEntity> {
	
	void insertMiddle(@Param("id") Long id, @Param("tagIds") Long[] tagIds);
	
	@Select("select house_const_id from t_shop_const where shop_lease_id = #{shopId}")
	Long[] selectTags(Long shopId);
	
	void deleteTags(@Param("shopId") Long shopId);
	
	/**
	 * @return java.lang.Long[]
	 * @Author lihao
	 * @Description 根据店铺id查询房源类型标签
	 * @Date 2020/12/17 18:42
	 * @Param [shopId]
	 **/
	Long[] selectTypeTags(Long shopId);
	
	Long[] selectBusinessTags(Long shopId);  // 注意：若使用了mybatisPlugin会提示结果类型不匹配
	
	
	List<Long> selectMiddle(List<Long> advantage);
}
