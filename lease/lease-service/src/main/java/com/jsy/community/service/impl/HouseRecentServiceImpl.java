package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseRecentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseRecentEntity;
import com.jsy.community.mapper.HouseRecentMapper;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author YuLF
 * @since 2020-2-19 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class HouseRecentServiceImpl extends ServiceImpl<HouseRecentMapper, HouseRecentEntity> implements IHouseRecentService {

    @Resource
    private HouseRecentMapper houseRecentMapper;

    @Override
    public void saveLeaseBrowse(Object proceed, String uid) {
        //标记此次请求为 增加用户的最近浏览
        CommonResult<?> commonResult = (CommonResult<?>) proceed;
        Object data = commonResult.getData();
        HouseRecentEntity houseRecentEntity = null;
        //以下为对 出租房屋 或者 商铺 的详情接口 的结果 进行切面
        //房屋租赁 出租房屋
        if (data instanceof HouseLeaseVO) {
            HouseLeaseVO vo = (HouseLeaseVO) data;
            houseRecentEntity = HouseRecentEntity.builder().acreage(vo.getHouseSquareMeter().doubleValue())
                    .address(vo.getHouseAddress()).browseTitle(vo.getHouseTitle())
                    .browseType(0).houseId(vo.getId())
                    .houseImage(CollectionUtils.isEmpty(vo.getHouseImage()) ? null : vo.getHouseImage().get(0))
                    .price(vo.getHousePrice() + "/" + vo.getHouseUnit())
                    .tag(vo.getHouseAdvantageId())
                    .build();
        }
        //商铺
        if (data instanceof Map) {
            //拿到 li hao 写的商铺详情接口里面map 里面封装的对象
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
            ShopLeaseVO vo = JSONObject.parseObject(jsonObject.getString("shop"), new TypeReference<>() {
            });
            if( Objects.isNull(vo) ){
                return;
            }
            //添加至房屋最近浏览实体
            houseRecentEntity = HouseRecentEntity.builder().acreage(vo.getShopAcreage())
                    .address(vo.getShopAddress()).browseTitle(vo.getTitle())
                    .browseType(1).houseId(vo.getId())
                    .houseImage(isEmpty(vo.getImgPath()) ? null : vo.getImgPath()[0])
                    .price(vo.getMonthMoney() + "/月")
                    .tag(vo.getShopFacility())
                    .build();
        }
        //保存至用户 租赁最近浏览表t_house_recent
        if (!Objects.isNull(houseRecentEntity)) {
            houseRecentEntity.setId(SnowFlake.nextId());
            houseRecentEntity.setUid(uid);
            houseRecentEntity.setCreateTime(LocalDateTime.now());
            houseRecentMapper.insert(houseRecentEntity);
        }
    }


    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

}
