package com.jsy.community.controller;

import com.jsy.community.api.IPropertyMarketCategoryService;
import com.jsy.community.api.IPropertyMarketService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyMarketQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.proprietor.ProprietorMarketVO;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@Api(tags = "社区集市")
@RestController
@RequestMapping("/market")
// @ApiJSYController
public class PropertyMarketController {
    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyMarketService marketService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IPropertyMarketCategoryService categoryService;

    private final String[] img ={"jpg","png","jpeg"};
    private static final String BUCKET_NAME = "market";
    private static final String CATEGORY_NAME = "热门商品";

//    /**
//     * @Description: 发布新商品
//     * @Param: [marketQO]
//     * @Return: com.jsy.community.vo.CommonResult
//     * @Author: Tian
//     * @Date: 2021/8/21-15:43
//     **/
//    @PostMapping("/addMarket")
//    @ApiOperation("社区集市发布商品")
//    @Login
//    public CommonResult addMarket(@RequestBody ProprietorMarketQO marketQO){
//        String userId = UserUtils.getUserId();
//        int i = marketQO.getPrice().compareTo(BigDecimal.valueOf(BigDecimal.ROUND_DOWN));
//        if (marketQO.getNegotiable()==0){   //选择不面议  价格不能小于0
//                if (i<0){
//                    throw new JSYException("价格有误,请重新输入");
//                }
//        }else {
//            marketQO.setPrice(new BigDecimal(0));  //面议价格设为0
//        }
//        ValidatorUtils.validateEntity(marketQO,ProprietorMarketQO.proprietorMarketValidated.class);
//        boolean b = marketService.addMarket(marketQO,userId);
//        return CommonResult.ok("发布成功");
//    }
//   /**
//    * @Description: 修改发布是商品
//    * @Param: [marketQO]
//    * @Return: com.jsy.community.vo.CommonResult
//    * @Author: Tian
//    * @Date: 2021/8/21-15:44
//    **/
//   @PostMapping("/updateMarket")
//    @ApiOperation("社区集市发布商品")
//    @Login
//    public CommonResult updateMarket(@RequestBody ProprietorMarketQO marketQO){
//        String userId = UserUtils.getUserId();
//       if (marketQO.getNegotiable()==0){   //选择不面议  价格不能小于0
//           if (marketQO.getPrice()==null){
//               throw new JSYException("价格有误,请重新输入");
//           }
//           int i = marketQO.getPrice().compareTo(BigDecimal.valueOf(0));
//           if (i<0){
//               throw new JSYException("价格有误,请重新输入");
//           }
//       }else {
//           marketQO.setPrice(new BigDecimal(0));  //面议价格设为0
//       }
//        boolean b = marketService.updateMarket(marketQO,userId);
//        return CommonResult.ok("修改成功");
//    }
    /**
     * @Description: 删除发布的商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/21-15:44
     **/
    @DeleteMapping("/deleteBlacklist")
    @ApiOperation("社区集市删除商品")
    @Permit("community:property:market:deleteBlacklist")
    public CommonResult deleteBlacklist(@RequestParam("id") Long id){

        boolean b = marketService.deleteBlacklist(id);
        return CommonResult.ok("删除成功");
    }




//    /**
//     * @Description: 查询用户已发布或已下架的商品
//     * @Param: [id]
//     * @Return: com.jsy.community.vo.CommonResult
//     * @Author: Tian
//     * @Date: 2021/8/21-15:44
//     **/
//    @PostMapping("/selectMarketPage")
//    @ApiOperation("查询用户已发布或已下架的商品")
//    @Login
//    public CommonResult selectMarketPage(@RequestBody  BaseQO<ProprietorMarketEntity> baseQO){
//        String userId = UserUtils.getUserId();
//        ValidatorUtils.validatePageParam(baseQO);
//        if (baseQO.getQuery()==null) {
//            baseQO.setQuery(new ProprietorMarketEntity());
//        }
//        Map<String,Object> map = marketService.selectMarketPage(baseQO,userId);
//        return CommonResult.ok(map,"查询成功");
//    }
    /**
     * @Description: 查询单条商品详情
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/21-15:44
     **/
    @GetMapping("/SelectOneMarket")
    @ApiOperation("查询单条商品详情")
    @Permit("community:property:market:SelectOneMarket")
    public CommonResult SelectOneMarket(@RequestParam("id") Long id){
      ProprietorMarketVO marketVO =  marketService.findOne(id);
        return CommonResult.ok(marketVO,"查询成功");
    }


