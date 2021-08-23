package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.IHouseService;
import com.jsy.community.api.IUserHouseService;
import com.jsy.community.api.IWQuestionnaireService;
import com.jsy.community.api.PropertyException;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.UserHouseEntity;
import com.jsy.community.entity.property.WOptions;
import com.jsy.community.entity.property.WProblem;
import com.jsy.community.entity.property.WQuestionnaire;
import com.jsy.community.entity.property.WResult;
import com.jsy.community.mapper.WOptionsMapper;
import com.jsy.community.mapper.WProblemMapper;
import com.jsy.community.mapper.WQuestionnaireMapper;
import com.jsy.community.mapper.WResultMapper;
import com.jsy.community.qo.property.*;
import com.jsy.community.util.Query;
import com.jsy.community.vo.property.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Arli
 * @since 2021-08-17
 */
@DubboService(version = Const.version, group = Const.group_property)
public class WQuestionnaireServiceImpl extends ServiceImpl<WQuestionnaireMapper, WQuestionnaire> implements IWQuestionnaireService {
    @Resource
    private WQuestionnaireMapper wQuestionnaireMapper;
    @Resource
    private WProblemMapper wProblemMapper;
    @Resource
    private WOptionsMapper wOptionsMapper;
    @Resource
    private WResultMapper wResultMapper;
    @Resource
    private IHouseService houseService;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private IUserHouseService userHouseService;


    @Override
    @Transactional
    public Boolean insterQuestionnaire(InsterQuestionnaireQO qo, Long adminCommunityId) {

        WQuestionnaire wQuestionnaire = new WQuestionnaire();
        BeanUtils.copyProperties(qo, wQuestionnaire);
        wQuestionnaire.setCommunityId(adminCommunityId)
                .setUuid(UUID.randomUUID().toString().replaceAll("\\-", ""));

        int insert = wQuestionnaireMapper.insert(wQuestionnaire);

        for (WProblemQO wProblemQO : qo.getProblemList()) {
            WProblem problem = new WProblem();
            BeanUtils.copyProperties(wProblemQO, problem);
            problem.setQuestionnaireId(wQuestionnaire.getId());
            int insert1 = wProblemMapper.insert(problem);
            if (wProblemQO.getProblemType() != 2) {//简答题
                for (WOptionsQO wOptionsQO : wProblemQO.getOptionsList()) {
                    WOptions wOptions = new WOptions();
                    BeanUtils.copyProperties(wOptionsQO, wOptions);
                    wOptions.setProblemId(problem.getId());
                    wOptions.setQuestionnaireId(wQuestionnaire.getId());
                    int insert2 = wOptionsMapper.insert(wOptions);
                }
            }


        }

        return true;
    }

    @Override
    public PageVO selectQuestionnaire(SelectQuestionnaireQO qo, Long adminCommunityId) {

        PageVO pageVO = new PageVO<SelectQuestionnaireQO>();
        Page<WQuestionnaire> page = new Page<>(qo.getPage(), qo.getSize());
        QueryWrapper<WQuestionnaire> queryWrapper = new QueryWrapper<>();
        ;
        queryWrapper.eq("community_id", adminCommunityId);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = sdf.format(date);

        //根据发布状态0未发布1已经发布
        if (qo.getReleaseStatus() != null) {
            if (qo.getReleaseStatus() == 0) {
                queryWrapper.eq("release_status", 0);
            }
            if (qo.getReleaseStatus() == 1) {
                queryWrapper.eq("release_status", 1);
            }

        }

        //0未开始（待发布）1进行中2已经就结束
        if (qo.getStatus() != null) {
            if (qo.getStatus() == 1) {
                queryWrapper.lt("opening", format);
                queryWrapper.gt("deadline", format);
            } else if (qo.getStatus() == 2) {
                queryWrapper.lt("deadline", format);
            } else if (qo.getStatus() == 0) {
                queryWrapper.gt("opening", format);
            }
        }


        Page<WQuestionnaire> page1 = wQuestionnaireMapper.selectPage(page, queryWrapper);
        pageVO.setPages(page1.getPages());
        pageVO.setCurrent(page1.getCurrent());
        pageVO.setTotal(page1.getTotal());
        pageVO.setSize(page1.getSize());

        List<SelectQuestionnaireVO> vos = new ArrayList<>();
        for (WQuestionnaire record : page1.getRecords()) {
            SelectQuestionnaireVO value = new SelectQuestionnaireVO();
            BeanUtils.copyProperties(record, value);
            //这里是参加已经调查的数量需要去数据库查询
            value.setStatisticalNumED(0);

            if (record.getRanges() == 0) {//部分楼栋需要进行查询楼栋的名称
                String buildings = record.getBuildings();
                String[] split = buildings.split(";");
                List<String> list = Arrays.asList(split);
                List<String> list1 = houseService.selectBuildingNameByIdList(list, adminCommunityId);
                String Building = "";
                for (String s : list1) {
                    Building = Building + s + ";";
                }
                Building = Building.substring(0, Building.length() - 1);
                value.setBuildings(Building);
            }
            vos.add(value);
        }
        pageVO.setRecords(vos);

        return pageVO;
    }

