package com.jsy.community.service.impl;

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
import com.jsy.community.entity.*;
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
import com.zhsj.base.api.entity.TransferEntity;
import com.zhsj.base.api.entity.UserDetail;
import com.zhsj.base.api.rpc.IBasePayRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.base.api.vo.UserImVo;
import com.zhsj.basecommon.utils.MD5Util;
import com.zhsj.im.chat.api.rpc.IImChatPublicPushRpcService;
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
 * @Description: 房屋租赁记录表服务实现
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

    /**
     * @param assetLeaseRecordEntity : 房屋租赁记录表实体
     * @author: Pipi
     * @description: 新增租赁签约记录
     * @return: java.lang.Integer
     * @date: 2021/8/31 16:01
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addLeaseRecord(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        // 检查租户是否实名认证
        Integer integer = userService.userIsRealAuth(assetLeaseRecordEntity.getTenantUid());
        if (integer != 2) {
            throw new LeaseException(JSYError.NO_REAL_NAME_AUTH);
        }
        // 检查签约是否已经存在(未完成签约或已签约但是还没到期的签约)
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
            throw new LeaseException("签约申请已经存在,请不要重复发起");
        }
        assetLeaseRecordEntity.setId(SnowFlake.nextId());
        assetLeaseRecordEntity.setDeleted(0L);
        assetLeaseRecordEntity.setReadMark(1);
        assetLeaseRecordEntity.setCreateTime(LocalDateTime.now());
        assetLeaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.INITIATE_CONTRACT.getCode());
        // 查询资产信息
        if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
            // 房屋
            QueryWrapper<HouseLeaseEntity> leaseEntityQueryWrapper = new QueryWrapper<>();
            leaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
            HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(leaseEntityQueryWrapper);
            if (houseLeaseEntity == null) {
                throw new LeaseException("该房屋不存在!");
            }
            assetLeaseRecordEntity.setHomeOwnerUid(houseLeaseEntity.getUid());
            assetLeaseRecordEntity.setCommunityId(houseLeaseEntity.getHouseCommunityId());
        } else if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
            // 商铺
            // 查商铺信息
            QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
            shopLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
            ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
            if (shopLeaseEntity == null) {
                throw new LeaseException("该商铺不存在!");
            }
            assetLeaseRecordEntity.setHomeOwnerUid(shopLeaseEntity.getUid());
            assetLeaseRecordEntity.setCommunityId(shopLeaseEntity.getCommunityId());
        } else {
            throw new LeaseException("请传递正确的资产类型;1:商铺;2:房屋");
        }
        //写入租赁操作数据
        LeaseOperationRecordEntity leaseOperationRecordEntity = new LeaseOperationRecordEntity();
        leaseOperationRecordEntity.setAssetType(assetLeaseRecordEntity.getAssetType());
        leaseOperationRecordEntity.setAssetLeaseRecordId(assetLeaseRecordEntity.getId());
        leaseOperationRecordEntity.setOperation(assetLeaseRecordEntity.getOperation());
        leaseOperationRecordEntity.setId(SnowFlake.nextId());
        leaseOperationRecordEntity.setDeleted(0L);
        leaseOperationRecordEntity.setCreateTime(LocalDateTime.now());
        leaseOperationRecordMapper.insert(leaseOperationRecordEntity);
        // 记录倒计时
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
                        // 倒计时三天
                        message.getMessageProperties().setHeader("x-delay", BusinessConst.ONE_DAY * BusinessConst.COUNTDOWN_TO_CONTRACT_APPLY);
                        return message;
                    }
                });
        // 写入租赁数据
        assetLeaseRecordMapper.insert(assetLeaseRecordEntity);

        //消息推送
        UserImVo userIm = userInfoRpcService.getEHomeUserIm(assetLeaseRecordEntity.getHomeOwnerUid());
        UserDetail userDetail = userInfoRpcService.getUserDetail(assetLeaseRecordEntity.getTenantUid());
        HashMap<Object, Object> map = new HashMap<>();
        map.put("type",6);
        map.put("dataId",null);
        PushInfoUtil.PushPublicTextMsg(iImChatPublicPushRpcService,userIm.getImId(),
                "合同签约",
                userDetail.getNickName()+"向你发起了房屋签约！",
                null,
                userDetail.getNickName()+"向你发起了房屋签约请求，在我的租赁中去查看吧，请在7天时间内处理房屋签约请求，过时系统将自动取消。",
                map,
                BusinessEnum.PushInfromEnum.CONTRACTSIGNING.getName());
        return String.valueOf(assetLeaseRecordEntity.getId());
    }

    /**
     * @param assetLeaseRecordEntity : 房屋租赁记录表实体
     * @param uid                    : 登录用户uid
     * @author: Pipi
     * @description: 对签约进行操作(租客取消申请 / 房东拒绝申请 / 租客再次申请 / 房东接受申请 / 拟定合同)
     * @return: java.lang.Integer
     * @date: 2021/9/3 10:30
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer operationContract(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        Integer result = 0;
        switch (assetLeaseRecordEntity.getOperationType()) {
            case 2:
                // 房东接受申请
                // 房东接受申请后,资产不能编辑,所以在这里将房屋信息写入记录表
                result = acceptingApply(assetLeaseRecordEntity, uid);
                break;
            case 3:
                // 房东点击拟定合同
                result = landlordContractPreparation(assetLeaseRecordEntity, uid);
                break;
            case 7:
                // 租客取消申请
                result = cancelApply(assetLeaseRecordEntity, uid);
                break;
            case 8:
                // 8房东拒绝申请
                result = rejectionApply(assetLeaseRecordEntity, uid);
                break;
            case 9:
                // 租客再次申请
                result = reapply(assetLeaseRecordEntity, uid);
                break;
            default:
                throw new LeaseException("未知操作!");
        }
        return result;
    }

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid:                    登录用户uid
     * @author: Pipi
     * @description: 房东接受申请;房东接受申请后,资产不能编辑,所以在这里将房屋信息写入记录表;
     * 房屋:整租不能同时接受多个申请,合租和单间出租可以
     * 商铺:不能同时接受多个申请
     * 可操作的是:1:租客发出申请;9:再次申请
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
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.ACCEPTING_APPLICATIONS.getCode());
        // 查询资产信息
        if (recordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
            // 2:房屋
            // 查询房屋信息
            QueryWrapper<HouseLeaseEntity> leaseEntityQueryWrapper = new QueryWrapper<>();
            leaseEntityQueryWrapper.eq("id", recordEntity.getAssetId());
            HouseLeaseEntity houseLeaseEntity = houseLeaseMapper.selectOne(leaseEntityQueryWrapper);
            if (houseLeaseEntity == null) {
                throw new LeaseException("该房屋已不存在!");
            }
            if (houseLeaseEntity.getHouseLeasemodeId() == 2) {
                // 整租,检查是否存在多个签约
                checkMultipleApply(assetLeaseRecordEntity.getId(), recordEntity.getAssetId());
            }
            // 查询房屋图片url
            List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
            // 写入房屋信息
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
            // 1:商铺
            // 商铺同房屋整租;检查是否存在多个签约
            checkMultipleApply(assetLeaseRecordEntity.getId(), recordEntity.getAssetId());
            // 查询商铺信息
            QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
            shopLeaseEntityQueryWrapper.eq("id", recordEntity.getAssetId());
            ShopLeaseEntity shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
            if (shopLeaseEntity == null) {
                throw new LeaseException("该商铺已不存在!");
            }
            // 查商铺图片
            QueryWrapper<ShopImgEntity> shopImgEntityQueryWrapper = new QueryWrapper<>();
            shopImgEntityQueryWrapper.eq("shop_id", recordEntity.getAssetId());
            shopImgEntityQueryWrapper.last("limit 1");
            ShopImgEntity shopImgEntity = shopImgMapper.selectOne(shopImgEntityQueryWrapper);
            // 写入商铺信息
            assetLeaseRecordEntity.setImageUrl(shopImgEntity != null ? shopImgEntity.getImgUrl() : null);
            assetLeaseRecordEntity.setTitle(shopLeaseEntity.getTitle());
            assetLeaseRecordEntity.setAdvantageId(shopLeaseEntity.getShopFacility());
            assetLeaseRecordEntity.setPrice(shopLeaseEntity.getMonthMoney());
            assetLeaseRecordEntity.setSummarize(shopLeaseEntity.getSummarize());
            assetLeaseRecordEntity.setCityId(shopLeaseEntity.getCityId());
            assetLeaseRecordEntity.setAreaId(shopLeaseEntity.getAreaId());
            assetLeaseRecordEntity.setFloor(shopLeaseEntity.getFloor());
        }
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        // 记录倒计时
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
                        // 倒计时7天
                        message.getMessageProperties().setHeader("x-delay", BusinessConst.ONE_DAY * BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT);
                        return message;
                    }
                });
        // 设置租客未读
        recordEntity.setReadMark(0);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid:                    登录用户uid
     * @author: Pipi
     * @description: 房东点击你拟定合同(可操作的是 : 接受申请 : 2)
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
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.CONTRACT_PREPARATION.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid:                    登录用户uid
     * @author: Pipi
     * @description: 租客取消申请(可取消的包含 : 发起申请 : 1 ; 重新申请 : 9)
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
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.CANCELLATION_REQUEST.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid:                    登录用户uid
     * @author: Pipi
     * @description: 房东拒绝申请(可操作的是 : 发起申请 : 1 ; 重新发起 : 9)
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
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.REJECTION_OF_APPLICATION.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        // 设置租客未读
        recordEntity.setReadMark(0);
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @param uid:                    登录用户uid
     * @author: Pipi
     * @description: 再次发起申请;可操作的是:8:拒绝申请;7:取消申请
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
            throw new LeaseException("签约信息不存在");
        }
        recordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode());
        //写入租赁操作数据
        addLeaseOperationRecord(recordEntity);
        // 记录倒计时
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
                        // 倒计时7天
                        message.getMessageProperties().setHeader("x-delay", BusinessConst.ONE_DAY * BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT);
                        return message;
                    }
                });
        return assetLeaseRecordMapper.updateById(recordEntity);
    }

    /**
     * @param id:      当前签约ID
     * @param assetId: 资产id
     * @author: Pipi
     * @description: 检查是否有多个签约;
     * 怎么算是多个签约:2:接受申请;3:拟定合同;4:等待支付房租;5:支付完成;31:(房东)发起签约/重新发起;32:取消发起;
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
            throw new LeaseException("该整租房屋/商铺已被其他人租赁");
        }
    }

    /**
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @author: Pipi
     * @description: 写入租赁操作数据
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
     * @param assetLeaseRecordEntity : 查询条件
     * @param uid                    : 登录用户uid
     * @author: Pipi
     * @description: 分页查询签约列表
     * @return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/2 14:38
     **/
    @Override
    public Map<String, List<AssetLeaseRecordEntity>> pageContractList(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        HashMap<String, List<AssetLeaseRecordEntity>> hashMap = new HashMap<>();
        // 已签约
        Map<Long, AssetLeaseRecordEntity> contractedMap = new HashMap<>();
        // 签约中
        Map<Long, AssetLeaseRecordEntity> underContractMap = new HashMap<>();
        // 未签约
        Map<Long, AssetLeaseRecordEntity> notContractedMap = new HashMap<>();
        // 已过期
        Map<Long, AssetLeaseRecordEntity> expiredContractedMap = new HashMap<>();

        if (assetLeaseRecordEntity.getIdentityType() == 1) {
            // 房东
            assetLeaseRecordEntity.setHomeOwnerUid(uid);
            List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.queryList(assetLeaseRecordEntity);
            if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
                // 查询状态为1,8,9,10的资产信息
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
                    // 商铺
                    QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                    shopLeaseEntityQueryWrapper.in("id", shopIds);
                    List<ShopLeaseEntity> shopLeaseEntities = shopLeaseMapper.selectList(shopLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(shopLeaseEntities)) {
                        for (ShopLeaseEntity shopLeaseEntity : shopLeaseEntities) {
                            // 查商铺图片
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
                    // 房屋
                    QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                    houseLeaseEntityQueryWrapper.in("id", houseIds);
                    List<HouseLeaseEntity> houseLeaseEntities = houseLeaseMapper.selectList(houseLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(houseLeaseEntities)) {
                        for (HouseLeaseEntity houseLeaseEntity : houseLeaseEntities) {
                            // 查询房屋图片
                            List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                            houseLeaseEntity.setHouseImgUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                            houseEntityMap.put(houseLeaseEntity.getId(), houseLeaseEntity);
                        }
                    }
                }
                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getAssetType() == 1 && shopEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // 商铺
                        record.setImageUrl(shopEntityMap.get(record.getAssetId()).getShopShowImg());
                        record.setTitle(shopEntityMap.get(record.getAssetId()).getTitle());
                        record.setAdvantageId(shopEntityMap.get(record.getAssetId()).getShopFacility());
                        record.setPrice(shopEntityMap.get(record.getAssetId()).getMonthMoney());
                        record.setSummarize(shopEntityMap.get(record.getAssetId()).getSummarize());
                    } else if (record.getAssetType() == 2 && houseEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // 房屋
                        record.setImageUrl(houseEntityMap.get(record.getAssetId()).getHouseImgUrl());
                        record.setTitle(houseEntityMap.get(record.getAssetId()).getHouseTitle());
                        record.setAdvantageId(houseEntityMap.get(record.getAssetId()).getHouseAdvantageId());
                        record.setTypeCode(houseEntityMap.get(record.getAssetId()).getHouseTypeCode());
                        record.setDirectionId(houseEntityMap.get(record.getAssetId()).getHouseDirectionId());
                        record.setPrice(houseEntityMap.get(record.getAssetId()).getHousePrice());
                        record.setHouseAdvantageCode(houseEntityMap.get(record.getAssetId()).getHouseAdvantageMap());
                        record.setHouseType(houseEntityMap.get(record.getAssetId()).getHouseTypeStr());
                    }
                    // 资产优势标签
                    List<Long> advantageId = MyMathUtils.analysisTypeCode(record.getAdvantageId());
                    if (!CollectionUtils.isEmpty(advantageId)) {
                        record.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                    }
                    // 房屋户型
                    if (StringUtils.isNotBlank(record.getTypeCode())) {
                        record.setHouseType(HouseHelper.parseHouseType(record.getTypeCode()));
                    }
                    // 房屋朝向
                    if (StringUtils.isNotBlank(record.getDirectionId())) {
                        record.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(record.getDirectionId())));
                    }
                }
                List<AssetLeaseRecordEntity> unexpired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() == 0).collect(Collectors.toList());
                List<AssetLeaseRecordEntity> expired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() == 1).collect(Collectors.toList());
                for (AssetLeaseRecordEntity record : unexpired) {
                    switch (record.getOperation()) {
                        case 1:
                            // 发起签约->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                record.setContractNumber(1);
                                notContractedMap.put(record.getAssetId(), record);
                            } else {
                                // 消息数量加1
                                notContractedMap.get(record.getAssetId()).setContractNumber(notContractedMap.get(record.getAssetId()).getContractNumber() + 1);
                            }
                            break;
                        case 2:
                            // 接受申请->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 3:
                            // 拟定合同->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 4:
                            // 等待支付房租->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 5:
                            // 支付完成->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 6:
                            // 完成签约->已签约
                            if (!contractedMap.containsKey(record.getAssetId())) {
                                contractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 7:
                            // 取消申请->不在房东列表展示
                            break;
                        case 8:
                            // 拒绝申请->不在房东列表展示
                            break;
                        case 9:
                            // 租客重新发起->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                record.setContractNumber(1);
                                notContractedMap.put(record.getAssetId(), record);
                            } else {
                                // 消息数量加1
                                notContractedMap.get(record.getAssetId()).setContractNumber(notContractedMap.get(record.getAssetId()).getContractNumber() + 1);
                            }
                            break;
                        case 31:
                            // (房东)发起签约/重新发起->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 32:
                            // 取消发起->签约中
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
            // 租客
            assetLeaseRecordEntity.setTenantUid(uid);
            List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.queryList(assetLeaseRecordEntity);
            if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
                // 查询操作类型为1,8,9,10的资产信息
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
                    // 商铺
                    QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                    shopLeaseEntityQueryWrapper.in("id", shopIds);
                    List<ShopLeaseEntity> shopLeaseEntities = shopLeaseMapper.selectList(shopLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(shopLeaseEntities)) {
                        for (ShopLeaseEntity shopLeaseEntity : shopLeaseEntities) {
                            // 查商铺图片
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
                    // 房屋
                    QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                    houseLeaseEntityQueryWrapper.in("id", houseIds);
                    List<HouseLeaseEntity> houseLeaseEntities = houseLeaseMapper.selectList(houseLeaseEntityQueryWrapper);
                    if (!CollectionUtils.isEmpty(houseLeaseEntities)) {
                        for (HouseLeaseEntity houseLeaseEntity : houseLeaseEntities) {
                            // 查询房屋图片
                            List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                            houseLeaseEntity.setHouseImgUrl(CollectionUtils.isEmpty(houseImgList) ? null : houseImgList.get(0));
                            houseEntityMap.put(houseLeaseEntity.getId(), houseLeaseEntity);
                        }
                    }
                }

                for (AssetLeaseRecordEntity record : assetLeaseRecordEntities) {
                    if (record.getAssetType() == 1 && shopEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // 商铺
                        record.setImageUrl(shopEntityMap.get(record.getAssetId()).getShopShowImg());
                        record.setTitle(shopEntityMap.get(record.getAssetId()).getTitle());
                        record.setAdvantageId(shopEntityMap.get(record.getAssetId()).getShopFacility());
                        record.setPrice(shopEntityMap.get(record.getAssetId()).getMonthMoney());
                        record.setSummarize(shopEntityMap.get(record.getAssetId()).getSummarize());
                    } else if (record.getAssetType() == 2 && houseEntityMap.get(record.getAssetId()) != null && (record.getOperation() == 1 || record.getOperation() == 8 || record.getOperation() == 9 || record.getOperation() == 10)) {
                        // 房屋
                        record.setImageUrl(houseEntityMap.get(record.getAssetId()).getHouseImgUrl());
                        record.setTitle(houseEntityMap.get(record.getAssetId()).getHouseTitle());
                        record.setAdvantageId(houseEntityMap.get(record.getAssetId()).getHouseAdvantageId());
                        record.setTypeCode(houseEntityMap.get(record.getAssetId()).getHouseTypeCode());
                        record.setDirectionId(houseEntityMap.get(record.getAssetId()).getHouseDirectionId());
                        record.setPrice(houseEntityMap.get(record.getAssetId()).getHousePrice());
                        record.setHouseAdvantageCode(houseEntityMap.get(record.getAssetId()).getHouseAdvantageMap());
                        record.setHouseType(houseEntityMap.get(record.getAssetId()).getHouseTypeStr());
                    }
                    // 资产优势标签
                    List<Long> advantageId = MyMathUtils.analysisTypeCode(record.getAdvantageId());
                    if (!CollectionUtils.isEmpty(advantageId)) {
                        record.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                    }
                    // 房屋户型
                    if (StringUtils.isNotBlank(record.getTypeCode())) {
                        record.setHouseType(HouseHelper.parseHouseType(record.getTypeCode()));
                    }
                    // 房屋朝向
                    if (StringUtils.isNotBlank(record.getDirectionId())) {
                        record.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(record.getDirectionId())));
                    }
                }
                List<AssetLeaseRecordEntity> unexpired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() == 0).collect(Collectors.toList());
                List<AssetLeaseRecordEntity> expired = assetLeaseRecordEntities.stream().filter(recordEntity -> recordEntity.getDeleted() == 1).collect(Collectors.toList());
                for (AssetLeaseRecordEntity record : unexpired) {
                    switch (record.getOperation()) {
                        case 1:
                            // 发起签约->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 2:
                            // 接受申请->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 3:
                            // 拟定合同->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 4:
                            // 等待支付房租->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 5:
                            // 支付完成->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 6:
                            // 完成签约->已签约
                            if (!contractedMap.containsKey(record.getAssetId())) {
                                contractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 7:
                            // 取消申请->不在租客列表展示
                            break;
                        case 8:
                            // 拒绝申请->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 9:
                            // 重新发起->未签约
                            if (!notContractedMap.containsKey(record.getAssetId())) {
                                notContractedMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 31:
                            // (房东)发起签约/重新发起->签约中
                            if (!underContractMap.containsKey(record.getAssetId())) {
                                underContractMap.put(record.getAssetId(), record);
                            }
                            break;
                        case 32:
                            // 取消发起->签约中
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
            throw new LeaseException("查询用户身份不明确!");
        }
        // 组装已签约
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
        // 组装签约中
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
        // 组装未签约
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
        // 组装已过期
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
     * @param assetLeaseRecordEntity : 查询条件
     * @author: Pipi
     * @description: 房东查看单个资产的签约列表
     * @return: java.util.List<com.jsy.community.entity.proprietor.AssetLeaseRecordEntity>
     * @date: 2021/9/6 14:53
     **/
    @Override
    public List<AssetLeaseRecordEntity> landlordContractList(AssetLeaseRecordEntity assetLeaseRecordEntity) {
        // 如果是未签约,要单独查资产信息,图片,这个时候要判断资产是房屋 还是商铺
        List<AssetLeaseRecordEntity> assetLeaseRecordEntities = assetLeaseRecordMapper.landlordContractListByAssetId(assetLeaseRecordEntity.getAssetId(),
                assetLeaseRecordEntity.getAssetType(),
                assetLeaseRecordEntity.getHomeOwnerUid(),
                assetLeaseRecordEntity.getContractStatus()
        );
        if (!CollectionUtils.isEmpty(assetLeaseRecordEntities)) {
            if (assetLeaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
                ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
                // 商铺
                QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                shopLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
                shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
                // 查商铺图片
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
                // 房屋
                HouseLeaseEntity houseLeaseEntity = new HouseLeaseEntity();
                QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                houseLeaseEntityQueryWrapper.eq("id", assetLeaseRecordEntity.getAssetId());
                houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                // 朝向
                houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                // 查出 房屋标签 ...
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
                        // 查出 房屋标签 ...
                        List<Long> advantageCode = MyMathUtils.analysisTypeCode(leaseRecordEntity.getAdvantageId());
                        if (!CollectionUtils.isEmpty(advantageCode)) {
                            leaseRecordEntity.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageCode, 4L));
                        }
                        // 房屋类型 code转换为文本 如 4室2厅1卫
                        leaseRecordEntity.setHouseType(HouseHelper.parseHouseType(leaseRecordEntity.getTypeCode()));
                        // 朝向
                        leaseRecordEntity.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(leaseRecordEntity.getDirectionId())));
                    }
                    setCountdown(leaseRecordEntity);
                }
            }
        }
        return assetLeaseRecordEntities;
    }

    /**
     * @param assetLeaseRecordEntity : 查询条件
     * @param uid                    : 登录用户uid
     * @author: Pipi
     * @description: 查询签约详情
     * @return: com.jsy.community.entity.proprietor.AssetLeaseRecordEntity
     * @date: 2021/9/6 17:39
     **/
    @Override
    public AssetLeaseRecordEntity contractDetail(AssetLeaseRecordEntity assetLeaseRecordEntity, String uid) {
        if (assetLeaseRecordEntity.getIdentityType() == 1) {
            // 房东
            assetLeaseRecordEntity.setHomeOwnerUid(uid);
        } else {
            // 租客
            assetLeaseRecordEntity.setTenantUid(uid);
        }
        AssetLeaseRecordEntity leaseRecordEntity = assetLeaseRecordMapper.queryDetail(assetLeaseRecordEntity);
        if (leaseRecordEntity == null) {
            // 为空直接返回
            return leaseRecordEntity;
        }
        // 租客红点已读
        if (assetLeaseRecordEntity.getIdentityType() == 2) {
            updateReadMark(leaseRecordEntity, 1);
        }
        if (leaseRecordEntity.getOperation() == 1 || leaseRecordEntity.getOperation() == 7 || leaseRecordEntity.getOperation() == 8 || leaseRecordEntity.getOperation() == 9 || leaseRecordEntity.getOperation() == 10) {
            // 记录表里面没有资产数据,要单独查
            if (leaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.SHOP.getCode()) {
                // 商铺
                ShopLeaseEntity shopLeaseEntity = new ShopLeaseEntity();
                QueryWrapper<ShopLeaseEntity> shopLeaseEntityQueryWrapper = new QueryWrapper<>();
                shopLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                shopLeaseEntity = shopLeaseMapper.selectOne(shopLeaseEntityQueryWrapper);
                // 查商铺图片
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
                // 房屋
                HouseLeaseEntity houseLeaseEntity = new HouseLeaseEntity();
                QueryWrapper<HouseLeaseEntity> houseLeaseEntityQueryWrapper = new QueryWrapper<>();
                houseLeaseEntityQueryWrapper.eq("id", leaseRecordEntity.getAssetId());
                houseLeaseEntity = houseLeaseMapper.selectOne(houseLeaseEntityQueryWrapper);
                List<String> houseImgList = houseLeaseMapper.queryHouseAllImgById(houseLeaseEntity.getHouseImageId());
                // 朝向
                houseLeaseEntity.setHouseDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(houseLeaseEntity.getHouseDirectionId())));
                // 查出 房屋标签 ...
                List<Long> advantageId = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseAdvantageId());
                if (!CollectionUtils.isEmpty(advantageId)) {
                    houseLeaseEntity.setHouseAdvantageMap(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
                }
                // 房屋类型 code转换为文本 如 4室2厅1卫
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
            // 记录表里面有资产数据
            if (leaseRecordEntity.getAssetType() == BusinessEnum.HouseTypeEnum.HOUSE.getCode()) {
                // 房屋
                // 朝向
                leaseRecordEntity.setDirectionId(BusinessEnum.HouseDirectionEnum.getDirectionName(Integer.valueOf(leaseRecordEntity.getDirectionId())));
                // 查出 房屋标签 ...
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
            // 租客,查看房东信息
            UserInfoVo userInfoVo = userService.proprietorDetails(leaseRecordEntity.getHomeOwnerUid());
            leaseRecordEntity.setLandlordName(userInfoVo.getRealName());
            leaseRecordEntity.setLandlordPhone(userInfoVo.getMobile());
        }
        // 租客信息
        UserInfoVo userInfoVo = userService.proprietorDetails(leaseRecordEntity.getTenantUid());
        leaseRecordEntity.setRealName(userInfoVo.getRealName());
        leaseRecordEntity.setTenantPhone(userInfoVo.getMobile());
        leaseRecordEntity.setTenantIdCard(userInfoVo.getIdCard());
        setCountdown(leaseRecordEntity);
        return leaseRecordEntity;
    }

    /**
     * @param conId : 合同Id
     * @author: Pipi
     * @description: 查询签约详情
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
     * @description: 设置签约倒计时和签约进程
     * @return: void
     * @date: 2021/9/15 11:20
     **/
    public void setCountdown(AssetLeaseRecordEntity leaseRecordEntity) {
        LeaseOperationRecordEntity leaseOperationRecordEntity = new LeaseOperationRecordEntity();
        switch (leaseRecordEntity.getOperation()) {
            case 1:
                // 发起签约
                // 签约进程
                leaseRecordEntity.setProgressNumber(1);
                // 发起签约,倒计时就是发起签约的时间加3天
                leaseRecordEntity.setCountdownFinish(leaseRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_TO_CONTRACT_APPLY));
                break;
            case 2:
                // 接受申请
                leaseRecordEntity.setProgressNumber(2);
                // 接受申请,倒计时就是接受申请的时间加7天
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 3:
                // 拟定合同
                leaseRecordEntity.setProgressNumber(2);
                // 倒计时就是接受申请的时间加7天
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 4:
                // 等待支付房租
                leaseRecordEntity.setProgressNumber(2);
                // 倒计时就是接受申请的时间加7天
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 5:
                // 支付完成
                // 签约进程
                leaseRecordEntity.setProgressNumber(3);
                // 倒计时就是接受申请的时间加7天
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 6:
                // 完成签约
                // 签约进程
                leaseRecordEntity.setProgressNumber(4);
                break;
            case 7:
                // 取消申请
                // 签约进程
                leaseRecordEntity.setProgressNumber(1);
                break;
            case 8:
                // 拒绝申请
                // 签约进程
                leaseRecordEntity.setProgressNumber(1);
                break;
            case 9:
                // 重新申请签约
                // 签约进程
                leaseRecordEntity.setProgressNumber(1);
                // 重新发起,倒计时就是重新发起的时间加3天
                // 查询重新发起的时间
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_TO_CONTRACT_APPLY));
                }
                break;
            case 31:
                // (房东)发起签约/重新发起
                // 签约进程
                leaseRecordEntity.setProgressNumber(2);
                // 倒计时就是接受申请的时间加7天
                leaseOperationRecordEntity = queryLeaseOperationRecord(leaseRecordEntity.getId(), leaseRecordEntity.getOperation());
                if (leaseOperationRecordEntity != null) {
                    leaseRecordEntity.setCountdownFinish(leaseOperationRecordEntity.getCreateTime().plusDays(BusinessConst.COUNTDOWN_DAYS_TO_CONTRACT));
                }
                break;
            case 32:
                // 取消发起
                // 签约进程
                leaseRecordEntity.setProgressNumber(2);
                // 倒计时就是接受申请的时间加7天
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
     * @description: 租客已读状态更新
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
     * @param leaseRecordId: 签约ID
     * @param operation:     操作进程
     * @author: Pipi
     * @description: 查询操作记录
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
     * @param assetLeaseRecordEntity: 签约实体
     * @author: Pipi
     * @description:签章调用相关操作(发起签约/重新发起:31、完成签约:6、取消发起:32)
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
                // 上链成功
                return blockchainSuccessful(assetLeaseRecordEntity);
            case 6:
                // 完成签约
                return completeContract(assetLeaseRecordEntity);
            case 31:
                // 发起签约/重新发起
                return landlordInitiatedContract(assetLeaseRecordEntity);
            case 32:
                // 取消发起
                return landlordCancelContract(assetLeaseRecordEntity);
            default:
                return 0;
        }
    }

    /**
     * @param assetLeaseRecordEntity:
     * @author: Pipi
     * @description: 完成签约
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
            // 修改房源出租状态
            if (leaseRecordEntity.getAssetType().equals(BusinessEnum.HouseTypeEnum.HOUSE.getCode())) {
                // 房屋
                UpdateWrapper<HouseLeaseEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", leaseRecordEntity.getAssetId());
                updateWrapper.set("lease_status", 1);
                houseLeaseMapper.update(new HouseLeaseEntity(), updateWrapper);
                // 绑定租客为房屋租客身份,向house_member表增加数据
                // 查询资产对应的真实房屋ID
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
                // 商铺
                UpdateWrapper<ShopLeaseEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", leaseRecordEntity.getAssetId());
                updateWrapper.set("lease_status", 1);
                shopLeaseMapper.update(new ShopLeaseEntity(), updateWrapper);
            }

            CommunityEntity communityEntity = communityService.getCommunityNameById(leaseRecordEntity.getCommunityId());
            //租客
            UserImVo eHomeUserIm = userInfoRpcService.getEHomeUserIm(leaseRecordEntity.getTenantUid());
            /*UserIMEntity userIM = userImService.selectUid(leaseRecordEntity.getTenantUid());
            UserEntity user = userService.getUser(leaseRecordEntity.getTenantUid());*/

            //房东
