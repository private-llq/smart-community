package com.jsy.community.controller;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.HouseEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.ProprietorVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author YuLF
 * @since 2020-11-25 13:45
 */
@Api(tags = "业主信息控制器")
@RestController
@RequestMapping("/proprietor")
@ApiJSYController
@Slf4j
public class ProprietorController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IProprietorService iProprietorService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseService iHouseService;

    /**
     * 下载录入业主信息excel、模板
     * @return          返回Excel模板
     */
    @GetMapping(params = {"downloadExcel"})
    @ApiOperation("下载业主信息录入Excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam long communityId) {
        //1.设置响应格式  设置响应头
        //1.1 查出数据库当前communityId对应的小区名和住户数量   返回结果如：name = 花园小区 coummunityUserNum = 54   如果根本没有这个小区，那就提前结束业务
        Map<String,Object> res = iHouseService.getCommunityNameAndUserAmountById(communityId);
        if(res == null){
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        //1.2 设置响应头
        MultiValueMap<String, String> multiValueMap = setHeader(res.get("name") + "业主登记表.xlsx");
        //2.生成Excel模板
        try {
            //2.1 查出数据库当前社区的所有楼栋、单元、楼层、门牌 用于excel模板录入业主信息选择
            List<HouseEntity> communityArchitecture = iHouseService.getCommunityArchitecture(communityId);
            //2.2 生成Excel 业主信息录入模板
            Workbook workbook = ProprietorExcelCommander.exportProprietorInfo(communityArchitecture, res);
            return new ResponseEntity<>(readWorkbook(workbook) , multiValueMap, HttpStatus.OK );
        } catch (IOException e) {
            //已接受。已经接受请求，但未处理完成
            log.error("com.jsy.community.controller.ProprietorController.downloadExcel：{}", e.getMessage());
            return new ResponseEntity<>(null , multiValueMap, HttpStatus.ACCEPTED );
        }
    }

    /**
     * 下载录入业主家属成员信息excel、模板
     * @return          返回Excel模板
     */
    @GetMapping(params = {"downloadMemberExcel"})
    @ApiOperation("下载业主家属成员信息录入Excel")
    public ResponseEntity<byte[]> downloadMemberExcel(@RequestParam long communityId) {
        List<UserEntity> userEntityList = getUserInfo(communityId);
        //获取excel 响应头信息
        MultiValueMap<String, String> multiValueMap = setHeader(userEntityList.get(0).getNickname() + "家属成员登记表.xlsx");
        try {
            //获得excel下载模板
            Workbook workbook = ProprietorExcelCommander.exportProprietorMember(userEntityList);
            //把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
            return new ResponseEntity<>(readWorkbook(workbook) , multiValueMap, HttpStatus.OK );
        } catch (IOException e) {
            log.error("com.jsy.community.controller.ProprietorController.downloadMemberExcel：{}", e.getMessage());
            return new ResponseEntity<>(null , multiValueMap, HttpStatus.ACCEPTED );
        }
    }

    /**
     * 按communityId 获取用户信息
     * @return       返回查找到的信息
     */
    private List<UserEntity> getUserInfo(Long communityId){
        List<UserEntity> userEntityList = iHouseService.getCommunityNameAndUserInfo(communityId);
        if( userEntityList == null || userEntityList.isEmpty() ){
            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), "目前这个小区没有任何用户信息!");
        }
        return userEntityList;
    }

    /**
     * 读取工作簿 返回字节数组
     * @param workbook      excel工作簿
     * @return              返回读取完成的字节数组
     */
    private byte[] readWorkbook(Workbook workbook) throws IOException {
        //2.3 把workbook转换为字节输入流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        //@Cleanup注解 会在作用域的末尾将调用is.close()方法，并使用了try/finally代码块 执行。
        @Cleanup InputStream is = new ByteArrayInputStream(bos.toByteArray());
        byte[] byt = new byte[is.available()];
        //2.4 读取字节流 响应实体返回
        int read = is.read(byt);
        return byt;
    }
    /**
     * 设置响应头信息
     * @param fileFullName  附件下载文件全名称
     * @return              返回响应头Map
     */
    private MultiValueMap<String, String> setHeader(String fileFullName){
        MultiValueMap<String, String> multiValueMap = new HttpHeaders();
        //设置响应类型为附件类型直接下载这种
        multiValueMap.set("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileFullName, StandardCharsets.UTF_8));
        //设置响应的文件mime类型为 xls类型
        multiValueMap.set("Content-type", "application/vnd.ms-excel");
        return multiValueMap;
    }

    /**
     * 【业主登记excel 导入】 登记
     * @param proprietorExcel   用户上传的excel
     * @param communityId       社区id
     * @return                  返回效验或登记结果
     */
    @PostMapping(params = {"importProprietorExcel"})
    @ApiOperation("导入业主信息excel")
    public CommonResult<?> importProprietorExcel(MultipartFile proprietorExcel, Long communityId){
        //参数验证
        validFileSuffix(proprietorExcel, communityId);
        //解析Excel  这里强转Object得保证 importProprietorExcel 实现类返回的类型是UserEntity
        List<UserEntity> userEntityList = ProprietorExcelCommander.importProprietorExcel(proprietorExcel,new HashMap<>(1));
        //数据库写入

        return CommonResult.ok(userEntityList);
    }

    /**
     * 【业主家属信息导入】
     * @param proprietorExcel   用户上传的excel
     * @param communityId       社区id
     * @return                  返回效验或登记结果
     */
    @PostMapping(params = {"importMemberExcel"})
    @ApiOperation("导入业主家属信息信息excel")
    public CommonResult<?> importMemberExcel(MultipartFile proprietorExcel, Long communityId){
        //参数验证
        validFileSuffix(proprietorExcel, communityId);
        //控制层需要传递给实现类的参数
        List<UserEntity> userInfoList = getUserInfo(communityId);
        //把List中的 realName 和uid 转换为Map存储
        Map<String, Object> userInfoParams = ProprietorExcelCommander.getAllUidAndNameForList(userInfoList, "realName", "uid");
        List<UserEntity> userEntityList = ProprietorExcelCommander.importMemberExcel(proprietorExcel, userInfoParams);
        //做数据库写入 userEntityList 读出来的数据
        iProprietorService.saveUserBatch(userEntityList, communityId);
        //todo 数据库信息写入
        return CommonResult.ok(userEntityList);
    }

    /**
     * excel 文件上传上来后 验证方法
     * @param file          excel文件
     * @param communityId   社区id
     */
    private void validFileSuffix(MultipartFile file, Long communityId){
        //参数非空验证
        if(null == file || communityId == null){
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        //文件后缀验证
        boolean extension = FilenameUtils.isExtension(file.getOriginalFilename(), ProprietorExcelCommander.SUPPORT_EXCEL_EXTENSION);
        if (!extension) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "只支持excel文件!");
        }
    }
    /**
     * TODO  Seata全局事务处理
     * 根据业主id 删除业主信息、业主关联的房屋、业主的家庭成员、业主的车辆信息
     * @return          返回删除是否成功
     */
    @DeleteMapping()
    @ApiOperation("删除业主信息")
    public CommonResult<Boolean> del(@RequestParam Long uid){
        //从JWT获取业主ID
        //Long uid = 12L;
        iProprietorService.del(uid);
        return CommonResult.ok();
    }

    /**
     * 分页查询业主信息
     * @param proprietorQOBaseQO   查询参数实体
     * @return                    返回删除是否成功
     */
    @PostMapping()
    @ApiOperation("分页查询业主信息")
    public CommonResult<List<ProprietorVO>> query(@RequestBody BaseQO<ProprietorQO> proprietorQOBaseQO){
        //1.验证分页 查询参数
        ValidatorUtils.validatePageParam(proprietorQOBaseQO);
        //2.查询信息返回
        return CommonResult.ok(iProprietorService.query(proprietorQOBaseQO));
    }

    /**
     * 修改业主信息
     * @param proprietorQO        参数实体
     * @return                    返回删除是否成功
     */
    @PutMapping()
    @ApiOperation("修改业主信息")
    public CommonResult<Boolean> update(@RequestBody ProprietorQO proprietorQO){
        //效验id
        ValidatorUtils.validateEntity(proprietorQO, ProprietorQO.propertyUpdateValid.class);
        return iProprietorService.update(proprietorQO) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }


}
