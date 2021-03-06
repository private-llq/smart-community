package com.jsy.community.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsy.community.annotation.IpLimit;
import com.jsy.community.annotation.businessLog;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IProprietorService;
import com.jsy.community.api.PropertyUserService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.ProprietorEntity;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.exception.JSYError;
import com.jsy.community.exception.JSYException;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.ProprietorQO;
import com.jsy.community.qo.property.RelationListQO;
import com.jsy.community.util.ProprietorExcelCommander;
import com.jsy.community.util.excel.impl.ProprietorInfoProvider;
import com.jsy.community.utils.*;
import com.jsy.community.vo.CommonResult;
import com.jsy.community.vo.FeeRelevanceTypeVo;
import com.jsy.community.vo.property.ProprietorVO;
import com.zhsj.base.api.constant.RpcConst;
import com.zhsj.base.api.entity.RealInfoDto;
import com.zhsj.base.api.rpc.IBaseUserInfoRpcService;
import com.zhsj.baseweb.annotation.LoginIgnore;
import com.zhsj.baseweb.annotation.Permit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author YuLF
 * @since 2020-11-25 13:45
 */
@Api(tags = "业主信息控制器")
@RestController
@RequestMapping("/proprietor")
// @ApiJSYController
@Slf4j
public class ProprietorController {

    @DubboReference(version = Const.version, group = Const.group, check = false)
    private IProprietorService iProprietorService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private IHouseService iHouseService;

    @DubboReference(version = Const.version, group = Const.group_property, check = false)
    private PropertyUserService iUserService;

    @DubboReference(version = RpcConst.Rpc.VERSION, group = RpcConst.Rpc.Group.GROUP_BASE_USER, check=false)
    private IBaseUserInfoRpcService baseUserInfoRpcService;

    /**
     * [2021.3.16] 根据物业端需求改造完成
     * 下载录入业主信息excel、模板
     *
     * @return 返回Excel模板
     */
    @LoginIgnore
    @IpLimit(prefix = "excel", second = 60, count = 5, desc = "下载业主信息录入Excel")
    @GetMapping(params = {"downloadExcel"})
    @ApiOperation("下载业主信息录入Excel")
    public ResponseEntity<byte[]> downloadExcel(HttpServletResponse response, @RequestParam long communityId) {
        //1. 设置响应头
        MultiValueMap<String, String> multiValueMap = ExcelUtil.setHeader("业主导入模板.xls");
        //2.生成Excel模板
        try {
            //2.2 生成Excel 业主信息录入模板
            Workbook workbook = ProprietorExcelCommander.exportProprietorInfo();
            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
        } catch (IOException e) {
            //已接受。已经接受请求，但未处理完成
            log.error("com.jsy.community.controller.ProprietorController.downloadExcel：{}", e.getMessage());
            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
        }
    }


    /**
     * 【业主登记excel 导入】 登记
     *
     * @param excel           用户上传的excel
     * @param communityId     社区id
     * @return 返回效验或登记结果
     */
    @PostMapping(params = {"importProprietorExcel"})
    @ApiOperation("导入业主信息excel")
    @Permit("community:property:proprietor")
    public CommonResult<ProprietorVO> importProprietorExcel(MultipartFile excel, Long communityId) {
        //参数验证
        validFileSuffix(excel, communityId);
        //查询当前社区 未被登记的所有 房屋编号 + house_id
        List<ProprietorVO> houseList = iHouseService.getCommunityHouseById(communityId);
        //解析错误 | 数据效验不通过 的数据集合  初始化错误集32
        List<ProprietorVO> errorVos = new ArrayList<>(32);
        //解析、常规格式效验Excel数据
        List<ProprietorEntity> proprietors = ProprietorExcelCommander.importProprietorExcel(excel, errorVos);
        Integer row = 0;
        if(CollectionUtil.isNotEmpty(proprietors)){
            //获取管理员姓名 用于标识每条业主数据的创建人
            RealInfoDto idCardRealInfo = baseUserInfoRpcService.getIdCardRealInfo(UserUtils.getUserId());
            String adminRealName = "";
            if (idCardRealInfo != null) {
                adminRealName = idCardRealInfo.getIdCardName();
            }
            //验证房屋编号
            validUserHouse(proprietors, houseList, errorVos, adminRealName);
            //在验证房屋编号后 数据集合如果不为空 就做数据库导入
            if( CollectionUtil.isNotEmpty(proprietors) ){
                //做数据库写入 proprietors 读出来的数据
                row = iProprietorService.saveUserBatch(proprietors, communityId);
            }
        }
        //excel导入失败的信息明细 文件下载地址
        String errorExcelAddr = null;
        //错误excel写入远程服务器 让物业人员可以直接下载
        if( CollectionUtil.isNotEmpty(errorVos) ){
             errorExcelAddr = uploadErrorExcel(errorVos);
        }
        //构造返回对象
        return CommonResult.ok(new ProprietorVO(row, errorVos.size(), errorExcelAddr));
    }



