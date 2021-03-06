package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICommunityService;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.config.ProprietorTopicNameEntity;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.dto.face.xu.XUFaceVisitorEditPersonDTO;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.VisitingCarRecordEntity;
import com.jsy.community.entity.VisitorEntity;
import com.jsy.community.entity.VisitorPersonRecordEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.CommunityHardWareMapper;
import com.jsy.community.mapper.VisitingCarRecordMapper;
import com.jsy.community.mapper.VisitorMapper;
import com.jsy.community.mapper.VisitorPersonRecordMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitorQO;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.VisitorEntryVO;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * ???????????? ???????????????
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group_proprietor)
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, VisitorEntity> implements IVisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    
    @Autowired
    private VisitorPersonRecordMapper visitorPersonRecordMapper;
    
    @Autowired
    private VisitingCarRecordMapper visitingCarRecordMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityService communityService;
    
    @Autowired
    private CommunityHardWareMapper communityHardWareMapper;
    
//    @Value("${}")
    private long visitorTimeLimit = 60*24*60; //?????? ???

    /**
     * @Description: ??????????????????
     * @author: Hu
     * @since: 2021/9/16 15:04
     * @Param:
     * @return:
     */
    @Override
    public VisitorEntity selectOneByIdv2(Long id) {
        VisitorEntity VisitorEntity = visitorMapper.selectOne(new QueryWrapper<VisitorEntity>().select("*").eq("id", id));
        VisitorEntity.setReasonStr(BusinessEnum.VisitReasonEnum.visitReasonMap.get(VisitorEntity.getReason()));
        VisitorEntity.setCarTypeStr(BusinessEnum.CarTypeEnum.CAR_TYPE_MAP.get(VisitorEntity.getCarType()));
        VisitorEntity.setIsCommunityAccessStr(BusinessEnum.CommunityAccessEnum.communityAccessMap.get(VisitorEntity.getIsCommunityAccess()));
        VisitorEntity.setIsCarBanAccessStr(BusinessEnum.BuildingAccessEnum.buildingAccessMap.get(VisitorEntity.getIsCarBanAccess()));
        return VisitorEntity;
    }

    /**
     * @param visitorEntity :
     * @author: Pipi
     * @description: ??????????????????????????????
     * @return: java.util.List<com.jsy.community.entity.VisitorEntity>
     * @date: 2021/10/26 11:41
     **/
    @Override
    public List<VisitorEntity> queryVisitorCar(VisitorEntity visitorEntity) {
        QueryWrapper<VisitorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("car_plate");
        queryWrapper.eq("community_id", visitorEntity.getCommunityId());
        queryWrapper.eq("uid", visitorEntity.getUid());
        queryWrapper.isNotNull("car_plate");
        Set<String> visitorEntitySet = new HashSet<>();
        if (!StringUtil.isNullOrEmpty(visitorEntity.getCarPlate())) {
            queryWrapper.like("car_plate", visitorEntity.getCarPlate());
            queryWrapper.last("order by create_time");
        } else {
            queryWrapper.last("order by create_time");
        }
        List<VisitorEntity> visitorEntities = visitorMapper.selectList(queryWrapper);
        List<VisitorEntity> visitorEntityList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(visitorEntities)){
            if (!StringUtil.isNullOrEmpty(visitorEntity.getCarPlate())) {
                // ???5???
                for (VisitorEntity entity : visitorEntities) {
                    if (visitorEntitySet.size() < 5) {
                        visitorEntitySet.add(entity.getCarPlate());
                    }
                }
            } else {
                // ???3???
                for (VisitorEntity entity : visitorEntities) {
                    if (visitorEntitySet.size() < 3) {
                        visitorEntitySet.add(entity.getCarPlate());
                    }
                }
            }

            for (String carPlate : visitorEntitySet) {
                VisitorEntity entity = new VisitorEntity();
                entity.setCarPlate(carPlate);
                visitorEntityList.add(entity);
            }
        }
        return visitorEntityList;
    }

    /**
    * @Description: ???????????? ??????
     * @Param: [visitorEntity]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public VisitorEntryVO appAddVisitor(VisitorEntity visitorEntity){
        long visitorId = SnowFlake.nextId();
        visitorEntity.setId(visitorId);
        if (visitorEntity.getTempCodeStatus() == 1) {
            visitorEntity.setStartTime(LocalDateTime.now());
            visitorEntity.setEndTime(LocalDateTime.now().plusMinutes(visitorEntity.getEffectiveTime()));
            CommunityEntity communityInfo = communityService.getCommunityNameById(visitorEntity.getCommunityId());
            if (communityInfo != null) {
                visitorEntity.setAddress(communityInfo.getName());
            }
        }
        visitorEntity.setCheckType(BusinessEnum.CheckTypeEnum.OWNER_AUTHORIZATION.getCode());
        int insert = visitorMapper.insert(visitorEntity);
        if(1 != insert){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"???????????? ????????????");
        }
        // ================================== ????????????????????????????????????start ======================================
        /*//????????????????????????
        List<VisitorPersonRecordEntity> personRecordList = visitorEntity.getVisitorPersonRecordList();
        if (!CollectionUtils.isEmpty(personRecordList)) {
            for (VisitorPersonRecordEntity personRecord : personRecordList) {
                try{
                    ValidatorUtils.validateEntity(personRecord);
                }catch (JSYException e){
                    throw new ProprietorException(e.getCode(),e.getMessage());
                }
                personRecord.setVisitorId(visitorId);
                personRecord.setId(SnowFlake.nextId());
            }
            addPersonBatch(personRecordList);
        }
        //????????????????????????
        List<VisitingCarRecordEntity> carRecordList = visitorEntity.getVisitingCarRecordList();
        //???????????????????????????????????????
        List<String> carPlateList = new ArrayList<>();
        if(!StringUtils.isEmpty(visitorEntity.getCarPlate())){
            carPlateList.add(visitorEntity.getCarPlate());
        }
        if (!CollectionUtils.isEmpty(carRecordList)) {
            for (VisitingCarRecordEntity carRecord : carRecordList) {
                try{
                    ValidatorUtils.validateEntity(carRecord);
                    if(StringUtils.isEmpty(carRecord.getCarPlate())){
                        throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(), "???????????????????????????");
                    }
                    if(!carRecord.getCarPlate().matches(BusinessConst.REGEX_OF_CAR) && !carRecord.getCarPlate().matches(BusinessConst.REGEX_OF_NEW_ENERGY_CAR)){
                        throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(), "??????????????????????????????");
                    }
                }catch (JSYException e){
                    throw new ProprietorException(e.getCode(),e.getMessage());
                }
                if(carPlateList.contains(carRecord.getCarPlate())){
                    throw new ProprietorException("???????????????????????????????????????????????????");
                }else{
                    carPlateList.add(carRecord.getCarPlate());
                }
                carRecord.setVisitorId(visitorId);
                carRecord.setId(SnowFlake.nextId());
            }
            addCarBatch(carRecordList);
        }*/
        // ================================== ????????????????????????????????????end ======================================

        //0???????????? ?????????????????????0 ????????????????????????
