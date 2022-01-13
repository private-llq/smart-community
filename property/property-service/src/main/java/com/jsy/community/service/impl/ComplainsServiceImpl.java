package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IComplainsService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.mapper.AdminUserMapper;
import com.jsy.community.mapper.ComplainsMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ComplainFeedbackQO;
import com.jsy.community.qo.property.PropertyComplaintsQO;
import com.jsy.community.vo.ComplainVO;
import com.jsy.community.vo.admin.AdminInfoVo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import jodd.util.StringUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: com.jsy.community
 * @description: 投诉建议
 * @author: Hu
 * @create: 2020-12-23 15:50
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class ComplainsServiceImpl extends ServiceImpl<ComplainsMapper, ComplainEntity> implements IComplainsService {
    @Autowired
    private ComplainsMapper complainsMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    /**
     * @Description: 投诉建议反馈
     * @author: Hu
     * @since: 2020/12/23 17:00
     * @Param:
     * @return:
     */
    @Override
    public void feedback(ComplainFeedbackQO complainFeedbackQO, AdminInfoVo userInfo) {
        ComplainEntity complainEntity = complainsMapper.selectById(complainFeedbackQO.getId());
        complainEntity.setStatus(1);
        complainEntity.setFeedbackBy(userInfo.getUid());
        complainEntity.setFeedbackTime(LocalDateTime.now());
        complainEntity.setFeedbackContent(complainFeedbackQO.getBody());
        complainsMapper.updateById(complainEntity);
    }
    /**
     * @Description: 查询所有投诉信息
     * @author: Hu
     * @since: 2020/12/23 17:01
     * @Param:
     * @return:
     */
    @Override
    public Map<String, Object> listAll(BaseQO<PropertyComplaintsQO> baseQO,AdminInfoVo userInfo) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        PropertyComplaintsQO qoQuery = baseQO.getQuery();
        if (qoQuery.getComplainTimeOut()!=null){
            qoQuery.setComplainTimeOut(qoQuery.getComplainTimeOut().plusDays(1));
        }
        if (qoQuery.getFeedbackTimeOut()!=null){
            qoQuery.setFeedbackTimeOut(qoQuery.getFeedbackTimeOut().plusDays(1));
        }
        Set<String> strings = new HashSet<>();
        if (StringUtil.isNotBlank(qoQuery.getKeyWord())) {
            strings = baseUserInfoRpcService.queryRealUserDetail(qoQuery.getKeyWord(), qoQuery.getKeyWord());
        }
        qoQuery.setCommunityId(userInfo.getCommunityId());
        Long page=(baseQO.getPage()-1)*baseQO.getSize();
        List<ComplainVO> complainVOS = complainsMapper.listAll(page, baseQO.getSize(), qoQuery, strings);
        if (!CollectionUtils.isEmpty(complainVOS)) {
            Set<String> uidSet = complainVOS.stream().map(ComplainVO::getUid).collect(Collectors.toSet());
            List<RealUserDetail> realUserDetails = baseUserInfoRpcService.getRealUserDetails(uidSet);
            Map<String, RealUserDetail> userDetailMap = realUserDetails.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
            Set<String> feedbackBySet = complainVOS.stream().map(ComplainVO::getFeedbackBy).collect(Collectors.toSet());
            Map<String, RealUserDetail> detailMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(feedbackBySet)) {
                List<RealUserDetail> realUserDetails1 = baseUserInfoRpcService.getRealUserDetails(feedbackBySet);
                detailMap = realUserDetails1.stream().collect(Collectors.toMap(RealUserDetail::getAccount, Function.identity()));
            }
            for (ComplainVO complainVO : complainVOS) {
                RealUserDetail realUserDetail = userDetailMap.get(complainVO.getUid());
                if (realUserDetail != null) {
                    complainVO.setMobile(realUserDetail.getPhone());
                    complainVO.setName(realUserDetail.getRealName());
                }
                RealUserDetail realUserDetail1 = detailMap.get(complainVO.getFeedbackBy());
                if (realUserDetail1 != null) {
                    complainVO.setFeedbackName(realUserDetail1.getRealName());
                }
            }
        }
        Long totel = complainsMapper.findTotel(baseQO.getQuery(), strings);
        Map<String, Object> map = new HashMap<>();
        map.put("totel",totel);
        map.put("list",complainVOS);
        return map;
    }
}
