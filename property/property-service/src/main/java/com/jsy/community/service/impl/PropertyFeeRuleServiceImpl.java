package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IPropertyFeeRuleService;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.PropertyFeeRuleEntity;
import com.jsy.community.entity.property.PropertyFeeRuleRelevanceEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.PropertyFeeRuleMapper;
import com.jsy.community.mapper.PropertyFeeRuleRelevanceMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.FeeRuleQO;
import com.jsy.community.qo.property.FeeRuleRelevanceQO;
import com.jsy.community.qo.property.UpdateRelevanceQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.jsy.community.vo.property.FeeRuleVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 小区物业收费规则
 * @author: Hu
 * @create: 2021-04-20 16:30
 **/
@DubboService(version = Const.version, group = Const.group_property)
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class PropertyFeeRuleServiceImpl extends ServiceImpl<PropertyFeeRuleMapper, PropertyFeeRuleEntity> implements IPropertyFeeRuleService {
    @Autowired
    private PropertyFeeRuleMapper propertyFeeRuleMapper;
    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private PropertyFeeRuleRelevanceMapper propertyFeeRuleRelevanceMapper;

    @Override
    public List selectRelevance(FeeRuleRelevanceQO feeRuleRelevanceQO) {
        if (feeRuleRelevanceQO.getType()==1){
            //查房屋
            return propertyFeeRuleRelevanceMapper.selectHouse(feeRuleRelevanceQO);
        }else{
            //查车位
            return propertyFeeRuleRelevanceMapper.selectCarPosition(feeRuleRelevanceQO);
        }
    }



    /**
     * @Description: 批量新增收费项目关联目标
     * @author: Hu
     * @since: 2021/9/6 14:07
     * @Param:
     * @return:
     */
    @Override
    public void addRelevance(UpdateRelevanceQO updateRelevanceQO) {
        List<PropertyFeeRuleRelevanceEntity> list = new LinkedList();
        PropertyFeeRuleRelevanceEntity entity = null;
        String[] split = updateRelevanceQO.getIds().split(",");
        for (String s : split) {
            entity = new PropertyFeeRuleRelevanceEntity();
            entity.setId(SnowFlake.nextId());
            entity.setRelevanceId(Long.parseLong(s));
            entity.setRuleId(updateRelevanceQO.getId());
            entity.setType(updateRelevanceQO.getType());
            list.add(entity);
        }
        if (list.size()!=0){
            propertyFeeRuleRelevanceMapper.save(list);
        }
    }

    /**
     * @Description: 删除收费项目中关联的房屋或者车位
     * @author: Hu
     * @since: 2021/9/6 13:57
     * @Param: [id]
     * @return: void
     */
    @Override
    public void deleteRelevance(Long id) {
        propertyFeeRuleRelevanceMapper.deleteById(id);
    }

    @Override
    public void statementStatus(AdminInfoVo userInfo, Integer status, Long id) {
        PropertyFeeRuleEntity entity = propertyFeeRuleMapper.selectById(id);
        if (entity!=null){
            if (status==1){
                entity.setReportStatus(1);;
            }else {
                entity.setReportStatus(0);;
            }
            propertyFeeRuleMapper.updateById(entity);
        }

    }

    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/9/1 9:50
     * @Param: [id]
     * @return: void
     */
    @Override
    public void delete(Long id) {
        propertyFeeRuleMapper.deleteById(id);
    }

    /**
     * @Description: 新增缴费规则
     * @author: Hu
     * @since: 2021/7/20 14:26
     * @Param: [communityId, propertyFeeRuleEntity]
     * @return: void
     */
    @Override
    public void saveOne(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
//        propertyFeeRuleEntity.setCommunityId(userInfo.getCommunityId());
        propertyFeeRuleEntity.setId(SnowFlake.nextId());
        propertyFeeRuleEntity.setCreateBy(userInfo.getUid());
        propertyFeeRuleEntity.setStatus(0);
        propertyFeeRuleEntity.setCreateTime(LocalDateTime.now());
        Integer size = propertyFeeRuleMapper.selectCount(new QueryWrapper<PropertyFeeRuleEntity>().eq("community_id", userInfo.getCommunityId()));
        size++;
        String value = String.valueOf(size);
        if (value.length()==1){
            propertyFeeRuleEntity.setSerialNumber("000"+value);
        }else {
            if(value.length()==2){
                propertyFeeRuleEntity.setSerialNumber("00"+value);
            }else {
                if (value.length()==3){
                    propertyFeeRuleEntity.setSerialNumber("0"+value);
                }else {
                    propertyFeeRuleEntity.setSerialNumber(value);
                }
            }
        }
        propertyFeeRuleMapper.insert(propertyFeeRuleEntity);
    }

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [userInfo, propertyFeeRuleEntity]
     * @return: void
     */
    @Override
    public void updateOneRule(AdminInfoVo userInfo, PropertyFeeRuleEntity propertyFeeRuleEntity) {
        propertyFeeRuleEntity.setUpdateBy(userInfo.getUid());
        propertyFeeRuleMapper.updateById(propertyFeeRuleEntity);
    }


    /**
     * @Description: 启用或者停用
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [userInfo, status, id]
     * @return: void
     */
    @Override
    public void startOrOut(AdminInfoVo userInfo, Integer status,Long id) {
        PropertyFeeRuleEntity ruleEntity = propertyFeeRuleMapper.selectById(id);
        if (ruleEntity!=null){
            ruleEntity.setStatus(status);
            ruleEntity.setUpdateBy(userInfo.getUid());
            propertyFeeRuleMapper.updateById(ruleEntity);
        }

    }


    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/5/21 11:07
     * @Param: [communityId, type]
     * @return: com.jsy.community.entity.property.PropertyFeeRuleEntity
     */
    @Override
    public PropertyFeeRuleEntity selectByOne(Long id) {
        return propertyFeeRuleMapper.selectById(id);
    }


    /**
     * @Description: 查询当前小区收费规则
     * @author: Hu
     * @since: 2021/5/21 11:08
     * @Param: [baseQO, communityId]
     * @return: java.util.Map<java.lang.Object,java.lang.Object>
     */
    @Override
    public Map<Object, Object> findList(BaseQO<FeeRuleQO> baseQO,Long communityId) {
        FeeRuleQO query = baseQO.getQuery();
        if (baseQO.getSize()==null||baseQO.getSize()<=0){
            baseQO.setSize(10L);
        }
        QueryWrapper<PropertyFeeRuleEntity> wrapper=new QueryWrapper<PropertyFeeRuleEntity>();
        List<FeeRuleVO> page = propertyFeeRuleMapper.findList((baseQO.getPage()-1)*baseQO.getSize(),baseQO.getSize(),baseQO.getQuery());
        for (FeeRuleVO feeRuleVO : page) {
            feeRuleVO.setPeriodName(BusinessEnum.FeeRulePeriodEnum.getName(feeRuleVO.getPeriod()));
        }
        Integer total = propertyFeeRuleMapper.findTotal(baseQO.getQuery());
        Map<Object, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",page);
        return map;
    }
    
    /**
     *@Author: DKS
     *@Description: 根据收费项目名称查询收费项目id
     *@Param: feeRuleName:
     *@Date: 2021/9/7 15:27
     **/
    @Override
    public Long selectFeeRuleIdByFeeRuleName(String feeRuleName, Long communityId) {
        return propertyFeeRuleMapper.selectFeeRuleIdByFeeRuleName(feeRuleName, communityId);
    }
}
