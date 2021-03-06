package com.jsy.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.*;
import com.jsy.community.config.LeaseTopicExConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.constant.ConstClasses;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.CompanyPayConfigEntity;
import com.jsy.community.entity.PayConfigureEntity;
import com.jsy.community.entity.PropertyCompanyEntity;
import com.jsy.community.entity.lease.AiliAppPayRecordEntity;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.entity.payment.WeChatOrderEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.entity.proprietor.LeaseOperationRecordEntity;
import com.jsy.community.entity.shop.ShopImgEntity;
import com.jsy.community.entity.shop.ShopLeaseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.*;
import com.jsy.community.untils.wechat.PublicConfig;
import com.jsy.community.untils.wechat.WechatConfig;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.*;
import com.jsy.community.utils.signature.ZhsjUtil;
import com.jsy.community.vo.UserInfoVo;
import com.jsy.community.vo.lease.HouseLeaseContractVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.domain.PayCallNotice;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.entity.TransferEntity;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBasePayRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.basecommon.constant.BaseConstant;
import com.zhsj.basecommon.utils.MD5Util;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
import com.zhsj.sign.api.rpc.IContractRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.client.methods.HttpPost;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Pipi
 * @Description: ?????????????????????????????????
 * @Date: 2021/8/31 14:48
 * @Version: 1.0
 **/
@Slf4j
@DubboService(version = Const.version, group = Const.group_lease)
public class AssetLeaseRecordServiceImpl extends ServiceImpl<AssetLeaseRecordMapper, AssetLeaseRecordEntity> implements AssetLeaseRecordService {

    @Value("${sign.user.protocol}")
    private String SIGN_USER_PROTOCOL;
    @Value("${sign.user.host}")
    private String SIGN_USER_HOST;
    @Value("${sign.user.port}")
    private String SIGN_USER_PORT;
    @Value("${sign.user.api.contract_overdue}")
    private String CONTRACT_OVERDUE;
    @Value("${sign.user.api.update-contract-pay-status}")
    private String MODIFY_ORDER_PAY_STATUS;

    @Autowired
    private AssetLeaseRecordMapper assetLeaseRecordMapper;

    @Autowired
    private ShopLeaseMapper shopLeaseMapper;

    @Autowired
    private HouseLeaseMapper houseLeaseMapper;

    @Autowired
    private ShopImgMapper shopImgMapper;

    @Autowired
    private LeaseOperationRecordMapper leaseOperationRecordMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference(version = Const.version,  group = Const.group_property, check = false)
    private IPropertyCompanyService propertyCompanyService;


    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ProprietorUserService userService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICommunityService communityService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IHouseService houseService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IHouseMemberService houseMemberService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IUserImService userImService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private AiliAppPayRecordService ailiAppPayRecordService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private IWeChatService weChatService;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICompanyPayConfigService companyPayConfigService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService userInfoRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBasePayRpcService basePayRpcService;

    @DubboReference(version = com.zhsj.im.chat.api.constant.RpcConst.Rpc.VERSION, group = com.zhsj.im.chat.api.constant.RpcConst.Rpc.Group.GROUP_IM_CHAT, check=false)
    private IImChatPublicPushRpcService iImChatPublicPushRpcService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPayConfigureService payConfigureService;

    @DubboReference(version = Const.version, group = Const.group_payment, check = false)
    private HousingRentalOrderService housingRentalOrderService;

    @DubboReference(version = BaseConstant.Rpc.VERSION, group = BaseConstant.Rpc.Group.GROUP_CONTRACT)
    private IContractRpcService contractRpcService;

