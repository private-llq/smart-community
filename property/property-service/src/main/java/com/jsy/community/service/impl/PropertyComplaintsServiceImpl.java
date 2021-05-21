package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyComplaintsService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.PropertyComplaintsEntity;
import com.jsy.community.entity.admin.AdminUserEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyComplaintsMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.property.PropertyComplaintsVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 物业投诉受理
 * @author: Hu
 * @create: 2021-03-19 13:37
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class PropertyComplaintsServiceImpl extends ServiceImpl<PropertyComplaintsMapper, PropertyComplaintsEntity> implements IPropertyComplaintsService {
    @Autowired
    private PropertyComplaintsMapper propertyComplaintsMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;


    /**
     * @Description: 投诉回复
     * @author: Hu
     * @since: 2021/5/21 11:06
     * @Param: [complainFeedbackQO]
     * @return: void
     */
    @Override
    public void complainFeedback(ComplainFeedbackQO complainFeedbackQO) {
        PropertyComplaintsEntity entity = propertyComplaintsMapper.selectById(complainFeedbackQO.getId());
        entity.setFeedbackContent(complainFeedbackQO.getBody());
        entity.setStatus(1);
        entity.setFeedbackBy(complainFeedbackQO.getUid());
        AdminUserEntity userEntity = adminUserMapper.selectOne(new QueryWrapper<AdminUserEntity>().eq("uid",complainFeedbackQO.getUid()));
        if (userEntity!=null){
            entity.setFeedbackName(userEntity.getRealName());
            entity.setCommunityId(userEntity.getCommunityId());
        }
        entity.setFeedbackTime(LocalDateTime.now());
        propertyComplaintsMapper.updateById(entity);
    }



    /**
     * @Description: 分页查询物业投诉接口
     * @author: Hu
     * @since: 2021/5/21 11:06
     * @Param: [baseQO]
     * @return: com.jsy.community.utils.PageInfo
     */
    @Override
    public PageInfo findList(BaseQO<PropertyComplaintsQO> baseQO) {
        PageInfo<Object> pageInfo = new PageInfo<>();
        PropertyComplaintsQO qoQuery = baseQO.getQuery();
        if (qoQuery.getComplainTimeOut()!=null) {
            qoQuery.setComplainTimeOut(qoQuery.getComplainTimeOut().plusDays(1));
        }
        Page page =propertyComplaintsMapper.findList(new Page<PropertyComplaintsEntity>(baseQO.getPage(),baseQO.getSize()),qoQuery);
        List<PropertyComplaintsVO> list = page.getRecords();
        if (list.size()>0){
            for (PropertyComplaintsVO entity : list) {
                entity.setTypeName(BusinessEnum.ComplainTypeEnum.getCode(entity.getType()));
            }
            page.setRecords(list);
        }
        BeanUtils.copyProperties(page,pageInfo);
        return pageInfo;
    }
}
