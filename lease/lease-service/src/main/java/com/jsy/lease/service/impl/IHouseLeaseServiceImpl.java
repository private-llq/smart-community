package com.jsy.lease.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.HouseLeaseQO;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.HouseLeaseVO;
import com.jsy.lease.api.IHouseConstService;
import com.jsy.lease.api.IHouseLeaseService;
import com.jsy.lease.mapper.HouseLeaseMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author YuLF
 * @since 2020-12-11 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class IHouseLeaseServiceImpl extends ServiceImpl<HouseLeaseMapper, HouseLeaseEntity> implements IHouseLeaseService {

    @Resource
    private HouseLeaseMapper houseLeaseMapper;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;

    /**
     * 新增出租房屋数据
     * @param houseLeaseQO   请求参数接收对象
     * @return               返回新增是否成功
     */
    @Transactional
    @Override
    public Boolean addLeaseSaleHouse(HouseLeaseQO houseLeaseQO) {
        //1.保存房源数据
        //TODO: 业务主键 生成行标识数据id
        houseLeaseQO.setId(SnowFlake.nextId());
        //保存房屋优势标签至中间表 随时入住、电梯楼、家电齐全等等
        if(houseLeaseQO.getHouseAdvantage() != null){
            long typeCode = MyMathUtils.getTypeCode(houseLeaseQO.getHouseAdvantage());
            houseLeaseQO.setHouseAdvantageId(typeCode);
        }
        //保存房屋家具id
        if(houseLeaseQO.getHouseAdvantage() != null){
            long typeCode = MyMathUtils.getTypeCode(houseLeaseQO.getHouseFurniture());
            houseLeaseQO.setHouseFurnitureId(typeCode);
        }
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
     * [全使用单表查询]
     * 根据参数对象条件查询 出租房屋数据
     * @param houseLeaseQO      查询参数对象
     * @return                  返回数据集合
     */
    @Transactional
    @Override
    public List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> houseLeaseQO) {
        houseLeaseQO.setPage((houseLeaseQO.getPage() - 1)*houseLeaseQO.getSize());
        //如果在查询时 用户筛选条件房屋优势标签为多选
        if ( houseLeaseQO.getQuery().getHouseAdvantage() != null && !houseLeaseQO.getQuery().getHouseAdvantage().isEmpty() ){
            long typeCode = MyMathUtils.getTypeCode(houseLeaseQO.getQuery().getHouseAdvantage());
            houseLeaseQO.getQuery().setHouseAdvantageId(typeCode);
        }
        //1.通过存在的条件 查出符合条件的分页所有房屋数据
        List<HouseLeaseVO> vos = houseLeaseMapper.queryHouseLeaseByList(houseLeaseQO);
        //根据数据字段id 查询 一对多的 房屋标签name和id 如 邻地铁、可短租、临街商铺等多标签、之类
        for(HouseLeaseVO vo : vos){
            List<Long> AdvantageID = MyMathUtils.analysisTypeCode(vo.getHouseAdvantageId());
            if( !AdvantageID.isEmpty() ){
                //1.房屋优势标签 如 随时入住、电梯楼... 属于该条数据多选的 type = t_house_const 里面的 house_const_type
                vo.setHouseAdvantage(houseConstService.getConstByTypeCodeForList(AdvantageID, 4L));
            }
            List<Long> FurnitureId = MyMathUtils.analysisTypeCode(vo.getHouseFurnitureId());
            if( !FurnitureId.isEmpty() ){
                //房屋家具标签 如 床、沙发... 属于该条数据多选的 type = t_house_const 里面的 house_const_type
                vo.setHouseFurniture(houseLeaseMapper.queryHouseConstNameByFurnitureId(FurnitureId, 13L));
            }
            //2.从中间表查出该出租房屋的 第一张显示图片 用于列表标签展示
            vo.setHouseImage(houseLeaseMapper.queryHouseImgById(vo.getHouseImageId(), vo.getId()));
            //3.通过出租方式id 查出 出租方式文本：如 合租、整租之类
            vo.setHouseLeaseMode(houseConstService.getConstNameByConstTypeCode(vo.getHouseLeasemodeId(), 11L));
            //4.通过户型id 查出 户型id对应的文本：如 四室一厅、三室一厅...
            vo.setHouseType(houseConstService.getConstNameByConstTypeCode(vo.getHouseTypeId(), 2L));
        }
        return vos;
    }


    /**
     * 根据id查询房屋详情单条数据
     * @param houseId       房屋id
     * @return              返回这条数据的详情
     */
    @Override
    public HouseLeaseVO queryHouseLeaseOne(Long houseId) {
        //1.查出单条数据
        HouseLeaseVO vo = houseLeaseMapper.queryHouseLeaseOne(houseId);
        //1.4查出 房屋标签 ...
        List<Long> AdvantageID = MyMathUtils.analysisTypeCode(vo.getHouseAdvantageId());
        if( !AdvantageID.isEmpty() ){
            //1.房屋优势标签 如 随时入住、电梯楼... 属于该条数据多选的 type = t_house_const 里面的 house_const_type
            vo.setHouseAdvantage(houseConstService.getConstByTypeCodeForList(AdvantageID, 4L));
        }
        //1.5查出 家具标签
        List<Long> furnitureId = MyMathUtils.analysisTypeCode(vo.getHouseFurnitureId());
        if( !furnitureId.isEmpty() ){
            vo.setHouseFurniture(houseLeaseMapper.queryHouseConstNameByFurnitureId(furnitureId, 13L));
        }
        //1.6查出该条数据所有图片
        vo.setHouseImage(houseLeaseMapper.queryHouseAllImgById(vo.getHouseImageId()));
        //1.7查出房屋朝向
        vo.setHouseDirection(BusinessEnum.HouseDirectionEnum.getDirectionName(vo.getHouseDirection()));
        return vo;
    }


    /**
     * 按参数对象属性更新房屋出租数据
     * @param qo                    参数对象
     * @return                      返回更新影响行数
     */
    @Transactional
    @Override
    public Boolean updateHouseLease(HouseLeaseQO qo) {
        //换算优势标签和家具id
        if( qo.getHouseAdvantage() != null && !qo.getHouseAdvantage().isEmpty() ){
            qo.setHouseAdvantageId(MyMathUtils.getTypeCode(qo.getHouseAdvantage()));
        }
        if( qo.getHouseFurniture() != null && !qo.getHouseFurniture().isEmpty() ){
            qo.setHouseFurnitureId(MyMathUtils.getTypeCode(qo.getHouseFurniture()));
        }
        //图片地址不为空
        if( qo.getHouseImage() != null && qo.getHouseImage().length > 0 ){
            //删除旧的图片
            houseLeaseMapper.deleteImageById(qo.getId());
            //存储新的图片
            qo.setHouseImageId(SnowFlake.nextId());
            houseLeaseMapper.saveHouseLeaseImageById(qo.getHouseImage(), qo.getHouseImageId(), qo.getId());
        }
        return houseLeaseMapper.updateHouseLease(qo);
    }


}
