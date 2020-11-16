package com.jsy.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsy.community.api.ICarService;
import com.jsy.community.constant.Const;
import com.jsy.community.entity.CarEntity;
import com.jsy.community.mapper.CarMapper;
import com.jsy.community.qo.BaseQO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 车辆 服务实现类
 * @author YuLF
 * @since 2020-11-10
 */
@Slf4j
@DubboService(version = Const.version, group = Const.group)
public class CarServiceImpl extends ServiceImpl<CarMapper, CarEntity> implements ICarService {

    @Resource
    private CarMapper carMapper;

    /**
     * 根据提供的参数对车辆进行分页查询
     * @param param  车辆分页条件查询参数
     * @return       返回当前页数据
     */
    @Override
    public Page<CarEntity> queryProprietorCar(BaseQO<CarEntity> param) {
        QueryWrapper<CarEntity> wrapper = new QueryWrapper<>();
        CarEntity query = param.getQuery();
        Page<CarEntity> pageCondition = new Page<>( param.getPage(), param.getSize() );
        wrapper.eq("uid", query.getUid());
        //按条件查询
        //...
        //wrapper.eq("check_status", 0);
        wrapper.eq("deleted", 0);
        Page<CarEntity> resultData = page(pageCondition, wrapper);
        log.info("查询所属人车辆满足条件行数："+resultData.getTotal() + "每页显示条数："+resultData.getSize());
        return resultData;
    }

    /**
     * 根据实体类字段 进行更新
     * @param carEntity 车辆修改参数实体对象
     * @return           返回修改影响行数
     */
    @Override
    public Integer updateProprietorCar(CarEntity carEntity) {
        return carMapper.update(carEntity, new UpdateWrapper<CarEntity>().eq("id",carEntity.getId()).eq("uid", carEntity.getUid()).eq("deleted", 0));
    }

    /**
     * 根据车辆id 进行逻辑删除
     * @param params 条件参数列表
     * @return       返回逻辑删除行数
     */
    @Override
    public Integer deleteProprietorCar(Map<String, Object> params) {
        //逻辑删除
        return carMapper.deleteByMap(params);
    }

    /**
     * 新增车辆操作方法
     * @param carEntity 车辆实体对象
     * @return 返回插入结果
     */
    @Override
    public Integer addProprietorCar(CarEntity carEntity) {
        return carMapper.insert(carEntity);
    }

    /**
     * 根据车牌查询车辆是否存在
     * @param carPlate 车牌
     * @return 返回车辆是否存在
     */
    @Override
    public Boolean carIsExist(String carPlate) {
        return carMapper.selectCount(new QueryWrapper<CarEntity>().eq("car_plate", carPlate).eq("deleted",0)) > 0;
    }

    /**
     * 车辆图片上传服务接口实现方法
     * @param carImage 图片文件流
     * @param fileName 文件名称
     * @return         上传成功将放回图片URL，否则返回Null
     */
    @Override
    public String carImageUpload(byte[] carImage, String fileName) {
        //4.临时本地上传方式
        FileOutputStream fileOutputStream = null;
        try {
            //图片文件流
            fileOutputStream = new FileOutputStream(new File("D:" + File.separator + "TestFileDirectory" + File.separator + fileName));
            fileOutputStream.write(carImage);
            //上传成功返回访问路径
            return "https://www.baidu.com/" + fileName;
        } catch (IOException e) {
            return null;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
