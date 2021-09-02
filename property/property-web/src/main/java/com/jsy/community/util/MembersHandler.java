package com.jsy.community.util;

import com.jsy.community.vo.property.HouseMemberVO;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @program: com.jsy.community
 * @description: 房间成员导入导出
 * @author: Hu
 * @create: 2021-08-31 15:43
 **/
public interface MembersHandler {
    /**
     * @Description: 成员表导出
     * @author: Hu
     * @since: 2021/8/31 15:47
     * @Param:
     * @return:
     */
    Workbook exportRelation(List<HouseMemberVO> houseMemberVOS);
}