//        if((visitorEntity.getIsCommunityAccess() != null && visitorEntity.getCommunityId() != 0)
//           || (visitorEntity.getIsBuildingAccess() != null && visitorEntity.getIsBuildingAccess() != 0)){
//           return getVisitorEntry(visitorEntity);// ??????????????????VO
//        }

        // ????????????????????????????????????
        log.info("?????????????????????{}", ProprietorTopicNameEntity.topicFaceXuServer);
        XUFaceVisitorEditPersonDTO xuFaceEditPersonDTO = new XUFaceVisitorEditPersonDTO();
        xuFaceEditPersonDTO.setOperator("addVisitor");
        xuFaceEditPersonDTO.setCommunityId(String.valueOf(visitorEntity.getCommunityId()));
        xuFaceEditPersonDTO.setVisitorEntity(visitorEntity);
        rabbitTemplate.convertAndSend(ProprietorTopicNameEntity.exFaceXu,ProprietorTopicNameEntity.topicFaceXuServer,JSON.toJSONString(xuFaceEditPersonDTO));

        VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
        visitorEntryVO.setId(visitorEntity.getId());
        return visitorEntryVO;
    }

//    /**
//     * @Description: ???????????????
//     * @Param: [jsonObject, hardwareType]
//     * @Return: void
//     * @Author: chq459799974
//     * @Date: 2021/4/25
//     **/
//    @Deprecated
//    @Override
//    public Map<String,Object> verifyQRCode(JSONObject jsonObject,Integer hardwareType){
//        Map<String, Object> returnMap = new HashMap<>();
//        returnMap.put("op","openDoor"); //?????????????????????????????????
//        Long visitorId = null; //????????????ID
//        String hardwareId = null; //??????ID
//        if(BusinessConst.HARDWARE_TYPE_XU_FACE.equals(hardwareType)){  //???????????????????????????
//            //?????????????????????????????????????????????id
//            visitorId = jsonObject.getJSONObject("info").getJSONObject("QRCodeInfo").getLong("visitorId");
//            hardwareId = jsonObject.getJSONObject("info").getString("facesluiceId");
//        }
//        //??????????????????
//        VisitorEntity visitorEntity = visitorMapper.selectById(visitorId);
//        //???????????????
//        if(visitorEntity == null){
//            returnMap.put("code","-1");
//            returnMap.put("msg","??????????????????????????????");
//            return returnMap;
//        }
//        //????????????
//        Long communityId = visitorEntity.getCommunityId();
////        Long hareWareCommunityId = communityHardWareMapper.queryCommunityIdByHardWareIdAndType(hardwareId, hardwareType);
//        List<Long> hareWareCommunityId = communityHardWareMapper.queryCommunityIdByHardWareIdAndType(hardwareId, hardwareType);//?????????
////        if(!communityId.equals(hareWareCommunityId)){
//        if(!hareWareCommunityId.contains(communityId)){ //?????????
//            returnMap.put("code","-1");
//            returnMap.put("msg","??????ID??????");
//            return returnMap;
//        }
//        //????????????
//        if(LocalDate.now().isBefore(visitorEntity.getStartTime())){
//            returnMap.put("code","-1");
//            returnMap.put("msg","??????????????????");
//            return returnMap;
//        }
//        if(visitorEntity.getEndTime() == null){ //???????????????
//            if(LocalDate.now().isAfter(visitorEntity.getStartTime())){ //???????????????????????????????????????????????????
//                returnMap.put("code","-1");
//                returnMap.put("msg","?????????????????????");
//                return returnMap;
//            }
//        }else if(LocalDate.now().isAfter(visitorEntity.getEndTime())){ //???????????????
//            returnMap.put("code","-1");
//            returnMap.put("msg","?????????????????????");
//            return returnMap;
//        }
//        returnMap.put("code","0");
//        returnMap.put("msg","?????????????????????");
//        returnMap.put("uid",visitorEntity.getUid());
//        returnMap.put("name",visitorEntity.getName());
//
//        //MQ????????????????????????????????????
//        VisitorHistoryEntity historyEntity = new VisitorHistoryEntity();
//        BeanUtils.copyProperties(visitorEntity,historyEntity);
//        historyEntity.setId(SnowFlake.nextId());
//        historyEntity.setVisitorId(visitorEntity.getUserId());
//        historyEntity.setAccessType(BusinessConst.ACCESS_TYPE_QRCODE);
//        pushVisitorRecord(historyEntity);
//        return returnMap;
//    }