    @Override
    public SelectQuestionnaireAllVO selectQuestionnaireAll(String id, Long adminCommunityId) {
        SelectQuestionnaireAllVO vo = new SelectQuestionnaireAllVO();//返回对象

        WQuestionnaire wQuestionnaire = wQuestionnaireMapper.selectById(id);//查询问卷对象
        BeanUtils.copyProperties(wQuestionnaire, vo);//赋值给问卷返回对象
        //查询问卷下面的额所有问题

        List<WProblem> wProblems = wProblemMapper.selectList(new QueryWrapper<WProblem>().eq("questionnaire_id", id));

        List<WProblemVO> wProblemVOList = new ArrayList<>();//问题返回对象的集合

        for (WProblem wProblem : wProblems) {
            WProblemVO wProblemVO = new WProblemVO();
            BeanUtils.copyProperties(wProblem, wProblemVO);

            List<WOptionsVO> wOptionsVOList = new ArrayList<>();
            List<WOptions> wOptions = wOptionsMapper.selectList(new QueryWrapper<WOptions>().eq("problem_id", wProblemVO.getId()));
            for (WOptions wOption : wOptions) {
                WOptionsVO wOptionsVO = new WOptionsVO();
                BeanUtils.copyProperties(wOption, wOptionsVO);
                wOptionsVOList.add(wOptionsVO);
            }
            wProblemVO.setWOptionsVOList(wOptionsVOList);

            wProblemVOList.add(wProblemVO);
        }
        vo.setWProblemVOList(wProblemVOList);

        return vo;
    }

    @Override
    public Boolean insterAnswer(Long adminCommunityId, String userUuid, InsterAnswerQO qo) {

        List<WResult> wResults = wResultMapper.selectList(new QueryWrapper<WResult>().eq("questionnaire_id", adminCommunityId).eq("user_uuid", userUuid));
        System.out.println(wResults.size()+"长度");
        if (wResults.size()>-1) {
            throw  new PropertyException(500,"已经提交过");
        }

        Long questionnaireId = qo.getId();//问卷id
        int insert = 0;
        for (InsterAnswerProblemQO problem : qo.getList()) {
            WResult entity = new WResult();
            entity.setQuestionnaireId(questionnaireId);
            entity.setProblemId(problem.getId());
            entity.setUserUuid(userUuid);
            entity.setResult(problem.getAnswer());
            entity.setResultType(problem.getType());
            insert = wResultMapper.insert(entity);
        }
        if (insert == qo.getList().size()) {
            return true;
        }
        return false;


    }

