package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.CarOperationService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.admin.AdminRoleEntity;
import com.jsy.community.entity.property.CarOperationLog;
import com.jsy.community.mapper.AdminRoleMapper;
import com.jsy.community.mapper.CarOperationMapper;
import com.jsy.community.qo.property.CarOperationLogQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.property.CarOperationLogVO;
import com.jsy.community.vo.property.PageVO;
import com.zhsj.baseweb.support.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DubboService(version = Const.version, group = Const.group_property)
@Slf4j
public class CarOperationServiceImpl extends ServiceImpl<CarOperationMapper, CarOperationLog> implements CarOperationService {


    @Resource
    private CarOperationMapper caroperationMapper;
    @Resource
    private AdminRoleMapper adminRoleMapper;

    @Override
    public PageVO selectCarOperationLogPag(CarOperationLogQO qo) {

        Long adminCommunityId =UserUtils.getAdminCommunityId();
        log.info("社区id"+adminCommunityId);
        PageVO<CarOperationLogVO> pageVO=new PageVO();


        Page<CarOperationLog> page = new Page<>(qo.getPage(), qo.getSize());

        QueryWrapper<CarOperationLog> queryWrapper = new QueryWrapper<>();


//        if (!ObjectUtils.isEmpty(qo.getUserRole())) {
//            queryWrapper.eq("user_role",qo.getUserRole());
//        }
        if (!ObjectUtils.isEmpty(qo.getCarNumber())) {
            queryWrapper.like("operation",qo.getCarNumber());
        }
        if (!ObjectUtils.isEmpty(qo.getTime())) {
            LocalDateTime localDateTime = qo.getTime().atStartOfDay();//当前
            LocalDateTime localDateTime1 = localDateTime.minusDays(-1);//第二天
            queryWrapper.le("operation_time",localDateTime1);
            queryWrapper.ge("operation_time", localDateTime);
            queryWrapper.eq("community_id",adminCommunityId);

        }

        Page<CarOperationLog> carOperationLogPage = caroperationMapper.selectPage(page, queryWrapper);
        List<CarOperationLog> records = carOperationLogPage.getRecords();
        List<CarOperationLogVO> voList=new ArrayList<>();
        for (CarOperationLog record : records) {
            CarOperationLogVO carOperationLogVO = new CarOperationLogVO();
           // AdminRoleEntity adminRoleEntity = adminRoleMapper.selectById(record.getUserRole());
            BeanUtils.copyProperties(record,carOperationLogVO);
           // carOperationLogVO.setUserRole(adminRoleEntity.getName());
            voList.add(carOperationLogVO);

        }
        pageVO.setRecords(voList);
        pageVO.setCurrent(carOperationLogPage.getCurrent());
        pageVO.setPages(carOperationLogPage.getPages());
        pageVO.setTotal(carOperationLogPage.getTotal());
        return pageVO;
    }
}