//    //??????????????????
//    private void pushVisitorRecord(VisitorHistoryEntity historyEntity){
//        rabbitTemplate.convertAndSend(RabbitMQCommonConfig.EX_PROPERTY, RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD,historyEntity);
//    }

//    /**
//    * @Description: ??????????????????
//     * @Param: [visitorEntity]
//     * @Return: com.jsy.community.vo.VisitorEntryVO
//     * @Author: chq459799974
//     * @Date: 2020/12/11
//    **/
//    private VisitorEntryVO getVisitorEntry(VisitorEntity visitorEntity){
//        VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
//        visitorEntryVO.setPassword(MyMathUtils.randomCode(7));
//        visitorEntryVO.setIsCommunityAccess(visitorEntity.getIsCommunityAccess());
//        visitorEntryVO.setIsBuildingAccess(visitorEntity.getIsBuildingAccess());
//        visitorEntryVO.setUid(visitorEntity.getUid());
//        String token = UUID.randomUUID().toString().replace("-","");
//        visitorEntryVO.setToken(token);
//        visitorEntryVO.setTimeLimit(visitorTimeLimit);
//        //??????????????????
//        if(visitorEntryVO.getIsCommunityAccess() != null && visitorEntryVO.getIsCommunityAccess() != 0){
//            redisTemplate.opsForValue().set("CEntry:" + token, JSON.toJSONString(visitorEntryVO), visitorTimeLimit, TimeUnit.MINUTES);
//        }
//        //??????????????????
//        if(visitorEntryVO.getIsBuildingAccess() != null && visitorEntryVO.getIsBuildingAccess() != 0){
//            redisTemplate.opsForValue().set("BEntry:" + token, JSON.toJSONString(visitorEntryVO), visitorTimeLimit, TimeUnit.MINUTES);
//        }
//        return visitorEntryVO;
//    }

