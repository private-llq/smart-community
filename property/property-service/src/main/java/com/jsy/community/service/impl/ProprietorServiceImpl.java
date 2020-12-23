package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.ProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.vo.ProprietorVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
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
    @Override
    public void saveUserBatch(List<UserEntity> userEntityList, Long communityId) {
        //1.验证 录入信息 房屋信息是否正确
        //1.1 通过社区id 拿到当前社区 所有未被登记的房屋信息
        //拿到当前社区结构层级
        List<HouseEntity>  houseEntities = proprietorMapper.getHouseListByCommunityId(communityId);
        //1.2 验证excel录入的每一个房屋信息 是否正确



    }

    /**
     * 验证用户录入的房屋信息是否能在数据库未登记房屋中 找到
     * @param NotVerified       未经验证的excel录入数据
     * @param verified          数据库已存在未登记的房屋信息数据
     */
    private void validHouseList(List<UserEntity> NotVerified, List<HouseEntity> verified){
        for( UserEntity userEntity : NotVerified ){
            HouseEntity houseEntity = userEntity.getHouseEntity();
        }
    }
}