    /**
     * @Description: 查询所有已发布的商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/21-15:44
     **/
    @PostMapping("/selectMarketAllPage")
    @ApiOperation("社区集市所有已发布商品")
    @Permit("community:property:market:selectMarketAllPage")
    public CommonResult selectMarketAllPage(@RequestBody  BaseQO<PropertyMarketQO> baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        if (baseQO.getQuery()==null){
            baseQO.setQuery(new PropertyMarketQO());
        }
        Map<String,Object> map =  marketService.selectMarketAllPage(baseQO);

        return CommonResult.ok(map,"查询成功");

    }


    /**
     * @Description: 查询黑名单的商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/21-15:44
     **/
    @PostMapping("/selectMarketBlacklist")
    @ApiOperation("查询黑名单的商品")
    @Permit("community:property:market:selectMarketBlacklist")
    public CommonResult selectMarketBlacklist(@RequestBody  BaseQO baseQO){
        ValidatorUtils.validatePageParam(baseQO);
        Map<String,Object> map =  marketService.selectMarketBlacklist(baseQO);

        return CommonResult.ok(map,"查询成功");

    }
    /**
     * @Description: 修改上下架商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/21-15:44
     **/
    @PostMapping("/updateState")
    @ApiOperation("修改上下架商品")
    @Permit("community:property:market:updateState")
    public CommonResult updateState(@RequestParam("id")Long id,@RequestParam("state") Integer state){
        boolean b = marketService.updateState(id,state);
        return CommonResult.ok("修改成功");
    }

    /**
     * @Description: 修改屏蔽商品
     * @Param: [id]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/21-15:44
     **/
    @PostMapping("/updateShield")
    @ApiOperation("修改屏蔽商品")
    @Permit("community:property:market:updateShield")
    public CommonResult updateShield(@RequestParam("id")Long id,@RequestParam("shield") Integer shield){
        boolean b = marketService.updateShield(id,shield);
        return CommonResult.ok("修改成功");
    }


    /**
     * @Description: 商品图片批量上传
     * @author: Tian
     * @since: 2021/8/23 17:32
     * @Param:
     * @return:
     */
    @ApiOperation("社区集市商品图片上传")
    @PostMapping(value = "/uploadMarketImages")
    @Permit("community:property:market:uploadMarketImages")
    public CommonResult uploadMarketImages(@RequestParam("images") MultipartFile[] images, HttpServletRequest request)  {
        for (MultipartFile imag : images) {
            //获取文件名
            String originalFilename = imag.getOriginalFilename();
            System.out.println(originalFilename+"文件名");

            String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            System.out.println(s);

            if (images.length>3){
                return CommonResult.error("图片只能上传三张");
            }
            //将String[] img ={"jpg","png","jpeg"}转换为 list集合  在判断图片中是否包含 jpg png jpeg
            if (!Arrays.asList(img).contains(s)) {
                return CommonResult.error("您上传的图片中包含非图片文件！请上传图片，可用后缀"+ Arrays.toString(img));
            }
        }
        //返回文件上传地址
        String[] upload = MinioUtils.uploadForBatch(images, BUCKET_NAME);
        StringBuilder filePath = new StringBuilder();
        for (int i=0;i<upload.length;i++){
            filePath.append(upload[i]);
            if (i!=upload.length-1){
                filePath.append(",");
            }
        }


        String[] split = filePath.toString().split(",");
        return CommonResult.ok(split,"上传成功");
    }

    @ApiOperation("社区集市商品图片删除")
    @DeleteMapping(value = "/deleteMarketImages")
    @Permit("community:property:market:deleteMarketImages")
    public CommonResult deleteMarketImages(@RequestParam("images") String images) throws Exception {
        //返回文件上传地址
        MinioUtils.removeFile(images);

        return CommonResult.ok("删除成功");
    }



}
