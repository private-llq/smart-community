package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseRecentService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseRecentEntity;
import com.jsy.community.mapper.HouseRecentMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.CommonUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
import io.netty.util.internal.StringUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

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
                    .tag(getTag(vo.getHouseAdvantageCode()))
                    .houseTypeCode(HouseHelper.parseHouseType(vo.getHouseTypeCode()) )
                    .leaseType(vo.getHouseLeaseMode())
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
                    .houseImage(CommonUtils.isEmpty(vo.getImgPath()) ? null : vo.getImgPath()[0])
                    .price(vo.getMonthMoney() + "/月")
                    .tag(getTag(vo.getShopFacilityStrings()))
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
     * 取商铺 或者 出租房屋的 标签
     * 如果有多个 只取3个 用于列表显示
     * 把一个Map类型或者是List类型的标签组合成String存储 如：临街门面,商业街,地铁近
     * @param obj   标签结果对象，出租房屋 是Map 商铺是List
     */
    public  String getTag(Object obj){
        StringBuilder sb = new StringBuilder();
        Collection<String> collection;
        if( obj instanceof Map ) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj));
            Map<String, Long> tags = JSONObject.parseObject(jsonObject.toString(), new TypeReference<>() {
            });
            collection = tags.keySet();
        } else {
            collection = castStrList(obj);
        }
        if( CollectionUtils.isEmpty(collection) ){
            return StringUtil.EMPTY_STRING;
        }
        Object[] object = collection.toArray();
        for( int i = 0; i < object.length; i++ ){
            if( i == 2 || i == object.length - 1){
                sb.append(object[i]);
                break;
            }
            sb.append(object[i]);
            sb.append(",");
        }
        return sb.toString();
    }

    private  List<String> castStrList(Object obj)
    {
        List<String> result = new ArrayList<>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add((String) o);
            }
            return result;
        }
        return null;
    }

    @Override
    public List<HouseRecentEntity> recentBrowseList(Integer leaseType, BaseQO<Object> qo, String uid) {
        QueryWrapper<HouseRecentEntity> wrapper = new QueryWrapper<>();
        wrapper.select("house_id","browse_title","acreage","address","price","house_image","lease_type","create_time","tag","house_type_code");
        wrapper.eq("uid", uid);
        wrapper.eq("browse_type", leaseType);
        wrapper.orderByDesc("create_time");
        Page<HouseRecentEntity> pageCondition = new Page<>( qo.getPage(), qo.getSize() );
        Page<HouseRecentEntity> resultData = houseRecentMapper.selectPage(pageCondition, wrapper);
        return resultData.getRecords();
    }

    @Override
    public Boolean clearRecentBrowse(Integer type, String userId) {
        return houseRecentMapper.deleteByUserInfo(type, userId) > 0;
    }




}
