package com.jsy.community.service.impl.visitor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.visitor.ITVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.visitor.VisitorEntity;
import com.jsy.community.mapper.visitor.TVisitorMapper;
import com.jsy.community.qo.visitor.VisitorQO;
import com.jsy.community.utils.MyPageUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 来访人员 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
@DubboService(version = Const.version, group = Const.group)
public class ITVisitorServiceImpl extends ServiceImpl<TVisitorMapper, VisitorEntity> implements ITVisitorService {

    @Autowired
    private TVisitorMapper tVisitorMapper;

    public Page<VisitorEntity> queryByPage(VisitorQO visitorQO){
        Page<VisitorEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, visitorQO); //设置分页参数
        return tVisitorMapper.selectPage(page, new QueryWrapper<VisitorEntity>().select("*"));
    }
}
