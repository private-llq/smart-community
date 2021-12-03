package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.entity.proprietor.VoteEntity;
import com.jsy.community.entity.proprietor.VoteUserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.service.IVoteService;
import com.jsy.community.utils.MinioUtils;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.CommonResult;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @program: com.jsy.community
 * @description: 投票问卷
 * @author: DKS
 * @create: 2021-11-08 10:26
 **/
@RestController
@RequestMapping("/vote")
@ApiJSYController
public class VoteController {

    @Resource
    private IVoteService voteService;
    
    /**
     * @Description: 投票问卷分页查询
     * @author: DKS
     * @since: 2021/11/8 10:34
     * @Param: [baseQO]
     * @return: com.jsy.community.vo.CommonResult<com.jsy.community.utils.PageInfo<com.jsy.community.entity.proprietor.VoteEntity>>
     */
    @ApiOperation("投票问卷分页查询")
    @PostMapping("/list")
    @Permit("community:admin:vote:list")
    public CommonResult<PageInfo<VoteEntity>> list(@RequestBody BaseQO<VoteEntity> baseQO){
        return CommonResult.ok(voteService.list(baseQO));
    }
    
    /**
     * @Description: 上传图片
     * @author: DKS
     * @since: 2021/11/8 10:48
     * @Param: [file]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("上传图片")
    @PostMapping("/file")
    @Permit("community:admin:vote:file")
    public CommonResult file(@RequestParam MultipartFile[] file){
        String[] votes = MinioUtils.uploadForBatch(file, "sys-vote");
        return CommonResult.ok(votes);
    }
    
    /**
     * @Description: 新增投票问卷
     * @author: DKS
     * @since: 2021/11/8 11:15
     * @Param: [voteEntity]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("新增")
    @PostMapping("/save")
    @Permit("community:admin:vote:save")
    public CommonResult save(@RequestBody VoteEntity voteEntity){
        voteService.saveBy(voteEntity);
        return CommonResult.ok();
    }
    
    /**
     * @Description: 投票问卷详情
     * @author: DKS
     * @since: 2021/11/8 11:14
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("查详情")
    @GetMapping("/getOne")
    @Permit("community:admin:vote:getOne")
    public CommonResult getOne(@RequestParam Long id){
        List<VoteUserEntity> one = voteService.getOne(id);
        return CommonResult.ok(one);
    }
    
    /**
     * @Description: 查图表
     * @author: DKS
     * @since: 2021/11/8 11:12
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("查图表")
    @GetMapping("/getChart")
    @Permit("community:admin:vote:getChart")
    public CommonResult getChart(@RequestParam Long id){
        Map<String, Object> chart = voteService.getChart(id);
        return CommonResult.ok(chart);
    }
    
    /**
     * @Description: 撤销或者删除
     * @author: DKS
     * @since: 2021/11/8 11:12
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("撤销或者删除")
    @DeleteMapping("/delete")
    @Permit("community:admin:vote:delete")
    public CommonResult delete(@RequestParam Long id){
        return CommonResult.ok(voteService.delete(id) ? "操作成功" : "操作失败");
    }
    
    /**
     * @Description: 查询详情
     * @author: DKS
     * @since: 2021/11/8 16:43
     * @Param: [id]
     * @return: com.jsy.community.vo.CommonResult
     */
    @ApiOperation("查询详情")
    @GetMapping("/getVote")
    @Permit("community:admin:vote:getVote")
    public CommonResult getVote(@RequestParam Long id){
        return CommonResult.ok(voteService.getVote(id));
    }
}
