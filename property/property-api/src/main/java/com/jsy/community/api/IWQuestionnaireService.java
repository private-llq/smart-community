package com.jsy.community.api;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.entity.property.WQuestionnaire;
import com.jsy.community.qo.property.*;
import com.jsy.community.vo.property.PageVO;
import com.jsy.community.vo.property.SelectQuestionnaireAllVO;
import com.jsy.community.vo.property.SelectQuestionnaireStatisticsVO;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Arli
 * @since 2021-08-17
 */
public interface IWQuestionnaireService extends IService<WQuestionnaire> {

    Boolean insterQuestionnaire(InsterQuestionnaireQO qo, Long adminCommunityId);

    PageVO selectQuestionnaire(SelectQuestionnaireQO qo, Long adminCommunityId);

    SelectQuestionnaireAllVO selectQuestionnaireAll(String id, Long adminCommunityId);

    Boolean insterAnswer(Long adminCommunityId, String userId, InsterAnswerQO qo);

    Boolean updateReleaseStatus(UpdateReleaseStatusQO qo);

    PageVO selectQuestionnaireListByUser(Long adminCommunityId, String userId, SelectQuestionnaireListByUserQO qo);

    SelectQuestionnaireStatisticsVO selectQuestionnaireStatistics(String id);
}
