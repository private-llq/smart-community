package com.jsy.community.api;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.*;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.VisitorQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.VisitorEntryVO;

import java.util.List;

/**
 * <p>
 * 来访人员 服务类
 * </p>
 *
 * @author chq459799974
 * @since 2020-11-11
 */
public interface IVisitorService extends IService<VisitorEntity> {
    
    /**
    * @Description: 访客登记 新增
     * @Param: [visitorEntity]
     * @Return: void
     * @Author: chq45799974
     * @Date: 2020/11/12
    **/
    VisitorEntryVO addVisitor(VisitorEntity visitorEntity);
    
    //验证二维码
    void verifyQRCode(JSONObject jsonObject,Integer hardWareType);
//    /**
//    * @Description: 访客门禁验证
//     * @Param: [token, type]
//     * @Return: boolean
//     * @Author: chq459799974
//     * @Date: 2020/12/11
//    **/
//    boolean verifyEntry(String token,Integer type);
    
    /**
    * @Description: 根据ID 删除访客登记申请
     * @Param: [id]
     * @Return: boolean
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    boolean deleteVisitorById(Long id);
    
    /**
    * @Description: 分页查询
     * @Param: [baseQO, uid]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.entity.VisitorEntity>
     * @Author: chq459799974
     * @Date: 2020/12/16
    **/
    PageInfo<VisitorEntity> queryByPage(BaseQO<VisitorQO> baseQO, String uid);
    
    /**
    * @Description: 根据ID单查访客
     * @Param: [id]
     * @Return: com.jsy.community.entity.VisitorEntity
     * @Author: chq459799974
     * @Date: 2020/11/16
    **/
    VisitorEntity selectOneById(Long id);
    
    /**
     * @Description: 根据ID单查随行人员记录
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitorPersonRecordEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    List<VisitorPersonRecordEntity> queryPersonRecordList(Long visitorid);
    
    /**
     * @Description: 根据ID单查随行车辆记录
     * @Param: [visitorid]
     * @Return: java.util.List<com.jsy.community.entity.VisitingCarRecordEntity>
     * @Author: chq459799974
     * @Date: 2020/11/12
     **/
    List<VisitingCarRecordEntity> queryCarRecordList(Long visitorid);
    
}
