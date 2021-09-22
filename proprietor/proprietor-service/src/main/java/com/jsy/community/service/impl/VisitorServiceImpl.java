package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.config.TopicExConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.*;
import com.jsy.community.qo.proprietor.VisitorQO;
import com.jsy.community.vo.VisitorEntryVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 来访人员 服务实现类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
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
    
    @Autowired
    private CommunityHardWareMapper communityHardWareMapper;
    
//    @Value("${}")
    private long visitorTimeLimit = 60*24*60; //单位 秒

    /**
     * @Description: 访客登记 新增V2
     * @Param: [visitorEntity]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @Override
    public VisitorEntryVO addVisitorV2(VisitorEntity visitorEntity) {
        long visitorId = SnowFlake.nextId();
        visitorEntity.setId(visitorId);
        int insert = visitorMapper.insert(visitorEntity);

        //把访客登记数据推送给小区
        rabbitTemplate.convertAndSend(TopicExConfig.EX_TOPIC_VISITOR_TO_COMMUNITY,TopicExConfig.QUEUE_VISITOR_TO_COMMUNITY + "." + visitorEntity.getCommunityId(),JSON.toJSONString(visitorEntity));

        //社区二维码权限，需要生成二维码 (演示版本，只有一个机器，分小区没分机器)
        if(BusinessEnum.CommunityAccessEnum.QR_CODE.getCode().equals(visitorEntity.getIsCommunityAccess())){
            //小区是否有二维码设备(目前只用炫优一体机判断)
            Integer count = communityHardWareMapper.countCommunityHardWare(visitorEntity.getCommunityId(), BusinessConst.HARDWARE_TYPE_XU_FACE);
            if(count < 1){
                return null;
            }
            VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
            visitorEntryVO.setId(visitorEntity.getId());
            return visitorEntryVO;
        }
        return null;
    }

    /**
     * @Description: 查询一条详情
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
    * @Description: 访客登记 新增
     * @Param: [visitorEntity]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public VisitorEntryVO addVisitor(VisitorEntity visitorEntity){
        long visitorId = SnowFlake.nextId();
        visitorEntity.setId(visitorId);
        int insert = visitorMapper.insert(visitorEntity);
        if(1 != insert){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"访客登记 新增失败");
        }
        //添加随行人员记录
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
        //添加随行车辆记录
        List<VisitingCarRecordEntity> carRecordList = visitorEntity.getVisitingCarRecordList();
        //检查一次来访中的车牌号重复
        List<String> carPlateList = new ArrayList<>();
        if(!StringUtils.isEmpty(visitorEntity.getCarPlate())){
            carPlateList.add(visitorEntity.getCarPlate());
        }
        if (!CollectionUtils.isEmpty(carRecordList)) {
            for (VisitingCarRecordEntity carRecord : carRecordList) {
                try{
                    ValidatorUtils.validateEntity(carRecord);
                    if(StringUtils.isEmpty(carRecord.getCarPlate())){
                        throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(), "随行车辆缺少车牌号");
                    }
                    if(!carRecord.getCarPlate().matches(BusinessConst.REGEX_OF_CAR) && !carRecord.getCarPlate().matches(BusinessConst.REGEX_OF_NEW_ENERGY_CAR)){
                        throw new ProprietorException(JSYError.REQUEST_PARAM.getCode(), "随行车辆车牌号不合法");
                    }
                }catch (JSYException e){
                    throw new ProprietorException(e.getCode(),e.getMessage());
                }
                if(carPlateList.contains(carRecord.getCarPlate())){
                    throw new ProprietorException("一次访问中不允许有两个以上相同车牌");
                }else{
                    carPlateList.add(carRecord.getCarPlate());
                }
                carRecord.setVisitorId(visitorId);
                carRecord.setId(SnowFlake.nextId());
            }
            addCarBatch(carRecordList);
        }
        //0是无权限 小区和楼栋都是0 则不需要后续操作
//        if((visitorEntity.getIsCommunityAccess() != null && visitorEntity.getCommunityId() != 0)
//           || (visitorEntity.getIsBuildingAccess() != null && visitorEntity.getIsBuildingAccess() != 0)){
//           return getVisitorEntry(visitorEntity);// 返回门禁权限VO
//        }
        
        //把访客登记数据推送给小区
        rabbitTemplate.convertAndSend(TopicExConfig.EX_TOPIC_VISITOR_TO_COMMUNITY,TopicExConfig.QUEUE_VISITOR_TO_COMMUNITY + "." + visitorEntity.getCommunityId(),JSON.toJSONString(visitorEntity));
        
        //社区二维码权限，需要生成二维码 (演示版本，只有一个机器，分小区没分机器)
        if(BusinessEnum.CommunityAccessEnum.QR_CODE.getCode().equals(visitorEntity.getIsCommunityAccess())
            || BusinessEnum.BuildingAccessEnum.QR_CODE.getCode().equals(visitorEntity.getIsCarBanAccess())){
            //小区是否有二维码设备(目前只用炫优一体机判断)
            Integer count = communityHardWareMapper.countCommunityHardWare(visitorEntity.getCommunityId(), BusinessConst.HARDWARE_TYPE_XU_FACE);
            if(count < 1){
                return null;
            }
            VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
            visitorEntryVO.setId(visitorEntity.getId());
            return visitorEntryVO;
        }
        return null;
    }
    
//    /**
//     * @Description: 验证二维码
//     * @Param: [jsonObject, hardwareType]
//     * @Return: void
//     * @Author: chq459799974
//     * @Date: 2021/4/25
//     **/
//    @Deprecated
//    @Override
//    public Map<String,Object> verifyQRCode(JSONObject jsonObject,Integer hardwareType){
//        Map<String, Object> returnMap = new HashMap<>();
//        returnMap.put("op","openDoor"); //二维码操作目前只有开门
//        Long visitorId = null; //访客登记ID
//        String hardwareId = null; //硬件ID
//        if(BusinessConst.HARDWARE_TYPE_XU_FACE.equals(hardwareType)){  //炫优人脸识别一体机
//            //机器传过来的二维码中的访客登记id
//            visitorId = jsonObject.getJSONObject("info").getJSONObject("QRCodeInfo").getLong("visitorId");
//            hardwareId = jsonObject.getJSONObject("info").getString("facesluiceId");
//        }
//        //查询访客记录
//        VisitorEntity visitorEntity = visitorMapper.selectById(visitorId);
//        //无访客记录
//        if(visitorEntity == null){
//            returnMap.put("code","-1");
//            returnMap.put("msg","访客登记记录查找失败");
//            return returnMap;
//        }
//        //小区不对
//        Long communityId = visitorEntity.getCommunityId();
////        Long hareWareCommunityId = communityHardWareMapper.queryCommunityIdByHardWareIdAndType(hardwareId, hardwareType);
//        List<Long> hareWareCommunityId = communityHardWareMapper.queryCommunityIdByHardWareIdAndType(hardwareId, hardwareType);//测试用
////        if(!communityId.equals(hareWareCommunityId)){
//        if(!hareWareCommunityId.contains(communityId)){ //测试用
//            returnMap.put("code","-1");
//            returnMap.put("msg","社区ID错误");
//            return returnMap;
//        }
//        //来访过早
//        if(LocalDate.now().isBefore(visitorEntity.getStartTime())){
//            returnMap.put("code","-1");
//            returnMap.put("msg","未到访问时间");
//            return returnMap;
//        }
//        if(visitorEntity.getEndTime() == null){ //无结束时间
//            if(LocalDate.now().isAfter(visitorEntity.getStartTime())){ //暂定二维码在访问开始时间内一天有效
//                returnMap.put("code","-1");
//                returnMap.put("msg","已超出访问时间");
//                return returnMap;
//            }
//        }else if(LocalDate.now().isAfter(visitorEntity.getEndTime())){ //有结束时间
//            returnMap.put("code","-1");
//            returnMap.put("msg","已超出访问时间");
//            return returnMap;
//        }
//        returnMap.put("code","0");
//        returnMap.put("msg","二维码验证通过");
//        returnMap.put("uid",visitorEntity.getUid());
//        returnMap.put("name",visitorEntity.getName());
//
//        //MQ向物业端新增一条访客记录
//        VisitorHistoryEntity historyEntity = new VisitorHistoryEntity();
//        BeanUtils.copyProperties(visitorEntity,historyEntity);
//        historyEntity.setId(SnowFlake.nextId());
//        historyEntity.setVisitorId(visitorEntity.getId());
//        historyEntity.setAccessType(BusinessConst.ACCESS_TYPE_QRCODE);
//        pushVisitorRecord(historyEntity);
//        return returnMap;
//    }

