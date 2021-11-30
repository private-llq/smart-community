package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IComplainService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ComplainEntity;
import com.jsy.community.qo.proprietor.ComplainQO;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.SnowFlake;
import com.jsy.community.utils.UserUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.proprietor.ComplainVO;
import com.zhsj.baseweb.annotation.Permit;
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
// @ApiJSYController
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
    @ApiOperation("用户投诉建议接口")
    @PostMapping("/addComplain")
    @Permit("community:proprietor:complain:addComplain")
    public CommonResult addComplain(@RequestBody ComplainEntity complainEntity){
        String userInfo = UserUtils.getUserId();
        complainEntity.setUid(userInfo);
        complainEntity.setCommunityId(complainEntity.getCommunityId());
        complainEntity.setStatus(0);
        complainEntity.setId(SnowFlake.nextId());
        complainEntity.setComplainTime(LocalDateTime.now());
        complainService.addComplain(complainEntity);
        return CommonResult.ok();
    }

    /**
     * @Description: 查询用户所有的投诉建议
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    @ApiOperation("用户查询所有投诉建议")
    @GetMapping("/selectUserIdComplain")
    @Permit("community:proprietor:complain:selectUserIdComplain")
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
    @ApiOperation("投诉建议图片批量上传")
    @PostMapping(value = "/uploadComplainImages")
    @Permit("community:proprietor:complain:uploadComplainImages")
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
        for (int i=0;i<upload.length;i++){
            filePath.append(upload[i]);
            if (i!=upload.length-1){
                filePath.append(",");
            }
        }
        String[] split = filePath.toString().split(",");
        return CommonResult.ok(split,"上传成功");
    }

/*******************************************************************************************************************************************************/
    /**
     * @Description: 新投诉建议接口
     * @Param: [complainEntity]
     * @Return: com.jsy.community.vo.CommonResult
     * @Author: Tian
     * @Date: 2021/8/20-11:06
     **/
    @ApiOperation("新用户投诉建议接口")
    @PostMapping("/appendComplain")
    @Permit("community:proprietor:complain:appendComplain")
    public CommonResult appendComplain(@RequestBody ComplainQO complainQO){
        String userInfo = UserUtils.getUserId();
        complainQO.setUid(userInfo);
        boolean b = complainService.appendComplain(complainQO);
        return CommonResult.ok("投诉成功");
    }

    /**
     * @Description: 查询用户所有的投诉建议
     * @author: Hu
     * @since: 2020/12/23 11:30
     * @Param:
     * @return:
     */
    @ApiOperation("用户查询所有投诉建议")
    @GetMapping("/selectComplain")
    @Permit("community:proprietor:complain:selectComplain")
    public CommonResult selectComplain(){
        String userId = UserUtils.getUserId();
        List<ComplainVO> complainEntities=complainService.selectComplain(userId);
        return CommonResult.ok(complainEntities,"查询成功");
    }


}
