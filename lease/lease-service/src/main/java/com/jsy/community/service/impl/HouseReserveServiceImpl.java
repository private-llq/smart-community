package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.BusinessEnum;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.mapper.HouseReserveMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.util.HouseHelper;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.vo.lease.HouseImageVo;
import com.jsy.community.vo.lease.HouseReserveVO;
import com.jsy.community.api.IHouseConstService;
import com.jsy.community.api.IHouseReserveService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.mapper.HouseLeaseMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YuLF
 * @since 2020-12-26 14:22
 */
@DubboService(version = Const.version, group = Const.group_lease)
public class HouseReserveServiceImpl extends ServiceImpl<HouseReserveMapper, HouseReserveEntity> implements IHouseReserveService {

    @Resource
    private HouseReserveMapper houseReserveMapper;

    @Resource
    private HouseLeaseMapper houseLeaseMapper;

    @Resource
    private HouseAsyncActuator houseAsyncActuator;

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;


    /**
     * 提交预约信息
     *
     * @return 返回是否预约信息提交成功
     * @author YuLF
     * @Param qo   请求参数对象
     * @since 2020/12/26 16:25
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean add(HouseReserveEntity qo) {
        // 查询预约房屋是否存在
        Integer integer = houseReserveMapper.existHouseLeaseId(qo.getHouseLeaseId());
        if (integer == 0) {
            throw new LeaseException("房屋信息不存在!");
        }
        qo.setId(SnowFlake.nextId());
        qo.setReserveStatus(1);
        Integer insert = houseReserveMapper.insertReserve(qo);
        //推送
        if (!insert.equals(BusinessConst.ZERO)) {
            houseAsyncActuator.pushMsg(qo.getReserveUid(), qo.getId(), "预约请求", "预约了您的房子", null);
            return true;
        }
        return false;
    }


    /**
     * 取消预约信息
     *
     * @param qo 取消预约 接收 参数 对象
     * @return 返回取消是否成功修改
     */
    @Override
    public Boolean cancel(HouseReserveQO qo) {
        Integer integer = 0;
        //0代表着状态预约已取消
        qo.setReserveStatus(0);
        String pushMsgUid = null;
        if (qo.getRequestType() == 1) {
            // 我预约的
            integer = houseReserveMapper.cancelMyReserveState(qo);
            // 通知房东,房东uid由推送方法获取
        } else {
            // 预约我的
            integer = houseReserveMapper.cancelReserveMeState(qo);
            // 通知租客
            // 获取这条预约的预约人uid
            HouseReserveEntity houseReserveEntity = baseMapper.selectById(qo.getId());
            pushMsgUid = houseReserveEntity.getReserveUid();
        }
        //推送
        if (!integer.equals(BusinessConst.ZERO)) {
            houseAsyncActuator.pushMsg(qo.getReserveUid(), qo.getId(), "取消预约", "取消了预约", pushMsgUid);
            return true;
        }
        return false;
    }


