package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.UserEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ProprietorQO;
import com.jsy.community.vo.ProprietorVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 业主 Mapper 接口
 * @author YuLF
 * @since 2020-11-25
 */
public interface ProprietorMapper extends BaseMapper<UserEntity> {

    /**
     * [物业]通过分页参数查询 业主信息
     * @param queryParam    查询参数
     * @return              返回查询的业主信息
     */
    List<ProprietorVO> query(BaseQO<ProprietorQO> queryParam);

    /**
     * [物业]更新业主信息
     * @param proprietorQO  待更新参数实体
     * @return              返回sql影响行数
     */
    int update(ProprietorQO proprietorQO);
}
