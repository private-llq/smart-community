package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.IUserDataService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.utils.BadWordUtil2;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @program: com.jsy.community
 * @description: 用户个人信息
 * @author: Hu
 * @create: 2021-03-11 13:37
 **/
@Api(tags = "生活缴费前端控制器--查询")
@RestController
@RequestMapping("/userdata")
@ApiJSYController
public class UserDataController {

    private final String[] img ={"jpg","png","jpeg"};

    String regex = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserDataService userDataService;

    @GetMapping("/selectUserDataOne")
    @ApiOperation("查询个人信息")
    @Login
    public CommonResult selectUserDataOne(){
        String userId = UserUtils.getUserId();
        UserDataVO userDataVO = userDataService.selectUserDataOne(userId);
        return CommonResult.ok(userDataVO);
    }
    @PostMapping("/addAvatar")
    @ApiOperation("上传头像")
    @Login
    public CommonResult avatarUrl(@RequestParam("file") MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String s = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!Arrays.asList(img).contains(s)) {
            return CommonResult.error("请上传图片！可用后缀"+ Arrays.toString(img));
        }
        String upload = MinioUtils.upload(file, "avatar");
        return CommonResult.ok(upload,"上传成功");
    }
    @PutMapping("/updateUserData")
    @ApiOperation("修改个人信息")
    @Login
    public CommonResult updateUserData(@RequestBody UserDataQO userDataQO){
        if (BadWordUtil2.isContaintBadWord(userDataQO.getNickname(),BadWordUtil2.minMatchTYpe)){
            return CommonResult.error("该名称不可用：名称中存在敏感字！");
        }
        try {
            String s = new String(userDataQO.getNickname().getBytes("GBK"));
            if (s.length()<2||s.length()>16){
                return CommonResult.error("请输入2到16个字符，可使用英文、数字、汉子！");
            }
            Pattern pattern = Pattern.compile(regex);
            if (pattern.matcher(userDataQO.getNickname()).find()){
                return CommonResult.error("名称不能包含特殊字符！");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String userId = UserUtils.getUserId();
        userDataService.updateUserData(userDataQO,userId);
        return CommonResult.ok();
    }

}

