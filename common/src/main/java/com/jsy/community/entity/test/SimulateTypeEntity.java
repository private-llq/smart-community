package com.jsy.community.entity.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lihao
 * @ClassName SimulateTypeEntity
 * @Date 2020/12/10  15:53
 * @Description
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SimulateTypeEntity implements Serializable {
	private Integer id;
	private String company;
}
