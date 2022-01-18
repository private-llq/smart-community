package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICarBlackListService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.property.CarBlackListEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.util.CarOperation;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import springfox.bean.validators.plugins.schema.MinMaxAnnotationPlugin;

import java.util.regex.Pattern;

@Api(tags = "车辆黑名单")
@RestController
@RequestMapping("/carBlackList")
@Slf4j
// @ApiJSYController
public class CarBlackListController {

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private ICarBlackListService blackListService;

   private static String el="[京津晋冀蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新][ABCDEFGHJKLMNPQRSTUVWXY][\\dABCDEFGHJKLNMxPQRSTUVWXYZ]{5}|[京津晋冀蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新][ABCDEFGHJKLMNPQRSTUVWXY][1-9DF][1-9ABCDEFGHJKLMNPQRSTUVWXYZ]\\d{3}[1-9DF]";


//    public static void main(String[] args) {
//       String els="[京津晋冀蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新][ABCDEFGHJKLMNPQRSTUVWXY][\\dABCDEFGHJKLNMxPQRSTUVWXYZ]{5}|" +
//                  "[京津晋冀蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新][ABCDEFGHJKLMNPQRSTUVWXY][1-9DF][1-9ABCDEFGHJKLMNPQRSTUVWXYZ]\\d{3}[1-9DF]";
//        String carNumber="沪A99996D";
//        boolean matches = Pattern.matches(els,carNumber);
//        System.out.println(matches);
//    }



    /**
     * 分页查询 黑名单
     * @param baseQO 车牌号
     * @return
     */
    @PostMapping("carBlackListPage")
    @Permit("community:property:carBlackList:carBlackListPage")
    public CommonResult<PageInfo> carBlackListPage(@RequestBody BaseQO<String> baseQO){
        PageInfo<CarBlackListEntity> pageInfo = blackListService.carBlackListPage(baseQO, UserUtils.getAdminCommunityId());
        return CommonResult.ok(pageInfo);
    }
    /**
     * 查询车牌是否是 黑名单
     * @param
     * @return
     */
    @PostMapping("carBlackListOne")
    @Permit("community:property:carBlackList:carBlackListOne")
    public CommonResult carBlackListEntity(@RequestParam("carNumber")String carNumber){
        Long adminCommunityId = UserUtils.getAdminCommunityId();

        CarBlackListEntity carBlackListEntity = blackListService.carBlackListOne(carNumber,adminCommunityId);

        return CommonResult.ok(carNumber,"查询成功");
    }


    /**
     * 添加进入黑名单
     * @param carBlackListEntity
     * @return
     */
    @PostMapping("saveBlackList")
    @CarOperation(operation = "新增了【车辆黑名单】")
    @Permit("community:property:carBlackList:saveBlackList")
    public CommonResult saveBlackList(@RequestBody CarBlackListEntity carBlackListEntity){
        String carNumber = carBlackListEntity.getCarNumber();
        boolean matches = Pattern.matches(el, carNumber);
        log.info("格式"+matches);
        if(!matches){
            throw new PropertyException(500,"车牌号格式错误");
        }
        blackListService.saveBlackList(carBlackListEntity,UserUtils.getAdminCommunityId());
        return CommonResult.ok();

    }


    /**
     * 移除黑名单
     * @param uid
     * @return
     */

    @DeleteMapping("delBlackList")
    @CarOperation(operation = "移除了【车辆黑名单】")
    public CommonResult delBlackList(@RequestParam("uid") String uid){
        blackListService.delBlackList(uid);
        return CommonResult.ok();
    }
    }
