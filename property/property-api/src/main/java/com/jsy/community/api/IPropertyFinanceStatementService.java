package com.jsy.community.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.PropertyFinanceStatementEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.StatementQO;
import com.jsy.community.utils.PageInfo;
import com.jsy.community.vo.StatementVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author: Pipi
 * @Description: 物业财务-结算单服务
 * @Date: 2021/4/22 16:55
 * @Version: 1.0
 **/
public interface IPropertyFinanceStatementService extends IService<PropertyFinanceStatementEntity> {
    /**
     *@Author: Pipi
     *@Description: 定时产生结算单
     *@Param: :
     *@Return: void
     *@Date: 2021/4/22 16:59
     **/
    void timingStatement();
    
    /**
    * @Description: 结算单号批量查 单号-结算单数据 映射
     * @Param: [nums]
     * @Return: java.util.Map<java.lang.String,com.jsy.community.entity.property.PropertyFinanceStatementEntity>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    Map<String,PropertyFinanceStatementEntity> queryByStatementNumBatch(Collection<String> nums);

    /**
    * @Description: 条件查询批量结算单号
     * @Param: [query]
     * @Return: java.util.List<java.lang.String>
     * @Author: chq459799974
     * @Date: 2021/4/23
    **/
    List<String> queryStatementNumsByCondition(PropertyFinanceStatementEntity query);

    /**
     *@Author: Pipi
     *@Description: 物业财务-结算单列表
     *@Param: statementQO:
     *@Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.jsy.community.vo.StatementVO>
     *@Date: 2021/4/23 17:01
     **/
    PageInfo<PropertyFinanceStatementEntity> getStatementList(BaseQO<StatementQO> statementQO);

    /**
     *@Author: Pipi
     *@Description: 查询导出结算单数据
     *@Param: :
     *@Return: java.util.List<com.jsy.community.entity.property.PropertyFinanceStatementEntity>
     *@Date: 2021/4/24 15:23
     **/
    List<StatementVO> getDownloadStatementList(StatementQO statementQO);
}
