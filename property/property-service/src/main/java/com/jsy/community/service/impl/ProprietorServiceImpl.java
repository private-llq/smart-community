package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.mapper.ProprietorMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ProprietorQO;
import com.jsy.community.vo.ProprietorVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
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
}
