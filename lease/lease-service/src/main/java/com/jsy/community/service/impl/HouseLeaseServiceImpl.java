package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.LeaseException;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.utils.es.ElasticSearchImport;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.lease.HouseLeaseVO;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.api.IHouseConstService;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.mapper.HouseLeaseMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static java.util.regex.Pattern.*;

/**
 * @author YuLF
 * @since 2020-12-11 09:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class HouseLeaseServiceImpl extends ServiceImpl<HouseLeaseMapper, HouseLeaseEntity> implements IHouseLeaseService {

    @Resource
    private HouseLeaseMapper houseLeaseMapper;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;

    /**
     * 新增出租房屋数据
     *
     * @param qo 请求参数接收对象
     * @return 返回新增是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean addLeaseSaleHouse(HouseLeaseQO qo) {
        //1.保存房源数据
        qo.setId(SnowFlake.nextId());
        //保存房屋优势标签至中间表 随时入住、电梯楼、家电齐全等等
        if (qo.getHouseAdvantage() != null) {
            long typeCode = MyMathUtils.getTypeCode(qo.getHouseAdvantage());
            qo.setHouseAdvantageId(typeCode);
        }
        //保存房屋家具id
        if (qo.getHouseAdvantage() != null) {
            long typeCode = MyMathUtils.getTypeCode(qo.getHouseFurniture());
            qo.setHouseFurnitureId(typeCode);
        }
        //保存房屋图片标签
        qo.setHouseImageId(SnowFlake.nextId());
        houseLeaseMapper.insertHouseImages(qo);
        boolean b = houseLeaseMapper.insertHouseLease(qo) > 0;
        if(b){
            //导入es
            ElasticSearchImport.elasticOperation(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.INSERT, qo.getHouseTitle(), qo.getHouseImage()[0]);
        }
        return b;
    }

    /**
     * 根据用户id和 rowGuid 删除出租房源数据
     *
     * @param id     业务主键
     * @param userId 用户id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delLeaseHouse(Long id, String userId) {
        //删除中间表 关于 这个用户关联的所有图片地址信息
        houseLeaseMapper.deleteImageById(id);
        //Es中删除
        ElasticSearchImport.elasticOperation(id, RecordFlag.LEASE_HOUSE, Operation.DELETE, null, null);
        //删除 t_house_lease 信息
        return houseLeaseMapper.delHouseLeaseInfo(id, userId) > 0;
    }


    /**
     * [全使用单表查询]
     * 根据参数对象条件查询 出租房屋数据
     *
     * @param qo 查询参数对象
     * @return 返回数据集合
     */
    @Override
    public List<HouseLeaseVO> queryHouseLeaseByList(BaseQO<HouseLeaseQO> qo) {
        qo.setPage((qo.getPage() - 1) * qo.getSize());
        //如果在查询时 用户筛选条件房屋优势标签为多选
        if (qo.getQuery().getHouseAdvantage() != null && !qo.getQuery().getHouseAdvantage().isEmpty()) {
            long typeCode = MyMathUtils.getTypeCode(qo.getQuery().getHouseAdvantage());
            qo.getQuery().setHouseAdvantageId(typeCode);
        }
        //1.通过存在的条件 查出符合条件的分页所有房屋数据
        List<HouseLeaseVO> vos = houseLeaseMapper.queryHouseLeaseByList(qo);
        //根据数据字段id 查询 一对多的 房屋标签name和id 如 邻地铁、可短租、临街商铺等多标签、之类
        getHouseFieldTag(vos);
        return vos;
    }


    /**
     * 根据id查询房屋详情单条数据
     *
     * @param houseId 房屋id
     * @return 返回这条数据的详情
     */
    @Override
    public HouseLeaseVO queryHouseLeaseOne(Long houseId, String uid) {
        //1.查出单条数据
        HouseLeaseVO vo = houseLeaseMapper.queryHouseLeaseOne(houseId);

        if( vo == null ){
            return null;
        }

        //1.4查出 房屋标签 ...
        List<Long> advantageId = MyMathUtils.analysisTypeCode(vo.getHouseAdvantageId());
        if (!advantageId.isEmpty()) {
            //1.房屋优势标签 如 随时入住、电梯楼... 属于该条数据多选的 type = t_house_const 里面的 house_const_type
            vo.setHouseAdvantage(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
        }
        //1.5查出 家具标签
        List<Long> furnitureId = MyMathUtils.analysisTypeCode(vo.getHouseFurnitureId());
        if (!furnitureId.isEmpty()) {
            vo.setHouseFurniture(houseLeaseMapper.queryHouseConstNameByFurnitureId(furnitureId, 13L));
        }
        //1.6查出该条数据所有图片
        vo.setHouseImage(houseLeaseMapper.queryHouseAllImgById(vo.getHouseImageId()));
        //1.7查出房屋朝向
        vo.setHouseDirection(BusinessEnum.HouseDirectionEnum.getDirectionName(vo.getHouseDirection()));
        //1.8 查出房屋是否是被当前用户已收藏
        vo.setFavorite(houseLeaseMapper.isFavorite(houseId, uid) > 0);
        //1.9 房屋类型 code转换为文本 如 4室2厅1卫
        vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));
        return vo;
    }


    /**
     * 按参数对象属性更新房屋出租数据
     *
     * @param qo 参数对象
     * @return 返回更新影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateHouseLease(HouseLeaseQO qo) {
        //排除id的情况下 验证其他属性字段是否为Null 如果为Null则不进入dao层
        if(ValidatorUtils.fieldIsNull(qo, Arrays.asList("id", "uid"))){
            throw new LeaseException("没有需要被更新的数据!");
        }
        //换算优势标签和家具id
        if (qo.getHouseAdvantage() != null && !qo.getHouseAdvantage().isEmpty()) {
            qo.setHouseAdvantageId(MyMathUtils.getTypeCode(qo.getHouseAdvantage()));
        }
        if (qo.getHouseFurniture() != null && !qo.getHouseFurniture().isEmpty()) {
            qo.setHouseFurnitureId(MyMathUtils.getTypeCode(qo.getHouseFurniture()));
        }
        //图片地址不为空
        if (qo.getHouseImage() != null && qo.getHouseImage().length > 0) {
            //删除旧的图片
            houseLeaseMapper.deleteImageById(qo.getId());
            //存储新的图片
            qo.setHouseImageId(SnowFlake.nextId());
            houseLeaseMapper.saveHouseLeaseImageById(qo.getHouseImage(), qo.getHouseImageId(), qo.getId());
        }
        //Es更新数据
        ElasticSearchImport.elasticOperation(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), isEmpty(qo.getHouseImage()) ? null : qo.getHouseImage()[0]);
        return houseLeaseMapper.updateHouseLease(qo);
    }

    private boolean isEmpty(Object[] array){
        return array == null || array.length == 0;
    }

    /**
     * 按用户id和 社区id查询 房主在当前社区出租的房源
     *
     * @param qo      包含用户id
     * @return 返回业主拥有的房产
     */
    @Override
    public List<HouseLeaseVO> ownerLeaseHouse(BaseQO<HouseLeaseQO> qo) {
        qo.setPage((qo.getPage() -  1) * qo.getSize());
        List<HouseLeaseVO> vos = houseLeaseMapper.ownerLeaseHouse(qo);
        vos.forEach( vo -> {
            //拿到第一张图片
            vo.setHouseImage(houseLeaseMapper.queryHouseImgById(vo.getHouseImageId(), vo.getId()));
            //房屋类型code转换成文本 如 040202 转换为 4室2厅2卫
            vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));
        });
        return vos;
    }

    /**
     * 通过用户id社区id房屋id验证用户是否存在此处房产
     *
     * @param userId           用户id
     * @param houseCommunityId 社区id
     * @param houseId          房屋id
     * @return 返回是否存在结果
     */
    @Override
    public boolean existUserHouse(String userId, Long houseCommunityId, Long houseId) {
        return houseLeaseMapper.isExistUserHouse(userId, houseCommunityId, houseId) == 0;
    }


    /**
     * 根据用户id 和社区id 查询用户在这个社区的可发布房源
     *
     * @param userId      用户id
     * @param communityId 社区id
     * @return 返回List数据 如果有多条
     */
    @Override
    public List<HouseVo> ownerHouse(String userId, Long communityId) {
        return houseLeaseMapper.ownerHouse(userId, communityId);
    }


    /**
     * [为了后续方便修改、使用单表匹配搜索] 去缓存取标签的方式
     * 按小区名或房屋出租标题或房屋地址模糊搜索匹配接口
     *
     * @param qo 搜索参数
     * @return 返回搜索到的列表
     */
    @Override
    public List<HouseLeaseVO> searchLeaseHouse(BaseQO<HouseLeaseQO> qo) {
        //分页
        qo.setPage( (qo.getPage() - 1) * qo.getSize() );
        String searchText = qo.getQuery().getSearchText();
        //验证 text 如果是纯数字 则按照租金来搜索
        boolean b = compile("^[0-9]*$").matcher(searchText).find();
        List<HouseLeaseVO> vos;
        if (b && StrUtil.isNotBlank(searchText)){
            //按租金搜索
            vos = houseLeaseMapper.searchLeaseHouseByPrice(qo);
        } else {
            //按 地址 标题搜索
            vos = houseLeaseMapper.searchLeaseHouseByText(qo);
        }
        if( vos == null ){
            return null;
        }
        getHouseFieldTag(vos);
        return vos;
    }



    /**
     * 验证houseId是否已经发布
     */
    @Override
    public boolean alreadyPublish(Long houseId) {
        return houseLeaseMapper.alreadyPublish(houseId) > 0;
    }



    /**
     * @author YuLF
     * @Param   vos         查询好的数据列表
     * @since   2020/12/30 16:39
     */
    private void getHouseFieldTag(List<HouseLeaseVO> vos) {
        //对字段一对多的标签做处理
        for (HouseLeaseVO vo : vos) {
            //从数字中解析出 常量的 code
            List<Long> advantageId = MyMathUtils.analysisTypeCode(vo.getHouseAdvantageId());
            if (!advantageId.isEmpty()) {
                //1.房屋优势标签 如 随时入住、电梯楼... 属于该条数据多选的 type = t_house_const 里面的 house_const_type
                vo.setHouseAdvantage(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
            }
            //2.从中间表查出该出租房屋的 第一张显示图片 用于列表标签展示
            vo.setHouseImage(houseLeaseMapper.queryHouseImgById(vo.getHouseImageId(), vo.getId()));
            //3.通过出租方式id 查出 出租方式文本：如 合租、整租之类
            vo.setHouseLeaseMode(houseConstService.getConstNameByConstTypeCode(vo.getHouseLeasemodeId(), 11L));
            //4.通过户型id 查出 户型id对应的文本：如 四室一厅、三室一厅...
            vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));
        }
    }


}
