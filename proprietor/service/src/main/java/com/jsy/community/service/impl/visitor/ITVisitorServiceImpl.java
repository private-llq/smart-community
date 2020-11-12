package com.jsy.community.service.impl.visitor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.visitor.ITVisitorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.visitor.VisitorEntity;
import com.jsy.community.mapper.visitor.TVisitorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.visitor.VisitorQO;
import com.jsy.community.utils.MyPageUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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
    
    /**
     * @Description: 分页查询
     * @Param: [BaseQO<VisitorQO>]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.visitor.VisitorEntity>
     * @Author: chq459799974
     * @Date: 2020/11/11
     **/
    @Override
    public Page<VisitorEntity> queryByPage(BaseQO<VisitorQO> baseQO){
        Page<VisitorEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO); //设置分页参数
        VisitorQO visitorQO = baseQO.getQuery();
        QueryWrapper<VisitorEntity> queryWrapper = new QueryWrapper<VisitorEntity>().select("*");
        if(!StringUtils.isEmpty(visitorQO.getName())){
            queryWrapper.eq("name",visitorQO.getName());
        }
        if(!StringUtils.isEmpty(visitorQO.getContact())){
            queryWrapper.eq("contact",visitorQO.getContact());
        }
        queryAddress(queryWrapper,visitorQO);
        return tVisitorMapper.selectPage(page, queryWrapper);
    }
    
    /** 查询楼栋 */
    private void queryAddress(QueryWrapper queryWrapper, VisitorQO visitorQO){
        if(!StringUtils.isEmpty(visitorQO.getUnit())){
            queryWrapper.eq("unit",visitorQO.getUnit());
            if(!StringUtils.isEmpty(visitorQO.getBuilding())){
                queryWrapper.eq("building",visitorQO.getBuilding());
                if(!StringUtils.isEmpty(visitorQO.getFloor())){
                    queryWrapper.eq("floor",visitorQO.getFloor());
                    if(!StringUtils.isEmpty(visitorQO.getDoor())){
                        queryWrapper.eq("door",visitorQO.getDoor());
                    }
                }
            }
        }
    }
}
