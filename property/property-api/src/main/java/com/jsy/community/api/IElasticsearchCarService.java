package com.jsy.community.api;

import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import com.jsy.community.qo.property.ElasticsearchCarSearchQO;

import java.util.Map;

/**
 * @program: com.jsy.community
 * @description:  物业车辆查询
 * @author: Hu
 * @create: 2021-03-25 15:29
 **/
public interface IElasticsearchCarService {
    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/3/25 15:30
     * @Param:
     * @return:
     */
    void deleteData(Long id);

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/3/25 15:30
     * @Param:
     * @return:
     */
    void updateData(ElasticsearchCarQO elasticsearchCarQO);

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/3/25 15:30
     * @Param:
     * @return:
     */
    void insertData(ElasticsearchCarQO elasticsearchCarQO);

    /**
     * @Description: 查询
     * @author: Hu
     * @since: 2021/3/26 9:14
     * @Param:
     * @return:
     */
    Map<String, Object> searchData(BaseQO<ElasticsearchCarSearchQO> baseQO);
}
