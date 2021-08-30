package com.jsy.community.entity.property;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jsy.community.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Pipi
 * @Description: 陌生人脸记录实体
 * @Date: 2021/8/26 15:05
 * @Version: 1.0
 **/
@Data
@TableName("t_stranger_record")
public class StrangerRecordEntiy extends BaseEntity {

    // 陌生人抓拍库ID
    private Long snapId;

    // 社区ID
    private Long communityId;

    // 一体机id(序列号)
    private String facesluiceId;

    // 一体机名称
    private String facesluiceName;

    // 进口:"entr"，出口:"exit",无方向:"unknow"
    private String direction;

    // 实时检测人脸温度
    private Double temperature;

    // 实时检测人脸温度是否超过阈值,0：没超过；1：超过（温度检测机器版本支持）
    private Integer temperatureAlarm;

    // 图片的base64编码(1M以内)
    private String pic;

    // 是否已同步 0.否 1.是
    private Integer isSync;

    // 批次号
    private Integer version;
}
