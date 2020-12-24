package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.ProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.ProprietorVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业主 服务实现类
 * @author YuLF
 * @since 2020-11-25
 */
@DubboService(version = Const.version, group = Const.group)
public class ProprietorServiceImpl extends ServiceImpl<ProprietorMapper, UserEntity> implements IProprietorService {

    @Resource
    private ProprietorMapper proprietorMapper;


    @DubboReference(version = Const.version, group = Const.group, check = false)
    private ICarService iCarService;

    /**
     *  TODO seata 全局事务处理
     * @author YuLF
     * @since  2020/11/28 9:46
     * @Param  id  - 被删除的业主Id
     */
    @Override
    public void del(Long id) {
        //删除业主车辆信息
        Map<String, Object> map = new HashMap<>(1);
        map.put("uid", id);
        iCarService.deleteProprietorCar(map);
        //删除业主关联的家庭成员

        //删除业主关联的房屋

        //删除业主信息
        proprietorMapper.deleteById(id);


    }

    /**
     * 通过传入的参数更新业主信息
     * @param proprietorQO 更新业主信息参数
     * @return             返回是否更新成功
     */
    @Override
    public Boolean update(ProprietorQO proprietorQO) {
        return proprietorMapper.update(proprietorQO) > 0;
    }

    /**
     * 通过分页参数查询 业主信息
     * @param  queryParam   查询参数
     * @return              返回查询的业主信息
     */
    @Override
    public List<ProprietorVO> query(BaseQO<ProprietorQO> queryParam) {
        //避免sql 参数为空 执行失败
        if(queryParam.getQuery() == null){
            queryParam.setQuery(new ProprietorQO());
        }
        queryParam.setPage((queryParam.getPage() - 1) * queryParam.getSize());
        return proprietorMapper.query(queryParam);
    }



    /**
     * 录入业主信息业主房屋绑定信息至数据库
     * @author YuLF
     * @since  2020/12/23 17:35
     * @Param  userEntityList           业主信息参数列表
     * @Param  communityId              社区id
     */
    @Transactional
    @Override
    public void saveUserBatch(List<UserEntity> userEntityList, Long communityId) {
        //1.验证 录入信息 房屋信息是否正确
        //1.1 拿到当前社区结构层级
        Integer houseLevelMode = proprietorMapper.queryHouseLevelModeById(communityId);

        if( houseLevelMode == null ){
            throw new PropertyException("没有这个社区!");
        }

        //1.2 通过社区id 和 社区结构 拿到当前社区 所有未被登记的房屋信息
        List<HouseEntity>  houseEntities = proprietorMapper.getHouseListByCommunityId(communityId, houseLevelMode);
        //1.3 验证excel录入的每一个房屋信息 是否正确  目前 TODO: 没有验证 已经被登记过房屋的用户
        validHouseList(userEntityList, houseEntities);
        //2 为所有用户注册
        //2.1 设置uid 及 id
        userEntityList.forEach( w -> {
            w.setUid(UserUtils.createUserToken());
            w.setId(SnowFlake.nextId());
            //由物业导入 所有人都是业主 因为家属在另一个导入表里面
            w.setHouseholderId(1L);
        });
        //2.2 批量注册 t_user_auth
        proprietorMapper.registerBatch(userEntityList);
        //2.3 批量发送短信告知用户 您的账号已注册 请使用电话号码验证码登录
        //TODO: 批量发送短信 userEntityList
        //3 为所有用户 登记信息 t_user
        proprietorMapper.insertUserBatch(userEntityList);
        //4.为所有用户 登记房屋 状态都是已审核

    }

    /**
     * 验证用户录入的房屋信息是否能在数据库未登记房屋中 找到
     * @param NotVerified       未经验证的excel录入数据
     * @param verified          数据库已存在未登记的房屋信息数据
     */
    private void validHouseList(List<UserEntity> NotVerified, List<HouseEntity> verified){
        for( UserEntity userEntity : NotVerified ){
            //每一个 excel 录入的房屋信息
            HouseEntity houseEntity = userEntity.getHouseEntity();
            if( !judgeExist(houseEntity, verified) ){
                throw new PropertyException(userEntity.getRealName() + "电话："+userEntity.getMobile() + " 这一行的房屋信息填写错误, 如果已经登记房屋,请移除它 原因：在这个社区并没有能匹配的房屋!");
            }
        }
    }



    /**
     * 通过  houseEntity 中的某些属性 来 houseEntityList匹配是否存在这个对象
     * @author YuLF
     * @since  2020/12/24 14:44
     * @Param  houseEntity          需要验证的房屋对象
     * @Param  houseEntityList      房屋列表
     */
    private boolean judgeExist(HouseEntity houseEntity, List<HouseEntity> houseEntityList){
        return houseEntityList.stream().anyMatch(w -> String.valueOf(w.getBuilding()).equals(houseEntity.getBuilding())
                && String.valueOf(w.getDoor()).equals(houseEntity.getDoor())
                && String.valueOf(w.getUnit()).equals(houseEntity.getUnit())
                && String.valueOf(w.getFloor()).equals(houseEntity.getFloor()));
    }
}
