package com.jsy.community.service.impl;

import com.jsy.community.api.ITenementService;
import com.jsy.community.constant.Const;
import com.jsy.community.mapper.TenementMapper;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.PropertyRelationQO;
import com.jsy.community.vo.PropertyTenementVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: com.jsy.community
 * @description:  物业租户查询
 * @author: Hu
 * @create: 2021-03-10 14:36
 **/
@DubboService(version = Const.version, group = Const.group_property)
public class TenementServiceImpl implements ITenementService {
    @Autowired
    private TenementMapper tenementMapper;


    /**
     * @Description: 分页查询
     * @author: Hu
     * @since: 2021/5/21 11:13
     * @Param: [baseQO]
     * @return: java.util.Map
     */
    @Override
    public Map list(BaseQO<PropertyRelationQO> baseQO) {
        if (baseQO.getSize()==null||baseQO.getSize()==0){
            baseQO.setSize(10L);
        }
        Long page=(baseQO.getPage()-1)*baseQO.getSize();
        List<PropertyTenementVO> relationVOS = tenementMapper.list(baseQO.getQuery(), page, baseQO.getSize());
        for (PropertyTenementVO relationVO : relationVOS) {
            relationVO.setHousing(replaceStr(relationVO.getHousing()));
            relationVO.setHouseTypeName(relationVO.getHouseType()==1?"商铺":relationVO.getHouseType()==2?"住宅":"");
        }
        Map map = new HashMap<>();
        map.put("list",relationVOS);
        map.put("total",tenementMapper.getTotal(baseQO.getQuery()));
        return map;
    }


    public static String replaceStr(String str){
        StringBuffer buf = new StringBuffer();
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        while(m.find()){
            String chinese = m.group();//匹配出的中文
            String pinyin = "";//在你的中文与拼音对应中找到对应拼音。
            m.appendReplacement(buf, pinyin);
        }
        return m.appendTail(buf).toString();
    }
}
