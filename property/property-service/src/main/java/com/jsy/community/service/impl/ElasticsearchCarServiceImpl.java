package com.jsy.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.jsy.community.api.IElasticsearchCarService;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.qo.BaseQO;
import com.jsy.community.qo.property.ElasticsearchCarQO;
import com.jsy.community.qo.property.ElasticsearchCarSearchQO;
import com.jsy.community.utils.es.ElasticsearchCarUtil;
import com.jsy.community.vo.property.ElasticsearchCarVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @program: com.jsy.community
 * @description:
 * @author: Hu
 * @create: 2021-03-25 14:45
 **/
@DubboService(version = Const.version, group = Const.group_property)
@Slf4j
public class ElasticsearchCarServiceImpl implements IElasticsearchCarService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * @Description: 删除
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    @Override
    public void deleteData(Long id){
        //第一个参数为操作索引名称、第二个参数为删除文档的id
        DeleteRequest deleteRequest = new DeleteRequest(
                BusinessConst.INDEX_CAR, id+"");
        DeleteResponse response = null;
        try {
            response = restHighLevelClient.delete(deleteRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("删除失败："+e.getMessage());
        }
        DocWriteResponse.Result result = response.getResult();
    }

    /**
     * @Description: 修改
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    @Override
    public void updateData(ElasticsearchCarQO elasticsearchCarQO) {
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(BusinessConst.INDEX_CAR);
        //docAsUpsert表示 如果文档不存在则创建
        updateRequest.docAsUpsert(true);
        updateRequest.id(elasticsearchCarQO.getId()+"");
        //转换为json串发给elasticsearch
        updateRequest.doc(JSON.toJSONString(elasticsearchCarQO), XContentType.JSON);
        UpdateResponse update = null;
        try {
            update = restHighLevelClient.update(updateRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("修改失败："+e.getMessage());
        }
        DocWriteResponse.Result result = update.getResult();
    }

    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    @Override
    public void insertData(ElasticsearchCarQO elasticsearchCarQO){
        //构造函数传入索引名、其他两个构造函数传入type和id的已停止使用，
        IndexRequest indexRequest = new IndexRequest(BusinessConst.INDEX_CAR);
        indexRequest.id(elasticsearchCarQO.getId()+"");
        //转换为json串发给elasticsearch
        indexRequest.source(JSON.toJSONString(elasticsearchCarQO), XContentType.JSON);
        //发送保存 第一个参数是构造的请求，第二个参数是请求头需要的一些操作如验证token
        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("新增失败："+e.getMessage());
        }
        DocWriteResponse.Result result = indexResponse.getResult();
    }



    /**
     * @Description: 新增
     * @author: Hu
     * @since: 2021/3/25 15:37
     * @Param:
     * @return:
     */
    @Override
    public List<ElasticsearchCarVO> searchData(BaseQO<ElasticsearchCarSearchQO> baseQO){
        ElasticsearchCarUtil.search(baseQO,restHighLevelClient);
        return null;
    }

}
