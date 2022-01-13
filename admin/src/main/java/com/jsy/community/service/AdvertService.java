package com.jsy.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.dto.advert.AdvertDto;
import com.jsy.community.entity.admin.AdvertEntity;
import com.jsy.community.qo.admin.AddAdvertQO;
import com.jsy.community.qo.admin.AdvertQO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告服务层
 * @date 2021/12/25 11:29
 */
public interface AdvertService extends IService<AdvertEntity> {
    /**
     * 新增一条广告
     * @param param 新增信息
     * @return 是否成功
     */
    boolean insertAdvert(AddAdvertQO param);
    /**
     * 分页条件查询广告列表
     * @param qo 查询条件
     * @return 广告列表
     */
    IPage<AdvertDto> toPage(AdvertQO qo);
    /**
     * 修改广告信息
     * @param entity 广告对象
     * @return 是否成功
     */
    boolean updateAdvert(AdvertEntity entity);
    /**
     * 上传广告图片
     * @param file 文件
     * @return 文件访问地址
     */
    String fileUpload(MultipartFile file);
}
