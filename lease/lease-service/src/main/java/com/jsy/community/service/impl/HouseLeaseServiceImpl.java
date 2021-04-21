package com.jsy.community.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseConstService;
import com.jsy.community.api.IHouseLeaseService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.lease.HouseLeaseEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.mapper.HouseLeaseMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.MyMathUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.es.ElasticsearchImportProvider;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import com.jsy.community.vo.HouseVo;
import com.jsy.community.vo.lease.HouseImageVo;
import com.jsy.community.vo.lease.HouseLeaseSimpleVO;
import com.jsy.community.vo.lease.HouseLeaseVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

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
     * 保存房屋图片标签
     * 【整租、单间、合租】
     *
     * @param qo 参数对象
     */
    private void saveImage(HouseLeaseQO qo) {
        qo.setHouseImageId(SnowFlake.nextId());
        houseLeaseMapper.insertHouseImages(qo);
    }

    /**
     * 整租新增出租房屋数据
     * 后面需求改变可能会改动接口，暂不提取重复代码
     *
     * @param qo 请求参数接收对象
     * @return 返回新增是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addWholeLeaseHouse(HouseLeaseQO qo) {
        //1.保存房源数据
        qo.setId(SnowFlake.nextId());
        //保存房屋优势标签至中间表 随时入住、电梯楼、家电齐全等等
        qo.setHouseAdvantageId(MyMathUtils.getTypeCode(qo.getHouseAdvantageCode()));
        //保存房屋家具id
        qo.setHouseFurnitureId(MyMathUtils.getTypeCode(qo.getHouseFurnitureCode()));
        //保存出租要求
        qo.setLeaseRequireId(MyMathUtils.getTypeCode(qo.getLeaseRequireCode()));
        //保存区域id
        qo.setHouseAreaId(houseLeaseMapper.selectAreaIdByCommunityId(qo.getHouseCommunityId()));
        saveImage(qo);
        ElasticsearchImportProvider.elasticOperationSingle(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.INSERT, qo.getHouseTitle(), qo.getHouseImage()[0]);
        return houseLeaseMapper.addWholeLeaseHouse(qo) > 0;
    }


    /**
     * 后面需求改变可能会改动接口，暂不提取重复代码
     * 单间新增
     *
     * @param qo 请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSingleLeaseHouse(HouseLeaseQO qo) {
        //单间新增房源 相对于 整租 只是多一个 公共设施 、 房间设施  相当于把整租的家具(houseFurnitureCode) 分成了两部分
        qo.setId(SnowFlake.nextId());
        //保存房屋优势标签至中间表 随时入住、电梯楼、家电齐全等等
        qo.setHouseAdvantageId(MyMathUtils.getTypeCode(qo.getHouseAdvantageCode()));
        //这里置空 是因为 在sql中需要 `使用动态sql 进行空判断存储至数据库，这个参数是需要在业务上进行运算才能得到结果而不是前端传值
        qo.setCommonFacilitiesId(null);
        //保存公共设施  公共设施可能为Null
        if (!CollectionUtils.isEmpty(qo.getCommonFacilitiesCode())) {
            qo.setCommonFacilitiesId(MyMathUtils.getTypeCode(qo.getCommonFacilitiesCode()));
        }
        //保存 房间设施
        qo.setRoomFacilitiesId(MyMathUtils.getTypeCode(qo.getRoomFacilitiesCode()));
        //保存出租要求
        qo.setLeaseRequireId(MyMathUtils.getTypeCode(qo.getLeaseRequireCode()));
        //保存区域id
        qo.setHouseAreaId(houseLeaseMapper.selectAreaIdByCommunityId(qo.getHouseCommunityId()));
        //保存房屋图片标签
        saveImage(qo);
        ElasticsearchImportProvider.elasticOperationSingle(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.INSERT, qo.getHouseTitle(), qo.getHouseImage()[0]);
        return houseLeaseMapper.addSingleLeaseHouse(qo) > 0;
    }

    /**
     * 后面需求改变可能会改动接口，暂不提取重复代码
     * 合租新增
     *
     * @param qo 请求参数
     */
    @Override
    @Transactional( rollbackFor = Exception.class)
    public boolean addCombineLeaseHouse(HouseLeaseQO qo) {
        qo.setCommonFacilitiesId(null);
        qo.setRoommateExpectId(null);
        //单间新增房源 相对于 整租 只是多一个 公共设施 、 房间设施  相当于把整租的家具(houseFurnitureCode) 分成了两部分
        qo.setId(SnowFlake.nextId());
        //保存公共设施  公共设施可能为Null
        if (!CollectionUtils.isEmpty(qo.getCommonFacilitiesCode())) {
            qo.setCommonFacilitiesId(MyMathUtils.getTypeCode(qo.getCommonFacilitiesCode()));
        }
        //保存 房间设施
        qo.setRoomFacilitiesId(MyMathUtils.getTypeCode(qo.getRoomFacilitiesCode()));
        //保存出租要求
        qo.setLeaseRequireId(MyMathUtils.getTypeCode(qo.getLeaseRequireCode()));
        //保存室友期望
        if (!CollectionUtils.isEmpty(qo.getRoommateExpectCode())) {
            qo.setRoommateExpectId(MyMathUtils.getTypeCode(qo.getRoommateExpectCode()));
        }
        //保存区域id
        qo.setHouseAreaId(houseLeaseMapper.selectAreaIdByCommunityId(qo.getHouseCommunityId()));
        //保存房屋图片标签
        saveImage(qo);
        ElasticsearchImportProvider.elasticOperationSingle(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.INSERT, qo.getHouseTitle(), qo.getHouseImage()[0]);
        return houseLeaseMapper.addCombineLeaseHouse(qo) > 0;
    }


    /**
     * 根据用户id和 rowGuid 删除出租房源数据
     *
     * @param id     业务主键
     * @param userId 用户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delLeaseHouse(Long id, String userId) {
        //删除中间表 关于 这个用户关联的所有图片地址信息
        houseLeaseMapper.deleteImageById(id);
        //删除 t_house_lease 信息
        ElasticsearchImportProvider.elasticOperationSingle(id, RecordFlag.LEASE_HOUSE, Operation.DELETE, null, null);
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
        if (!CollectionUtils.isEmpty(qo.getQuery().getHouseAdvantageCode())) {
            long typeCode = MyMathUtils.getTypeCode(qo.getQuery().getHouseAdvantageCode());
            qo.getQuery().setHouseAdvantageId(typeCode);
        }
        //1.通过存在的条件 查出符合条件的分页所有房屋数据
        List<HouseLeaseVO> vos = houseLeaseMapper.queryHouseLeaseByList(qo);
        if( CollectionUtils.isEmpty(vos) ){
            return vos;
        }
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

        if (vo == null) {
            return null;
        }
        // 设立冗余字段
        HashMap<String, Long> redundancy = new HashMap<>();

        //1.1 公共设施标签解析
        List<Long> commonFacilities = MyMathUtils.analysisTypeCode(vo.getCommonFacilitiesId());
        if (!CollectionUtils.isEmpty(commonFacilities)) {
            vo.setCommonFacilitiesCode(houseConstService.getConstByTypeCodeForList(commonFacilities, 24L));
        }

        //1.2 房屋设施标签解析
        List<Long> roomFacilities = MyMathUtils.analysisTypeCode(vo.getRoomFacilitiesId());
        if (!CollectionUtils.isEmpty(roomFacilities)) {
            vo.setRoomFacilitiesCode(houseConstService.getConstByTypeCodeForList(roomFacilities, 23L));
        }

        //1.4查出 房屋标签 ...
        List<Long> advantageId = MyMathUtils.analysisTypeCode(vo.getHouseAdvantageId());
        if (!CollectionUtils.isEmpty(advantageId)) {
            vo.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
        }

        //1.5查出 家具标签
        List<Long> furnitureId = MyMathUtils.analysisTypeCode(vo.getHouseFurnitureId());
        Map<String, Long> houseFurniture = new HashMap<>();
        if (!CollectionUtils.isEmpty(furnitureId)) {
            vo.setHouseFurniture(houseLeaseMapper.queryHouseConstNameByFurnitureId(furnitureId, 13L));
            houseFurniture = houseConstService.getConstByTypeCodeForList(furnitureId, 13L);
        }

        //1.6查出该条数据所有图片
        vo.setHouseImage(houseLeaseMapper.queryHouseAllImgById(vo.getHouseImageId()));
        //1.7查出房屋朝向
        vo.setHouseDirection(BusinessEnum.HouseDirectionEnum.getDirectionName(vo.getHouseDirectionId()));
        //1.8 查出房屋是否是被当前用户已收藏
        vo.setFavorite(houseLeaseMapper.isFavorite(houseId, uid) > 0);
        //1.9 房屋类型 code转换为文本 如 4室2厅1卫
        vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));

        // 为冗余熟悉添加值
        if (vo.getCommonFacilitiesCode() != null) {
            redundancy.putAll(vo.getCommonFacilitiesCode());
        }
        if (vo.getRoomFacilitiesCode() != null) {
            redundancy.putAll(vo.getRoomFacilitiesCode());
        }
        if (vo.getHouseAdvantageCode() != null) {
            redundancy.putAll(vo.getHouseAdvantageCode());
        }
        if (houseFurniture != null) {
            redundancy.putAll(houseFurniture);
        }
        vo.setRedundancy(redundancy);

        return vo;
    }


    /**
     * 【整租更新】按参数对象属性更新房屋出租数据
     *
     * @param qo 参数对象
     * @return 返回更新影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateWholeLease(HouseLeaseQO qo) {
        //换算优势标签、和家具id、 出租要求
        if (!CollectionUtils.isEmpty(qo.getHouseAdvantageCode())) {
            qo.setHouseAdvantageId(MyMathUtils.getTypeCode(qo.getHouseAdvantageCode()));
        }
        if (!CollectionUtils.isEmpty(qo.getHouseFurnitureCode())) {
            qo.setHouseFurnitureId(MyMathUtils.getTypeCode(qo.getHouseFurnitureCode()));
        }
        //保存区域id
        qo.setHouseAreaId(houseLeaseMapper.selectAreaIdByCommunityId(qo.getHouseCommunityId()));
        //出租要求
        setLeaseRequireCode(qo);
        updateHouseImage(qo);
        ElasticsearchImportProvider.elasticOperationSingle(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), qo.getHouseImage()[0]);
        return houseLeaseMapper.updateHouseLease(qo) > 0;
    }


    /**
     * 【单间更新】
     *
     * @param qo 参数对象
     * @return 返回更新影响行数
     */
    @Override
    public Boolean updateSingleRoom(HouseLeaseQO qo) {
        //换算优势标签、和公共设施、房间设施id、 出租要求
        if (!CollectionUtils.isEmpty(qo.getHouseAdvantageCode())) {
            qo.setHouseAdvantageId(MyMathUtils.getTypeCode(qo.getHouseAdvantageCode()));
        }
        //公共设施 房屋设施
        if (!CollectionUtils.isEmpty(qo.getCommonFacilitiesCode())) {
            qo.setCommonFacilitiesId(MyMathUtils.getTypeCode(qo.getCommonFacilitiesCode()));
        }
        if (!CollectionUtils.isEmpty(qo.getRoomFacilitiesCode())) {
            qo.setRoomFacilitiesId(MyMathUtils.getTypeCode(qo.getRoomFacilitiesCode()));
        }
        //保存城市区域id
        qo.setHouseAreaId(houseLeaseMapper.selectAreaIdByCommunityId(qo.getHouseCommunityId()));
        //出租要求
        setLeaseRequireCode(qo);
        updateHouseImage(qo);
        ElasticsearchImportProvider.elasticOperationSingle(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), qo.getHouseImage()[0]);
        return houseLeaseMapper.updateHouseLease(qo) > 0;
    }

    /**
     * 更新图片
     *
     * @param qo 参数对象
     */
    private void updateHouseImage(HouseLeaseQO qo) {
        //图片地址不为空
        if (!isEmpty(qo.getHouseImage())) {
            //删除旧的图片
            houseLeaseMapper.deleteImageById(qo.getId());
            //存储新的图片
            qo.setHouseImageId(SnowFlake.nextId());
            houseLeaseMapper.saveHouseLeaseImageById(qo.getHouseImage(), qo.getHouseImageId(), qo.getId());
        }
    }

    /**
     * 【整租和单间】
     * 设置出租要求
     *
     * @param qo 参数对象
     */
    private void setLeaseRequireCode(HouseLeaseQO qo) {
        if (!CollectionUtils.isEmpty(qo.getLeaseRequireCode())) {
            qo.setLeaseRequireId(MyMathUtils.getTypeCode(qo.getLeaseRequireCode()));
        }
    }

    /**
     * 【合租更新】
     *
     * @param qo 参数对象
     * @return 返回更新影响行数
     */
    @Override
    public Boolean updateCombineLease(HouseLeaseQO qo) {
        //对于整租和单间的值置空
        qo.setLeaseRequireId(null);
        qo.setHouseFurnitureId(null);
        //公共设施 房屋设施
        if (!CollectionUtils.isEmpty(qo.getCommonFacilitiesCode())) {
            qo.setCommonFacilitiesId(MyMathUtils.getTypeCode(qo.getCommonFacilitiesCode()));
        }
        if (!CollectionUtils.isEmpty(qo.getRoomFacilitiesCode())) {
            qo.setRoomFacilitiesId(MyMathUtils.getTypeCode(qo.getRoomFacilitiesCode()));
        }
        //室友期望
        if (!CollectionUtils.isEmpty(qo.getRoommateExpectCode())) {
            qo.setRoommateExpectId(MyMathUtils.getTypeCode(qo.getRoommateExpectCode()));
        }
        //保存城市区域id
        qo.setHouseAreaId(houseLeaseMapper.selectAreaIdByCommunityId(qo.getHouseCommunityId()));
        updateHouseImage(qo);
        ElasticsearchImportProvider.elasticOperationSingle(qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), qo.getHouseImage()[0]);
        return houseLeaseMapper.updateHouseLease(qo) > 0;
    }

    private boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 按用户id和 社区id查询 房主在当前社区出租的房源
     *
     * @param qo 包含用户id
     * @return 返回业主拥有的房产
     */
    @Override
    public List<HouseLeaseVO> ownerLeaseHouse(BaseQO<HouseLeaseQO> qo) {
        qo.setPage((qo.getPage() - 1) * qo.getSize());
        List<HouseLeaseVO> vos = houseLeaseMapper.ownerLeaseHouse(qo);
        List<Long> voImageIds = new ArrayList<>(vos.size());
        vos.forEach(vo -> {
            voImageIds.add(vo.getHouseImageId());
            //房屋类型code转换成文本 如 040202 转换为 4室2厅2卫
            vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));
        });
        if( !CollectionUtils.isEmpty(voImageIds) ){
            //根据 图片 id 集合  in 查出所有的图片 url 和 对应的租赁id
            List<HouseImageVo> houseImageVos = houseLeaseMapper.selectBatchImage(voImageIds);
            //由于一个图片可能存在于多条 列表数据 只显示一条 根据图片id(field_id)是用Map 去重   toMap里面第一个逗号前面的参数 是作为Map的Key  第二个逗号前面的 是作为Map的值， 第三个参数是 重载函数，如果Map中有重复 那就还是用前面的值
            Map<Long, HouseImageVo> houseImageVoMap = houseImageVos.stream().collect(Collectors.toMap(HouseImageVo::getFieldId, houseImageVo -> houseImageVo,(value1, value2) -> value1));
            //把图片 设置到返回集合每一个对象
            vos.forEach( vo -> {
                //该数据的图片对象不为空!
                HouseImageVo houseImageVo = houseImageVoMap.get(vo.getHouseImageId());
                if( Objects.nonNull(houseImageVo) ){
                    vo.setHouseImage(Collections.singletonList(houseImageVo.getImgUrl()));
                }
            });
        }
        return vos;
    }


    /**
     * 通过用户id社区id房屋id验证用户是否存在此处房产
     *
     * @param userId           用户id
     * @param houseCommunityId 社区id
     * @param houseId          房屋id
     * @param operation        操作符
     */
    @Override
    public void checkHouse(String userId, Long houseCommunityId, Long houseId, Operation operation) {
        if( houseLeaseMapper.isExistUserHouse(userId, houseCommunityId, houseId) == 0 ){
            throw new LeaseException(JSYError.BAD_REQUEST.getCode(), "您在此处未登记房产!");
        }
        if( operation == Operation.INSERT ){
            Integer publishLeaseRow = houseLeaseMapper.getPublishLease(userId, houseId);
            if( publishLeaseRow >= BusinessConst.USER_PUBLISH_LEASE_MAX){
                throw new LeaseException(JSYError.NOT_IMPLEMENTED.getCode(), "您发布的房源已经达到最大发布数量!");
            }
        }
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
        qo.setPage((qo.getPage() - 1) * qo.getSize());
        String searchText = qo.getQuery().getSearchText();
        //验证 text 如果是纯数字 则按照租金来搜索
        boolean b = compile("^[0-9]*$").matcher(searchText).find();
        List<HouseLeaseVO> vos;
        if (b && StrUtil.isNotBlank(searchText)) {
            //按租金搜索
            vos = houseLeaseMapper.searchLeaseHouseByPrice(qo);
        } else {
            //按 地址 标题搜索
            vos = houseLeaseMapper.searchLeaseHouseByText(qo);
        }
        if (CollectionUtils.isEmpty(vos)) {
            return null;
        }
        getHouseFieldTag(vos);
        return vos;
    }


    /**
     * 验证houseId是否已经发布
     */
    @Override
    public Boolean alreadyPublish(Long houseId) {
        return houseLeaseMapper.alreadyPublish(houseId) > 0;
    }

    @Override
    public List<CommunityEntity> allCommunity(Long cityId, String userId) {
        return houseLeaseMapper.allCommunity(cityId, userId);
    }

    @Override
    public HouseLeaseVO editDetails(Long houseId, String uid) {
        HouseLeaseVO vo = houseLeaseMapper.editDetails(houseId, uid);
        if (Objects.isNull(vo)) {
            return null;
        }
        //查出小区名称 和 房屋地址
        Map<String, String> communityNameAndHouseAddr = houseLeaseMapper.getUserAddrById(vo.getHouseCommunityId(), vo.getHouseId());
        if( Objects.nonNull( communityNameAndHouseAddr ) ){
            vo.setHouseCommunityName( communityNameAndHouseAddr.get("communityName") );
            vo.setHouseAddress( communityNameAndHouseAddr.get("houseAddress") );
        }
        //查出房屋朝向
        vo.setHouseDirection(BusinessEnum.HouseDirectionEnum.getDirectionName(vo.getHouseDirectionId()));
        //房屋类型 code转换为文本 如 4室2厅1卫
        vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));
        vo.setHouseImage(houseLeaseMapper.queryHouseAllImgById(vo.getHouseImageId()));
        return vo;
    }

    /**
     *@Author: Pipi
     *@Description: 查询房屋出租数据单条简略详情
     *@param: houseId: 出租房屋主键
     *@Return: com.jsy.community.vo.lease.HouseLeaseSimpleVO
     *@Date: 2021/3/27 16:25
     **/
    @Override
    public HouseLeaseSimpleVO queryHouseLeaseSimpleDetail(Long houseId) {
        //1.查出单条数据
        HouseLeaseSimpleVO vo = houseLeaseMapper.queryHouseLeaseSimpleDetail(houseId);

        if (vo == null) {
            return null;
        }
        //1.2查出该条数据所有图片
        vo.setHouseImage(houseLeaseMapper.queryHouseAllImgById(vo.getHouseImageId()));
        //1.3 房屋类型 code转换为文本 如 4室2厅1卫
        vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));
        //1.4查出小区名称 和 房屋地址
        Map<String, String> communityNameAndHouseAddr = houseLeaseMapper.getUserAddrById(vo.getHouseCommunityId(), vo.getHouseId());
        if( Objects.nonNull( communityNameAndHouseAddr ) ){
            vo.setHouseCommunityName( communityNameAndHouseAddr.get("communityName") );
            vo.setHouseAddress( communityNameAndHouseAddr.get("houseAddress") );
        }
        return vo;
    }


    /**
     * @author YuLF
     * @Param vos         查询好的数据列表
     * @since 2020/12/30 16:39
     */
    private void getHouseFieldTag(List<HouseLeaseVO> vos) {
        List<Long> voImageIds = new ArrayList<>(vos.size());
        //对字段一对多的标签做处理
        for (HouseLeaseVO vo : vos) {
            // 设立冗余字段
            HashMap<String, Long> redundancy = new HashMap<>();
            //从数字中解析出 常量的 code
            List<Long> advantageId = MyMathUtils.analysisTypeCode(vo.getHouseAdvantageId());
            if (!CollectionUtils.isEmpty(advantageId)) {
                //1.房屋优势标签 如 随时入住、电梯楼... 属于该条数据多选的 type = t_house_const 里面的 house_const_type
                vo.setHouseAdvantageCode(houseConstService.getConstByTypeCodeForList(advantageId, 4L));
            } else {
                //如果优势标签得到的结果为Null 则该数据是合租
                Map<String, Long> advantageMap = new HashMap<>(2);
                //合租 列表数据 标签1 房屋朝向
                advantageMap.put(BusinessEnum.HouseDirectionEnum.getDirectionName(vo.getHouseDirectionId()), vo.getHouseDirectionId().longValue());
                //合租 列表数据 标签2 装修情况
                advantageMap.put(houseConstService.getConstNameByConstTypeCode(vo.getDecorationTypeId(), 18L), vo.getDecorationTypeId());
                vo.setHouseAdvantageCode(advantageMap);
            }

            //2.从中间表查出该出租房屋的 第一张显示图片 用于列表标签展示
            voImageIds.add(vo.getHouseImageId());
            //3.通过出租方式id 查出 出租方式文本：如 合租、整租之类
            vo.setHouseLeaseMode(houseConstService.getConstNameByConstTypeCode(vo.getHouseLeasemodeId(), 11L));
            //4.通过户型id 查出 户型id对应的文本：如 四室一厅、三室一厅...
            vo.setHouseType(HouseHelper.parseHouseType(vo.getHouseTypeCode()));

            //5.1 公共设施标签解析
            List<Long> commonFacilities = MyMathUtils.analysisTypeCode(vo.getCommonFacilitiesId());
            if (!CollectionUtils.isEmpty(commonFacilities)) {
                vo.setCommonFacilitiesCode(houseConstService.getConstByTypeCodeForList(commonFacilities, 24L));
            }
            //5.2 房屋设施标签解析
            List<Long> roomFacilities = MyMathUtils.analysisTypeCode(vo.getRoomFacilitiesId());
            if (!CollectionUtils.isEmpty(roomFacilities)) {
                vo.setRoomFacilitiesCode(houseConstService.getConstByTypeCodeForList(roomFacilities, 23L));
            }
            //5.3查出 家具标签
            List<Long> furnitureId = MyMathUtils.analysisTypeCode(vo.getHouseFurnitureId());
            Map<String, Long> houseFurniture = new HashMap<>();
            if (!CollectionUtils.isEmpty(furnitureId)) {
                vo.setHouseFurniture(houseLeaseMapper.queryHouseConstNameByFurnitureId(furnitureId, 13L));
                houseFurniture = houseConstService.getConstByTypeCodeForList(furnitureId, 13L);
            }
            // 为冗余熟悉添加值
            if (vo.getCommonFacilitiesCode() != null) {
                redundancy.putAll(vo.getCommonFacilitiesCode());
            }
            if (vo.getRoomFacilitiesCode() != null) {
                redundancy.putAll(vo.getRoomFacilitiesCode());
            }
            if (vo.getHouseAdvantageCode() != null) {
                redundancy.putAll(vo.getHouseAdvantageCode());
            }
            if (houseFurniture != null) {
                redundancy.putAll(houseFurniture);
            }
            vo.setRedundancy(redundancy);
        }
        //设置图片url
        if( !CollectionUtils.isEmpty(voImageIds) ){
            //根据 图片 id 集合  in 查出所有的图片 url 和 对应的租赁id
            List<HouseImageVo> houseImageVos = houseLeaseMapper.selectBatchImage(voImageIds);
            //由于一个图片可能存在于多条 列表数据 只显示一条 根据图片id(field_id)是用Map 去重   toMap里面第一个逗号前面的参数 是作为Map的Key  第二个逗号前面的 是作为Map的值， 第三个参数是 重载函数，如果Map中有重复 那就还是用前面的值
            Map<Long, HouseImageVo> houseImageVoMap = houseImageVos.stream().collect(Collectors.toMap(HouseImageVo::getFieldId, houseImageVo -> houseImageVo,(value1, value2) -> value1));
            //把图片 设置到返回集合每一个对象
            vos.forEach( vo -> {
                //该数据的图片对象不为空!
                HouseImageVo houseImageVo = houseImageVoMap.get(vo.getHouseImageId());
                if( Objects.nonNull(houseImageVo) ){
                    vo.setHouseImage(Collections.singletonList(houseImageVo.getImgUrl()));
                }
            });
        }
    }


}
