package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.mapper.ProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.utils.CardUtil;
import com.jsy.community.utils.DateUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.ProprietorVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 业主 服务实现类
 *
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class ProprietorServiceImpl extends ServiceImpl<ProprietorMapper, UserEntity> implements IProprietorService {

    @Resource
    private ProprietorMapper proprietorMapper;


    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService iCarService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IUserHouseService iUserHouseService;




    @Override
    public Boolean unbindHouse( Long houseId, Long communityId ) {
        return proprietorMapper.unbindHouse( houseId , communityId ) > 0;
    }



    /**
     * 通过传入的参数更新业主信息
     * @param qo 更新业主信息参数
     * @return 返回是否更新成功
     */
    @Transactional( rollbackFor = {Exception.class})
    @Override
    public Boolean update(ProprietorQO qo, String adminUid) {
        checkHouse(qo.getCommunityId(), qo.getHouseId(), Operation.UPDATE);
        String uid = getUidById(qo.getId());
        if( Objects.isNull(uid) ){
            throw new PropertyException("数据id错误!不存在此用户");
        }
        //更新业主房屋认证信息
        UserHouseEntity userHouse = new UserHouseEntity();
        userHouse.setCommunityId(qo.getCommunityId());
        userHouse.setHouseId(qo.getHouseId());
        userHouse.setCheckStatus(1);
        iUserHouseService.update(userHouse, new UpdateWrapper<UserHouseEntity>().eq("id", qo.getUserHouseId()).eq("uid",uid));

        //管理员姓名
        String adminUserName = proprietorMapper.queryAdminNameByUid(adminUid);
        proprietorMapper.insertOperationLog(SnowFlake.nextId(), null, null, adminUserName, DateUtils.now(), uid);
        return proprietorMapper.update(qo) > 0;
    }





    /**
     * 通过分页参数查询 业主信息
     *
     * @param queryParam 查询参数
     * @return 返回查询的业主信息
     */
    @Override
    public List<ProprietorVO> query(BaseQO<ProprietorQO> queryParam) {
        //避免sql 参数为空 执行失败
        if (queryParam.getQuery() == null) {
            queryParam.setQuery(new ProprietorQO());
        }
        queryParam.setPage((queryParam.getPage() - 1) * queryParam.getSize());
        List<ProprietorVO> query = proprietorMapper.query(queryParam);
        query.forEach( vo -> {
            //用户记录 创建人 / 创建时间
            vo.setCreateDate( proprietorMapper.queryCreateDateByUid( vo.getUid() ));
            //用户记录 最近更新人 / 更新时间
            vo.setUpdateDate( proprietorMapper.queryUpdateDateByUid( vo.getUid() ));
            //TODO : 18-2-10-501 18栋2单元10楼 住宅 查询房屋字符串

            //通过身份证获得 用户的 年龄 、性别
            Map<String, Object> sexAndAge = CardUtil.getSexAndAge(vo.getIdCard());
            vo.setAge( sexAndAge.get("age").toString() );
            vo.setGender( sexAndAge.get("sex").toString() );
        });
        return query;
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
    public void saveUserBatch(List<UserEntity> userEntityList, Long communityId) {
        //1.验证 录入信息 房屋信息是否正确
        //1.1 拿到当前社区结构层级
        Integer houseLevelMode = proprietorMapper.queryHouseLevelModeById(communityId);

        if (houseLevelMode == null) {
            throw new PropertyException("没有这个社区!");
        }

        //1.2 通过社区id 和 社区结构 拿到当前社区 所有未被登记的房屋信息
        List<HouseEntity> houseEntities = proprietorMapper.getHouseListByCommunityId(communityId, houseLevelMode);
        //1.3 验证excel录入的每一个房屋信息 是否正确  目前
        validHouseList(userEntityList, houseEntities);
        //2 为所有用户注册
        //2.1 设置uid 及 id
        userEntityList.forEach(w -> {
            w.setUid(UserUtils.createUserToken());
            w.setId(SnowFlake.nextId());
            //由物业导入 所有人都是业主 因为家属在另一个导入表里面  householder_id 在数据库表示：业主ID 0.未认证 1.业主 其他.隶属业主ID
            w.setHouseholderId(1L);
        });
        //2.2 批量注册 t_user_auth
        proprietorMapper.registerBatch(userEntityList);
        //2.3 批量发送短信告知用户 您的账号已注册 请使用电话号码验证码登录
        //TODO: 批量发送短信 userEntityList  使用异步线程
        //3 为所有用户 登记信息 t_user
        proprietorMapper.insertUserBatch(userEntityList);
        //4.为所有用户 登记房屋 状态都是已审核
        userEntityList.forEach(h -> h.getHouseEntity().setId(SnowFlake.nextId()));
        proprietorMapper.registerHouseBatch(userEntityList, communityId);
    }


    /**
     * 通过当前社区id查出的当前社区所有已登记的房屋
     *
     * @return 返回当前社区已经被登记的所有房屋信息
     * @author YuLF
     * @Param
     * @since 2020/12/25 11:10
     */
    @Override
    public List<HouseVo> queryHouseByCommunityId(long communityId) {
        //1.拿到的当前社区层级结构
        Integer houseLevelMode = proprietorMapper.queryHouseLevelModeById(communityId);
        return proprietorMapper.queryHouseByCommunityId(communityId, houseLevelMode);
    }


    /**
     * [excel] 导入业主家属信息
     *
     * @author YuLF
     * @Param userEntityList     用户家属信息
     * @since 2020/12/25 14:47
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer saveUserMemberBatch(List<UserEntity> userEntityList, long communityId) {
        //1.验证所有家属信息 选择的 业主 是否拥有当前房产
        //1.1 拿到当前社区所有已被业主登记的房屋文本信息 如 1栋2单元2层1-2 和  ID  用于作为excel选择的所属房屋 取出 他对应的houseId
        List<HouseVo> houseVos = queryHouseByCommunityId(communityId);
        //2. 验证当前家属信息 房屋信息 和当前选择的业主及房屋信息 是否和数据库的信息一致 如果不一致 说明物业选择有误
        //2.1 拿到数据库当前社区已登记并且已审核 的 房屋houseId 和 uid 用于验证当前信息是否在数据库存在
        List<UserHouseEntity> userHouseList = queryUserHouseByCommunityId(communityId);
        validHouseMember(userEntityList, houseVos, userHouseList);
        //3.录入家属信息至数据库
        return proprietorMapper.saveUserMemberBatch(userEntityList ,communityId);
    }


    @Transactional( rollbackFor = {Exception.class})
    @Override
    public void addUser(ProprietorQO qo, String adminUid) {
        //用户身份证查出 用户信息
        String uid = proprietorMapper.queryUserByIdCard(qo.getIdCard());
        String createPerson = null, createTime = null, updatePerson = null, updateTime = null;
        //管理员姓名
        String adminUserName = proprietorMapper.queryAdminNameByUid(adminUid);
        if( uid == null ){
            //新增
            qo.setId(SnowFlake.nextId());
            qo.setUid(UserUtils.createUserToken());
            proprietorMapper.saveUser(qo);
            createTime = DateUtils.now();
            createPerson = adminUserName;
        } else {
            //更新业主 已有数据
            qo.setUid(uid);
            proprietorMapper.update(qo);
            updateTime = DateUtils.now();
            updatePerson = adminUserName;
        }
        proprietorMapper.insertOperationLog( SnowFlake.nextId(), createPerson, createTime, updatePerson, updateTime, uid );
        //添加房屋认证信息
        checkHouse(qo.getCommunityId(), qo.getHouseId(), Operation.INSERT);
        UserHouseEntity useHouse = new UserHouseEntity();
        useHouse.setId(SnowFlake.nextId());
        useHouse.setCheckStatus(1);
        useHouse.setHouseId(qo.getHouseId());
        useHouse.setUid(qo.getUid());
        useHouse.setCommunityId(qo.getCommunityId());
        iUserHouseService.save(useHouse);
        //TODO 短信通知业主
    }

    /**
     * 通过数据id 拿到uid
     * @param id        数据id
     */
    public String getUidById(Long id){
        return proprietorMapper.selectUidById(id);
    }


    private void checkHouse(Long communityId, Long houseId, Operation operation) {
        Integer resRow = proprietorMapper.existHouse(communityId, houseId);
        if ( resRow == 0 ){
            throw new PropertyException("房屋不存在!");
        }
        if( operation == Operation.INSERT ){
            Integer resRow1 = proprietorMapper.checkHouse(houseId);
            if( resRow1 >= 1 ){
                throw new PropertyException("选择的房屋已被认证!");
            }
        }
    }


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
     * [业主信息录入验证]验证用户录入的房屋信息是否能在数据库未登记房屋中 找到
     *
     * @param notVerified 未经验证的excel录入数据
     * @param verified    数据库已存在未登记的房屋信息数据
     */
    private void validHouseList(List<UserEntity> notVerified, List<HouseEntity> verified) {
        for (UserEntity userEntity : notVerified) {
            HouseEntity houseEntity = userEntity.getHouseEntity();
            //验证该房屋 是否能在 数据库中有匹配
            if (!judgeExist(houseEntity, verified)) {
                throw new PropertyException(" '姓名：" + userEntity.getRealName() + " 电话：" + userEntity.getMobile() + "' 这一行的房屋信息填写错误,  原因：在这个社区未被登记房屋中并没有能匹配的房屋! 或 该房屋信息已经被其他业主登记");
            }
        }
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


}
