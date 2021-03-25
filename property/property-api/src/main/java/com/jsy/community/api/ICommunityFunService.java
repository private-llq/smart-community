package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.CommunityFunEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.CommunityFunOperationQO;
import com.jsy.community.qo.property.CommunityFunQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.admin.AdminInfoVo;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2020-12-09 10:49
 **/
public interface ICommunityFunService extends IService<CommunityFunEntity> {
    PageInfo findList(BaseQO<CommunityFunQO> baseQO);


    /**
     * @Description: 下线
     * @author: Hu
     * @since: 2021/2/3 15:11
     * @Param:
     * @return:
     */
    void tapeOut(Long id);

    /**
     * @Description: 上线
     * @author: Hu
     * @since: 2021/2/3 15:15
     * @Param:
     * @return:
     */
    void popUpOnline(Long id,AdminInfoVo adminInfoVo);

    /**
     * @Description: 查询一条详情
     * @author: Hu
     * @since: 2021/2/3 15:19
     * @Param:
     * @return:
     */
    CommunityFunEntity selectOne(Long id);

    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/2/3 15:21
     * @Param:
     * @return:
     */
    void deleteById(Long id);

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/2/3 15:22
     * @Param:
     * @return:
     */
    void updateOne(CommunityFunOperationQO communityFunOperationQO, AdminInfoVo adminInfoVo);

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/2/3 15:23
     * @Param:
     * @return:
     */
    void insetOne(CommunityFunOperationQO communityFunOperationQO,AdminInfoVo adminInfoVo);
}
