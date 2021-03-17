package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IComplainService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.qo.proprietor.PropertyComplainQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 投诉~意见
 * @author: Hu
 * @create: 2020-12-23 10:51
 **/
@Api(tags = "投诉建议前端控制器")
@RestController
@RequestMapping("/complain")
@ApiJSYController
@Login(allowAnonymous = true)
public class ComplainController {

    private final String[] img ={"jpg","png","jpeg"};

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IComplainService complainService;


    private static final String BUCKET_NAME = "complain";

    /**
     * @Description: 用户投诉接口
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    @Login
    @ApiOperation("用户投诉建议接口")
    @PostMapping("/addComplain")
    public CommonResult addComplain(@RequestBody ComplainEntity complainEntity){
        String userId = UserUtils.getUserId();
        complainEntity.setUid(userId);
        complainEntity.setStatus(0);
        complainEntity.setId(SnowFlake.nextId());
        complainEntity.setComplainTime(LocalDateTime.now());
        complainService.addComplain(complainEntity);
        return CommonResult.ok();
    }
    @Login
    @ApiOperation("物业投诉接口")
    @PostMapping("/propertyComplain")
    public CommonResult propertyComplain(@RequestBody PropertyComplainQO propertyComplainQO){
        String userId = UserUtils.getUserId();
        propertyComplainQO.setUid(userId);
        complainService.propertyComplain(propertyComplainQO);
        return CommonResult.ok();
    }
    /**
     * @Description: 查询用户所有的投诉建议
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    @Login
    @ApiOperation("用户查询所有投诉建议")
    @GetMapping("/selectUserIdComplain")
    public CommonResult selectUserIdComplain(){
        String userId = UserUtils.getUserId();

        List<ComplainEntity> complainEntities=complainService.selectUserIdComplain(userId);
        return CommonResult.ok(complainEntities);
    }

    /**
     * @Description: 投诉图片
     * @author: Hu
     * @since: 2021/2/23 17:32
     * @Param:
     * @return:
     */
    @Login
    @ApiOperation("投诉建议图片批量上传")
    @PostMapping(value = "/uploadComplainImages")
    public CommonResult uploadComplainImages(@RequestParam("complainImages")MultipartFile[] complainImages, HttpServletRequest request)  {
        for (MultipartFile complainImage : complainImages) {
            String originalFilename = complainImage.getOriginalFilename();
            String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            if (!Arrays.asList(img).contains(s)) {
                return CommonResult.error("您上传的图片中包含非图片文件！请上传图片，可用后缀"+ Arrays.toString(img));
            }
        }
        String[] upload = MinioUtils.uploadForBatch(complainImages, BUCKET_NAME);
        StringBuilder filePath = new StringBuilder();
        for (String s : upload) {
            filePath.append(s);
            filePath.append(",");
        }
        return CommonResult.ok(filePath,"上传成功");
    }



}