    /**
     * 全部预约信息
     *
     * @author YuLF
     * @since 2020/12/28 10:22
     */
    @Override
    public List<HouseReserveVO> whole(BaseQO<HouseReserveQO> qo, String uid) {
        qo.setPage((qo.getPage() - 1) * qo.getSize());
        List<HouseReserveVO> reserveVos = new ArrayList<>();
        if (qo.getQuery().getRequestType() == 1) {
            //1.查出我的预约信息
            reserveVos = houseReserveMapper.meReserveHouse(qo, uid);
        } else {
            //2.查出预约我的信息
            reserveVos = houseReserveMapper.reserveMeHouse(qo, uid);
        }
        //返回VO
        List<Long> voImageIds = new ArrayList<>(reserveVos.size());
        reserveVos.forEach(r -> {
            //1.从缓存通过id和类型取出 中文Name
            //整租还是合租
            r.setHouseLeaseMode(houseConstService.getConstNameByConstTypeCode(Long.parseLong(r.getHouseLeaseMode()), 11L));
            //押金方式
            r.setHouseLeaseDeposit(houseConstService.getConstNameByConstTypeCode(Long.parseLong(r.getHouseLeaseDeposit()), 1L));
            //房型结构：三室一厅、四室一厅...
            r.setHouseType(HouseHelper.parseHouseType(r.getHouseType()));
            //平方米
            r.setHouseSquareMeter(r.getHouseSquareMeter() + "m²");
            //房屋朝向
            r.setHouseDirection(BusinessEnum.HouseDirectionEnum.getDirectionName(r.getHouseDirectionId()));
            //2. 获取图片查询
            voImageIds.add(r.getHouseImageId());
        });
        //设置图片url
        if (!CollectionUtils.isEmpty(voImageIds)) {
            //根据 图片 id 集合  in 查出所有的图片 url 和 对应的租赁id
            List<HouseImageVo> houseImageVos = houseLeaseMapper.selectBatchImage(voImageIds);
            //由于一个图片可能存在于多条 列表数据 只显示一条 根据图片id(field_id)是用Map 去重   toMap里面第一个逗号前面的参数 是作为Map的Key  第二个逗号前面的 是作为Map的值， 第三个参数是 重载函数，如果Map中有重复 那就还是用前面的值
            Map<Long, HouseImageVo> houseImageVoMap = houseImageVos.stream().collect(Collectors.toMap(HouseImageVo::getFieldId, houseImageVo -> houseImageVo, (value1, value2) -> value1));
            //把图片 设置到返回集合每一个对象
            reserveVos.forEach(vo -> {
                //该数据的图片对象不为空!
                HouseImageVo houseImageVo = houseImageVoMap.get(vo.getHouseImageId());
                if (Objects.nonNull(houseImageVo)) {
                    vo.setHouseImageUrl(Collections.singletonList(houseImageVo.getImgUrl()));
                }
            });
        }
        return reserveVos;
    }


    /**
     * 确认预约信息
     *
     * @param qo 预约信息id参数对象
     * @return 返回确认是否成功修改
     */
    @Override
    public Boolean confirm(HouseReserveQO qo, String uid) {
        //验证当前提交请求的用户是否有权利修改这条预约信息 房子是他自己发布的 他才有权利确认
        String databasesUid = houseReserveMapper.getUidByHouseReserveId(qo.getId());
        if (!uid.equals(databasesUid)) {
            throw new LeaseException("您无权对此预约信息进行确认!");
        }
        Integer row = houseReserveMapper.confirm(qo);
        return row > 0;
    }

    @Override
    public Boolean reject(HouseReserveQO qo) {
        Integer integer = houseReserveMapper.rejectReserve(qo);
        if (!integer.equals(BusinessConst.ZERO)) {
            houseAsyncActuator.pushMsg(qo.getReserveUid(), qo.getId(), "拒绝预约", "不方便预约", null);
            return true;
        }
        return false;
    }

    /**
     * @Author: Pipi
     * @Description: 删除预约消息
     * @param: qo:
     * @Return: java.lang.Boolean
     * @Date: 2021/3/30 11:35
     **/
    @Override
    public Boolean delete(HouseReserveQO qo) {
        Integer result = 0;
        // 我预约的只能删除已取消和已完成状态的消息
        // 预约我的不能删除预约中的
        if (qo.getRequestType() == 1) {
            // 我预约的
            result = houseReserveMapper.deleteMyReserve(qo);
        } else {
            // 预约我的
            result = houseReserveMapper.deleteReserveMe(qo);
        }
        return result > 0;
    }

    /**
     *@Author: Pipi
     *@Description: 租房用户确认完成看房
     *@param: qo:
     *@Return: java.lang.Boolean
     *@Date: 2021/3/30 15:25
     **/
    @Override
    public Boolean completeChecking(HouseReserveQO qo) {
        // 完成看房只能由预约人操作完成
        Integer result = houseReserveMapper.completeChecking(qo);
        return result > 0;
    }


}
