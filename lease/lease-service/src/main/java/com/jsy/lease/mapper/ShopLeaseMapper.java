package com.jsy.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
	
	void deleteTags(@Param("tagIds") Long[] both);
	
	/**
	 * @return java.lang.Long[]
	 * @Author lihao
	 * @Description 根据店铺id查询房源类型标签
	 * @Date 2020/12/17 18:42
	 * @Param [shopId]
	 **/
	@Select("SELECT\n" +
		"\ttsc.house_const_id \n" +
		"FROM\n" +
		"\tt_shop_lease tsl\n" +
		"\tINNER JOIN t_shop_const tsc ON tsl.id = tsc.shop_lease_id\n" +
		"\tINNER JOIN t_house_const thc on thc.id = tsc.house_const_id\n" +
		"where tsl.id = #{shopId} and thc.house_const_type = 7 ")
	Long[] selectTypeTags(Long shopId);
	
	@Select("SELECT\n" +
		"\ttsc.house_const_id \n" +
		"FROM\n" +
		"\tt_shop_lease tsl\n" +
		"\tINNER JOIN t_shop_const tsc ON tsl.id = tsc.shop_lease_id\n" +
		"\tINNER JOIN t_house_const thc on thc.id = tsc.house_const_id\n" +
		"where tsl.id = #{shopId} and thc.house_const_type = 8 ")
	Long[] selectBusinessTags(Long shopId);
}
