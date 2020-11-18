package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitorPersonService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.VisitorPersonEntity;
import com.jsy.community.mapper.VisitorPersonMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 访客随行人员 服务实现类
 * </p>
 *
 * @author jsy
 * @since 2020-11-12
 */
@DubboService(version = Const.version, group = Const.group)
public class VisitorPersonServiceImpl extends ServiceImpl<VisitorPersonMapper, VisitorPersonEntity> implements IVisitorPersonService {
	
    @Autowired
    private VisitorPersonMapper visitorPersonMapper;
    
    /**
    * @Description: 根据关联的访客表ID 列表查询
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitorPersonEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @Override
    public List<VisitorPersonEntity> queryPersonList(Long visitorid){
	    return visitorPersonMapper.selectList(new QueryWrapper<VisitorPersonEntity>()
				    .select("id,name,mobile")
				    .eq("visitor_id",visitorid)
			    );
    }
	
}