//    /**
//     * @Description: ??????????????????
//     * @Param: [token, type]
//     * @Return: void
//     * @Author: chq459799974
//     * @Date: 2020/12/11
//     **/
//    //TODO ????????????????????????????????????
//    public boolean verifyEntry(String token,Integer type){
//        if(BusinessEnum.EntryTypeEnum.COMMUNITY.getCode().equals(type)){ //??????????????????
//            String cEntry = redisTemplate.opsForValue().get("CEntry:" + token);
//            if(StringUtils.isEmpty(cEntry)){
//                return false;
//            }
//            VisitorEntryVO visitor = JSONObject.parseObject(cEntry, VisitorEntryVO.class);
//            switch (String.valueOf(visitor.getIsCommunityAccess())){
//                case BusinessConst.ACCESS_COMMUNITY_QR_CODE :
//                    //TODO ???????????????
//                    return true;
//                case BusinessConst.ACCESS_COMMUNITY_FACE :
//                    //TODO ??????
//                    return true;
//                default:
//                    return false;
//            }
//        }else if(BusinessEnum.EntryTypeEnum.BUILDING.getCode().equals(type)){ //??????????????????
//            String bEntry = redisTemplate.opsForValue().get("BEntry:" + token);
//            if(StringUtils.isEmpty(bEntry)){
//                return false;
//            }
//            VisitorEntryVO visitor = JSONObject.parseObject(bEntry, VisitorEntryVO.class);
//            switch (String.valueOf(visitor.getIsBuildingAccess())){
//                case BusinessConst.ACCESS_BUILDING_QR_CODE:
//                    //TODO ???????????????
//                    return true;
//                case BusinessConst.ACCESS_BUILDING_COMMUNICATION:
//                    //TODO ????????????
//                    return true;
//                default:
//                    return false;
//            }
//        }
//        return false;
//    }

    /**
     * @Description: ????????????????????????
     * @Param: [personRecordList]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/10
     **/
    private void addPersonBatch(List<VisitorPersonRecordEntity> personRecordList){
        int result = visitorPersonRecordMapper.addPersonBatch(personRecordList);
        if(result != personRecordList.size()){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"????????????????????????");
        }
    }

    /**
     * @Description: ????????????????????????
     * @Param: [carList]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/10
     **/
    private void addCarBatch(List<VisitingCarRecordEntity> carRecordList){
        int result = visitingCarRecordMapper.addCarBatch(carRecordList);
        if(result != carRecordList.size()){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"????????????????????????");
        }
    }

	/**
	 * @Description: ??????ID ????????????????????????
	 * @Param: [id]
	 * @Return: boolean
	 * @Author: chq459799974
	 * @Date: 2020/11/16
	 **/
    @Transactional(rollbackFor = Exception.class)
	@Override
    public boolean deleteVisitorById(Long id){
	    int result = visitorMapper.deleteById(id);
	    if(result == 1){
            deletePersonAndCar(id);
	    	return true;
	    }
	    return false;
    }

    /**
     * @Description: ???????????? ??????????????????(???????????????????????????????????????)
     * @Param: [visitorId]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    private void deletePersonAndCar(Long visitorId){
        Map<String, Object> opMap = new HashMap<>();
        opMap.put("visitor_id",visitorId);
        visitorPersonRecordMapper.deleteByMap(opMap);
        visitingCarRecordMapper.deleteByMap(opMap);
    }

    /**
     * @Description: ????????????
     * @Param: [baseQO, uid]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitorEntity>
     * @Author: chq459799974
     * @Date: 2020/12/16
     **/
    @Override
    public PageInfo<VisitorEntity> queryByPage(BaseQO<VisitorQO> baseQO, String uid){
        Page<VisitorEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO);
        QueryWrapper<VisitorEntity> queryWrapper = new QueryWrapper<VisitorEntity>().select("*").eq("uid",uid);
        VisitorQO visitorQO = baseQO.getQuery();
        if(visitorQO != null){
            if(!StringUtils.isEmpty(visitorQO.getName())){
                queryWrapper.eq("name",visitorQO.getName());
            }
            if(!StringUtils.isEmpty(visitorQO.getContact())){
                queryWrapper.eq("contact",visitorQO.getContact());
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<VisitorEntity> resultPage = visitorMapper.selectPage(page, queryWrapper);
        PageInfo<VisitorEntity> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(resultPage,pageInfo);
        return pageInfo;
    }

    /**
    * @Description: ??????ID????????????
     * @Param: [id]
     * @Return: com.jsy.community.entity.VisitorEntity
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public VisitorEntity selectOneById(Long id){
        VisitorEntity visitorEntity = visitorMapper.selectOne(new QueryWrapper<VisitorEntity>().select("*").eq("id", id));
        if (visitorEntity == null) {
            return visitorEntity;
        }
        visitorEntity.setReasonStr(BusinessEnum.VisitReasonEnum.visitReasonMap.get(visitorEntity.getReason()));
        visitorEntity.setCarTypeStr(BusinessEnum.CarTypeEnum.CAR_TYPE_MAP.get(visitorEntity.getCarType()));
        visitorEntity.setIsCommunityAccessStr(BusinessEnum.CommunityAccessEnum.communityAccessMap.get(visitorEntity.getIsCommunityAccess()));
        visitorEntity.setIsCarBanAccessStr(BusinessEnum.BuildingAccessEnum.buildingAccessMap.get(visitorEntity.getIsCarBanAccess()));
        if (visitorEntity.getEndTime() != null && visitorEntity.getEndTime().isBefore(LocalDateTime.now())) {
            visitorEntity.setExpireStatus(1);
        } else {
            visitorEntity.setExpireStatus(0);
        }
        return visitorEntity;
    }
    
    /**
     * @Description: ??????ID????????????????????????
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitorPersonRecordEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @Override
    public List<VisitorPersonRecordEntity> queryPersonRecordList(Long visitorid){
        return visitorPersonRecordMapper.selectList(new QueryWrapper<VisitorPersonRecordEntity>()
            .select("id,name,mobile")
            .eq("visitor_id",visitorid)
        );
    }
    
    /**
     * @Description: ??????ID????????????????????????
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitingCarRecordEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @Override
    public List<VisitingCarRecordEntity> queryCarRecordList(Long visitorid){
        List<VisitingCarRecordEntity> entityList = visitingCarRecordMapper.selectList(new QueryWrapper<VisitingCarRecordEntity>()
            .select("id,car_plate,car_type")
            .eq("visitor_id", visitorid)
        );
        for (VisitingCarRecordEntity entity : entityList) {
            entity.setCarTypeStr(BusinessEnum.CarTypeEnum.CAR_TYPE_MAP.get(entity.getCarType()));
        }
        return entityList;
    }
}