    /**
     * @param assetLeaseRecordEntity : ???????????????????????????
     * @author: Pipi
     * @description: ????????????????????????
     * @return: java.lang.Integer
     * @date: 2021/8/31 16:01
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addLeaseRecord(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        // ??????????????????????????????
        Integer integer = userService.userIsRealAuth(assetLeaseRecordEntity.getTenantUid());
        if (integer != 2) {
            throw new LeaseException(JSYError.NO_REAL_NAME_AUTH);
        }
        // ??????????????????????????????(??????????????????????????????????????????????????????)
        QueryWrapper<AssetLeaseRecordEntity> assetLeaseRecordEntityQueryWrapper = new QueryWrapper<>();
        assetLeaseRecordEntityQueryWrapper.eq("asset_id", assetLeaseRecordEntity.getAssetId());
        assetLeaseRecordEntityQueryWrapper.eq("tenant_uid", assetLeaseRecordEntity.getTenantUid());
        assetLeaseRecordEntityQueryWrapper.and(
                wapper -> wapper.ne("operation", BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode())
                        .or(newwapper ->
                                newwapper.eq("operation", BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode())
                                        .gt("end_date", new Date())
                        )
        );
        AssetLeaseRecordEntity RecordExistEntity = assetLeaseRecordMapper.selectOne(assetLeaseRecordEntityQueryWrapper);
        if (RecordExistEntity != null) {
            throw new LeaseException("????????????????????????,?????????????????????");
        }
        assetLeaseRecordEntity.setId(SnowFlake.nextId());
        assetLeaseRecordEntity.setDeleted(0L);
        assetLeaseRecordEntity.setReadMark(1);
        assetLeaseRecordEntity.setCreateTime(LocalDateTime.now());
        assetLeaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        // ??????????????????
        if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
            // ??????
            QueryWrapper<HouseLeaseEntity> leaseEntityQueryWrapper = new QueryWrapper<>();
            leaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
            HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(leaseEntityQueryWrapper);
            if (houseLeaseEntity == null) {
                throw new LeaseException("??????????????????!");
            }
            assetLeaseRecordEntity.setHomeOwnerUid(houseLeaseEntity.getUid());
            assetLeaseRecordEntity.setCommunityId(houseLeaseEntity.getHouseCommunityId());
        } else if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
            // ??????
            // ???????????????
            QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
            shopLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
            ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
            if (shopLeaseEntity == null) {
                throw new LeaseException("??????????????????!");
            }
            assetLeaseRecordEntity.setHomeOwnerUid(shopLeaseEntity.getUid());
            assetLeaseRecordEntity.setCommunityId(shopLeaseEntity.getCommunityId());
        } else {
            throw new LeaseException("??????????????????????????????;1:??????;2:??????");
        }
        //????????????????????????
        LeaseOperationRecordEntity leaseOperationRecordEntity = new LeaseOperationRecordEntity();
        leaseOperationRecordEntity.setAssetType(assetLeaseRecordEntity.getAssetType());
        leaseOperationRecordEntity.setAssetLeaseRecordId(assetLeaseRecordEntity.getId());
        leaseOperationRecordEntity.setOperation(assetLeaseRecordEntity.getOperation());
        leaseOperationRecordEntity.setId(SnowFlake.nextId());
        leaseOperationRecordEntity.setDeleted(0L);
        leaseOperationRecordEntity.setCreateTime(LocalDateTime.now());
        leaseOperationRecordMapper.insert(leaseOperationRecordEntity);
        // ???????????????
        JSONObject pushMap = new JSONObject();
        pushMap.put("id", assetLeaseRecordEntity.getId());
        pushMap.put("operation", assetLeaseRecordEntity.getOperation());
        pushMap.put("operationTime", LocalDateTime.now());
        rabbitTemplate.convertAndSend(LeaseTopicExConfig.DELAY_EX_TOPIC_TO_LEASE_CONTRACT,
                LeaseTopicExConfig.DELAY_QUEUE_TO_LEASE_CONTRACT,
                pushMap.toString(),
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        // ???????????????
                        message.getMessageProperties().setHeader("x-delay", BusinessConst.ONE_DAY * BusinessConst.COUNTDOWN_TO_CONTRACT_APPLY);
                        return message;
                    }
                });
        // ??????????????????
        assetLeaseRecordMapper.insert(assetLeaseRecordEntity);

        //????????????
        UserImVo userIm = userInfoRpcService.getEHomeUserIm(assetLeaseRecordEntity.getHomeOwnerUid());
        UserDetail userDetail = userInfoRpcService.getUserDetail(assetLeaseRecordEntity.getTenantUid());
        HashMap<Object, Object> map = new HashMap<>();
        map.put("type",6);
        map.put("dataId",null);
        PushInfoUtil.PushPublicTextMsg(iImChatPublicPushRpcService,userIm.getImId(),
                "????????????",
                userDetail.getNickName()+"??????????????????????????????",
                null,
                userDetail.getNickName()+"???????????????????????????????????????????????????????????????????????????7?????????????????????????????????????????????????????????????????????",
                map,
                BusinessEnum.PushInfromEnum.CONTRACTSIGNING.getName());
        return String.valueOf(assetLeaseRecordEntity.getId());
    }

    /**
     * @param assetLeaseRecordEntity : ???????????????????????????
     * @param uid                    : ????????????uid
     * @author: Pipi
     * @description: ?????????????????????(?????????????????? / ?????????????????? / ?????????????????? / ?????????????????? / ????????????)
     * @return: java.lang.Integer
     * @date: 2021/9/3 10:30
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer operationContract(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        Integer result = 0;
        switch (assetLeaseRecordEntity.getOperationType()) {
            case 2:
                // ??????????????????
                // ?????????????????????,??????????????????,?????????????????????????????????????????????
                result = acceptingApply(assetLeaseRecordEntity, uid);
                break;
            case 3:
                // ????????????????????????
                result = landlordContractPreparation(assetLeaseRecordEntity, uid);
                break;
            case 7:
                // ??????????????????
                result = cancelApply(assetLeaseRecordEntity, uid);
                break;
            case 8:
                // 8??????????????????
                result = rejectionApply(assetLeaseRecordEntity, uid);
                break;
            case 9:
                // ??????????????????
                result = reapply(assetLeaseRecordEntity, uid);
                break;
            default:
                throw new LeaseException("????????????!");
        }
        return result;
    }

    /**
     * @param assetLeaseRecordEntity: ???????????????????????????
     * @param uid:                    ????????????uid
     * @author: Pipi
     * @description: ??????????????????;?????????????????????,??????????????????,?????????????????????????????????????????????;
     * ??????:????????????????????????????????????,???????????????????????????
     * ??????:??????????????????????????????
     * ???????????????:1:??????????????????;9:????????????
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:17
     **/
    public Integer acceptingApply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("home_owner_uid", uid);
        ArrayList<Integer> processStatusList = new ArrayList<>();
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode());
        queryWrapper.in("operation", processStatusList);
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("?????????????????????");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.ACCEPTING_APPLICATIONS.getCode());
        // ??????????????????
        if (recordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
            // 2:??????
            // ??????????????????
            QueryWrapper<HouseLeaseEntity> leaseEntityQueryWrapper = new QueryWrapper<>();
            leaseEntityQueryWrapper.eq("id", recordEntity.getAssetId());
            HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(leaseEntityQueryWrapper);
            if (houseLeaseEntity == null) {
                throw new LeaseException("?????????????????????!");
            }
            if (houseLeaseEntity.getHouseLeasemodeId() == 2) {
                // ??????,??????????????????????????????
                checkMultipleApply(assetLeaseRecordEntity.getId(), recordEntity.getAssetId());
            }
            // ??????????????????url
            List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
            // ??????????????????
            recordEntity.setImageUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
            recordEntity.setTitle(houseLeaseEntity.getHouseTitle());
            recordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
            recordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
            recordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
            recordEntity.setPrice(houseLeaseEntity.getHousePrice());
            recordEntity.setProvinceId(houseLeaseEntity.getHouseProvinceId());
            recordEntity.setCityId(houseLeaseEntity.getHouseCityId());
            recordEntity.setAreaId(houseLeaseEntity.getHouseAreaId());
            recordEntity.setAddress(houseLeaseEntity.getHouseAddress());
            recordEntity.setFloor(houseLeaseEntity.getHouseFloor());
        } else {
            // 1:??????
            // ?????????????????????;??????????????????????????????
            checkMultipleApply(assetLeaseRecordEntity.getId(), recordEntity.getAssetId());
            // ??????????????????
            QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
            shopLeaseEntityQueryWrapper.eq("id", recordEntity.getAssetId());
            ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
            if (shopLeaseEntity == null) {
                throw new LeaseException("?????????????????????!");
            }
            // ???????????????
            QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
            shopImgEntityQueryWrapper.eq("shop_id", recordEntity.getAssetId());
            shopImgEntityQueryWrapper.last("limit 1");
            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
            // ??????????????????
            assetLeaseRecordEntity.setImageUrl(shopImgEntity != null ? shopImgEntity.getImgUrl() : null);
            assetLeaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
            assetLeaseRecordEntity.setAdvantageId(shopLeaseEntity.getShopFacility());
            assetLeaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
            assetLeaseRecordEntity.setSummarize(shopLeaseEntity.getSummarize());
            assetLeaseRecordEntity.setCityId(shopLeaseEntity.getCityId());
            assetLeaseRecordEntity.setAreaId(shopLeaseEntity.getAreaId());
            assetLeaseRecordEntity.setFloor(shopLeaseEntity.getFloor());
        }
        //????????????????????????
        addLeaseOperationRecord(recordEntity);
        // ???????????????
        JSONObject pushMap = new JSONObject();
        pushMap.put("id", recordEntity.getId());
        pushMap.put("operation", recordEntity.getOperation());
        pushMap.put("operationTime", LocalDateTime.now());
        rabbitTemplate.convertAndSend(LeaseTopicExConfig.DELAY_EX_TOPIC_TO_LEASE_CONTRACT,
                LeaseTopicExConfig.DELAY_QUEUE_TO_LEASE_CONTRACT,
                pushMap.toString(),
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        // ?????????7???
                        message.getMessageProperties().setHeader("x-delay", BusinessConst.ONE_DAY * BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT);
                        return message;
                    }
                });
        // ??????????????????
        recordEntity.setReadMark(0);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: ???????????????????????????
     * @param uid:                    ????????????uid
     * @author: Pipi
     * @description: ???????????????????????????(??????????????? : ???????????? : 2)
     * @return: java.lang.Integer
     * @date: 2021/9/9 18:13
     **/
    public Integer landlordContractPreparation(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("home_owner_uid", uid);
        queryWrapper.eq("operation", BusinessEnum.ContractingProcessStatusEnum.ACCEPTING_APPLICATIONS.getCode());
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("?????????????????????");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.CONTRACT_PREPARATION.getCode());
        //????????????????????????
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: ???????????????????????????
     * @param uid:                    ????????????uid
     * @author: Pipi
     * @description: ??????????????????(?????????????????? : ???????????? : 1 ; ???????????? : 9)
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:25
     **/
    public Integer cancelApply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("tenant_uid", uid);
        ArrayList<Integer> processStatusList = new ArrayList<>();
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode());
        queryWrapper.in("operation", processStatusList);
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("?????????????????????");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.CANCELLATION_REQUEST.getCode());
        //????????????????????????
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: ???????????????????????????
     * @param uid:                    ????????????uid
     * @author: Pipi
     * @description: ??????????????????(??????????????? : ???????????? : 1 ; ???????????? : 9)
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:29
     **/
    public Integer rejectionApply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("home_owner_uid", uid);
        ArrayList<Integer> processStatusList = new ArrayList<>();
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        processStatusList.add(BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode());
        queryWrapper.in("operation", processStatusList);
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("?????????????????????");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.REJECTION_OF_APPLICATION.getCode());
        //????????????????????????
        addLeaseOperationRecord(recordEntity);
        // ??????????????????
        recordEntity.setReadMark(0);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: ???????????????????????????
     * @param uid:                    ????????????uid
     * @author: Pipi
     * @description: ??????????????????;???????????????:8:????????????;7:????????????
     * @return: java.lang.Integer
     * @date: 2021/9/3 14:35
     **/
    public Integer reapply(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.eq("tenant_uid", uid);
        queryWrapper.in("operation", new ArrayList<>(
                Arrays.asList(BusinessEnum.ContractingProcessStatusEnum.REJECTION_OF_APPLICATION.getCode(),
                        BusinessEnum.ContractingProcessStatusEnum.CANCELLATION_REQUEST.getCode()
                )
        ));
        AssetLeaseRecordEntity recordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (recordEntity == null) {
            throw new LeaseException("?????????????????????");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode());
        //????????????????????????
        addLeaseOperationRecord(recordEntity);
        // ???????????????
        JSONObject pushMap = new JSONObject();
        pushMap.put("id", recordEntity.getId());
        pushMap.put("operation", recordEntity.getOperation());
        pushMap.put("operationTime", LocalDateTime.now());
        rabbitTemplate.convertAndSend(LeaseTopicExConfig.DELAY_EX_TOPIC_TO_LEASE_CONTRACT,
                LeaseTopicExConfig.DELAY_QUEUE_TO_LEASE_CONTRACT,
                pushMap.toString(),
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        // ?????????7???
                        message.getMessageProperties().setHeader("x-delay", BusinessConst.ONE_DAY * BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT);
                        return message;
                    }
                });
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param id:      ????????????ID
     * @param assetId: ??????id
     * @author: Pipi
     * @description: ???????????????????????????;
     * ????????????????????????:2:????????????;3:????????????;4:??????????????????;5:????????????;31:(??????)????????????/????????????;32:????????????;
     * @return: java.lang.Boolean
     * @date: 2021/9/4 14:20
     **/
    private void checkMultipleApply(Long id, Long assetId) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_id", assetId);
        queryWrapper.ne("id", id);
        List<Integer> processStatusList = new ArrayList<>(Arrays.asList(2, 3, 4, 5, 31, 32, 33));
        queryWrapper.in("operation", processStatusList);
        queryWrapper.last("limit 0,1");
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (assetLeaseRecordEntity != null) {
            throw new LeaseException("???????????????/???????????????????????????");
        }
    }

    /**
     * @param assetLeaseRecordEntity: ???????????????????????????
     * @author: Pipi
     * @description: ????????????????????????
     * @return: void
     * @date: 2021/9/3 14:22
     **/
    private void addLeaseOperationRecord(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        LeaseOperationRecordEntity leaseOperationRecordEntity = new LeaseOperationRecordEntity();
        leaseOperationRecordEntity.setAssetType(assetLeaseRecordEntity.getAssetType());
        leaseOperationRecordEntity.setAssetLeaseRecordId(assetLeaseRecordEntity.getId());
        leaseOperationRecordEntity.setOperation(assetLeaseRecordEntity.getOperation());
        leaseOperationRecordEntity.setId(SnowFlake.nextId());
        leaseOperationRecordEntity.setDeleted(0L);
        leaseOperationRecordEntity.setCreateTime(LocalDateTime.now());
        leaseOperationRecordMapper.insert(leaseOperationRecordEntity);
    }

    /**
     * @param assetLeaseRecordEntity : ????????????
     * @param uid                    : ????????????uid
     * @author: Pipi
     * @description: ????????????????????????
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/2 14:38
     **/
    @Override
    public Map<String, List<AssetLeaseRecordEntity>> pageContractList(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        HashMap<String, List<AssetLeaseRecordEntity>> hashMap = new HashMap<>();
        // ?????????
        Map<Long, AssetLeaseRecordEntity> contractedMap = new HashMap<>();
        // ?????????
        Map<Long, AssetLeaseRecordEntity> underContractMap = new HashMap<>();
        // ?????????
        Map<Long, AssetLeaseRecordEntity> notContractedMap = new HashMap<>();
        // ?????????
        Map<Long, AssetLeaseRecordEntity> expiredContractedMap = new HashMap<>();

        if (assetLeaseRecordEntity.getIdentityType() == 1) {
            // ??????
            assetLeaseRecordEntity.setHomeOwnerUid(uid);
            List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.queryList(assetLeaseRecordEntity);
            if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
                // ???????????????1,8,9,10???????????????
                Set<Long> houseIds = new HashSet<>();
                Set<Long> shopIds = new HashSet<>();
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9 || record.getOperation() == 10) {
                        if (StringUtils.isBlank(record.getTitle())) {
                            if (record.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
                                houseIds.add(record.getAssetId());
                            } else {
                                shopIds.add(record.getAssetId());
                            }
                        }
                    }
                }
                Map<Long, ShopLeaseEntity> shopEntityMap = new HashMap<>();
                Map<Long, HouseLeaseEntity> houseEntityMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(shopIds)) {
                    // ??????
                    QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                    shopLeaseEntityQueryWrapper.in("id", shopIds);
                    List<ShopLeaseEntity> shopLeaseEntities = shopLeaseMapper.selectList(shopLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(shopLeaseEntities)) {
                        for (ShopLeaseEntity shopLeaseEntity : shopLeaseEntities) {
                            // ???????????????
                            QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                            shopImgEntityQueryWrapper.eq("shop_id", shopLeaseEntity.getId());
                            shopImgEntityQueryWrapper.last("limit 1");
                            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                            shopLeaseEntity.setShopShowImg(shopImgEntity == null ? null : shopImgEntity.getImgUrl());
                            shopEntityMap.put(shopLeaseEntity.getId(), shopLeaseEntity);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(houseIds)) {
                    // ??????
                    QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                    houseLeaseEntityQueryWrapper.in("id", houseIds);
                    List<HouseLeaseEntity> houseLeaseEntities = houseLeaseMapper.selectList(houseLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(houseLeaseEntities)) {
                        for (HouseLeaseEntity houseLeaseEntity : houseLeaseEntities) {
                            // ??????????????????
                            List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                            houseLeaseEntity.setHouseImgUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                            houseEntityMap.put(houseLeaseEntity.getId(), houseLeaseEntity);
                        }
                    }
                }
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getAssetType() == 1 && shopEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // ??????
                        record.setImageUrl(shopEntityMap.get(record.getAssetId()).getShopShowImg());
                        record.setTitle(shopEntityMap.get(record.getAssetId()).getTitle());
                        record.setAdvantageId(shopEntityMap.get(record.getAssetId()).getShopFacility());
                        record.setPrice(shopEntityMap.get(record.getAssetId()).getMonthMoney());
                        record.setSummarize(shopEntityMap.get(record.getAssetId()).getSummarize());
                    } else if (record.getAssetType() == 2 && houseEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // ??????
                        record.setImageUrl(houseEntityMap.get(record.getAssetId()).getHouseImgUrl());
                        record.setTitle(houseEntityMap.get(record.getAssetId()).getHouseTitle());
                        record.setAdvantageId(houseEntityMap.get(record.getAssetId()).getHouseAdvantageId());
                        record.setTypeCode(houseEntityMap.get(record.getAssetId()).getHouseTypeCode());
                        record.setDirectionId(houseEntityMap.get(record.getAssetId()).getHouseDirectionId());
                        record.setPrice(houseEntityMap.get(record.getAssetId()).getHousePrice());
                        record.setHouseAdvantageCode(houseEntityMap.get(record.getAssetId()).getHouseAdvantageMap());
                        record.setHouseType(houseEntityMap.get(record.getAssetId()).getHouseTypeStr());
                    }
                    // ??????????????????
                    List<Long> advantageId = MyMathUtils.analysisTypeCode(record.getAdvantageId());
                    if (!CollectionUtils.isEmpty(advantageId)) {
                        record.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                    }
                    // ????????????
                    if (StringUtils.isNotBlank(record.getTypeCode())) {
                        record.setHouseType(HouseHelper.parseHouseType(record.getTypeCode()));
                    }
                    // ????????????
                    if (StringUtils.isNotBlank(record.getDirectionId())) {
                        record.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(record.getDirectionId())));
                    }
                }
                List<AssetLeaseRecordEntity> unexpired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() == 0).collect(Collectors.toList());
                List<AssetLeaseRecordEntity> expired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() != 0).collect(Collectors.toList());
                for (AssetLeaseRecordEntity record : unexpired) {
                    switch (record.getOperation()) {
                        case 1:
                            // ????????????->?????????
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                record.setContractNumber(1);
                                notContractedMap.put(record.getAssetId(), record);
                            } else {
                                // ???????????????1
                                notContractedMap.get(record.getAssetId()).setContractNumber(notContractedMap.get(record.getAssetId()).getContractNumber() + 1);
                            }
                            break;
                        case 2:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 3:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 4:
                            // ??????????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 5:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 6:
                            // ????????????->?????????
                            if (!contractedMap.containsKey(record.getAssetId())) {
                                contractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 7:
                            // ????????????->????????????????????????
                            break;
                        case 8:
                            // ????????????->????????????????????????
                            break;
                        case 9:
                            // ??????????????????->?????????
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                record.setContractNumber(1);
                                notContractedMap.put(record.getAssetId(), record);
                            } else {
                                // ???????????????1
                                notContractedMap.get(record.getAssetId()).setContractNumber(notContractedMap.get(record.getAssetId()).getContractNumber() + 1);
                            }
                            break;
                        case 31:
                            // (??????)????????????/????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 32:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        default:
                            break;
                    }
                }
                for (AssetLeaseRecordEntity entity : expired) {
                    if (!expiredContractedMap.containsKey(entity.getAssetId())) {
                        expiredContractedMap.put(entity.getAssetId(), entity);
                    }
                }
            }
        } else if (assetLeaseRecordEntity.getIdentityType() == 2) {
            // ??????
            assetLeaseRecordEntity.setTenantUid(uid);
            List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.queryList(assetLeaseRecordEntity);
            if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
                // ?????????????????????1,8,9,10???????????????
                List<Long> houseIds = new ArrayList<>();
                List<Long> shopIds = new ArrayList<>();
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9 || record.getOperation() == 10) {
                        if (StringUtils.isBlank(record.getTitle())) {
                            if (record.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
                                houseIds.add(record.getAssetId());
                            } else {
                                shopIds.add(record.getAssetId());
                            }
                        }
                    }
                }
                Map<Long, ShopLeaseEntity> shopEntityMap = new HashMap<>();
                Map<Long, HouseLeaseEntity> houseEntityMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(shopIds)) {
                    // ??????
                    QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                    shopLeaseEntityQueryWrapper.in("id", shopIds);
                    List<ShopLeaseEntity> shopLeaseEntities = shopLeaseMapper.selectList(shopLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(shopLeaseEntities)) {
                        for (ShopLeaseEntity shopLeaseEntity : shopLeaseEntities) {
                            // ???????????????
                            QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                            shopImgEntityQueryWrapper.eq("shop_id", shopLeaseEntity.getId());
                            shopImgEntityQueryWrapper.last("limit 1");
                            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                            shopLeaseEntity.setShopShowImg(shopImgEntity == null ? null : shopImgEntity.getImgUrl());
                            shopEntityMap.put(shopLeaseEntity.getId(), shopLeaseEntity);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(houseIds)) {
                    // ??????
                    QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                    houseLeaseEntityQueryWrapper.in("id", houseIds);
                    List<HouseLeaseEntity> houseLeaseEntities = houseLeaseMapper.selectList(houseLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(houseLeaseEntities)) {
                        for (HouseLeaseEntity houseLeaseEntity : houseLeaseEntities) {
                            // ??????????????????
                            List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                            houseLeaseEntity.setHouseImgUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                            houseEntityMap.put(houseLeaseEntity.getId(), houseLeaseEntity);
                        }
                    }
                }

                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getAssetType() == 1 && shopEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // ??????
                        record.setImageUrl(shopEntityMap.get(record.getAssetId()).getShopShowImg());
                        record.setTitle(shopEntityMap.get(record.getAssetId()).getTitle());
                        record.setAdvantageId(shopEntityMap.get(record.getAssetId()).getShopFacility());
                        record.setPrice(shopEntityMap.get(record.getAssetId()).getMonthMoney());
                        record.setSummarize(shopEntityMap.get(record.getAssetId()).getSummarize());
                    } else if (record.getAssetType() == 2 && houseEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // ??????
                        record.setImageUrl(houseEntityMap.get(record.getAssetId()).getHouseImgUrl());
                        record.setTitle(houseEntityMap.get(record.getAssetId()).getHouseTitle());
                        record.setAdvantageId(houseEntityMap.get(record.getAssetId()).getHouseAdvantageId());
                        record.setTypeCode(houseEntityMap.get(record.getAssetId()).getHouseTypeCode());
                        record.setDirectionId(houseEntityMap.get(record.getAssetId()).getHouseDirectionId());
                        record.setPrice(houseEntityMap.get(record.getAssetId()).getHousePrice());
                        record.setHouseAdvantageCode(houseEntityMap.get(record.getAssetId()).getHouseAdvantageMap());
                        record.setHouseType(houseEntityMap.get(record.getAssetId()).getHouseTypeStr());
                    }
                    // ??????????????????
                    List<Long> advantageId = MyMathUtils.analysisTypeCode(record.getAdvantageId());
                    if (!CollectionUtils.isEmpty(advantageId)) {
                        record.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                    }
                    // ????????????
                    if (StringUtils.isNotBlank(record.getTypeCode())) {
                        record.setHouseType(HouseHelper.parseHouseType(record.getTypeCode()));
                    }
                    // ????????????
                    if (StringUtils.isNotBlank(record.getDirectionId())) {
                        record.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(record.getDirectionId())));
                    }
                }
                List<AssetLeaseRecordEntity> unexpired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() == 0).collect(Collectors.toList());
                List<AssetLeaseRecordEntity> expired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() != 0).collect(Collectors.toList());
                for (AssetLeaseRecordEntity record : unexpired) {
                    switch (record.getOperation()) {
                        case 1:
                            // ????????????->?????????
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 2:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 3:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 4:
                            // ??????????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 5:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 6:
                            // ????????????->?????????
                            if (!contractedMap.containsKey(record.getAssetId())) {
                                contractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 7:
                            // ????????????->????????????????????????
                            break;
                        case 8:
                            // ????????????->?????????
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 9:
                            // ????????????->?????????
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 31:
                            // (??????)????????????/????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 32:
                            // ????????????->?????????
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        default:
                            break;
                    }
                }
                for (AssetLeaseRecordEntity entity : expired) {
                    expiredContractedMap.put(entity.getId(), entity);
                }
            }
        } else {
            throw new LeaseException("???????????????????????????!");
        }
        // ???????????????
        if (!CollectionUtils.isEmpty(contractedMap)) {
            for (Long assetId : contractedMap.keySet()) {
                if (hashMap.containsKey("contracted")) {
                    hashMap.get("contracted").add(contractedMap.get(assetId));
                } else {
                    List<AssetLeaseRecordEntity> assetLeaseRecordEntities = new ArrayList<>();
                    assetLeaseRecordEntities.add(contractedMap.get(assetId));
                    hashMap.put("contracted", assetLeaseRecordEntities);
                }
            }
        }
        // ???????????????
        if (!CollectionUtils.isEmpty(underContractMap)) {
            for (Long assetId : underContractMap.keySet()) {
                if (hashMap.containsKey("underContract")) {
                    hashMap.get("underContract").add(underContractMap.get(assetId));
                } else {
                    List<AssetLeaseRecordEntity> assetLeaseRecordEntities = new ArrayList<>();
                    assetLeaseRecordEntities.add(underContractMap.get(assetId));
                    hashMap.put("underContract", assetLeaseRecordEntities);
                }
            }
        }
        // ???????????????
        if (!CollectionUtils.isEmpty(notContractedMap)) {
            for (Long assetId : notContractedMap.keySet()) {
                if (hashMap.containsKey("notContracted")) {
                    hashMap.get("notContracted").add(notContractedMap.get(assetId));
                } else {
                    List<AssetLeaseRecordEntity> assetLeaseRecordEntities = new ArrayList<>();
                    assetLeaseRecordEntities.add(notContractedMap.get(assetId));
                    hashMap.put("notContracted", assetLeaseRecordEntities);
                }
            }
        }
        // ???????????????
        if (!CollectionUtils.isEmpty(expiredContractedMap)) {
            for (Long assetId : expiredContractedMap.keySet()) {
                if (hashMap.containsKey("expiredContracted")) {
                    hashMap.get("expiredContracted").add(expiredContractedMap.get(assetId));
                } else {
                    List<AssetLeaseRecordEntity> assetLeaseRecordEntities = new ArrayList<>();
                    assetLeaseRecordEntities.add(expiredContractedMap.get(assetId));
                    hashMap.put("expiredContracted", assetLeaseRecordEntities);
                }
            }
        }
        return hashMap;
    }

    /**
     * @param assetLeaseRecordEntity : ????????????
     * @author: Pipi
     * @description: ???????????????????????????????????????
     * @return: java.util.List<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/6 14:53
     **/
    @Override
    public List<AssetLeaseRecordEntity> landlordContractList(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        // ??????????????????,????????????????????????,??????,???????????????????????????????????? ????????????
        List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.landlordContractListByAssetId(assetLeaseRecordEntity.getAssetId(),
                assetLeaseRecordEntity.getAssetType(),
                assetLeaseRecordEntity.getHomeOwnerUid(),
                assetLeaseRecordEntity.getContractStatus()
        );
        if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
            if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
                ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
                // ??????
                QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                shopLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
                shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
                // ???????????????
                QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                shopImgEntityQueryWrapper.eq("shop_id", assetLeaseRecordEntity.getAssetId());
                shopImgEntityQueryWrapper.last("limit 1");
                ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                for (AssetLeaseRecordEntity leaseRecordEntity : assetLeaseRecordEntities) {
                    UserDetail userDetail = userInfoRpcService.getUserDetail(leaseRecordEntity.getTenantUid());
                    leaseRecordEntity.setRealName(userDetail.getNickName());
                    leaseRecordEntity.setAvatarUrl(userDetail.getAvatarThumbnail());
                    leaseRecordEntity.setImageUrl(shopImgEntity == null ? null : shopImgEntity.getImgUrl());
                    leaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
                    leaseRecordEntity.setSummarize(shopLeaseEntity.getSummarize());
                    leaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
                    setCountdown(leaseRecordEntity);
                }
            } else {
                // ??????
                HouseLeaseEntity houseLeaseEntity = new HouseLeaseEntity();
                QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                houseLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
                houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                if (houseLeaseEntity != null) {
                    List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                    // ??????
                    houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                    // ?????? ???????????? ...
                    List<Long> advantageId = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseAdvantageId());
                    if (!CollectionUtils.isEmpty(advantageId)) {
                        houseLeaseEntity.setHouseAdvantageMap(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                    }
                    for (AssetLeaseRecordEntity leaseRecordEntity : assetLeaseRecordEntities) {
                        if (leaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode()
                            || leaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.CANCELLATION_REQUEST.getCode()
                            || leaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.REJECTION_OF_APPLICATION.getCode()
                            || leaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode()
                            || leaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.EXPIRED.getCode()
                        ) {
                            UserDetail userDetail = userInfoRpcService.getUserDetail(leaseRecordEntity.getTenantUid());
                            leaseRecordEntity.setRealName(userDetail.getNickName());
                            leaseRecordEntity.setAvatarUrl(userDetail.getAvatarThumbnail());
                            leaseRecordEntity.setImageUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                            leaseRecordEntity.setTitle(houseLeaseEntity.getHouseTitle());
                            leaseRecordEntity.setPrice(houseLeaseEntity.getHousePrice());
                            leaseRecordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
                            leaseRecordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
                            leaseRecordEntity.setHouseType(HouseHelper.parseHouseType(houseLeaseEntity.getHouseTypeCode()));
                            leaseRecordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
                            leaseRecordEntity.setHouseAdvantageCode(houseLeaseEntity.getHouseAdvantageMap());
                        } else {
                            // ?????? ???????????? ...
                            List<Long> advantageCode = MyMathUtils.analysisTypeCode(leaseRecordEntity.getAdvantageId());
                            if (!CollectionUtils.isEmpty(advantageCode)) {
                                leaseRecordEntity.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageCode, 4L));
                            }
                            // ???????????? code??????????????? ??? 4???2???1???
                            leaseRecordEntity.setHouseType(HouseHelper.parseHouseType(leaseRecordEntity.getTypeCode()));
                            // ??????
                            leaseRecordEntity.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(leaseRecordEntity.getDirectionId())));
                        }
                        setCountdown(leaseRecordEntity);
                    }
                }
            }
        }
        return assetLeaseRecordEntities;
    }

    /**
     * @param assetLeaseRecordEntity : ????????????
     * @param uid                    : ????????????uid
     * @author: Pipi
     * @description: ??????????????????
     * @return: com.jsy.community.entity.proprietor.AssetLeaseRecordEntity
     * @date: 2021/9/6 17:39
     **/
    @Override
    public AssetLeaseRecordEntity contractDetail(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        if (assetLeaseRecordEntity.getIdentityType() == 1) {
            // ??????
            assetLeaseRecordEntity.setHomeOwnerUid(uid);
        } else {
            // ??????
            assetLeaseRecordEntity.setTenantUid(uid);
        }
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.queryDetail(assetLeaseRecordEntity);
        if (leaseRecordEntity == null) {
            // ??????????????????
            return leaseRecordEntity;
        }
        // ??????????????????
        if (assetLeaseRecordEntity.getIdentityType() == 2) {
            updateReadMark(leaseRecordEntity, 1);
        }
        if (leaseRecordEntity.getOperation() == 1 || leaseRecordEntity.getOperation() == 7 || leaseRecordEntity.getOperation() == 8 || leaseRecordEntity.getOperation() == 9 || leaseRecordEntity.getOperation() == 10) {
            // ?????????????????????????????????,????????????
            if (leaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
                // ??????
                ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
                QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                shopLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
                // ???????????????
                QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
                shopImgEntityQueryWrapper.eq("shop_id", leaseRecordEntity.getAssetId());
                shopImgEntityQueryWrapper.last("limit 1");
                ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
                leaseRecordEntity.setImageUrl(shopImgEntity == null ? null : shopImgEntity.getImgUrl());
                leaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
                leaseRecordEntity.setSummarize(shopLeaseEntity.getSummarize());
                leaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
                leaseRecordEntity.setFloor(shopLeaseEntity.getFloor());
            } else {
                // ??????
                HouseLeaseEntity houseLeaseEntity = new HouseLeaseEntity();
                QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                houseLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                // ??????
                houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                // ?????? ???????????? ...
                List<Long> advantageId = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseAdvantageId());
                if (!CollectionUtils.isEmpty(advantageId)) {
                    houseLeaseEntity.setHouseAdvantageMap(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                }
                // ???????????? code??????????????? ??? 4???2???1???
                houseLeaseEntity.setHouseTypeStr(HouseHelper.parseHouseType(houseLeaseEntity.getHouseTypeCode()));
                leaseRecordEntity.setImageUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                leaseRecordEntity.setTitle(houseLeaseEntity.getHouseTitle());
                leaseRecordEntity.setAdvantageId(houseLeaseEntity.getHouseAdvantageId());
                leaseRecordEntity.setHouseAdvantageCode(houseLeaseEntity.getHouseAdvantageMap());
                leaseRecordEntity.setTypeCode(houseLeaseEntity.getHouseTypeCode());
                leaseRecordEntity.setHouseType(houseLeaseEntity.getHouseTypeStr());
                leaseRecordEntity.setDirectionId(houseLeaseEntity.getHouseDirectionId());
                leaseRecordEntity.setPrice(houseLeaseEntity.getHousePrice());
                leaseRecordEntity.setProvinceId(houseLeaseEntity.getHouseProvinceId());
                leaseRecordEntity.setCityId(houseLeaseEntity.getHouseCityId());
                leaseRecordEntity.setAreaId(houseLeaseEntity.getHouseAreaId());
                leaseRecordEntity.setFloor(houseLeaseEntity.getHouseFloor());
                if (assetLeaseRecordEntity.getIdentityType() == 2) {
                    String province = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseProvinceId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseProvinceId());
                    String city = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseCityId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseCityId());
                    String area = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseAreaId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseAreaId());
                    String fullAddress = province + city + area + houseLeaseEntity.getHouseAddress();
                    leaseRecordEntity.setFullAddress(fullAddress);
                }
            }
        } else {
            // ??????????????????????????????
            if (leaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
                // ??????
                // ??????
                leaseRecordEntity.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(leaseRecordEntity.getDirectionId())));
                // ?????? ???????????? ...
                List<Long> advantageId = MyMathUtils.analysisTypeCode(leaseRecordEntity.getAdvantageId());
                if (!CollectionUtils.isEmpty(advantageId)) {
                    leaseRecordEntity.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                }
                leaseRecordEntity.setHouseType(HouseHelper.parseHouseType(leaseRecordEntity.getTypeCode()));
                if (assetLeaseRecordEntity.getIdentityType() == 2) {
                    String province = redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getProvinceId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getProvinceId());
                    String city = redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getCityId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getCityId());
                    String area = redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getAreaId()) == null ? "" : redisTemplate.opsForValue().get("RegionSingle:" + leaseRecordEntity.getAreaId());
                    String fullAddress = province + city + area + leaseRecordEntity.getAddress();
                    leaseRecordEntity.setFullAddress(fullAddress);
                }
            }
        }
        if (assetLeaseRecordEntity.getIdentityType() == 2) {
            // ??????,??????????????????
            UserInfoVo userInfoVo = userService.proprietorDetails(leaseRecordEntity.getHomeOwnerUid());
            leaseRecordEntity.setLandlordName(userInfoVo.getRealName());
            leaseRecordEntity.setLandlordPhone(userInfoVo.getMobile());
        }
        // ????????????
        UserInfoVo userInfoVo = userService.proprietorDetails(leaseRecordEntity.getTenantUid());
        leaseRecordEntity.setRealName(userInfoVo.getRealName());
        leaseRecordEntity.setTenantPhone(userInfoVo.getMobile());
        leaseRecordEntity.setTenantIdCard(userInfoVo.getIdCard());
        setCountdown(leaseRecordEntity);
        return leaseRecordEntity;
    }

    /**
     * @param conId : ??????Id
     * @author: Pipi
     * @description: ??????????????????
     * @return: {@link AssetLeaseRecordEntity}
     * @date: 2021/12/22 11:31
     **/
    @Override
    public AssetLeaseRecordEntity contractDetail(String conId) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("con_id", conId);
        return assetLeaseRecordMapper.selectOne(queryWrapper);
    }

    /**
     * @param leaseRecordEntity:
     * @author: Pipi
     * @description: ????????????????????????????????????
     * @return: void
     * @date: 2021/9/15 11:20
     **/
    public void setCountdown(AssetLeaseRecordEntity leaseRecordEntity) {
        LeaseOperationRecordEntity leaseOperationRecordEntity = new LeaseOperationRecordEntity();
        switch (leaseRecordEntity.getOperation()) {
            case 1:
                // ????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(1);
                // ????????????,???????????????????????????????????????3???
                leaseRecordEntity.setCountdownFinish(leaseRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_TO_CONTRACT_APPLY));
                break;
            case 2:
                // ????????????
                leaseRecordEntity.setProgressNumber(2);
                // ????????????,???????????????????????????????????????7???
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 3:
                // ????????????
                leaseRecordEntity.setProgressNumber(2);
                // ???????????????????????????????????????7???
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 4:
                // ??????????????????
                leaseRecordEntity.setProgressNumber(2);
                // ???????????????????????????????????????7???
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 5:
                // ????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(3);
                // ???????????????????????????????????????7???
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 6:
                // ????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(4);
                break;
            case 7:
                // ????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(1);
                break;
            case 8:
                // ????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(1);
                break;
            case 9:
                // ??????????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(1);
                // ????????????,???????????????????????????????????????3???
                // ???????????????????????????
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_TO_CONTRACT_APPLY));
                }
                break;
            case 31:
                // (??????)????????????/????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(2);
                // ???????????????????????????????????????7???
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 32:
                // ????????????
                // ????????????
                leaseRecordEntity.setProgressNumber(2);
                // ???????????????????????????????????????7???
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            default:
                break;
        }
    }

    /**
     * @param assetLeaseRecordEntity:
     * @author: Pipi
     * @description: ????????????????????????
     * @return: void
     * @date: 2021/9/14 18:26
     **/
    public void updateReadMark(AssetLeaseRecordEntity assetLeaseRecordEntity, Integer status) {
        UpdateWrapper<AssetLeaseRecordEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", assetLeaseRecordEntity.getId());
        updateWrapper.set("read_mark", status);
        assetLeaseRecordMapper.update(assetLeaseRecordEntity, updateWrapper);
    }

    /**
     * @param leaseRecordId: ??????ID
     * @param operation:     ????????????
     * @author: Pipi
     * @description: ??????????????????
     * @return: com.jsy.community.entity.proprietor.LeaseOperationRecordEntity
     * @date: 2021/9/9 17:52
     **/
    private LeaseOperationRecordEntity queryLeaseOperationRecord(Long leaseRecordId, Integer operation) {
        QueryWrapper<LeaseOperationRecordEntity> leaseOperationRecordEntityQueryWrapper = new QueryWrapper<>();
        leaseOperationRecordEntityQueryWrapper.eq("asset_lease_record_id", leaseRecordId);
        leaseOperationRecordEntityQueryWrapper.eq("operation", operation);
        leaseOperationRecordEntityQueryWrapper.orderByDesc("create_time");
        leaseOperationRecordEntityQueryWrapper.last("limit 0, 1");
        return leaseOperationRecordMapper.selectOne(leaseOperationRecordEntityQueryWrapper);
    }

    /**
     * @param assetLeaseRecordEntity: ????????????
     * @author: Pipi
     * @description:????????????????????????(????????????/????????????:31???????????????:6???????????????:32)
     * @return: java.lang.Integer
     * @date: 2021/9/7 10:18
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer signatureOperation(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        AssetLeaseRecordEntity assetLeaseRecordEntity1 = new AssetLeaseRecordEntity();
        switch (assetLeaseRecordEntity.getOperationType()) {
            case 4:
                // ????????????
                return blockchainSuccessful(assetLeaseRecordEntity);
            case 6:
                // ????????????
                return completeContract(assetLeaseRecordEntity);
            case 31:
                // ????????????/????????????
                return landlordInitiatedContract(assetLeaseRecordEntity);
            case 32:
                // ????????????
                return landlordCancelContract(assetLeaseRecordEntity);
            default:
                return 0;
        }
    }

    /**
     * @param assetLeaseRecordEntity:
     * @author: Pipi
     * @description: ????????????
     * @return: java.lang.Integer
     * @date: 2021/9/14 10:55
     **/
    public Integer completeContract(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("con_id", assetLeaseRecordEntity.getConId());
        queryWrapper.eq("home_owner_uid", assetLeaseRecordEntity.getHomeOwnerUid());
        queryWrapper.eq("tenant_uid", assetLeaseRecordEntity.getTenantUid());
        queryWrapper.eq("operation", 5);
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (leaseRecordEntity != null) {
            leaseRecordEntity.setBlockStatus(2);
            leaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode());
            addLeaseOperationRecord(leaseRecordEntity);
            // ????????????????????????
            if (leaseRecordEntity.getAssetType().equals(BusinessEnum.HouseTypeEnum.HOUSE.getCode())) {
                // ??????
                UpdateWrapper<HouseLeaseEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", leaseRecordEntity.getAssetId());
                updateWrapper.set("lease_status", 1);
                houseLeaseMapper.update(new HouseLeaseEntity(), updateWrapper);
                // ?????????????????????????????????,???house_member???????????????
                // ?????????????????????????????????ID
                QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                houseLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                if (houseLeaseEntity != null) {
                    houseMemberService.addMember(leaseRecordEntity.getTenantUid(),
                            leaseRecordEntity.getHomeOwnerUid(),
                            leaseRecordEntity.getCommunityId(),
                            houseLeaseEntity.getHouseId(),
                            leaseRecordEntity.getEndDate().atStartOfDay()
                    );
                }

            } else if (leaseRecordEntity.getAssetType().equals(BusinessEnum.HouseTypeEnum.SHOP.getCode())) {
                // ??????
                UpdateWrapper<ShopLeaseEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", leaseRecordEntity.getAssetId());
                updateWrapper.set("lease_status", 1);
                shopLeaseMapper.update(new ShopLeaseEntity(), updateWrapper);
            }

            CommunityEntity communityEntity = communityService.getCommunityNameById(leaseRecordEntity.getCommunityId());
            //??????
            UserImVo eHomeUserIm = userInfoRpcService.getEHomeUserIm(leaseRecordEntity.getTenantUid());
            /*UserIMEntity userIM = userImService.selectUid(leaseRecordEntity.getTenantUid());
            UserEntity user = userService.getUser(leaseRecordEntity.getTenantUid());*/

            //??????
//            UserIMEntity userIMEntity = userImService.selectUid(leaseRecordEntity.getHomeOwnerUid());
//            UserEntity userEntity = userService.getUser(leaseRecordEntity.getHomeOwnerUid());
//
//
//            HashMap<Object, Object> map = new HashMap<>();
//            map.put("type",3);
//            map.put("dataId",null);
//            PushInfoUtil.PushPublicTextMsg(userIMEntity.getImId(),
//                    "????????????",
//                    "?????????????????????"+user.getRealName()+"????????????",
//                    null,
//                    "?????????????????????"+user.getRealName()+"???????????????"+communityEntity.getName()+"??????"+leaseRecordEntity.getAddress()+"??????????????????????????????",
//                    map,BusinessEnum.PushInfromEnum.CONTRACTSIGNING.getName());
//
//            PushInfoUtil.PushPublicTextMsg(userIM.getImId(),
//                    "????????????",
//                    "?????????????????????"+userEntity.getRealName()+"????????????",
//                    null,
//                    "?????????????????????"+userEntity.getRealName()+"???????????????"+communityEntity.getName()+"??????"+leaseRecordEntity.getAddress()+"??????????????????????????????",
//                    map,
//                    BusinessEnum.PushInfromEnum.CONTRACTSIGNING.getName());
            return assetLeaseRecordMapper.updateById(leaseRecordEntity);
        } else {
            return 0;
        }
    }

    /**
     * @param assetLeaseRecordEntity:
     * @author: Pipi
     * @description: ??????????????????/??????????????????
     * @return: java.lang.Integer
     * @date: 2021/9/14 10:58
     **/
    public Integer landlordInitiatedContract(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("asset_type", assetLeaseRecordEntity.getAssetType());
        queryWrapper.eq("asset_id", assetLeaseRecordEntity.getAssetId());
        queryWrapper.eq("home_owner_uid", assetLeaseRecordEntity.getHomeOwnerUid());
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        queryWrapper.in("operation", new ArrayList<>(Arrays.asList(2, 3, 32)));
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (leaseRecordEntity != null) {
            leaseRecordEntity.setConId(assetLeaseRecordEntity.getConId());
            leaseRecordEntity.setStartDate(assetLeaseRecordEntity.getStartDate());
            leaseRecordEntity.setEndDate(assetLeaseRecordEntity.getEndDate());
            leaseRecordEntity.setConName(assetLeaseRecordEntity.getConName());
            leaseRecordEntity.setInitiator(assetLeaseRecordEntity.getInitiator());
            leaseRecordEntity.setSignatory(assetLeaseRecordEntity.getSignatory());
            leaseRecordEntity.setReadMark(0);
            leaseRecordEntity.setBlockStatus(1);
            leaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.LANDLORD_INITIATED_CONTRACT.getCode());
            addLeaseOperationRecord(leaseRecordEntity);

            //????????????
//            UserIMEntity userIMEntity = userImService.selectUid(assetLeaseRecordEntity.getTenantUid());
//            if (userIMEntity!=null){
//                UserEntity userEntity = userService.getUser(assetLeaseRecordEntity.getHomeOwnerUid());
//                HashMap<Object, Object> map = new HashMap<>();
//                map.put("type",2);
//                map.put("dataId",leaseRecordEntity.getUserId());
//                PushInfoUtil.PushPublicTextMsg(userIMEntity.getImId(),
//                        "????????????",
//                        userEntity.getRealName()+"??????????????????????????????",
//                        null,
//                        userEntity.getRealName()+"????????????????????????????????????????????????24?????????????????????????????????????????????????????????",
//                        map,
//                        BusinessEnum.PushInfromEnum.CONTRACTSIGNING.getName());
//            }
            return assetLeaseRecordMapper.updateById(leaseRecordEntity);
        } else {
            return 0;
        }
    }

    /**
     * @param assetLeaseRecordEntity:
     * @author: Pipi
     * @description: ????????????????????????
     * @return: java.lang.Integer
     * @date: 2021/9/14 11:04
     **/
    public Integer landlordCancelContract(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("home_owner_uid", assetLeaseRecordEntity.getHomeOwnerUid());
        queryWrapper.eq("con_id", assetLeaseRecordEntity.getConId());
        queryWrapper.eq("operation", 31);
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (leaseRecordEntity != null) {
            // ??????????????????????????????
            AiliAppPayRecordEntity ailiAppPayRecordEntity = ailiAppPayRecordService.queryOrderNoByServiceOrderNo(assetLeaseRecordEntity.getConId());
            if (ailiAppPayRecordEntity != null && ailiAppPayRecordEntity.getTradeStatus() == 2) {
                // ?????????,????????????
                throw new LeaseException("?????????????????????,????????????");
            }
            WeChatOrderEntity weChatOrderEntity = weChatService.quereIdByServiceOrderNo(assetLeaseRecordEntity.getConId());
            if (weChatOrderEntity != null && weChatOrderEntity.getOrderStatus() == 2) {
                // ?????????,????????????
                throw new LeaseException("?????????????????????,????????????");
            }
            // ???????????????????????????????????????
            // ???????????????????????????,?????????????????????????????????????????????????????????????????????,??????????????????????????????
            if (weChatOrderEntity != null && weChatOrderEntity.getCompanyId() != null) {
                // ????????????????????????
                CompanyPayConfigEntity companyConfig = companyPayConfigService.getCompanyConfig(weChatOrderEntity.getCompanyId(),1);
                WechatConfig.setConfig(companyConfig);
                try {
                    PublicConfig.closeOrder(weChatOrderEntity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TODO ????????????
                weChatService.deleteByOrder(weChatOrderEntity.getId());
                // TODO ????????????
                redisTemplate.delete("Wechat_Lease:" + weChatOrderEntity.getServiceOrderNo());
            }
            // ??????????????????
            leaseRecordEntity.setBlockStatus(null);
            leaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.CANCEL_LAUNCH.getCode());
            addLeaseOperationRecord(leaseRecordEntity);
            return assetLeaseRecordMapper.updateById(leaseRecordEntity);
        } else {
            return 0;
        }
    }

    /**
     * @param assetLeaseRecordEntity:
     * @author: Pipi
     * @description: ????????????????????????????????????
     * @return: java.lang.Integer
     * @date: 2021/9/14 11:19
     **/
    public Integer blockchainSuccessful(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("con_id", assetLeaseRecordEntity.getConId());
        queryWrapper.eq("operation", 6);
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (leaseRecordEntity != null) {
            leaseRecordEntity.setBlockStatus(4);
            addLeaseOperationRecord(leaseRecordEntity);
            return assetLeaseRecordMapper.updateById(leaseRecordEntity);
        } else {
            return 0;
        }
    }


    /**
     * @param conId : ????????????
     * @author: Pipi
     * @description: ?????????????????????????????????
     * @return: void
     * @date: 2021/9/9 18:24
     **/
    @Override
    public void updateOperationPayStatus(String conId, Integer payType, BigDecimal total,String orderNum) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("con_id", conId);
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        log.info("????????????:{}???????????????", conId);
        if (assetLeaseRecordEntity != null) {
            log.info("????????????:{}???????????????ID,??????????????????????????????????????????", conId, assetLeaseRecordEntity.getId());
            assetLeaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.PAYMENT_COMPLETED.getCode());
            addLeaseOperationRecord(assetLeaseRecordEntity);
            assetLeaseRecordMapper.updateById(assetLeaseRecordEntity);


            UserImVo userIm = userInfoRpcService.getEHomeUserIm(assetLeaseRecordEntity.getTenantUid());
            PropertyCompanyEntity companyEntity = propertyCompanyService.selectCompany(assetLeaseRecordEntity.getCommunityId());
            RealInfoDto idCardRealInfo = userInfoRpcService.getIdCardRealInfo(assetLeaseRecordEntity.getTenantUid());
            RealInfoDto realInfoDto = userInfoRpcService.getIdCardRealInfo(assetLeaseRecordEntity.getHomeOwnerUid());
            if (ObjectUtil.isNull(idCardRealInfo) || ObjectUtil.isNull(realInfoDto)) {
                throw new ProprietorException(JSYError.ACCOUNT_NOT_EXISTS);
            }
            //????????????
            // ????????????,??????????????????????????????,????????????
            /*contractRpcService.communityOrderUpLink("????????????",
                    1,
                    payType,
                    total,
                    orderNum,
                    idCardRealInfo.getIdCardNumber(),
                    realInfoDto.getIdCardNumber(),
                    "??????????????????",
                    null);*/
            /*CochainResponseEntity cochainResponseEntity = OrderCochainUtil.orderCochain("????????????",
                    1,
                    payType,
                    total,
                    orderNum,
                    userDetail.getAccount(),
                    companyEntity.getUnifiedSocialCreditCode(),
                    "??????????????????",
                    null);
            log.info("???????????????"+cochainResponseEntity);*/

            //????????????
            Map<Object, Object> map = new HashMap<>();
            map.put("type", 10);
            map.put("dataId", conId);
            map.put("orderNum", orderNum);
            PushInfoUtil.pushPayAppMsg(iImChatPublicPushRpcService,userIm.getImId(),
                    payType,
                    total.toString(),
                    null,
                    "????????????",
                    map,
                    BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());
        } else {
            log.info("??????????????????:{}???????????????ID", conId);
        }
    }

    /**
     * @param payCallNotice :
     * @author: Pipi
     * @description: ??????????????????????????????
     * @return:
     * @date: 2021/12/21 18:09
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOperationPayStatus(PayCallNotice payCallNotice) {
        try {
            String sysOrderNo = payCallNotice.getSysOrderNo();
            BigDecimal payAmount = payCallNotice.getPayAmount();
            AssetLeaseRecordEntity leaseRecordEntity = queryRecordByConId(payCallNotice.getBusOrderNo());
            if (leaseRecordEntity != null && leaseRecordEntity.getOperation() == 32) {
                log.info("?????????????????????");
                // ?????????????????????????????????,?????????,??????????????????????????????????????????,????????????????????????
                // TODO ????????????????????????
                PayConfigureEntity serviceConfig;
                serviceConfig = payConfigureService.getCompanyConfig(1L);
                ConstClasses.AliPayDataEntity.setConfig(serviceConfig);
                AlipayUtils.orderRefund(sysOrderNo, payAmount);
                ailiAppPayRecordService.deleteByOrderNo(Long.parseLong(sysOrderNo));
                return false;
            } else {
                // ??????????????????????????????
                log.info("????????????????????????????????????1111111");
                Map<String, Object> map = completeLeasingOrder(sysOrderNo, leaseRecordEntity.getConId());
                // ??????????????????????????????
                log.info("????????????????????????????????????");
                updateOperationPayStatus(leaseRecordEntity.getConId(), 2, payAmount, sysOrderNo);
                if (0 != (int) map.get("code")) {
                    log.info("????????????????????????????????????");
                    throw new PaymentException((int) map.get("code"), String.valueOf(map.get("msg")));
                }
                // ??????????????????
                /*log.info("????????????????????????");
                TransferEntity transferEntity = new TransferEntity();
                transferEntity.setSendUid(1456196574147923970L);
                transferEntity.setSendPayPwd("1234");
                UserDetail userDetail = userInfoRpcService.getUserDetail(leaseRecordEntity.getHomeOwnerUid());
                transferEntity.setReceiveUid(userDetail.getUserId());
                transferEntity.setCno("RMB");
                transferEntity.setAmount(payAmount.setScale(2));
                transferEntity.setRemark("??????????????????");
                transferEntity.setType(BusinessEnum.BaseOrderRevenueTypeEnum.LEASE.getExpensesType());
                transferEntity.setTitle("??????????????????");
                transferEntity.setSource(BusinessEnum.BaseOrderSourceEnum.LEASE.getSource());

                Map signMap = (Map) JSONObject.toJSON(transferEntity);
                signMap.put("communicationSecret", BusinessEnum.BaseOrderSourceEnum.LEASE.getSecret());
                String sign = MD5Util.signStr(signMap);
                log.info("communicationSecret --> {}", BusinessEnum.BaseOrderSourceEnum.LEASE.getSecret());
                log.info("??????==========:{},{}", sign, MD5Util.getMd5Str(sign));
                transferEntity.setSign(MD5Util.getMd5Str(sign));
                basePayRpcService.transfer(transferEntity);*/
                basePayRpcService.receiveCall(payCallNotice.getSysOrderNo());
                // userAccountService.rentalIncome(leaseRecordEntity.getConId(), tradeAmount, leaseRecordEntity.getHomeOwnerUid());
                log.info("????????????/???????????????????????????????????????????????????" + sysOrderNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @author: Pipi
     * @description: ???????????????????????????????????????????????????
     * @param orderNo: ????????????????????????
     * @param housingContractOderNo: ????????????????????????
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @date: 2021/8/16 17:05
     **/
    public Map<String, Object> completeLeasingOrder(String orderNo, String housingContractOderNo) {
        log.info("????????????1");
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("leaseContractUuid", housingContractOderNo);
        bodyMap.put("isPayment", true);
        bodyMap.put("orderUuid", orderNo);
        log.info("????????????2");
        log.info("????????????3,{}",SIGN_USER_PROTOCOL);
        log.info("????????????4,{}",SIGN_USER_HOST);
        log.info("????????????5,{}",SIGN_USER_PORT);
        log.info("????????????6,{}",MODIFY_ORDER_PAY_STATUS);
        //url
        String url = SIGN_USER_PROTOCOL + SIGN_USER_HOST + ":" + SIGN_USER_PORT + MODIFY_ORDER_PAY_STATUS;
        log.info("??????URL:{}", url);
        // ????????????
        String bodyString = ZhsjUtil.postEncrypt(JSON.toJSONString(bodyMap));
        log.info("????????????:{}", bodyString);
        //??????http??????
        HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url, bodyString);
        //??????header
        MyHttpUtils.setDefaultHeader(httpPost);
        //??????????????????
        MyHttpUtils.setRequestConfig(httpPost);
        //??????
        String httpResult;
        JSONObject result = null;
        try {
            //???????????????????????????
            httpResult = (String)MyHttpUtils.exec(httpPost, MyHttpUtils.ANALYZE_TYPE_STR);
            result = JSON.parseObject(httpResult);
            if(0 == result.getIntValue("code")){
                returnMap.put("code",0);
                log.info("??????????????????????????????");
            }else if(-1 == result.getIntValue("code")){
                returnMap.put("code",-1);
                returnMap.put("msg",result.getString("message"));
                log.error("????????????????????????????????????????????????");
            }else{
                returnMap.put("code",JSYError.INTERNAL.getCode());
                returnMap.put("msg","????????????");
                log.error("???????????????????????? - ?????????????????????????????????" + result.getIntValue("code") + " ??????????????????" + result.getString("message"));
            }
            return returnMap;
        } catch (Exception e) {
            log.error("???????????????????????? - http????????????????????????json????????????" + result);
            log.error(e.getMessage());
            returnMap.put("code", JSYError.INTERNAL.getCode());
            returnMap.put("msg","????????????");
            return returnMap;
        }
    }

    /**
     * @param id       : ??????ID
     * @param opration : ????????????;1:(??????)??????????????????;2:????????????;9:????????????;31:(??????)????????????/????????????;32:????????????;
     * @author: Pipi
     * @description: ?????????????????????
     * @return: void
     * @date: 2021/9/13 16:10
     **/
    @Override
    public void countdownOpration(Long id, Integer opration, LocalDateTime operationTime) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (assetLeaseRecordEntity != null) {
            if (opration == BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode() && assetLeaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode()) {
                // ???????????????,????????????????????????,????????????
                assetLeaseRecordMapper.setExpiredById(id);
            }
            if (opration == BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode() && assetLeaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode()) {
                // ?????????????????????,??????????????????????????????,??????????????????????????????,??????????????????????????????
                // ??????????????????
                QueryWrapper<LeaseOperationRecordEntity> leaseOperationRecordEntityQueryWrapper = new QueryWrapper<>();
                leaseOperationRecordEntityQueryWrapper.eq("asset_lease_record_id", id);
                leaseOperationRecordEntityQueryWrapper.eq("operation", BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode());
                leaseOperationRecordEntityQueryWrapper.orderByDesc("create_time");
                leaseOperationRecordEntityQueryWrapper.last(" limit 1");
                LeaseOperationRecordEntity leaseOperationRecordEntity = leaseOperationRecordMapper.selectOne(leaseOperationRecordEntityQueryWrapper);
                if (leaseOperationRecordEntity.getCreateTime().compareTo(operationTime) == 0) {
                    // ????????????,??????????????????,????????????
                    assetLeaseRecordMapper.setExpiredById(id);
                }
            }
            if (opration == BusinessEnum.ContractingProcessStatusEnum.ACCEPTING_APPLICATIONS.getCode()
                    && assetLeaseRecordEntity.getOperation() != BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode()
                    && assetLeaseRecordEntity.getOperation() != BusinessEnum.ContractingProcessStatusEnum.PAYMENT_COMPLETED.getCode()
            ) {
                // ?????????????????????,??????????????????????????????,??????????????????
                // ???????????????????????????,????????????????????????
                if (assetLeaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.LANDLORD_INITIATED_CONTRACT.getCode()) {
                    // ????????????????????????
                    notificationOfSignatureCancellationContract(assetLeaseRecordEntity.getConId());
                }
                // TODO ??????????????????????????????,????????????
                assetLeaseRecordMapper.setExpiredById(id);
            }
        }
    }

    /**
     * @param conId: ????????????
     * @author: Pipi
     * @description: ????????????????????????
     * @return: void
     * @date: 2021/9/17 11:20
     **/
    public void notificationOfSignatureCancellationContract(String conId) {
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("conId", conId);
        //url
        String url = SIGN_USER_PROTOCOL + SIGN_USER_HOST + ":" + SIGN_USER_PORT + CONTRACT_OVERDUE;
        // ????????????
        String bodyString = ZhsjUtil.postEncrypt(JSON.toJSONString(bodyMap));
        //??????http??????
        HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url, bodyString);
        //??????header
        MyHttpUtils.setDefaultHeader(httpPost);
        //??????????????????
        MyHttpUtils.setRequestConfig(httpPost);
        //??????
        String httpResult;
        JSONObject result = null;
        try {
            //???????????????????????????
            httpResult = (String) MyHttpUtils.exec(httpPost, MyHttpUtils.ANALYZE_TYPE_STR);
            result = JSON.parseObject(httpResult);
            if (0 == result.getIntValue("code")) {
                log.info("??????{}????????????", conId);
            } else {
                log.error("??????{}????????????,?????????:{},??????:{}", conId, result.getIntValue("code"), result.getString("message"));
            }
        } catch (Exception e) {
            log.error("???????????? - http????????????????????????json????????????" + result);
            log.error(e.getMessage());
        }
    }

    /**
     * @param conId : ??????ID
     * @author: Pipi
     * @description: ??????????????????
     * @return: com.jsy.community.entity.proprietor.AssetLeaseRecordEntity
     * @date: 2021/9/16 17:26
     **/
    @Override
    public AssetLeaseRecordEntity queryRecordByConId(String conId) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("con_id", conId);
        return assetLeaseRecordMapper.selectOne(queryWrapper);
    }

    @Override
    public HouseLeaseContractVO queryContractPreFillInfo(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        HouseLeaseContractVO houseLeaseContractVO = new HouseLeaseContractVO();

        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", assetLeaseRecordEntity.getId());
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        if (leaseRecordEntity == null) {
            return houseLeaseContractVO;
        }
        if (leaseRecordEntity.getAssetId() != null) {
            if (leaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
                // ??????
                QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                shopLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
                String city = StringUtils.isBlank(shopLeaseEntity.getCity()) ? "" : shopLeaseEntity.getCity();
                String area = StringUtils.isBlank(shopLeaseEntity.getArea()) ? "" : shopLeaseEntity.getArea();
                houseLeaseContractVO.setAddress(city + area);
                houseLeaseContractVO.setBuiltupArea(String.valueOf(shopLeaseEntity.getShopAcreage()));
                List<Long> faciltyList = MyMathUtils.analysisTypeCode(shopLeaseEntity.getShopFacility());
                if (!CollectionUtils.isEmpty(faciltyList)) {
                    Map<String, Long> constByTypeCodeForList = houseConstService.getConstByTypeCodeForList(faciltyList, 16L);
                    String facilities = constByTypeCodeForList.keySet().toString();
                    facilities = StringUtils.isBlank(facilities) ? "" : facilities;
                    houseLeaseContractVO.setFacilities(facilities.replaceAll("\\[", "").replaceAll("\\]", ""));
                }
                houseLeaseContractVO.setMonthlyRent(shopLeaseEntity.getMonthMoney());
                if (shopLeaseEntity.getMonthMoney() != null) {
                    houseLeaseContractVO.setMonthlyRentInWords(ChineseYuanUtil.convert(shopLeaseEntity.getMonthMoney().toString()));
                }
                if (StringUtils.isNotBlank(shopLeaseEntity.getDefrayType())) {
                    switch (shopLeaseEntity.getDefrayType()) {
                        case "????????????":
                            houseLeaseContractVO.setPaymentOptions("A");
                            break;
                        case "???1???3":
                            houseLeaseContractVO.setPaymentOptions("B");
                            break;
                        case "???1???6":
                            houseLeaseContractVO.setPaymentOptions("C");
                            break;
                        case "??????":
                            houseLeaseContractVO.setPaymentOptions("D");
                            break;
                        default:
                            break;
                    }
                }
            } else {
                // ??????
                QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                houseLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                String city = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseCityId());
                String area = redisTemplate.opsForValue().get("RegionSingle:" + houseLeaseEntity.getHouseAreaId());
                String address = houseLeaseEntity.getHouseAddress() == null ? "" : houseLeaseEntity.getHouseAddress();
                houseLeaseContractVO.setAddress(city == null ? "" : city + area == null ? "" : area + address);
                houseLeaseContractVO.setBuiltupArea(String.valueOf(houseLeaseEntity.getHouseSquareMeter()));
                List<Long> decorationTypeIds = MyMathUtils.analysisTypeCode(houseLeaseEntity.getDecorationTypeId());
                if (!CollectionUtils.isEmpty(decorationTypeIds)) {
                    Map<String, Long> constByTypeCodeForList = houseConstService.getConstByTypeCodeForList(decorationTypeIds, 18L);
                    houseLeaseContractVO.setDecorationLevel(constByTypeCodeForList.keySet().toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                }
                // ????????????
                List<Long> houseFurnitureIds = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseFurnitureId());
                Map<String, Long> facilitiesMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(houseFurnitureIds)) {
                    Map<String, Long> constByTypeCodeForList = houseConstService.getConstByTypeCodeForList(houseFurnitureIds, 13L);
                    if (!CollectionUtils.isEmpty(constByTypeCodeForList)) {
                        facilitiesMap.putAll(constByTypeCodeForList);
                    }
                }
                // ????????????
                List<Long> roomFacilitiesIds = MyMathUtils.analysisTypeCode(houseLeaseEntity.getRoomFacilitiesId());
                if (!CollectionUtils.isEmpty(roomFacilitiesIds)) {
                    Map<String, Long> constByTypeCodeForList = houseConstService.getConstByTypeCodeForList(houseFurnitureIds, 24L);
                    if (!CollectionUtils.isEmpty(constByTypeCodeForList)) {
                        facilitiesMap.putAll(constByTypeCodeForList);
                    }
                }
                // ????????????
                List<Long> commonFacilitiesIds = MyMathUtils.analysisTypeCode(houseLeaseEntity.getCommonFacilitiesId());
                if (!CollectionUtils.isEmpty(houseFurnitureIds)) {
                    Map<String, Long> constByTypeCodeForList = houseConstService.getConstByTypeCodeForList(houseFurnitureIds, 23L);
                    if (!CollectionUtils.isEmpty(constByTypeCodeForList)) {
                        facilitiesMap.putAll(constByTypeCodeForList);
                    }
                }
                String facilities = facilitiesMap.keySet().toString();
                facilities = StringUtils.isBlank(facilities) ? "" : facilities;
                houseLeaseContractVO.setFacilities(facilities.replaceAll("\\[", "").replaceAll("\\]", ""));
                houseLeaseContractVO.setMonthlyRent(houseLeaseEntity.getHousePrice());
                if (houseLeaseEntity.getHousePrice() != null) {
                    houseLeaseContractVO.setMonthlyRentInWords(ChineseYuanUtil.convert(houseLeaseEntity.getHousePrice().toString()));
                }
                if (houseLeaseEntity.getHouseLeasedepositId() != null) {
                    switch (houseLeaseEntity.getHouseLeasedepositId()) {
                        case 1:
                            houseLeaseContractVO.setPaymentOptions("A");
                            break;
                        case 2:
                            houseLeaseContractVO.setPaymentOptions("B");
                            break;
                        case 4:
                            houseLeaseContractVO.setPaymentOptions("C");
                            break;
                        case 8:
                            houseLeaseContractVO.setPaymentOptions("D");
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        UserInfoVo userInfoVo = userService.queryUserInfo(leaseRecordEntity.getTenantUid());
        if (userInfoVo != null) {
            houseLeaseContractVO.setPartyB(userInfoVo.getRealName());
            houseLeaseContractVO.setTelB(userInfoVo.getMobile());
            houseLeaseContractVO.setIdCardB(userInfoVo.getIdCard());
        }
        return houseLeaseContractVO;
    }
    
    /**
     * @Description: ????????????id???????????????????????????
     * @author: DKS
     * @since: 2022/1/6 9:31
     * @Param: [assetId]
     * @return: java.util.Map<java.lang.Long,java.util.List<java.lang.String>>
     */
    @Override
    public Map<Long, List<String>> queryConIdList(Collection<?> assetId) {
        List<AssetLeaseRecordEntity> entities = assetLeaseRecordMapper.selectList(new QueryWrapper<AssetLeaseRecordEntity>().in("asset_id", assetId));
        if (entities == null) {
            return new HashMap<>();
        }
        return entities.stream().filter(assetLeaseRecordEntity -> StringUtils.isNotBlank(assetLeaseRecordEntity.getConId()))
            .collect(Collectors.groupingBy(AssetLeaseRecordEntity::getAssetId,
            Collectors.mapping(AssetLeaseRecordEntity::getConId, Collectors.toList())));
    }
}