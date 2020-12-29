package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseLeaseConstEntity;
import com.jsy.community.entity.lease.HouseReserveEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseReserveQO;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.lease.HouseReserveVO;
import com.jsy.community.api.IHouseConstService;
import com.jsy.community.api.IHouseReserveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YuLF
 * @since 2020-12-26 13:55
 */
@Slf4j
@ApiJSYController
@RestController
@Api(tags = "出租房屋预约控制器")
    @RequestMapping("/house/reserve")
public class HouseReserveController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseReserveService iHouseReserveService;


    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IHouseConstService houseConstService;

    @Login
    @GetMapping("/datetime")
    @ApiOperation("预约常量时间查询接口")
    public CommonResult<Map<String, List<String>>> datetime() {
        //获得租房可选择的预约时间常量
        List<HouseLeaseConstEntity> houseConstListByType = houseConstService.getHouseConstListByType(String.valueOf(15));
        List<String> reserveTime =  houseConstListByType.stream().map(HouseLeaseConstEntity::getHouseConstName).collect(Collectors.toList());
        Map<String, List<String>> reserveDateTime = new HashMap<>(2);
        reserveDateTime.put("reserveTime", reserveTime);
        //算出今天、明天、+后面5天
        reserveDateTime.put("reserveDate", getWeekDate());
        return CommonResult.ok(reserveDateTime);
    }


    @Login
    @PostMapping("/add")
    @ApiOperation("预约提交接口")
    public CommonResult<Boolean> add(@RequestBody HouseReserveEntity qo) {
        //基本参数验证
        ValidatorUtils.validateEntity(qo, HouseReserveEntity.add.class);
        //2.房屋验证
        qo.setReserveUid(UserUtils.getUserId());
        return CommonResult.ok(iHouseReserveService.add(qo),"提交预约成功!");
    }


    @Login
    @DeleteMapping("/cancel")
    @ApiOperation("预约取消接口")
    public CommonResult<Boolean> cancel( @RequestBody HouseReserveQO qo) {
        ValidatorUtils.validateEntity(qo, HouseReserveQO.cancel.class);
        qo.setReserveUid(UserUtils.getUserId());
        Boolean cancel = iHouseReserveService.cancel(qo);
        return CommonResult.ok( cancel ? "取消预约成功!" : "取消预约失败!数据不存在");
    }

    @Login
    @PostMapping("/confirm")
    @ApiOperation("预约确认接口")
    public CommonResult<Boolean> confirm( @RequestBody HouseReserveQO qo ) {
        Boolean confirm = iHouseReserveService.confirm(qo, UserUtils.getUserId());
        return CommonResult.ok( confirm ? "确认预约成功!" : "确认预约失败!重复提交或数据不存在!");
    }


    @Login
    @PostMapping("/whole")
    @ApiOperation("全部预约接口")
    public CommonResult<List<HouseReserveVO>> whole(@RequestBody BaseQO<HouseReserveQO> qo) {
        //预约分为两部分：1. 租客预约我发布的房子  2.我预约其他人发布的房子
        ValidatorUtils.validatePageParam(qo);
        return CommonResult.ok(iHouseReserveService.whole(qo, UserUtils.getUserId()));
    }


    /**
     * 获取今天、明天以及后面5天的字符串，
     */
    private List<String> getWeekDate(){
        List<String> list = new ArrayList<>(7);
        list.add("今天");
        list.add("明天");
        Date currentDate = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("MM.dd");
        Calendar c = Calendar.getInstance();
        //获取今明天后面的5天
        for( int i  = 2 ; i < 7; i++){
            c.add(Calendar.DAY_OF_MONTH, i);
            list.add(sf.format(c.getTime()) + " " + getWeekOfDate(currentDate, i, c));
        }
        return list;
    }

    /**
     * 获取 day 天后的周几
     * @param date  当前时间
     * @param day   几天后
     * @param cal   日期对象
     * @return      返回 day天后的周几
     */
    private String getWeekOfDate(Date date, int day, Calendar cal) {
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        cal.setTime(date);
        //今天周几的下标
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        int i = w + day;
        if(  i  - 7 < 0 ){
            return weekDays[i];
        } else {
            return weekDays[i - 7];
        }
    }
}