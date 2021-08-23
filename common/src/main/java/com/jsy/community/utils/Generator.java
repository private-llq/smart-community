package com.jsy.community.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;

public class Generator {
    public static void main(String[] args) {
        //构建代码自动生成器对象
        AutoGenerator autoGenerator = new AutoGenerator();
        //配置自动生成策略
        // 1、全局配置：
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");   //获取当前项目所在目录
        //System.out.println("获取当前项目所在目录"+projectPath);
        gc.setOutputDir("/D:\\model");         //自定义代码生成后的存放目录
        gc.setAuthor("Arli");                                  //设置项目作者
        gc.setOpen(false);                                     //代码生成后是否打开文件夹
        gc.setFileOverride(false);                             //是否覆盖
       // gc.setServiceName("%sService");                        //去Service的I前缀
        gc.setIdType(IdType.ASSIGN_UUID);                        //自定义主键生成策略
        gc.setDateType(DateType.ONLY_DATE);                    //自定义日期类型
       // gc.setSwagger2(true);                                  //实体使用swagger2注解
        autoGenerator.setGlobalConfig(gc);                     //添加全局配置
        //2、设置数据源:
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://222.178.212.29:3306/project_dev?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("helloworld123");
        dsc.setDbType(DbType.MYSQL);                          //指定数据库类型
        autoGenerator.setDataSource(dsc);                     //添加数据源配置
        //3、包名配置:
        PackageConfig pc = new PackageConfig();
        //pc.setModuleName("rbac");                             //指定生成的模块名称
       // pc.setParent("com.example");                             //设置模块中的父目录名
        pc.setEntity("entity");                                 //设置实体类目录名
        //pc.setMapper("mapper");                                 //设置mapper目录名
       // pc.setService("service");                               //设置service目录名
        //pc.setController("controller");                         //设置controller目录名
        autoGenerator.setPackageInfo(pc);
        //4、数据库表配置:
        StrategyConfig strategy = new StrategyConfig();
        // 设置要生成的实体类对应映射的表名
        strategy.setInclude("t_w_options","t_w_problem","t_w_questionnaire","t_w_result");
        strategy.setTablePrefix("t_");                       //去除表名前缀
        //设置表名生成策略，下划线转驼峰
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //设置列名生成策略，下划线转驼峰
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);                 //自动lombok；
        strategy.setLogicDeleteFieldName("deleted");         //设置使用逻辑删除策略的属性名
        // 自动填充配置 TableFill
        TableFill gmtCreate = new TableFill("create_time", FieldFill.INSERT);
        TableFill gmtModified = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(gmtCreate);
        tableFills.add(gmtModified);strategy.setTableFillList(tableFills);
        strategy.setVersionFieldName("version");             // 乐观锁
        //strategy.setRestControllerStyle(true);               //生成 @RestController 控制器
        strategy.setControllerMappingHyphenStyle(true);      //驼峰转连字符--->localhost:8080/hello_id_2
        autoGenerator.setStrategy(strategy);
        //执行自动生成
        autoGenerator.execute();
    }
}
