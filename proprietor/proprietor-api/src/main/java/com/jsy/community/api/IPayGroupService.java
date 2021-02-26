package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.PayGroupEntity;

/**
 * <p>
 * 户号组 服务类
 * </p>
 *
 * @author jsy
 * @since 2020-12-10
 */
public interface IPayGroupService extends IService<PayGroupEntity> {
    /**
     * @Description: 删除自定义组
     * @author: Hu
     * @since: 2021/2/3 10:03
     * @Param:
     * @return:
     */
    void delete(String name,String userId);

    /**
     * @Description: 新增自定义组名
     * @author: Hu
     * @since: 2021/2/3 10:20
     * @Param:
     * @return:
     */
    void insertGroup(String name, String userId);

    /**
     * @Description: 修改自定义组名
     * @author: Hu
     * @since: 2021/2/26 16:26
     * @Param:
     * @return:
     */
    void updateGroup(Long id, String name, String userId);
}
