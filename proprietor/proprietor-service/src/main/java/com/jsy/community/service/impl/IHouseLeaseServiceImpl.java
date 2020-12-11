package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseEntity;
import com.jsy.community.mapper.HouseLeaseMapper;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author YuLF
 * @since 2020-12-11 09:22
 */
@DubboService(version = Const.version, group = Const.group_proprietor)
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
        //TODO: 业务主键暂用UUID代替 生成行标识数据id
        String rowGuid = getUUID();
        houseLeaseQO.setRowGuid(rowGuid);
        //保存房屋优势标签至中间表
        //TODO: 验证房屋优势标签数值边界范围有效性
        houseLeaseQO.setHouseAdvantageId(getUUID());
        houseLeaseMapper.insertHouseAdvantage(houseLeaseQO);
        //保存房屋图片标签
        //TODO: 验证图片路径有效性
        houseLeaseQO.setHouseImageId(getUUID());
        houseLeaseMapper.insertHouseImages(houseLeaseQO);
        return houseLeaseMapper.insertHouseLease(houseLeaseQO)  > 0;
    }

    /**
     * 根据用户id和 rowGuid 删除出租房源数据
     * @param rowGuid       业务主键
     * @param userId        用户id
     */
    @Transactional
    @Override
    public boolean delLeaseHouse(String rowGuid, String userId) {
        //删除中间表 关于 这个用户关联的所有信息
        houseLeaseMapper.delUserMiddleInfo(rowGuid);
        //删除 t_house_lease 信息
        return  houseLeaseMapper.delHouseLeaseInfo(rowGuid)> 0 ;
    }

    private String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
