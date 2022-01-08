package com.jsy.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jsy.community.dto.advert.AdvertDto;
import com.jsy.community.entity.admin.AdvertEntity;
import com.jsy.community.qo.admin.AddAdvertQO;
import com.jsy.community.qo.admin.AdvertQO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author xrq
 * @version 1.0
 * @Description: 广告服务层
 * @date 2021/12/25 11:29
 */
public interface AdvertService extends IService<AdvertEntity> {
    boolean insertAdvert(AddAdvertQO param);

    IPage<AdvertDto> toPage(AdvertQO qo);

    boolean updateAdvert(AdvertEntity entity);

    String fileUpload(MultipartFile file);
}
