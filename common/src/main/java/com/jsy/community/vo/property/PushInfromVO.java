package com.jsy.community.vo.property;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Pipi
 * @Description: 推送消息返参
 * @Date: 2021/7/23 16:05
 * @Version: 1.0
 **/
@Data
public class PushInfromVO implements Serializable {

    private String id;

    private String idStr;

    // 推送消息标题
    private String pushTitle;

    // 推送消息内容
    private String pushMsg;

    // 推送者ID
    private List<String> acctId;

    // 推送者名称
    private List<String> acctName;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;
}