    /**
     * 写入业主信息导入错误信息 和 把错误信息excel文件上传至文件服务器
     *
     * @param errorVos 错误信息集合
     * @return 返回excel文件下载地址
     */
    public String uploadErrorExcel(List<ProprietorVO> errorVos) {
        //1.获取业主信息excel模板
        Workbook defaultWorkbook = ProprietorExcelCommander.exportProprietorDefaultInfo();
        XSSFSheet sheet = (XSSFSheet) defaultWorkbook.getSheet(ProprietorExcelCommander.PROPRIETOR_SHEET_NAME);
        XSSFCellStyle xssfCellStyle = ProprietorExcelCommander.provideBackground(defaultWorkbook);
        //每行excel数据
        XSSFRow row;
        //每列数据
        XSSFCell cell;
        //每一个错误信息对象
        ProprietorVO vo;
        //2.往excel模板内写入数据  从第三行开始 前两行是 标题和字段
        for (int index = 0; index < errorVos.size(); index++) {
            row = sheet.createRow(index + 2);
            //创建列
            for (int j = 0; j < ProprietorExcelCommander.PROPRIETOR_TITLE_FIELD.length; j++) {
                cell = row.createCell(j);
                vo = errorVos.get(index);
                switch (j) {
                    case 0:
                        cell.setCellValue(vo.getRealName());
                        break;
                    case 1:
                        cell.setCellValue(vo.getIdCard());
                        break;
                    case 2:
                        cell.setCellValue(vo.getMobile());
                        break;
                    case 3:
                        cell.setCellValue(vo.getHouseNumber());
                        break;
                    case 4:
                        cell.setCellValue(vo.getWechat());
                        break;
                    case 5:
                        cell.setCellValue(vo.getQq());
                        break;
                    case 6:
                        cell.setCellValue(vo.getEmail());
                        break;
                    case 7:
                        cell.setCellValue(vo.getRemark());
                        //设定备注红色背景
                        cell.setCellStyle(xssfCellStyle);
                        break;
                    case 8:
                    default:
                        break;
                }
            }
        }
        try {
            byte[] bytes = ExcelUtil.readWorkbook(defaultWorkbook);
            MultipartFile multipartFile = new MockMultipartFile("file", "proprietorExcel", "application/vnd.ms-excel", bytes);
            return MinioUtils.upload(multipartFile, ProprietorExcelCommander.BUCKET_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过物业提交的数据 和 数据库该社区已存在的数据进行效验
     *
     * @param userEntityList 物业提交的业主信息数据集合
     * @param houseList      数据库该社区已存在的未被登记的房屋编号 + id 集合
     * @param adminRealName  操作员姓名
     * @param errorVos       错误信息集合
     */
    private void validUserHouse(List<ProprietorEntity> userEntityList, List<ProprietorVO> houseList, List<ProprietorVO> errorVos, String adminRealName) {
        List<ProprietorVO> difference = difference(userEntityList, houseList, errorVos, adminRealName);
        errorVos.addAll(difference);
    }

    /**
     * 找出两个集合中  houseNumber 不匹配的属性
     * 房屋编号验证
     * @param source        用户集合
     * @param target        目标对比集合
     * @param adminRealName 操作员姓名
     * @return 返回差异集合
     */
    private List<ProprietorVO> difference(List<ProprietorEntity> source, List<ProprietorVO> target, List<ProprietorVO> errorVos, String adminRealName) {
        //房屋编号不匹配的数据
        List<ProprietorVO> list = new ArrayList<>(32);
        //房屋编号不匹配的entity
        List<ProprietorEntity> removeList = new ArrayList<>();
        AtomicBoolean atomicBoolean;
        ProprietorVO vo;
        LocalDateTime now = LocalDateTime.now();
        for (ProprietorEntity so : source) {
            atomicBoolean = new AtomicBoolean(false);
            for (ProprietorVO ta : target) {
                //正确数据：如果 物业录入的信息 编号和 数据库一致 则 把 数据库t_house的id设置进该数据
                if (Objects.equals(so.getHouseNumber(), ta.getHouseNumber())) {
                    so.setHouseId(ta.getHouseId());
                    so.setCommunityId(ta.getCommunityId());
                    so.setIdentificationType(1);
                    so.setId(SnowFlake.nextId());
                    so.setCreateBy(adminRealName);
                    so.setCreateTime(now);
                    atomicBoolean.set(true);
                    break;
                }
            }
            //不匹配 房屋编号在 物业已有的找不到
            if (atomicBoolean.get() == Boolean.FALSE) {
                vo = ProprietorInfoProvider.setVo(errorVos,so.getRealName(), "该房屋编号不存在于当前社区!或已被登记.");
                boolean isExistObj = errorVos.stream().anyMatch(vo3 -> so.getRealName().equals(vo3.getRealName()));
                if( !isExistObj ){
                    BeanUtils.copyProperties(so, vo);
                    list.add(vo);
                }
                removeList.add(so);
            }
        }
        //从需要导入数据库的 list中移除 房屋编号 不匹配|不正确 的数据
        source.removeAll(removeList);
        return list;
    }

    private void notNull(List<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new JSYException("没有数据可以导入!");
        }
    }


//    /**
//     * 下载录入业主家属成员信息excel、模板
//     *
//     * @return 返回Excel模板
//     */
//    @IpLimit(prefix = "excel", second = 60, count = 5, desc = "下载业主家属成员信息录入Excel")
//    @GetMapping(params = {"downloadMemberExcel"})
//    @ApiOperation("下载业主家属成员信息录入Excel")
//    public ResponseEntity<byte[]> downloadMemberExcel(@RequestParam long communityId) {
//        List<UserEntity> userEntityList = getUserInfo(communityId);
//        //获取excel 响应头信息
//        MultiValueMap<String, String> multiValueMap = ExcelUtil.setHeader(userEntityList.get(0).getNickname() + "家属成员登记表.xlsx");
//        try {
//            //存储 需要携带的信息
//            Map<String, Object> res = new HashMap<>(3);
//            //拿到当前社区 已登记的房屋信息List 如：1栋1单元1楼1-1
//            List<HouseVo> houseVos = iProprietorService.queryHouseByCommunityId(communityId);
//            //取出所有的小区房屋地址
//            List<String> communityHouseAddr = houseVos.stream().map(HouseVo::getMergeName).collect(Collectors.toList());
//
//            res.put("name", userEntityList.get(0).getNickname() + "业主家属成员登记表");
//            res.put("communityId", communityId);
//            res.put("communityHouseAddr", communityHouseAddr);
//
//            //获得excel下载模板
//            Workbook workbook = ProprietorExcelCommander.exportProprietorMember(userEntityList, res);
//            //把workbook工作簿转换为字节数组 放入响应实体以附件形式输出
//            return new ResponseEntity<>(ExcelUtil.readWorkbook(workbook), multiValueMap, HttpStatus.OK);
//        } catch (IOException e) {
//            log.error("com.jsy.community.controller.ProprietorController.downloadMemberExcel：{}", e.getMessage());
//            return new ResponseEntity<>(null, multiValueMap, HttpStatus.ACCEPTED);
//        }
//    }

//    /**
//     * 按communityId 获取用户信息
//     *
//     * @return 返回查找到的信息
//     */
//    private List<UserEntity> getUserInfo(Long communityId) {
//        List<UserEntity> userEntityList = iHouseService.getCommunityNameAndUserInfo(communityId);
//        if (userEntityList == null || userEntityList.isEmpty()) {
//            throw new JSYException(JSYError.NOT_IMPLEMENTED.getCode(), "目前这个小区没有任何用户信息!");
//        }
//        return userEntityList;
//    }


//    /**
//     * 【业主家属信息导入】
//     *
//     * @param proprietorExcel 用户上传的excel
//     * @param communityId     社区id
//     * @return 返回效验或登记结果
//     */
//    @PostMapping(params = {"importMemberExcel"})
//    @ApiOperation("导入业主家属信息信息excel")
//    public CommonResult<?> importMemberExcel(MultipartFile proprietorExcel, Long communityId) {
//        //参数验证
//        validFileSuffix(proprietorExcel, communityId);
//        //控制层需要传递给实现类的参数
//        List<UserEntity> userInfoList = getUserInfo(communityId);
//        //把List中的 realName 和uid 转换为Map存储
//        Map<String, Object> userInfoParams = ExcelUtil.getAllUidAndNameForList(userInfoList, "realName", "uid");
//        userInfoParams.put("communityId", communityId);
//        List<UserEntity> userEntityList = ProprietorExcelCommander.importMemberExcel(proprietorExcel, userInfoParams);
//
//        notNull(userEntityList);
//
//        //数据库信息写入
//        Integer row = iProprietorService.saveUserMemberBatch(userEntityList, communityId);
//        return CommonResult.ok("本次导入家属信息成功" + row + "条! 请至管理平台检查");
//    }

    /**
     * excel 文件上传上来后 验证方法
     *
     * @param file        excel文件
     * @param communityId 社区id
     */
    private void validFileSuffix(MultipartFile file, Long communityId) {
        //参数非空验证
        if (null == file || communityId == null) {
            throw new JSYException(JSYError.BAD_REQUEST);
        }
        //文件后缀验证
        boolean extension = FilenameUtils.isExtension(file.getOriginalFilename(), ExcelUtil.SUPPORT_EXCEL_EXTENSION);
        if (!extension) {
            throw new JSYException(JSYError.REQUEST_PARAM.getCode(), "只支持excel文件!");
        }
    }


    /**
     * 分页查询业主信息
     *
     * @param baseQo 查询参数实体
     * @return 返回删除是否成功
     */
    @PostMapping()
    @ApiOperation("分页查询业主信息")
    @Permit("community:property:proprietor")
    public CommonResult<Page<ProprietorVO>> query(@RequestBody BaseQO<ProprietorQO> baseQo) {
        //1.验证分页 查询参数
        ValidatorUtils.validatePageParam(baseQo);
        if (baseQo.getQuery() == null) {
            baseQo.setQuery(new ProprietorQO());
        }
        baseQo.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        //1.2 验证提交的参数 如果数字类型的 串 不允许 传双引号，
        ValidatorUtils.validateEntity(baseQo.getQuery(), ProprietorQO.PropertySearchValid.class);
        //2.查询信息返回
        return CommonResult.ok(iProprietorService.query(baseQo));
    }

    @PutMapping()
    @ApiOperation("更新业主信息")
    @businessLog(operation = "编辑",content = "更新了【业主信息】")
    @Permit("community:property:proprietor")
    public CommonResult<Boolean> update(@RequestBody ProprietorQO qo) {
        ValidatorUtils.validateEntity(qo, ProprietorQO.PropertyUpdateValid.class);
        return iProprietorService.update(qo, UserUtils.getUserId()) ? CommonResult.ok() : CommonResult.error(JSYError.NOT_IMPLEMENTED);
    }

    @PostMapping("/addUser")
    @ApiOperation("添加业主信息")
    @businessLog(operation = "新增",content = "新增了【业主信息】")
    @Permit("community:property:proprietor:addUser")
    public CommonResult<Boolean> addUser(@RequestBody ProprietorQO qo) {
        qo.setCommunityId(UserUtils.getAdminCommunityId());
        ValidatorUtils.validateEntity(qo, ProprietorQO.PropertyAddValid.class);
        iProprietorService.addUser(qo, UserUtils.getUserId());
        return CommonResult.ok("新增成功!");
    }


    /**
     * 删除业主的房屋认证信息
     * 实则 只是去除 用户 和 房屋信息的信息解绑
     */
    @DeleteMapping()
    @ApiOperation("删除业主信息")
    @businessLog(operation = "删除",content = "删除了【业主信息】")
    @Permit("community:property:proprietor")
    public CommonResult<Boolean> del(@RequestParam Long id) {
        //TODO : 验证物业人员是否具有管理这个社区的权限
        Boolean isSuccess = iProprietorService.unbindHouse(id);
        return isSuccess ? CommonResult.ok("删除成功!") : CommonResult.error("删除失败!");
    }

    /**
     * @author: Pipi
     * @description: 查询未绑定房屋列表
     * @param: baseQO:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/6/12 11:57
     **/
    @PostMapping("/unboundHouseList")
    @ApiOperation("查询未绑定房屋列表")
    @Permit("community:property:proprietor:unboundHouseList")
    public CommonResult getUnboundHouseList(@RequestBody BaseQO<RelationListQO> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new RelationListQO());
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        List<FeeRelevanceTypeVo> unboundHouseList = iProprietorService.getUnboundHouseList(baseQO);
        return CommonResult.ok(unboundHouseList);
    }

    /**
     * @author: Pipi
     * @description: 人脸管理查询人脸分页列表
     * @param baseQO: 查询条件
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/8 16:34
     **/
    @PostMapping("/v2/facePageList")
    @Permit("community:property:proprietor:v2:facePageList")
    public CommonResult facePageList(@RequestBody BaseQO<UserEntity> baseQO) {
        if (baseQO.getQuery() == null) {
            baseQO.setQuery(new UserEntity());
        }
        if (baseQO.getPage() == null || baseQO.getPage() <= 0) {
            baseQO.setPage(1L);
        }
        if (baseQO.getSize() == null || baseQO.getSize() <= 0) {
            baseQO.setSize(5L);
        }
        baseQO.getQuery().setCommunityId(UserUtils.getAdminCommunityId());
        return CommonResult.ok(iUserService.facePageList(baseQO));
    }

    /**
     * @author: Pipi
     * @description: 人脸操作(启用/禁用人脸)
     * @param userEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/22 10:33
     **/
    @PostMapping("/v2/faceOpration")
    @Permit("community:property:proprietor:v2:faceOpration")
    public CommonResult faceOpration(@RequestBody UserEntity userEntity) {
        ValidatorUtils.validateEntity(userEntity, UserEntity.FaceOprationValidate.class);
        Integer integer = iUserService.faceOpration(userEntity, UserUtils.getAdminCommunityId());
        return integer > 0 ? CommonResult.ok("操作成功") : CommonResult.error("操作失败");
    }

    /**
     * @author: Pipi
     * @description: 删除人脸
     * @param userEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/23 17:19
     **/
    @PostMapping("/v2/deleteFace")
    @Permit("community:property:proprietor:v2:deleteFace")
    public CommonResult deleteFace(@RequestBody UserEntity userEntity) {
        ValidatorUtils.validateEntity(userEntity, UserEntity.FaceDeleteValidate.class);
        Integer integer = iUserService.deleteFace(userEntity, UserUtils.getAdminCommunityId());
        return integer > 0 ? CommonResult.ok("删除成功") : CommonResult.error("删除失败");
    }

    /**
     * @author: Pipi
     * @description: 新增APP用户人脸
     * @param userEntity:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/23 17:54
     **/
    @PostMapping("/v2/addFace")
    @Permit("community:property:proprietor:v2:addFace")
    public CommonResult addFace(@RequestBody UserEntity userEntity) {
        ValidatorUtils.validateEntity(userEntity, UserEntity.AddFaceValidate.class);
        Integer integer = iUserService.addFace(userEntity, UserUtils.getAdminCommunityId());
        return integer > 0 ? CommonResult.ok("新增成功") : CommonResult.error("新增失败");
    }

    /**
     * @author: Pipi
     * @description: 上传用户人脸
     * @param file:
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/9/24 9:35
     **/
    @PostMapping("/v2/uploadFace")
    @Permit("community:property:proprietor:v2:uploadFace")
    public CommonResult uploadFace(MultipartFile file) {
        String upload = MinioUtils.upload(file, "face-url");
        return CommonResult.ok(upload, "上传成功");
    }
}
