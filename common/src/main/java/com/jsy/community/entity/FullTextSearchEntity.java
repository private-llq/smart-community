package com.jsy.community.entity;

import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author YuLF
 * @since 2021-02-02 14:31
 */
@Data
@ToString
public class FullTextSearchEntity implements Serializable {

    private static final long serialVersionUID = 3530698217996558817L;

    //id（标识数据唯一性）、name（搜索标题）、picture（数据头像，只取一对一的数据头像）、flag（数据标记（租房、商铺、消息、趣事））
    /**
     * 数据唯一id
     */
    private Long id;
    /**
     * 他只对t_acct_push_inform的数据生效（冗余字段）消息推送id
     */
    private Long acctId;
    /**
     * 数据唯一标题 用于全文搜索 出来显示
     */
    private String title;
    /**
     * 全文搜索副标题  用于全文搜索 出来显示 类似于 搜索到店铺名称 下一行显示店铺地址一样
     */
    private String subTitle;
    /**
     * 数据图片 如果有的话
     */
    private String picture;
    /**
     * 数据标记flag 数据标记（租房、商铺、消息、趣事））
     */
    private RecordFlag flag;
    /**
     * 该数据向ES中的操作
     */
    private Operation operation;
    /**
     * 社区id
     */
    private Long communityId;

}
