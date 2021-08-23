package com.jsy.community.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IWOptionsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.WOptions;
import com.jsy.community.mapper.WOptionsMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Arli
 * @since 2021-08-17
 */
@DubboService(version = Const.version, group = Const.group_property)
public class WOptionsServiceImpl extends ServiceImpl<WOptionsMapper, WOptions> implements IWOptionsService {

}
