package com.jsy.community.controller;


import com.alibaba.fastjson.JSON;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.Log;
import com.jsy.community.annotation.RequireRecentBrowse;
import com.jsy.community.api.IShopLeaseService;
import com.jsy.community.api.LeaseException;
import com.jsy.community.constant.*;
import com.jsy.community.entity.CommunityEntity;
import com.jsy.community.entity.log.ProprietorLog;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.qo.shop.ShopQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.shop.IndexShopVO;
import com.jsy.community.vo.shop.ShopDetailsVO;
import com.jsy.community.vo.shop.ShopLeaseVO;
import com.jsy.community.vo.shop.UserShopLeaseVO;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author lihao
 * @since 2020-12-17
 */
@Slf4j
// @ApiJSYController
@RestController
@RequestMapping("/shop")
@Api(tags = "?????????????????????")
public class ShopLeaseController {

    @DubboReference(version = Const.version, group = Const.group_lease, check = false)
    private IShopLeaseService shopLeaseService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @Author lihao
     * @Description ??????????????????
     * @Date 2021/1/13 15:49
     **/
    private static final Integer HEAD_MAX = 3;

    /**
     * @Author lihao
     * @Description ?????????????????????
     * @Date 2021/1/13 15:50
     **/
    private static final Integer MIDDLE_MAX = 8;

    /**
     * @Author lihao
     * @Description ?????????????????????
     * @Date 2021/1/13 15:50
     **/
    private static final Integer OTHER_MAX = 8;

    /**
     * @Author lihao
     * @Description ???????????????  ?????????????????? XX???
     * @Date 2021/1/13 15:55
     **/
    private static final double NORM_MONEY = 10000.00;

    /**
     * @Author lihao
     * @Description ???????????????  ?????????????????? ??????
     * @Date 2021/1/13 15:55
     **/
    private static final double MIN_MONEY = 0.00;

    /**
     * @Author lihao
     * @Description ?????????????????????????????????
     * @Date 2021/2/7 15:55
     **/
    private static final Integer IMG_MAX = 19;

    @ApiOperation("??????????????????")
    @PostMapping("/uploadHeadImg")
    // @Permit("community:lease:shop:uploadHeadImg")
    public CommonResult uploadHeadImg(@RequestParam("file") MultipartFile[] files) {
        if (files.length > HEAD_MAX) {
            return CommonResult.error("??????????????????3??????");
        }
        String[] strings = MinioUtils.uploadForBatch(files, UploadBucketConst.SHOP_HEAD_BUCKET);
        for (String string : strings) {
            redisTemplate.opsForSet().add(UploadRedisConst.SHOP_HEAD_IMG_PART, string);
        }
        return CommonResult.ok(strings);
    }

    @ApiOperation("?????????????????????")
    @PostMapping("/uploadMiddleImg")
    // @Permit("community:lease:shop:uploadMiddleImg")
    public CommonResult uploadMiddleImg(@RequestParam("file") MultipartFile[] files) {
        if (files.length > MIDDLE_MAX) {
            return CommonResult.error("?????????????????????8??????");
        }
        String[] filePaths = MinioUtils.uploadForBatch(files, UploadBucketConst.SHOP_MIDDLE_BUCKET);
        for (String filePath : filePaths) {
            redisTemplate.opsForSet().add(UploadRedisConst.SHOP_MIDDLE_IMG_PART, filePath);
        }
        return CommonResult.ok(filePaths);
    }

    @ApiOperation("?????????????????????")
    @PostMapping("/uploadOtherImg")
    // @Permit("community:lease:shop:uploadOtherImg")
    public CommonResult uploadOtherImg(@RequestParam("file") MultipartFile[] files) {
        if (files.length > OTHER_MAX) {
            return CommonResult.error("?????????????????????8??????");
        }
        String[] filePaths = MinioUtils.uploadForBatch(files, UploadBucketConst.SHOP_OTHER_BUCKET);
        for (String filePath : filePaths) {
            redisTemplate.opsForSet().add(UploadRedisConst.SHOP_OTHER_IMG_PART, filePath);
        }
        return CommonResult.ok(filePaths);
    }


