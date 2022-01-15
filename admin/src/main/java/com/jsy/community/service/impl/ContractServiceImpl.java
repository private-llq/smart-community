package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.mapper.ContractMapper;
import com.jsy.community.mapper.UserMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ContractQO;
import com.jsy.community.service.IContractService;
import com.jsy.community.utils.PageInfo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealUserDetail;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 合同管理 服务实现类
 * </p>
 *
 * @author DKS
 * @since 2021-10-29
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, AssetLeaseRecordEntity> implements IContractService {

    @Resource
    private ContractMapper contractMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check = false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;
    
    /**
     * @Description: 合同管理分页查询
     * @Param: [baseQO]
     * @Return: com.jsy.community.utils.PageInfo<com.jsy.community.entity.AssetLeaseRecordEntity>
     * @Author: DKS
     * @since 2021/10/29  11:01
     **/
    @Override
    public PageInfo<AssetLeaseRecordEntity> queryContractPage(BaseQO<ContractQO> baseQO) {
        baseQO.setPage((baseQO.getPage() - 1) * baseQO.getSize());
        List<AssetLeaseRecordEntity> entities = contractMapper.selectContractPage(baseQO);
        if (CollectionUtils.isEmpty(entities)) {
            return new PageInfo<>();
        }
    
        Set<String> homeOwnerUid = entities.stream().map(AssetLeaseRecordEntity::getHomeOwnerUid).collect(Collectors.toSet());
        Set<String> tenantUid = entities.stream().map(AssetLeaseRecordEntity::getTenantUid).collect(Collectors.toSet());
//        Set<Long> homeOwnerUids = homeOwnerUid.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toSet());
//        Set<Long> tenantUids = tenantUid.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toSet());
        List<RealUserDetail> realUserDetailsByUid = baseUserInfoRpcService.getRealUserDetails(homeOwnerUid);
        List<RealUserDetail> realUserDetailsByUid1 = baseUserInfoRpcService.getRealUserDetails(tenantUid);
        Map<Long, RealUserDetail> realUserDetailMap = realUserDetailsByUid.stream().collect(Collectors.toMap(RealUserDetail::getId, Function.identity()));
        Map<Long, RealUserDetail> realUserDetailMap1 = realUserDetailsByUid1.stream().collect(Collectors.toMap(RealUserDetail::getId, Function.identity()));
    
        for (AssetLeaseRecordEntity entity : entities) {
            // 补充合同类型
            if (entity.getAssetType() == 1 || entity.getAssetType() == 2) {
                entity.setContractTypeName("房屋租赁合同");
            } else {
                entity.setContractTypeName("车位购买合同");
            }
            // 补充发起方(甲方:业主)电话和签约方(乙方:租客)电话
//            UserEntity userEntity = userMapper.getUserMobileByUid(entity.getHomeOwnerUid());
//            UserEntity userEntity1 = userMapper.getUserMobileByUid(entity.getTenantUid());
	        if (realUserDetailMap.get(Long.parseLong(entity.getHomeOwnerUid())) != null) {
		        entity.setInitiatorMobile(realUserDetailMap.get(Long.parseLong(entity.getHomeOwnerUid())).getPhone());
	        }
	        if (realUserDetailMap1.get(Long.parseLong(entity.getTenantUid())) != null) {
		        entity.setSignatoryMobile(realUserDetailMap1.get(Long.parseLong(entity.getTenantUid())).getPhone());
	        }
            // 补充状态
            if (entity.getOperation() == 1 || entity.getOperation() == 9) {
                entity.setContractStatusName("未签约");
            } else if (entity.getOperation() == 2 || entity.getOperation() == 3 || entity.getOperation() == 4 || entity.getOperation() == 5
            || entity.getOperation() == 31 || entity.getOperation() == 32) {
                entity.setContractStatusName("签约中");
            } else if (entity.getOperation() == 6) {
                entity.setContractStatusName("已签约");
            }else if (entity.getOperation() == 10) {
                entity.setContractStatusName("已过期");
            }
        }
        PageInfo<AssetLeaseRecordEntity> pageInfo = new PageInfo<>();
        pageInfo.setRecords(entities);
        pageInfo.setTotal(contractMapper.getContractPageCount(baseQO));
        pageInfo.setSize(baseQO.getSize());
        pageInfo.setCurrent(baseQO.getPage() + 1);
        return pageInfo;
    }
}
