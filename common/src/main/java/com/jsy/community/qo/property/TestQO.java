package com.jsy.community.qo.property;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-04-15 13:44
 **/
@Data
public class TestQO implements Serializable {

    private List<ElasticsearchCarQO> cars=new ArrayList<>();
}