    @ApiOperation("????????????")
    @PostMapping("/addShop")
    @Log(operationType = LogTypeConst.INSERT, module = LogModule.LEASE, isSaveRequestData = true)
    // @Permit("community:lease:shop:addShop")
    public CommonResult addShop(@RequestBody ShopQO shop) {
        String[] imgPath = shop.getImgPath();
        if (imgPath == null || imgPath.length <= 0) {
            throw new LeaseException("?????????????????????");
        }
        if (imgPath.length > IMG_MAX) {
            throw new LeaseException("??????????????????????????????");
        }
        // ????????????????????? shop-head-img ??? shop-middle-img ??? shop-other-img?????????
        for (String s : imgPath) {
            boolean b = s.contains("shop-head-img") || s.contains("shop-middle-img") || s.contains("shop-other-img");
            if (!b) {
                throw new LeaseException("???????????????????????????????????????????????????");
            }
        }

        // ???????????????????????? shop-head-img ??? shop-middle-img ??? shop-other-img
        List<String> heads = new ArrayList<>();
        List<String> middles = new ArrayList<>();
        List<String> others = new ArrayList<>();
        int headCount = 0;
        int middleCount = 0;
        int otherCount = 0;
        for (String s : imgPath) {
            if (s.contains("shop-head-img")) {
                heads.add(s);
                headCount += 1;
            }
            if (s.contains("shop-middle-img")) {
                middles.add(s);
                middleCount += 1;
            }
            if (s.contains("shop-other-img")) {
                others.add(s);
                otherCount += 1;
            }
        }
        if (headCount == 0 || middleCount == 0 || otherCount == 0) {
            throw new LeaseException("?????????????????????");
        }
        if (headCount > HEAD_MAX) {
            throw new LeaseException("??????????????????????????????");
        }
        if (headCount > MIDDLE_MAX) {
            throw new LeaseException("?????????????????????????????????");
        }
        if (headCount > OTHER_MAX) {
            throw new LeaseException("?????????????????????????????????");
        }

        shop.setUid(UserUtils.getUserId());
        // ????????????
        shop.setSource(1);
        ValidatorUtils.validateEntity(shop, ShopQO.addShopValidate.class);
        shopLeaseService.addShop(shop);

        // ?????????????????????
        for (String head : heads) {
            redisTemplate.opsForSet().add(UploadRedisConst.SHOP_HEAD_IMG_ALL, head);
        }
        for (String middle : middles) {
            redisTemplate.opsForSet().add(UploadRedisConst.SHOP_MIDDLE_IMG_ALL, middle);
        }
        for (String other : others) {
            redisTemplate.opsForSet().add(UploadRedisConst.SHOP_OTHER_IMG_ALL, other);
        }

        return CommonResult.ok();
    }


    @ApiOperation("??????????????????")
    @RequireRecentBrowse
    @GetMapping("/getShop")
    @LoginIgnore({"00000tourist"})
    // @Permit("community:lease:shop:getShop")
    public CommonResult getShop(@ApiParam("??????id") @RequestParam Long shopId) {
        Map<String, Object> map = shopLeaseService.getShop(shopId, UserUtils.getUserId());
        if (map == null) {
            return CommonResult.ok(null);
        }

        // ??????????????????10000??????XX.XX??????
        ShopLeaseVO shop = (ShopLeaseVO) map.get("shop");

        BigDecimal monthMoney = shop.getMonthMoney();
        if (monthMoney.doubleValue() > NORM_MONEY) {
            String s = String.format("%.2f", monthMoney.doubleValue() / NORM_MONEY) + "???";
            shop.setMonthMoneyString(s);
        } else if (monthMoney.compareTo(BigDecimal.valueOf(MIN_MONEY)) == 0) {
            String s = "??????";
            shop.setMonthMoneyString(s);
        } else {
            String s = "" + shop.getMonthMoney();
            int i = s.lastIndexOf(".");
            String substring = s.substring(0, i) + "???";
            shop.setMonthMoneyString(substring);
        }
        // ??????????????????10000??????XX.XX??????

        // ??????????????????10000??????XX.XX??????
        BigDecimal transferMoney = shop.getTransferMoney();
        if (transferMoney.doubleValue() > NORM_MONEY) {
            String s = String.format("%.2f", transferMoney.doubleValue() / NORM_MONEY) + "???";
            shop.setTransferMoneyString(s);

        } else if (transferMoney.doubleValue() < MIN_MONEY) {
            String s = "??????";
            shop.setTransferMoneyString(s);
        } else {
            String s = "" + shop.getTransferMoney();
            int i = s.lastIndexOf(".");
            String substring = s.substring(0, i) + "???";
            shop.setTransferMoneyString(substring);
        }
        // ??????????????????10000??????XX.XX??????

        map.put("shop", shop);
        return CommonResult.ok(map);
    }

    @ApiOperation("????????????id????????????????????????????????????")
    @GetMapping("/getShopForUpdate")
    // @Permit("community:lease:shop:getShopForUpdate")
    public CommonResult getShopForUpdate(@ApiParam("??????id") @RequestParam Long shopId) {
        ShopDetailsVO detailsVO = shopLeaseService.getShopForUpdate(shopId);
        return CommonResult.ok(detailsVO);
    }