//            UserIMEntity userIMEntity = userImService.selectUid(leaseRecordEntity.getHomeOwnerUid());
//            UserEntity userEntity = userService.getUser(leaseRecordEntity.getHomeOwnerUid());
//
//
//            HashMap<Object, Object> map = new HashMap<>();
//            map.put("type",3);
//            map.put("dataId",null);
//            PushInfoUtil.PushPublicTextMsg(userIMEntity.getImId(),
//                    "合同签约",
//                    "恭喜你，和租客"+user.getRealName()+"签约完成",
//                    null,
//                    "恭喜你，和租客"+user.getRealName()+"签约完成："+communityEntity.getName()+"小区"+leaseRecordEntity.getAddress()+"房屋的房屋合同签署。",
//                    map,BusinessEnum.PushInfromEnum.CONTRACTSIGNING.getName());
//
//            PushInfoUtil.PushPublicTextMsg(userIM.getImId(),
//                    "合同签约",
//                    "恭喜你，和房东"+userEntity.getRealName()+"签约完成",
//                    null,
//                    "恭喜你，和房东"+userEntity.getRealName()+"签约完成："+communityEntity.getName()+"小区"+leaseRecordEntity.getAddress()+"房屋的房屋合同签署。",
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
     * @description: 房东发起签约/重新发起签约
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

            //消息推送