    @Override
    public Boolean updateReleaseStatus(UpdateReleaseStatusQO qo) {
        WQuestionnaire wQuestionnaire = new WQuestionnaire();
        wQuestionnaire.setId(qo.getQuestionnaireId());
        wQuestionnaire.setReleaseStatus(qo.getReleaseStatus());
        int i = wQuestionnaireMapper.updateById(wQuestionnaire);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public PageVO selectQuestionnaireListByUser(Long adminCommunityId, String userId, SelectQuestionnaireListByUserQO qo) {
        //查询用户所在的楼栋id
        System.out.println(userId);
        String buildingId = wQuestionnaireMapper.selectbBuildingId(userId);
        System.out.println(buildingId + "______________________________");
        PageVO pageVO = new PageVO<WQuestionnaire>();
        Page page = new Page(qo.getPage(), qo.getSize());

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = sdf.format(date);

        QueryWrapper<WQuestionnaire> queryWrapper = new QueryWrapper<WQuestionnaire>()
                .eq("community_id", adminCommunityId)//这个小区
                .eq("release_status", 1)//已经发布
                //正在进行中
                .lt("opening", format)
                .gt("deadline", format)
                .eq("ranges", 1)
                .or(x -> x.eq("ranges", 0)
                        .like("buildings", buildingId)
                        .eq("community_id", adminCommunityId)//这个小区
                        .eq("release_status", 1)//已经发布
                        //正在进行中
                        .lt("opening", format)
                        .gt("deadline", format));

        Page page1 = wQuestionnaireMapper.selectPage(page, queryWrapper);
        pageVO.setPages(page1.getPages());
        pageVO.setCurrent(page1.getCurrent());
        pageVO.setTotal(page1.getTotal());
        pageVO.setSize(page1.getSize());
        pageVO.setRecords(page1.getRecords());
        return pageVO;
    }

    @Override
    public SelectQuestionnaireStatisticsVO selectQuestionnaireStatistics(String questionnaireId) {
        SelectQuestionnaireStatisticsVO vo = new SelectQuestionnaireStatisticsVO();
        //查询所有的答案
        List<WResult> wResults = wResultMapper.selectList(new QueryWrapper<WResult>().eq("questionnaire_id", questionnaireId));

        //问卷调查的所有问题
        List<WProblem> wProblems = wProblemMapper.selectList(new QueryWrapper<WProblem>().eq("questionnaire_id", questionnaireId));

        List<ProblemStatisticsVO> problemStatisticsVOS = new ArrayList<>();//返回的问题集合

        for (WProblem problem : wProblems) {//遍历调查的所有问题

            ProblemStatisticsVO problemStatisticsVO = new ProblemStatisticsVO();//返回的问题实例vo
            BeanUtils.copyProperties(problem, problemStatisticsVO);

            if (problem.getProblemType() == 0 || problem.getProblemType() == 1) {//单选题或者多选
                int num = 0;
                for (WResult wResult : wResults) {
                    if (wResult.getProblemId() == problem.getId()) {
                        num++;
                    }
                }
                problemStatisticsVO.setProblemAmount(num);//问题的回答总数


                ArrayList<WOptionsStatisticsVO> wOptionsStatisticsVOArrayList = new ArrayList<>();
                //查询所有选项
                List<WOptions> wOptions = wOptionsMapper.selectList(new QueryWrapper<WOptions>().eq("problem_id", problem.getId()));

                for (WOptions wOption : wOptions) {
                    WOptionsStatisticsVO wOptionsStatisticsVO = new WOptionsStatisticsVO();
                    BeanUtils.copyProperties(wOption, wOptionsStatisticsVO);

                    int numRest = 0;
                    for (WResult wResult : wResults) {
                        List<String> list = Arrays.asList(wResult.getResult().split(";"));
                        boolean contains = list.contains( wOption.getId()+"");
                        if (contains) {
                            numRest++;
                        }

                    }
                    wOptionsStatisticsVO.setAmount(numRest);//选项的选择数量


                    wOptionsStatisticsVOArrayList.add(wOptionsStatisticsVO);
                    problemStatisticsVO.setWOptionsStatisticsVOArrayList(wOptionsStatisticsVOArrayList);



                }


            }


            if (problem.getProblemType() == 2) {//简答题
                BeanUtils.copyProperties(problem, problemStatisticsVO);
                ArrayList<String>  ShortAnswerList=new ArrayList<>();
                for (WResult wResult : wResults) {
                    if (problem.getId()==wResult.getProblemId()) {
                        ShortAnswerList.add(wResult.getResult());

                    }
                }
                problemStatisticsVO.setShortAnswerList(ShortAnswerList);

               }
            problemStatisticsVOS.add(problemStatisticsVO);

            vo.setProblemStatisticsVOList(problemStatisticsVOS);
        }





        return vo;

    }


}