    @ApiOperation("????????????")
    @PostMapping("/updateShop")
    // @Permit("community:lease:shop:updateShop")
//	@Log(operationType = LogTypeConst.UPDATE, module = LogModule.LEASE, isSaveRequestData = true)
    public CommonResult updateShop(@RequestBody ShopQO shop) {
        String[] imgPath = shop.getImgPath();

        if (imgPath == null || imgPath.length <= 0) {
            throw new LeaseException("?????????????????????");
        }

        // ????????????????????? shop-head-img ??? shop-middle-img ??? shop-other-img?????????
        for (String s : imgPath) {
            boolean b = s.contains("shop-head-img") || s.contains("shop-middle-img") || s.contains("shop-other-img");
            if (!b) {
                throw new LeaseException("???????????????????????????????????????????????????");
            }
        }
        // ???????????????????????? shop-head-img ??? shop-middle-img ??? shop-other-img
        int headCount = 0;
        int middleCount = 0;
        int otherCount = 0;
        for (String s : imgPath) {
            if (s.contains("shop-head-img")) {
                headCount += 1;
            }
            if (s.contains("shop-middle-img")) {
                middleCount += 1;
            }
            if (s.contains("shop-other-img")) {
                otherCount += 1;
            }
        }
        if (headCount == 0 || middleCount == 0 || otherCount == 0) {
            throw new LeaseException("?????????????????????");
        }

        shop.setUid(UserUtils.getUserId());
        shop.setSource(1);
        ValidatorUtils.validateEntity(shop, ShopQO.updateShopValidate.class);
        shopLeaseService.updateShop(shop);
        return CommonResult.ok();
    }


    @ApiOperation("????????????")
    @DeleteMapping("/cancelShop")
    // @Permit("community:lease:shop:cancelShop")
    public CommonResult cancelShop(@ApiParam("??????id") @RequestParam Long shopId) {
        String userId = UserUtils.getUserId();
        shopLeaseService.cancelShop(userId, shopId);
        return CommonResult.ok();
    }

    @ApiOperation("?????????????????????????????????")
    @GetMapping("/listShop")
    // @Permit("community:lease:shop:listShop")
    public CommonResult listShop() {
        String userId = UserUtils.getUserId();
        List<UserShopLeaseVO> shops = shopLeaseService.listUserShop(userId);
        return CommonResult.ok(shops);
    }

    @ApiOperation("????????????")
    @GetMapping("/moreOption")
    // @Permit("community:lease:shop:moreOption")
    public CommonResult moreOption() {
        Map<String, Object> map = shopLeaseService.moreOption();
        return CommonResult.ok(map);
    }

    @ApiOperation("???????????????????????????[?????????????????????]")
    @GetMapping("/getPublishTags")
    // @Permit("community:lease:shop:getPublishTags")
    public CommonResult getPublishTags() {
        Map<String, Object> map = shopLeaseService.getPublishTags();
        return CommonResult.ok(map);
    }

    @ApiOperation("????????????id??????????????????")
    @GetMapping("/getCommunity")
    // @Permit("community:lease:shop:getCommunity")
    public CommonResult getCommunity(Long areaId) {
        List<CommunityEntity> communityList = shopLeaseService.getCommunity(areaId);
        return CommonResult.ok(communityList);
    }

    @ApiOperation("??????httpclient")
    @PostMapping("/httpclient")
    // @Permit("community:lease:shop:httpclient")
    public CommonResult getHttpclient(@RequestBody ProprietorLog log) {
        System.out.println(log);
        System.out.println("1");
        return CommonResult.ok();
    }

    /**
     * POST---????????????(????????????)
     *
     * @date 2018???7???13??? ??????4:18:50
     */
    public static void mai2n(String[] args) {
        // ??????Http?????????(???????????????:???????????????????????????;??????:?????????HttpClient???????????????????????????)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // ??????Post??????
        HttpPost httpPost = new HttpPost("http://localhost:8001/api/v1/lease/shop/httpclient");
        ProprietorLog user = new ProprietorLog();
        user.setName("?????????");

        // ????????????????????????fastjson??????Object?????????json?????????;
        // (????????????com.alibaba.fastjson.JSON???)
        String jsonString = JSON.toJSONString(user);

        StringEntity entity = new StringEntity(jsonString, "UTF-8");

        // post???????????????????????????????????????????????????;?????????entity??????post????????????
        httpPost.setEntity(entity);

        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // ????????????
        CloseableHttpResponse response = null;
        try {
            // ??????????????????(??????)Post??????
            response = httpClient.execute(httpPost);
            // ????????????????????????????????????
            HttpEntity responseEntity = response.getEntity();

            System.out.println("???????????????:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("?????????????????????:" + responseEntity.getContentLength());
                System.out.println("???????????????:" + EntityUtils.toString(responseEntity));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // ????????????
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @ApiOperation("????????????????????????????????????")
    @PostMapping("/getShopByCondition")
    @LoginIgnore({"00000tourist"})
    // @Permit("community:lease:shop:getShopByCondition")
    public CommonResult<PageInfo> getShopByCondition(@RequestBody BaseQO<HouseLeaseQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new HouseLeaseQO());
        }
        HouseLeaseQO query = baseQO.getQuery();
        if (query.getShopBusinessIdArrays() != null) {
            for (Long array : query.getShopBusinessIdArrays()) {
                if (array == 9) {
                    query.setShopBusinessIdArrays(null);
                }
            }
        }
        if (query.getShopTypeIdArrays() != null) {
            for (Long array : query.getShopTypeIdArrays()) {
                if (array == 1) {
                    query.setShopTypeIdArrays(null);
                }
            }
        }
        baseQO.setQuery(query);
        PageInfo<IndexShopVO> pageInfo = shopLeaseService.getShopByCondition(baseQO);
        return CommonResult.ok(pageInfo);
    }


}