//            UserIMEntity userIMEntity = userImService.selectUid(assetLeaseRecordEntity.getTenantUid());
//            if (userIMEntity!=null){
//                UserEntity userEntity = userService.getUser(assetLeaseRecordEntity.getHomeOwnerUid());
//                HashMap<Object, Object> map = new HashMap<>();
//                map.put("type",2);
//                map.put("dataId",leaseRecordEntity.getId());
//                PushInfoUtil.PushPublicTextMsg(userIMEntity.getImId(),
//                        "合同签约",
//                        userEntity.getRealName()+"向你发起了房屋签约！",
//                        null,
//                        userEntity.getRealName()+"向你发起了房屋租赁合同签约，请在24小时处理。过时系统将自动取消。查看详情",
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
     * @description: 房东取消发起签约
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
            // 查询是否存在支付订单
            AiliAppPayRecordEntity ailiAppPayRecordEntity = ailiAppPayRecordService.queryOrderNoByServiceOrderNo(assetLeaseRecordEntity.getConId());
            if (ailiAppPayRecordEntity != null && ailiAppPayRecordEntity.getTradeStatus() == 2) {
                // 已支付,不能取消
                throw new LeaseException("租客已支付租金,不能取消");
            }
            WeChatOrderEntity weChatOrderEntity = weChatService.quereIdByServiceOrderNo(assetLeaseRecordEntity.getConId());
            if (weChatOrderEntity != null && weChatOrderEntity.getOrderStatus() == 2) {
                // 已支付,不能取消
                throw new LeaseException("租客已支付租金,不能取消");
            }
            // 微信订单能够在未支付前作废
            // 但是支付宝订单不行,所以这里做的处理是在租客支付回调时查看签约状态,如果是已经取消则退款
            if (weChatOrderEntity != null && weChatOrderEntity.getCompanyId() != null) {
                // 作废微信支付订单
                CompanyPayConfigEntity companyConfig = companyPayConfigService.getCompanyConfig(weChatOrderEntity.getCompanyId(),1);
                WechatConfig.setConfig(companyConfig);
                try {
                    PublicConfig.closeOrder(weChatOrderEntity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TODO 需要测试
                weChatService.deleteByOrder(weChatOrderEntity.getId());
                // TODO 需要测试
                redisTemplate.delete("Wechat_Lease:" + weChatOrderEntity.getServiceOrderNo());
            }
            // 更新签约数据
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
     * @description: 更新区块链上链状态到成功
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
     * @param conId : 合同编号
     * @author: Pipi
     * @description: 更新签约到支付完成状态
     * @return: void
     * @date: 2021/9/9 18:24
     **/
    @Override
    public void updateOperationPayStatus(String conId, Integer payType, BigDecimal total,String orderNum) {
        QueryWrapper<AssetLeaseRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("con_id", conId);
        AssetLeaseRecordEntity assetLeaseRecordEntity = assetLeaseRecordMapper.selectOne(queryWrapper);
        log.info("签约合同:{}完成了支付", conId);
        if (assetLeaseRecordEntity != null) {
            log.info("找到合同:{}相关的签约ID,开始更新签约状态到已完成支付", conId, assetLeaseRecordEntity.getId());
            assetLeaseRecordEntity.setOperation(BusinessEnum.ContractingProcessStatusEnum.PAYMENT_COMPLETED.getCode());
            addLeaseOperationRecord(assetLeaseRecordEntity);
            assetLeaseRecordMapper.updateById(assetLeaseRecordEntity);


            UserImVo userIm = userInfoRpcService.getEHomeUserIm(assetLeaseRecordEntity.getTenantUid());
            PropertyCompanyEntity companyEntity = propertyCompanyService.selectCompany(assetLeaseRecordEntity.getCommunityId());
            UserDetail userDetail = userInfoRpcService.getUserDetail(assetLeaseRecordEntity.getTenantUid());
            //支付上链
            CochainResponseEntity cochainResponseEntity = OrderCochainUtil.orderCochain("停车费",
                    1,
                    payType,
                    total,
                    orderNum,
                    userDetail.getAccount(),
                    companyEntity.getUnifiedSocialCreditCode(),
                    "房屋租金支付",
                    null);
            log.info("支付上链："+cochainResponseEntity);

            //消息推送
            Map<Object, Object> map = new HashMap<>();
            map.put("type", 10);
            map.put("dataId", conId);
            map.put("orderNum", orderNum);
            PushInfoUtil.pushPayAppMsg(iImChatPublicPushRpcService,userIm.getImId(),
                    payType,
                    total.toString(),
                    null,
                    "租赁缴费",
                    map,
                    BusinessEnum.PushInfromEnum.HOUSEMANAGE.getName());
        } else {
            log.info("没有找到合同:{}相关的签约ID", conId);
        }
    }

    /**
     * @param payCallNotice :
     * @author: Pipi
     * @description: 签约支付后的回调处理
     * @return:
     * @date: 2021/12/21 18:09
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOperationPayStatus(PayCallNotice payCallNotice) {
        try {
            String sysOrderNo = payCallNotice.getSysOrderNo();
            BigDecimal payAmount = payCallNotice.getPayAmount();
            AssetLeaseRecordEntity leaseRecordEntity = queryRecordByConId(sysOrderNo);
            if (leaseRecordEntity != null && leaseRecordEntity.getOperation() == 32) {
                log.info("房东已取消发起");
                // 签约是房东取消发起状态,则退款,并且不修改合同签约的支付状态,同时删除支付订单
                // TODO 退款功能需要测试
                PayConfigureEntity serviceConfig;
                serviceConfig = payConfigureService.getCompanyConfig(1L);
                ConstClasses.AliPayDataEntity.setConfig(serviceConfig);
                AlipayUtils.orderRefund(sysOrderNo, payAmount);
                ailiAppPayRecordService.deleteByOrderNo(Long.parseLong(sysOrderNo));
                return false;
            } else {
                // 修改签章合同支付状态
                log.info("开始修改签章合同支付状态");
                Map<String, Object> map = housingRentalOrderService.completeLeasingOrder(sysOrderNo, leaseRecordEntity.getConId());
                // 修改租房签约支付状态
                log.info("开始修改租房签约支付状态");
                updateOperationPayStatus(leaseRecordEntity.getConId(), 2, payAmount, sysOrderNo);
                if (0 != (int) map.get("code")) {
                    log.info("修改签章合同支付状态失败");
                    throw new PaymentException((int) map.get("code"), String.valueOf(map.get("msg")));
                }
                // 增加房东余额
                log.info("开始修改房东余额");
                TransferEntity transferEntity = new TransferEntity();
                transferEntity.setSendUid(1456196574147923970L);
                transferEntity.setSendPayPwd("1234");
                UserDetail userDetail = userInfoRpcService.getUserDetail(leaseRecordEntity.getHomeOwnerUid());
                transferEntity.setReceiveUid(userDetail.getId());
                transferEntity.setCno("RMB");
                transferEntity.setAmount(payAmount.setScale(2));
                transferEntity.setRemark("房屋租金入账");
                transferEntity.setType(BusinessEnum.BaseOrderRevenueTypeEnum.LEASE.getExpensesType());
                transferEntity.setTitle("房屋租金入账");
                transferEntity.setSource(BusinessEnum.BaseOrderSourceEnum.LEASE.getSource());

                Map signMap = (Map) JSONObject.toJSON(transferEntity);
                signMap.put("communicationSecret", BusinessEnum.BaseOrderSourceEnum.LEASE.getSecret());
                String sign = MD5Util.signStr(signMap);
                log.info("communicationSecret --> {}", BusinessEnum.BaseOrderSourceEnum.LEASE.getSecret());
                log.info("签名==========:{},{}", sign, MD5Util.getMd5Str(sign));
                transferEntity.setSign(MD5Util.getMd5Str(sign));
                basePayRpcService.transfer(transferEntity);
                basePayRpcService.receiveCall(payCallNotice.getSysOrderNo());
                // userAccountService.rentalIncome(leaseRecordEntity.getConId(), tradeAmount, leaseRecordEntity.getHomeOwnerUid());
                log.info("房屋押金/房租缴费订单状态修改完成，订单号：" + sysOrderNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param id       : 签约ID
     * @param opration : 操作类型;1:(租客)发起租赁申请;2:接受申请;9:重新发起;31:(房东)发起签约/重新发起;32:取消发起;
     * @author: Pipi
     * @description: 倒计时相关操作
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
                // 发起签约后,房东接受申请超时,删除签约
                assetLeaseRecordMapper.setExpiredById(id);
            }
            if (opration == BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode() && assetLeaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode()) {
                // 重新申请签约后,因为可以重发重新申请,所以要判断倒计时之后,是否有重复的重新申请
                // 查询操作记录
                QueryWrapper<LeaseOperationRecordEntity> leaseOperationRecordEntityQueryWrapper = new QueryWrapper<>();
                leaseOperationRecordEntityQueryWrapper.eq("asset_lease_record_id", id);
                leaseOperationRecordEntityQueryWrapper.eq("operation", BusinessEnum.ContractingProcessStatusEnum.REAPPLY.getCode());
                leaseOperationRecordEntityQueryWrapper.orderByDesc("create_time");
                leaseOperationRecordEntityQueryWrapper.last(" limit 1");
                LeaseOperationRecordEntity leaseOperationRecordEntity = leaseOperationRecordMapper.selectOne(leaseOperationRecordEntityQueryWrapper);
                if (leaseOperationRecordEntity.getCreateTime().compareTo(operationTime) == 0) {
                    // 时间相同,是同一次操作,删除签约
                    assetLeaseRecordMapper.setExpiredById(id);
                }
            }
            if (opration == BusinessEnum.ContractingProcessStatusEnum.ACCEPTING_APPLICATIONS.getCode()
                    && assetLeaseRecordEntity.getOperation() != BusinessEnum.ContractingProcessStatusEnum.COMPLETE_CONTRACT.getCode()
                    && assetLeaseRecordEntity.getOperation() != BusinessEnum.ContractingProcessStatusEnum.PAYMENT_COMPLETED.getCode()
            ) {
                // 房东接受申请后,倒计时结束没完成签约,支付的都删除
                // 如果是房东发起状态,通知签章作废合同
                if (assetLeaseRecordEntity.getOperation() == BusinessEnum.ContractingProcessStatusEnum.LANDLORD_INITIATED_CONTRACT.getCode()) {
                    // 通知签章作废合同
                    notificationOfSignatureCancellationContract(assetLeaseRecordEntity.getConId());
                }
                // TODO 可能会涉及到退款业务,需要补上
                assetLeaseRecordMapper.setExpiredById(id);
            }
        }
    }

    /**
     * @param conId: 合同编号
     * @author: Pipi
     * @description: 通知签章作废合同
     * @return: void
     * @date: 2021/9/17 11:20
     **/
    public void notificationOfSignatureCancellationContract(String conId) {
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("conId", conId);
        //url
        String url = SIGN_USER_PROTOCOL + SIGN_USER_HOST + ":" + SIGN_USER_PORT + CONTRACT_OVERDUE;
        // 加密参数
        String bodyString = ZhsjUtil.postEncrypt(JSON.toJSONString(bodyMap));
        //组装http请求
        HttpPost httpPost = MyHttpUtils.httpPostWithoutParams(url, bodyString);
        //设置header
        MyHttpUtils.setDefaultHeader(httpPost);
        //设置默认配置
        MyHttpUtils.setRequestConfig(httpPost);
        //执行
        String httpResult;
        JSONObject result = null;
        try {
            //执行请求，解析结果
            httpResult = (String) MyHttpUtils.exec(httpPost, MyHttpUtils.ANALYZE_TYPE_STR);
            result = JSON.parseObject(httpResult);
            if (0 == result.getIntValue("code")) {
                log.info("合同{}作废成功", conId);
            } else {
                log.error("合同{}作废失败,状态码:{},信息:{}", conId, result.getIntValue("code"), result.getString("message"));
            }
        } catch (Exception e) {
            log.error("合同作废 - http执行或解析异常，json解析结果" + result);
            log.error(e.getMessage());
        }
    }

    /**
     * @param conId : 合同ID
     * @author: Pipi
     * @description: 查询签约详情
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
                // 商铺
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
                        case "押一付一":
                            houseLeaseContractVO.setPaymentOptions("A");
                            break;
                        case "押1付3":
                            houseLeaseContractVO.setPaymentOptions("B");
                            break;
                        case "押1付6":
                            houseLeaseContractVO.setPaymentOptions("C");
                            break;
                        case "年付":
                            houseLeaseContractVO.setPaymentOptions("D");
                            break;
                        default:
                            break;
                    }
                }
            } else {
                // 房屋
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
                // 房屋家具
                List<Long> houseFurnitureIds = MyMathUtils.analysisTypeCode(houseLeaseEntity.getHouseFurnitureId());
                Map<String, Long> facilitiesMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(houseFurnitureIds)) {
                    Map<String, Long> constByTypeCodeForList = houseConstService.getConstByTypeCodeForList(houseFurnitureIds, 13L);
                    if (!CollectionUtils.isEmpty(constByTypeCodeForList)) {
                        facilitiesMap.putAll(constByTypeCodeForList);
                    }
                }
                // 房间设施
                List<Long> roomFacilitiesIds = MyMathUtils.analysisTypeCode(houseLeaseEntity.getRoomFacilitiesId());
                if (!CollectionUtils.isEmpty(roomFacilitiesIds)) {
                    Map<String, Long> constByTypeCodeForList = houseConstService.getConstByTypeCodeForList(houseFurnitureIds, 24L);
                    if (!CollectionUtils.isEmpty(constByTypeCodeForList)) {
                        facilitiesMap.putAll(constByTypeCodeForList);
                    }
                }
                // 公共配置
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
}