//    //推送访客记录
//    private void pushVisitorRecord(VisitorHistoryEntity historyEntity){
//        rabbitTemplate.convertAndSend(RabbitMQCommonConfig.EX_PROPERTY, RabbitMQCommonConfig.TOPIC_PROPERTY_VISITOR_RECORD,historyEntity);
//    }

//    /**
//    * @Description: 设置门禁权限
//     * @Param: [visitorEntity]
//     * @Return: com.jsy.community.vo.VisitorEntryVO
//     * @Author: chq459799974
//     * @Date: 2020/12/11
//    **/
//    private VisitorEntryVO getVisitorEntry(VisitorEntity visitorEntity){
//        VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
//        visitorEntryVO.setPassword(MyMathUtils.randomCode(7)); //TODO 密码保存与验证方案待定
//        visitorEntryVO.setIsCommunityAccess(visitorEntity.getIsCommunityAccess());
//        visitorEntryVO.setIsBuildingAccess(visitorEntity.getIsBuildingAccess());
//        visitorEntryVO.setUid(visitorEntity.getUid());
//        String token = UUID.randomUUID().toString().replace("-","");
//        visitorEntryVO.setToken(token);
//        visitorEntryVO.setTimeLimit(visitorTimeLimit);
//        //小区门禁权限
//        if(visitorEntryVO.getIsCommunityAccess() != null && visitorEntryVO.getIsCommunityAccess() != 0){
//            redisTemplate.opsForValue().set("CEntry:" + token, JSON.toJSONString(visitorEntryVO), visitorTimeLimit, TimeUnit.MINUTES);
//        }
//        //楼栋门禁权限
//        if(visitorEntryVO.getIsBuildingAccess() != null && visitorEntryVO.getIsBuildingAccess() != 0){
//            redisTemplate.opsForValue().set("BEntry:" + token, JSON.toJSONString(visitorEntryVO), visitorTimeLimit, TimeUnit.MINUTES);
//        }
//        return visitorEntryVO;
//    }
    
