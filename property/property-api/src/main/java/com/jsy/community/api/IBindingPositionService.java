package com.jsy.community.api;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.BindingPositionEntity;

import java.util.List;

public interface IBindingPositionService extends IService<BindingPositionEntity> {
    Integer saveBinding(BindingPositionEntity bindingPositionEntity);

    List<BindingPositionEntity> selectBinding(BindingPositionEntity bindingPositionEntity);

    void binding(BindingPositionEntity bindingPositionEntity);
}
