package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IVisitorService;
import com.jsy.community.api.ProprietorException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.*;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.MyPageUtils;
import com.jsy.community.qo.proprietor.VisitorQO;
import com.jsy.community.vo.VisitorEntryVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    
//    @Value("${}")
    private Integer visitorTimeLimit = 60*24; //分
    
    /**
    * @Description: 访客登记 新增
     * @Param: [visitorEntity]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/11/12
    **/
    @Override
    public VisitorEntryVO addVisitor(VisitorEntity visitorEntity){
        int insert = visitorMapper.insert(visitorEntity);
        if(1 != insert){
            throw new ProprietorException(JSYError.INTERNAL.getCode(),"访客登记 新增失败");
        }
        //0是无权限 小区和楼栋都是0 则不需要后续操作
        if((visitorEntity.getIsCommunityAccess() != null && visitorEntity.getCommunityId() != 0)
           || (visitorEntity.getIsBuildingAccess() != null && visitorEntity.getIsBuildingAccess() != 0)){
           return getVisitorEntry(visitorEntity);// 返回门禁权限VO
        }
        return null;
    }
    
    /**
    * @Description: 设置门禁权限
     * @Param: [visitorEntity]
     * @Return: com.jsy.community.vo.VisitorEntryVO
     * @Author: chq459799974
     * @Date: 2020/12/11
    **/
    private VisitorEntryVO getVisitorEntry(VisitorEntity visitorEntity){
        VisitorEntryVO visitorEntryVO = new VisitorEntryVO();
        visitorEntryVO.setPassword(MyMathUtils.randomCode(7));
        visitorEntryVO.setIsCommunityAccess(visitorEntity.getIsCommunityAccess());
        visitorEntryVO.setIsBuildingAccess(visitorEntity.getIsBuildingAccess());
        String token = UUID.randomUUID().toString().replace("-","");
        visitorEntryVO.setToken(token);
        visitorEntryVO.setTimeLimit(visitorTimeLimit);
        //小区门禁权限
        if(visitorEntryVO.getIsCommunityAccess() != null && visitorEntryVO.getIsCommunityAccess() != 0){
            redisTemplate.opsForValue().set("CEntry:" + token, String.valueOf(visitorEntryVO.getIsCommunityAccess()), visitorTimeLimit, TimeUnit.MINUTES);
        }
        //楼栋门禁权限
        if(visitorEntryVO.getIsBuildingAccess() != null && visitorEntryVO.getIsBuildingAccess() != 0){
            redisTemplate.opsForValue().set("BEntry:" + token, String.valueOf(visitorEntryVO.getIsBuildingAccess()), visitorTimeLimit, TimeUnit.MINUTES);
        }
        return visitorEntryVO;
    }
    
    /**
     * @Description: 访客门禁验证
     * @Param: [token, type]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/11
     **/
    //TODO 后续流程未知，返回值未定
    public void verifyEntry(String token,Integer type){
        if(BusinessEnum.EntryTypeEnum.COMMUNITY.getCode().equals(type)){ //小区门禁认证
            String cEntry = redisTemplate.opsForValue().get("CEntry:" + token);
            switch (cEntry){
                case BusinessConst.ACCESS_COMMUNITY_QR_CODE :
                    //TODO 二维码类型
                    break;
                case BusinessConst.ACCESS_COMMUNITY_FACE :
                    //TODO 人脸
                    break;
            }
        }else if(BusinessEnum.EntryTypeEnum.BUILDING.getCode().equals(type)){ //楼栋门禁验证
            String bEntry = redisTemplate.opsForValue().get("BEntry:" + token);
            switch (bEntry){
                case BusinessConst.ACCESS_BUILDING_QR_CODE:
                    //TODO 二维码类型
                    break;
                case BusinessConst.ACCESS_BUILDING_COMMUNICATION:
                    //TODO 可视对讲
                    break;
            }
        }
    }
    
    /**
     * @Description: 批量添加随行人员
     * @Param: [personRecordList]
     * @Return: void
     * @Author: chq459799974
     * @Date: 2020/12/10
     **/
    @Override
    public void addPersonBatch(List<VisitorPersonRecordEntity> personRecordList){
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
    @Override
    public void addCarBatch(List<VisitingCarRecordEntity> carRecordList){
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
	@Override
    public boolean deleteVisitorById(Long id){
	    int result = visitorMapper.deleteById(id);
	    if(result == 1){
	    	return true;
	    }
	    return false;
    }
    
    /**
     * @Description: 关联删除 访客关联数据(随行人员、随行车辆)
     * @Param: [visitorId]
     * @Return: int
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    @Override
    public void deletePersonAndCar(Long visitorId){
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
    public Page<VisitorEntity> queryByPage(BaseQO<VisitorQO> baseQO,String uid){
        Page<VisitorEntity> page = new Page<>();
        MyPageUtils.setPageAndSize(page, baseQO); //设置分页参数
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
        return visitorMapper.selectPage(page, queryWrapper);
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
        VisitorEntity.setCarTypeStr(BusinessEnum.CarTypeEnum.carTypeMap.get(VisitorEntity.getCarType()));
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
            entity.setCarTypeStr(BusinessEnum.CarTypeEnum.carTypeMap.get(entity.getCarType()));
        }
        return entityList;
    }
}