//    /**
//     * @Description: 访客门禁验证
//     * @Param: [token, type]
//     * @Return: void
//     * @Author: chq459799974
//     * @Date: 2020/12/11
//     **/
//    //TODO 后续流程未知，返回值未定
//    public boolean verifyEntry(String token,Integer type){
//        if(BusinessEnum.EntryTypeEnum.COMMUNITY.getCode().equals(type)){ //小区门禁验证
//            String cEntry = redisTemplate.opsForValue().get("CEntry:" + token);
//            if(StringUtils.isEmpty(cEntry)){
//                return false;
//            }
//            VisitorEntryVO visitor = JSONObject.parseObject(cEntry, VisitorEntryVO.class);
//            switch (String.valueOf(visitor.getIsCommunityAccess())){
//                case BusinessConst.ACCESS_COMMUNITY_QR_CODE :
//                    //TODO 二维码类型
//                    return true;
//                case BusinessConst.ACCESS_COMMUNITY_FACE :
//                    //TODO 人脸
//                    return true;
//                default:
//                    return false;
//            }
//        }else if(BusinessEnum.EntryTypeEnum.BUILDING.getCode().equals(type)){ //楼栋门禁验证
//            String bEntry = redisTemplate.opsForValue().get("BEntry:" + token);
//            if(StringUtils.isEmpty(bEntry)){
//                return false;
//            }
//            VisitorEntryVO visitor = JSONObject.parseObject(bEntry, VisitorEntryVO.class);
//            switch (String.valueOf(visitor.getIsBuildingAccess())){
//                case BusinessConst.ACCESS_BUILDING_QR_CODE:
//                    //TODO 二维码类型
//                    return true;
//                case BusinessConst.ACCESS_BUILDING_COMMUNICATION:
//                    //TODO 可视对讲
//                    return true;
//                default:
//                    return false;
//            }
//        }
//        return false;
//    }
    
    /**
     * @Description: 批量添加随行人员
     * @Param: [personRecordList]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/10
     **/
    private void addPersonBatch(List<VisitorPersonRecordEntity> personRecordList){
        int result = visitorPersonRecordMapper.addPersonBatch(personRecordList);
        if(result != personRecordList.size()){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"添加随行人员失败");
        }
    }
    
    /**
     * @Description: 批量添加随行车辆
     * @Param: [carList]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/10
     **/
    private void addCarBatch(List<VisitingCarRecordEntity> carRecordList){
        int result = visitingCarRecordMapper.addCarBatch(carRecordList);
        if(result != carRecordList.size()){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"添加随行车辆失败");
        }
    }
    
	/**
	 * @Description: 根据ID 删除访客登记申请
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
     * @Description: 关联删除 访客关联数据(随行人员记录、随行车辆记录)
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
     * @Description: 分页查询
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
    * @Description: 根据ID单查访客
     * @Param: [id]
     * @Return: com.jsy.community.entity.VisitorEntity
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    @Override
    public VisitorEntity selectOneById(Long id){
        VisitorEntity VisitorEntity = visitorMapper.selectOne(new QueryWrapper<VisitorEntity>().select("*").eq("id", id));
        VisitorEntity.setReasonStr(BusinessEnum.VisitReasonEnum.visitReasonMap.get(VisitorEntity.getReason()));
        VisitorEntity.setCarTypeStr(BusinessEnum.CarTypeEnum.CAR_TYPE_MAP.get(VisitorEntity.getCarType()));
        VisitorEntity.setIsCommunityAccessStr(BusinessEnum.CommunityAccessEnum.communityAccessMap.get(VisitorEntity.getIsCommunityAccess()));
        VisitorEntity.setIsCarBanAccessStr(BusinessEnum.BuildingAccessEnum.buildingAccessMap.get(VisitorEntity.getIsCarBanAccess()));
        return VisitorEntity;
    }
    
    /**
     * @Description: 根据ID单查随行人员记录
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
     * @Description: 根据ID单查随行车辆记录
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
