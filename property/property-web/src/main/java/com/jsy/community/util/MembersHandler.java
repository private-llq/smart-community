package com.jsy.community.util;

import com.jsy.community.vo.property.HouseMemberVO;
import com.jsy.community.vo.property.RelationImportErrVO;
import com.jsy.community.vo.property.RelationImportQO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * @Description: 成员表导入
     * @author: Hu
     * @since: 2021/8/31 15:47
     * @Param:
     * @return:
     */
    List<RelationImportQO> importRelation(MultipartFile file,List<RelationImportErrVO> errorVos);

    /**
     * @Description: 导出错误信息
     * @author: Hu
     * @since: 2021/9/3 16:04
     * @Param:
     * @return: 
     */
    Workbook exportErrorExcel(List<RelationImportErrVO> errorVos);
}
