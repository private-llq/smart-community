package com.jsy.community.entity.proprietor;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_car_blacklist")
public class CarBlackListEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * uuid
     */
    private String uid;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 备注
     */
    private String notes;

    /**
     * 加入时间
     */
    private LocalDateTime addTime;

}
