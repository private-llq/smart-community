package com.jsy.lease.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.HouseLeaseVO;
import com.jsy.lease.api.IHouseLeaseService;
import com.jsy.lease.mapper.HouseLeaseMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-11 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class IHouseLeaseServiceImpl extends ServiceImpl<HouseLeaseMapper, HouseLeaseEntity> implements IHouseLeaseService {

    @Resource
    private HouseLeaseMapper houseLeaseMapper;

    /**
     * 新增出租房屋数据
     * @param houseLeaseQO   请求参数接收对象
     * @return               返回新增是否成功
     */
    @Override
    @Transactional
    public Boolean addLeaseSaleHouse(HouseLeaseQO houseLeaseQO) {
        //1.保存房源数据
        //TODO: 业务主键 生成行标识数据id
        houseLeaseQO.setId(SnowFlake.nextId());
        //保存房屋优势标签至中间表
        //TODO: 验证房屋优势标签数值边界范围有效性
        houseLeaseQO.setHouseAdvantageId(SnowFlake.nextId());
        houseLeaseMapper.insertHouseAdvantage(houseLeaseQO);
        //保存房屋图片标签
        //TODO: 验证图片路径有效性
        houseLeaseQO.setHouseImageId(SnowFlake.nextId());
        houseLeaseMapper.insertHouseImages(houseLeaseQO);
        return houseLeaseMapper.insertHouseLease(houseLeaseQO)  > 0;
    }

    /**
     * 根据用户id和 rowGuid 删除出租房源数据
     * @param id            业务主键
     * @param userId        用户id
     */
    @Transactional
    @Override
    public boolean delLeaseHouse(Long id, String userId) {
        //删除中间表 关于 这个用户关联的所有信息
        houseLeaseMapper.delUserMiddleInfo(id);
        //删除 t_house_lease 信息
        return  houseLeaseMapper.delHouseLeaseInfo(id)> 0 ;
    }


    /**
     * 根据参数对象条件查询 出租房屋数据
     * @param houseLeaseQO      查询参数对象
     * @return                  返回数据集合
     */
    @Override
    public List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> houseLeaseQO) {
        //1.通过存在的条件 查出符合条件的分页所有房屋数据
        List<HouseLeaseVO> houseLeaseVOS = houseLeaseMapper.queryHouseLeaseByList(houseLeaseQO);
        //2.遍历每一个数据 查询 字段列 一对多的数据，
        List<String> houseAvatarsIds = new ArrayList<>(houseLeaseVOS.size());
        //房屋标签 如 邻地铁、可短租、之类
        for(HouseLeaseVO houseLeaseVO : houseLeaseVOS){
            houseAvatarsIds.add(houseLeaseVO.getHouseAdvantageId());
        }
        List<Integer> integerList = houseLeaseMapper.queryHouseLeaseConstByIds(houseAvatarsIds);
        return null;
    }



}
