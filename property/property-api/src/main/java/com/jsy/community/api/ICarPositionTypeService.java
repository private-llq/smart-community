package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.CarPositionTypeEntity;
import com.jsy.community.qo.property.UpdateCartPositionTypeQO;
import com.jsy.community.vo.property.SelectCartPositionTypeVO;

import java.util.List;

/**
 * <p>
 * 车位类型表 服务类
 * </p>
 *
 * @author Arli
 * @since 2021-08-05
 */
public interface ICarPositionTypeService extends IService<CarPositionTypeEntity> {

    Boolean insterCartPositionType(String description,Long CommunityId);

    boolean updateCartPositionType(UpdateCartPositionTypeQO qo,Long adminCommunityId);

    List<SelectCartPositionTypeVO> selectCartPositionType(Long adminCommunityId);

    Boolean deleteCartPositionType(String id);

    List<CarPositionTypeEntity> getAllType();
}
