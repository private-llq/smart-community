package com.jsy.community.controller.test;

import com.jsy.community.annotation.ApiJSYController;
import com.jsy.community.api.ICommonService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.FullTextSearchEntity;
import com.jsy.community.qo.lease.HouseLeaseQO;
import com.jsy.community.utils.es.ElasticSearchImport;
import com.jsy.community.utils.es.Operation;
import com.jsy.community.utils.es.RecordFlag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author YuLF
 * @since 2021-02-02 14:33
 */
@RestController
@RequestMapping("/leaseHouse")
@ApiJSYController
public class LeaseController {


    @DubboReference(version = Const.version, group = Const.group_proprietor, check = false)
    private ICommonService commonService;

    @GetMapping("/test")
    public List<FullTextSearchEntity> test(){
        return commonService.fullTextSearchEntities();
    }


    @PostMapping()
    public String insertLeaseHouse(@RequestBody HouseLeaseQO qo){
        System.out.println("----------------房屋请求数据插入--------------------");
        ElasticSearchImport.elasticOperation( qo.getId(), RecordFlag.LEASE_HOUSE, Operation.INSERT, qo.getHouseTitle(), qo.getHouseImage()[0]);
        //在操作完成时调用
        return "true";
    }


    @DeleteMapping()
    public String deleteLeaseHouse(@RequestParam Long id){
        System.out.println("----------------删除数据插入--------------------");
        ElasticSearchImport.elasticOperation(id, RecordFlag.LEASE_HOUSE, Operation.DELETE, null, null);
        //在操作完成时调用
        return "true";
    }

    @PutMapping()
    public String deleteLeaseHouse(@RequestBody HouseLeaseQO qo){
        System.out.println("----------------更新数据插入--------------------");
        ElasticSearchImport.elasticOperation( qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), qo.getHouseImage()[0]);
        //在操作完成时调用
        return "true";
    }

    @GetMapping()
    public String getLeaseHouse(@RequestBody HouseLeaseQO qo){
        System.out.println("----------------更新数据插入--------------------");
        ElasticSearchImport.elasticOperation( qo.getId(), RecordFlag.LEASE_HOUSE, Operation.UPDATE, qo.getHouseTitle(), qo.getHouseImage()[0]);
        //在操作完成时调用
        return "true";
    }

}
