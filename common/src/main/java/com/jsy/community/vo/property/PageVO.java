package com.jsy.community.vo.property;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("分页")
public class PageVO <E> implements Serializable {
    private Long total;   //总数
    private Long size;    //每页条数
    private Long current; //当前页
    private Long pages;   //页数
    private List<E> records; //数据
}
