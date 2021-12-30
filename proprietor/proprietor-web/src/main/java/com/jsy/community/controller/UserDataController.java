package com.jsy.community.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jsy.community.api.IUserAccountService;
import com.jsy.community.api.IUserDataService;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.proprietor.UserDataQO;
import com.jsy.community.utils.BadWordUtil2;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.UserAccountVO;
import com.jsy.community.vo.UserDataVO;
import com.jsy.community.vo.UserInfoVo;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.rpc.IBaseUpdateUserRpcService;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @program: com.jsy.community
 * @description: 用户个人信息
 * @author: Hu
 * @create: 2021-03-11 13:37
 **/
@Api(tags = "用户个人信息(个人中心)")
@RestController
@RequestMapping("/userdata")
// @ApiJSYController
public class UserDataController {

    private final String[] img ={"jpg","png","jpeg"};

    String regex = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserDataService userDataService;
    
    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserAccountService userAccountService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUpdateUserRpcService baseUpdateUserRpcService;

    @GetMapping("/selectUserDataOne")
    @ApiOperation("查询个人信息")
    // @Permit("community:proprietor:userdata:selectUserDataOne")
    public CommonResult selectUserDataOne(){
        String userId = UserUtils.getUserId();
        UserDataVO userDataVO = userDataService.selectUserDataOne(userId);
        UserAccountVO balance = userAccountService.queryBalance(userId);
        Integer tickets = userAccountService.countTicketByUid(userId);
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(userDataVO));
        jsonObject.put("balance",balance.getBalance().setScale(2, RoundingMode.HALF_UP).toPlainString());
        jsonObject.put("tickets",tickets);
        return CommonResult.ok(jsonObject,"查询成功");
    }
    
    @PostMapping("/addAvatar")
    @ApiOperation("上传头像")
    // @Permit("community:proprietor:userdata:addAvatar")
    @Deprecated
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
    @ApiOperation("修改个人信息(头像、生日)")
    // @Permit("community:proprietor:userdata:updateUserData")
    public CommonResult updateUserData(@RequestBody UserDataQO userDataQO){
        userDataQO.setNickname(null);
        baseUpdateUserRpcService.updateUserInfo(UserUtils.getUserToken(),
                userDataQO.getNickname(),
                userDataQO.getAvatarUrl(),
                userDataQO.getBirthdayTime() == null ? null : userDataQO.getBirthdayTime().toString(),
                true);
        /*String userId = UserUtils.getUserId();
        userDataQO.setNickname(null);
        userDataService.updateUserData(userDataQO,userId);*/
        return CommonResult.ok();
    }
    
    @PutMapping("/updateUserNickName")
    @ApiOperation("修改个人名称")
    // @Permit("community:proprietor:userdata:updateUserNickName")
    public CommonResult updateUserNickName(@RequestParam("nickname") String nickname){
        if(StringUtil.isNotBlank(nickname)){
            if (getWordCount(nickname) < 2 || getWordCount(nickname) > 32){
                return CommonResult.error("请输入2到16个字符，可使用英文、数字、汉字！");
            }
            if (BadWordUtil2.isContaintBadWord(nickname,BadWordUtil2.minMatchTYpe)){
                return CommonResult.error("该名称不可用：名称中存在敏感字！");
            }
            Pattern pattern = Pattern.compile(regex);
            if (pattern.matcher(nickname).find()){
                return CommonResult.error("名称不能包含特殊字符！");
            }
        }else {
            return CommonResult.error("名称不能为空！");
        }
        /*String userId = UserUtils.getUserId();
        UserDataQO dataQO = new UserDataQO();
        dataQO.setNickname(nickname);
        userDataService.updateUserData(dataQO,userId);*/
        baseUpdateUserRpcService.updateUserInfo(UserUtils.getUserToken(),
                nickname,
                null,
                null,
                true);
        return CommonResult.ok();
    }

    /**
     * @author: Pipi
     * @description: 获取字符串字符长度
     * @param s:
     * @return: {@link int}
     * @date: 2021/12/28 14:02
     **/
    public static int getWordCount(String s)
    {
        int length = 0;
        for(int i = 0; i < s.length(); i++)
        {
            int ascii = Character.codePointAt(s, i);
            if(ascii >= 0 && ascii <=255)
                // 因为你猜的原因,这里非英文也算是2个字符,不再只算一个字符
                // length++;
                length += 2;
            else
                length += 2;

        }
        return length;

    }

    /**
    * @Description: 账号安全状态查询
     * @Param: []
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: chq459799974
     * @Date: 2021/3/29
    **/
    @GetMapping("/safeStatus")
    @ApiOperation("账号安全状态查询")
    // @Permit("community:proprietor:userdata:safeStatus")
    public CommonResult querySafeStatus(){
        UserInfoVo userInfo = UserUtils.getUserInfo();
        Boolean payPasswordStatus = baseUserInfoRpcService.getPayPasswordStatus(userInfo.getId());
        Boolean loginPasswordStatus = baseUserInfoRpcService.getLoginPasswordStatus(userInfo.getId(), userInfo.getUid());
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("hasPayPassword", payPasswordStatus ? "1" : "0");
        returnMap.put("hasPassword", loginPasswordStatus ? "1" : "0");
        if (StringUtil.isNotBlank(userInfo.getMobile())) {
            String lastMobile = userInfo.getMobile().substring(7, 11);
            returnMap.put("mobile", userInfo.getMobile().substring(0, 3).concat("****").concat(lastMobile));
        }
        return CommonResult.ok(returnMap);
//        return CommonResult.ok(userDataService.querySafeStatus(UserUtils.getUserId()));
    }
}

