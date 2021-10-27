package com.jsy.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsy.community.entity.PushInformEntity;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.proprietor.OldPushInformQO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * 系统消息Mapper
 * @author YuLF
 * @since 2020-12-21 11:29
 **/
@Mapper
public interface SysInformMapper extends BaseMapper<PushInformEntity> {

    List<PushInformEntity> query(BaseQO<OldPushInformQO> baseQO);
    
    /**
     * 通过推送号ID 清理在t_acct_push_del 表中 用户手动在消息列表 左滑动删除的推送号记录
     * 主要是用于 在新增推送消息时，重新给用户推送未读信息，在t_acct_push_del存在的推送号账号，拉取用户消息列表时，不会被拉取
     * @param acctId    推送账号id
     */
    @Delete("delete from t_push_inform_del where acct_id = #{acctId}")
    void clearPushDel(@Param("acctId") Long acctId);
    
    /**
     * 删除社区消息时 同事物理删除用户已读信息
     * @param id  社区消息ID
     */
    @Delete("delete from t_user_inform where inform_id = #{id}")
    void  delUserReadInform(@Param("id") Long id);
    
    /**
     *@Author: DKS
     *@Description: 将推送消息更新为删除
     *@Param: id:消息ID
     *@Param: updateBy:更新人
     *@Return: java.lang.Integer
     *@Date: 2021/10/27 14:19
     **/
    @Update("update t_push_inform set deleted = #{id}, top_state = 0, update_by = #{updateBy}, update_time = now() where id = #{id}")
    Integer updateDeleted(@Param("id") Long id, @Param("updateBy") String updateBy);
}
