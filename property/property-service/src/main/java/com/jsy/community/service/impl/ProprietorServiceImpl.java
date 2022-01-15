package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.exception.JSYException;
import com.jsy.community.mapper.ProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.utils.CardUtil;
import com.jsy.community.utils.DateUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.FeeRelevanceTypeVo;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.property.ProprietorVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 业主 服务实现类。
 *
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class ProprietorServiceImpl extends ServiceImpl<ProprietorMapper, ProprietorEntity> implements IProprietorService {

    @Resource
    private ProprietorMapper proprietorMapper;

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService iCarService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IUserHouseService iUserHouseService;

    @Override
    public Boolean unbindHouse( Long id ) {
        return proprietorMapper.unbindHouse( id ) > 0;
    }

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    /**
     * 通过传入的参数更新业主信息
     * @param qo 更新业主信息参数
     * @return 返回是否更新成功
     */
    @Transactional( rollbackFor = {Exception.class})
    @Override
    public Boolean update(ProprietorQO qo, String adminUid) {
        //管理员姓名
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(UserUtils.getUserId());
        String adminUserName = "";
        if (idCardRealInfo != null) {
            adminUserName = idCardRealInfo.getIdCardName();
        }
        proprietorMapper.insertOperationLog(SnowFlake.nextId(),  adminUserName, DateUtils.now(), qo.getId(), 2);
        ProprietorEntity entity = new ProprietorEntity();
        BeanUtils.copyProperties( qo, entity );
        // 检查更新的房屋是否存在此业主之外的业主
        QueryWrapper<ProprietorEntity> queryWrapper = new QueryWrapper<>();
        // 查询此条记录之外的包含该房屋id不是删除的数据
        queryWrapper.eq("house_id", qo.getHouseId()).eq("deleted", 0).ne("id", entity.getId());
        ProprietorEntity selectOne = baseMapper.selectOne(queryWrapper);
        if (Objects.nonNull(selectOne)) {
            throw new JSYException("房屋业主已经存在,请不要重复添加!");
        }
        return proprietorMapper.updateById(entity) > 0;
    }

    @Transactional( rollbackFor = {Exception.class})
    @Override
    public void addUser(ProprietorQO qo, String adminUid) {
        // 检查新增的房屋是否存在业主
        QueryWrapper<ProprietorEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("house_id", qo.getHouseId()).eq("deleted", 0);
        ProprietorEntity selectOne = baseMapper.selectOne(queryWrapper);
        if (Objects.nonNull(selectOne)) {
            throw new JSYException("房屋业主已经存在,请不要重复添加!");
        }
        //管理员姓名
        RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(UserUtils.getUserId());
        String adminUserName = "";
        if (idCardRealInfo != null) {
            adminUserName = idCardRealInfo.getIdCardName();
        }
        ProprietorEntity entity = new ProprietorEntity();
        BeanUtils.copyProperties( qo, entity );
        entity.setId( SnowFlake.nextId() );
        entity.setCreateBy(adminUserName);
        proprietorMapper.insertOperationLog( SnowFlake.nextId(), adminUserName, DateUtils.now(), entity.getId(), 1 );
        proprietorMapper.insert(entity);
        //TODO 短信通知业主
    }

    /**
     * 通过分页参数查询 业主信息
     *
     * @param queryParam 查询参数
     * @return 返回查询的业主信息
     */
    @Override
    public Page<ProprietorVO> query(BaseQO<ProprietorQO> queryParam) {
        Page<ProprietorVO> page = new Page<>(queryParam.getPage(), queryParam.getSize());
        List<ProprietorVO> query = proprietorMapper.query(page,queryParam);
        query.forEach( vo -> {
            //通过身份证获得 用户的 年龄 、性别
            Map<String, Object> sexAndAge = CardUtil.getSexAndAge(vo.getIdCard());
            vo.setAge( sexAndAge.get("age").toString() );
            vo.setGender( sexAndAge.get("sex").toString() );
        });
        return page.setRecords(query);
    }

    /**
     * 录入业主信息业主房屋绑定信息至数据库
     *
     * @author YuLF
     * @Param userEntityList           业主信息参数列表
     * @Param communityId              社区id
     * @since 2020/12/23 17:35
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveUserBatch(List<ProprietorEntity> userEntityList, Long communityId) {
        return proprietorMapper.saveUserBatch(userEntityList, communityId);
    }

//    /**
//     * 通过当前社区id查出的当前社区所有已登记的房屋
//     *
//     * @return 返回当前社区已经被登记的所有房屋信息
//     * @author YuLF
//     * @Param
//     * @since 2020/12/25 11:10
//     */
//    @Override
//    public List<HouseVo> queryHouseByCommunityId(long communityId) {
//        //1.拿到的当前社区层级结构
//        Integer houseLevelMode = proprietorMapper.queryHouseLevelModeById(communityId);
//        return proprietorMapper.queryHouseByCommunityId(communityId, houseLevelMode);
//    }

//    /**
//     * [excel] 导入业主家属信息
//     *
//     * @author YuLF
//     * @Param userEntityList     用户家属信息
//     * @since 2020/12/25 14:47
//     */
//    @Transactional(rollbackFor = {Exception.class})
//    @Override
//    public Integer saveUserMemberBatch(List<UserEntity> userEntityList, long communityId) {
//        //1.验证所有家属信息 选择的 业主 是否拥有当前房产
//        //1.1 拿到当前社区所有已被业主登记的房屋文本信息 如 1栋2单元2层1-2 和  ID  用于作为excel选择的所属房屋 取出 他对应的houseId
//        List<HouseVo> houseVos = queryHouseByCommunityId(communityId);
//        //2. 验证当前家属信息 房屋信息 和当前选择的业主及房屋信息 是否和数据库的信息一致 如果不一致 说明物业选择有误
//        //2.1 拿到数据库当前社区已登记并且已审核 的 房屋houseId 和 uid 用于验证当前信息是否在数据库存在
//        List<UserHouseEntity> userHouseList = queryUserHouseByCommunityId(communityId);
//        validHouseMember(userEntityList, houseVos, userHouseList);
//        //3.录入家属信息至数据库
//        return proprietorMapper.saveUserMemberBatch(userEntityList ,communityId);
//    }

    /**
     * @author: Pipi
     * @description: 查询未绑定房屋列表
     * @param: baseQO:
     * @return: java.util.List<com.jsy.community.vo.HouseTypeVo>
     * @date: 2021/6/12 14:34
     */
    @Override
    public List<FeeRelevanceTypeVo> getUnboundHouseList(BaseQO<RelationListQO> baseQO) {
        if (baseQO.getPage() == null || baseQO.getPage() <= 0) {
            baseQO.setPage(1L);
        }
        if (baseQO.getSize() == null || baseQO.getSize() <= 0) {
            baseQO.setSize(10L);
        }
        Long page = baseQO.getPage() - 1;
        Long pageStart = page > 0 ? page * baseQO.getSize() : 0L;
        return proprietorMapper.getUnboundHouseList(baseQO.getQuery(), pageStart, baseQO.getSize());
    }

    /**
     * 通过数据id 拿到uid
     * @param id        数据id
     */
    @Deprecated
    public String getUidById(Long id){
        return proprietorMapper.selectUidById(id);
    }

//    @Deprecated
//    private void checkHouse(Long communityId, Long houseId, Operation operation) {
//        Integer resRow = proprietorMapper.existHouse(communityId, houseId);
//        if ( resRow == 0 ){
//            throw new PropertyException("房屋不存在!");
//        }
//        if( operation == Operation.INSERT ){
//            Integer resRow1 = proprietorMapper.checkHouse(houseId);
//            if( resRow1 >= 1 ){
//                throw new PropertyException("选择的房屋已被认证!");
//            }
//        }
//    }

    /**
     * 验证excel 业主家属信息 录入 业主 和 家属所属房屋 是否能在数据库匹配 如果不匹配则选择有误
     *  [业主家属验证]
     * @author YuLF
     * @Param userEntityList           excel 录入的信息 其中包括了业主的uid，家属的姓名 家属所属房屋、家属性别、身份证...
     * @Param houseVos                 数据库当前社区的 房屋地址 和对应的 houseId
     * @Param userHouseList            当前小区所有已审核业主房屋信息 包括了 houseId、uid
     * @since 2020/12/25 15:05
     */
    private static void validHouseMember(List<UserEntity> userEntityList, List<HouseVo> houseVos, List<UserHouseEntity> userHouseList) {
        for (UserEntity u : userEntityList) {
            Optional<HouseVo> cartOptional = houseVos.stream().filter(h -> u.getHouseEntity().getAddress().equals(h.getMergeName())).findFirst();
            // 存在
            if (cartOptional.isPresent()) {
                Long houseId = cartOptional.get().getHouseId();
                //通过房屋id 和 业主uid 验证当前业主是否拥有这套房屋
                boolean hasHouse = userHouseList.stream().anyMatch(w -> u.getUid().equals(w.getUid()) && w.getHouseId().equals(houseId));
                if (hasHouse) {
                    u.getHouseEntity().setHouseId(houseId);
                    u.setId(SnowFlake.nextId());
                    continue;
                }
                // 当前业主并不拥有选择的这套房屋
                throw new PropertyException("' 家属姓名：" + u.getRealName() + " 身份证：" + u.getIdCard() + " '这一行的房屋信息填写错误! '当前业主' 并没有登记这栋房屋. 请仔细核实房屋信息");
            }
            //如果 房屋不存在
            throw new PropertyException("' 家属姓名：" + u.getRealName() + " 身份证：" + u.getIdCard() + " '这一行的房屋信息填写错误! '当前社区' 并没有这栋房屋. 请仔细核实房屋信息");
        }
    }

    private List<UserHouseEntity> queryUserHouseByCommunityId(long communityId) {
        return proprietorMapper.queryUserHouseByCommunityId(communityId);
    }

    /**
     * 通过  houseEntity 中的某些属性 来 houseEntityList匹配是否存在这个对象
     *
     * @author YuLF
     * @Param houseEntity          需要验证的房屋对象
     * @Param houseEntityList      房屋列表
     * @since 2020/12/24 14:44
     */
    private boolean judgeExist(HouseEntity houseEntity, List<HouseEntity> houseEntityList) {
        boolean flag = false;
        HouseEntity matchHouse = null;
        for (HouseEntity house : houseEntityList) {
            if (String.valueOf(house.getFloor()).equals(houseEntity.getFloor())
                    && String.valueOf(house.getUnit()).equals(houseEntity.getUnit())
                    && String.valueOf(house.getBuilding()).equals(houseEntity.getBuilding())
                    && house.getDoor().equals(houseEntity.getDoor())) {
                flag = true;
                matchHouse = house;
                houseEntity.setHouseId(house.getHouseId());
                break;
            }
        }
        //如果能在 数据库里面的 houseEntityList 房屋信息找到这一条信息 那么 这条 数据库的信息理应移除 因为这条信息需要被登记 后面再验证时 只验证其他
        if (flag) {
            houseEntityList.remove(matchHouse);
        }
        return flag;
    }

    @Override
    public ProprietorEntity getByUser(String name, String mobile, Long houseId, Long communityId) {
        return proprietorMapper.selectOne(new QueryWrapper<ProprietorEntity>().eq("community_id",communityId).eq("house_id",houseId).eq("real_name",name).eq("mobile",mobile));
    }

    /**
     * @Description: 根据手机号查询绑定房屋的id
     * @Param: [mobile]
     * @Return:
     * @Author: DKS
     * @Date: 2021/08/16
     **/
    @Override
    public List<Long> queryBindHouseByMobile(String mobile, Long communityId) {
        return proprietorMapper.queryBindHouseByMobile(mobile, communityId);
    }

}
