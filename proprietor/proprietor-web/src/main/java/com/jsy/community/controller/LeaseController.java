package com.jsy.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.annotation.auth.Login;
import com.jsy.community.api.ICommonService;
import com.jsy.community.config.web.ElasticsearchConfig;
import com.jsy.community.constant.BusinessConst;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.entity.proprietor.AssetLeaseRecordEntity;
import com.jsy.community.utils.ValidatorUtils;
import com.jsy.community.vo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author YuLF
 * @since 2021-02-02 14:33
 */
@RestController
@RequestMapping("/leaseHouse")
@ApiJSYController
@RequiredArgsConstructor
public class LeaseController {

    private final RestHighLevelClient customElasticsearchClient;

    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;



    /**
     * 社区消息、社区趣事、租赁数据 批量导入es接口
     */
    @GetMapping("/esBulkImport")
    public String test() throws IOException {
        List<FullTextSearchEntity> fullTextSearchEntities = commonService.fullTextSearchEntities();
        BulkRequest bulkRequest = new BulkRequest();
        fullTextSearchEntities.forEach( l -> {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(l);
            jsonObject.put("searchTitle", l.getTitle());
            jsonObject.put("esId", l.getId());
            IndexRequest indexRequest = new IndexRequest(BusinessConst.FULL_TEXT_SEARCH_INDEX)
                    .id(String.valueOf(l.getId()))
                    .source(jsonObject.toJSONString(), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = customElasticsearchClient.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);
        if(bulk.status() == RestStatus.OK){
            return "true";
        }
        return "false";
    }

    /**
     * @author: Pipi
     * @description: 租赁发起签约
     * @param assetLeaseRecordEntity: 房屋租赁记录表实体
     * @return: com.jsy.community.vo.CommonResult
     * @date: 2021/8/31 15:18
     **/
    @Login
    @PostMapping("/v2/initContract")
    public CommonResult initContract(@RequestBody AssetLeaseRecordEntity assetLeaseRecordEntity) {
        ValidatorUtils.validateEntity(assetLeaseRecordEntity, AssetLeaseRecordEntity.InitContractValidate.class);
        return CommonResult.ok();
    }
}
