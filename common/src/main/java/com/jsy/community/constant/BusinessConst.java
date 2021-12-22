package com.jsy.community.constant;


/**
 * @author chq459799974
 * @description 业务相关常量
 * @since 2020-11-28 13:44
 **/
public interface BusinessConst {
    
    //============================ 短信验证码默认长度 =============================
    Integer SMS_VCODE_LENGTH_DEFAULT = 4;
    
    //============================ 系统常量 =============================
    /**
     * 系统类型-安卓
     */
    Integer SYS_TYPE_ANDROID = 1;
    /**
     * 系统类型-IOS
     */
    Integer SYS_TYPE_IOS = 2;
    
    //============================ 数字常量 =============================
    /**
     * Zero
     */
    Integer ZERO = 0;
    /**
     * ONE
     */
    Integer ONE = 1;


    //============================ 用户真实姓名 长度边界 =============================
    /**
     * 汉族最小姓名长度
     */
    Integer MIN_CHINESE_REAL_NAME_LENGTH = 2;
    /**
     * 汉族最大姓名长度
     */
    Integer MAX_CHINESE_REAL_NAME_LENGTH = 6;
    /**
     * 姓名最大长度
     */
    Integer MAX_REAL_NAME_LENGTH = 20;

    //============================ 用户性别 =============================
    /**
     * 未知
     */
    Integer SEX_UNKNOWN = 0;
    /**
     * 男
     */
    Integer SEX_MALE = 1;
    /**
     * 女
     */
    Integer SEX_FEMALE = 2;

    //============数据库t_user isRealAuth 实名认证状态=============
    /**
     * 已完全实名认证 - 三要素 身份证+姓名+实人认证
     */
    Integer CERTIFIED_FULL = 2;
    /**
     * 已部分实名认证 - 二要素 身份证+姓名
     */
    Integer CERTIFIED_PART = 1;
    /**
     * 未实名认证
     */
    Integer NO_REAL_NAME_AUTH = 0;

    /**
     * 房间成员查询类型 - 查询成员
     */
    Integer QUERY_HOUSE_MEMBER = 1;
    /**
     * 房间成员查询类型 - 查询邀请
     */
    Integer QUERY_HOUSE_MEMBER_INVITATION = 2;

    /**
     * 房屋租售 房屋介绍内容最大字符
     *
     * @author YuLF
     * @since 2020/12/10 10:29
     */
    short HOUSE_INTRODUCE_CHAR_MAX = 1000;

    /**
     * 房屋租售 房屋详细地址内容最大字符
     *
     * @author YuLF
     * @since 2020/12/10 10:29
     */
    short HOUSE_ADDRESS_CHAR_MAX = 128;

    /**
     * 房屋租售 房屋标题最大字符
     *
     * @author YuLF
     * @since 2020/12/10 10:29
     */
    short HOUSE_TITLE_CHAR_MAX = 32;

    /**
     * 房屋租售 选择省市区ID大范围数字
     *
     * @author YuLF
     * @since 2020/12/10 10:29
     */
    int HOUSE_ID_RANGE_MAX = 999999;

    /**
     * 房屋租售 房屋面积最大平方
     *
     * @author YuLF
     * @since 2020/12/10 10:29
     */
    short HOUSE_SQUARE_METER_MAX = Short.MAX_VALUE;

    /**
     * 房屋租售 房屋楼层最大字符
     *
     * @author YuLF
     * @since 2020/12/10 10:29
     */
    short HOUSE_FLOOR_CHAR_MAX = 10;

    //========= 小区门禁方式 ============
    /**
     * 小区门禁-无
     */
    String ACCESS_COMMUNITY_NONE = "0";
    /**
     * 小区门禁-二维码
     */
    String ACCESS_COMMUNITY_QR_CODE = "1";
    /**
     * 小区门禁-人脸识别
     */
    String ACCESS_COMMUNITY_FACE = "2";

    //========= 楼栋门禁方式 ============
    /**
     * 楼栋门禁-无
     */
    String ACCESS_BUILDING_NONE = "0";
    /**
     * 楼栋门禁-二维码
     */
    String ACCESS_BUILDING_QR_CODE = "1";
    /**
     * 楼栋门禁-可视对讲
     */
    String ACCESS_BUILDING_COMMUNICATION = "2";
    
    //======= 门禁进入方式(物业端)
    Integer ACCESS_TYPE_QRCODE = 1; //二维码
    Integer ACCESS_TYPE_FACE = 2; //人脸识别
    
    //========= 硬件类型 ============
    /**
     * 炫优人脸识别一体机
     */
    Integer HARDWARE_TYPE_XU_FACE = 1;

    //========= 证件类型 ================
    /**
     * 证件类型-身份证
     */
    Integer IDENTIFICATION_TYPE_IDCARD = 1;
    /**
     * 证件类型-护照
     */
    Integer IDENTIFICATION_TYPE_PASSPORT = 2;

    //========= 房间成员类型 ===========
    /**
     * 房间成员类型-亲属
     */
    Integer PERSON_TYPE_RELATIVE = 1;
    /**
     * 房间成员类型-租客
     */
    Integer PERSON_TYPE_TENANT = 2;

    /**
     * 租房异步线程池
     */
    String LEASE_ASYNC_POOL = "leaseAsyncThreadPool";

    /**
     * 业主异步线程池
     */
    String PROPRIETOR_ASYNC_POOL = "proprietorAsyncThreadPool";

    //========= 交易类型 ==========
    /**
     * 交易类型-私包
     */
    Integer BUSINESS_TYPE_PRIVATE_REDBAG = 1;
    /**
     * 交易类型-群红包
     */
    Integer BUSINESS_TYPE_GROUP_REDBAG = 2;
    /**
     * 交易类型-转账
     */
    Integer BUSINESS_TYPE_TRANSFER = 3;

    //========= 红包状态 ==========
    /**
     * 红包状态-未领取
     */
    Integer REDBAG_STATUS_UNCLAIMED = 0;
    /**
     * 红包状态-领取中
     */
    Integer REDBAG_STATUS_RECEIVING = 1;
    /**
     * 红包状态-已领完
     */
    Integer REDBAG_STATUS_FINISHED = 2;
    /**
     * 红包状态-已退回
     */
    Integer REDBAG_STATUS_BACK = -1;

    //========= 红包来源主体 ==========
    /**
     * 红包来源-个人
     */
    Integer REDBAG_FROM_TYPE_PERSON = 1;
    /**
     * 红包来源-官方
     */
    Integer REDBAG_FROM_TYPE_OFFICIAL = 2;

    //========= 交易行为 ==========
    /**
     * 交易行为-发红包
     */
    Integer BEHAVIOR_SEND = 1;
    /**
     * 交易行为-领红包
     */
    Integer BEHAVIOR_RECEIVE = 2;
    /**
     * 交易行为-退红包
     */
    Integer BEHAVIOR_BACK = 3;

    //========= 楼宇单位 ==========
    /**
     * 楼栋
     */
    int BUILDING_TYPE_BUILDING = 1;
    /**
     * 单元
     */
    int BUILDING_TYPE_UNIT = 2;
    /**
     * 楼层
     */
    int BUILDING_TYPE_FLOOR = 3;
    /**
     * 房间
     */
    int BUILDING_TYPE_DOOR = 4;
    
    /**
     * 单元里面查楼栋
     */
    int BUILDING_TYPE_UNIT_BUILDING = 5;
    
    /**
     * 房屋里面查楼栋
     */
    int BUILDING_TYPE_DOOR_BUILDING = 6;
    
    /**
     * 房屋里面查单元
     */
    int BUILDING_TYPE_DOOR_UNIT = 7;
    
    /**
     * 房屋里面查楼栋和单元
     */
    int BUILDING_TYPE_DOOR_BUILDING_UNIT = 8;

    //========= 文件上传 文件夹分类名称 ==========
    /**
     * APP用户系统默认头像 上传 至 文件服务器 的 bucket Name
     */
    String APP_SYS_DEFAULT_AVATAR_BUCKET_NAME = "sys-avatar";

    /**
     * 用户头像 上传 至 文件服务器 的 bucket Name
     */
    String AVATAR_BUCKET_NAME = "user-avatar";

    /**
     * 用户头像 人脸头像
     */
    String FAVE_AVATAR_BUCKET_NAME = "user-face-avatar";

    /**
     * 陌生人脸
     */
    String STRANGER_FACE_BUCKET_NAME = "stranger-face";

    /**
     * 物业押金凭证二维码
     */
    String DEPOSIT_QR_CODE = "deposit-qr-code-avatar";
    //车辆存储相关
    /**
     * 车辆图片 上传 至 文件服务器 的 bucket Name
     */
    String CAR_IMAGE_BUCKET_NAME = "car-avatar";
    /**
     * 车辆行驶证 存放 文件夹 的文件夹名称
     */
    String CAR_DRIVING_LICENSE_BUCKET_NAME = "car-driving-license";
    /**
     * 车辆图片 redis 的 缓存 bucket Name
     */
    String REDIS_CAR_IMAGE_BUCKET_NAME = "car_img_all";

    //========= 全文搜索 ==========
    /**
     * redis 存储 全文搜索热词的 前缀名称
     */
    String HOT_KEY_PREFIX = "fullTextSearch:hotKey:word";

    /**
     * redis 存储 全文搜索热词的 时间存储 前缀
     */
    String HOT_KEY_TIME_PREFIX = "fullTextSearch:hotKey:time";

    /**
     * 接口 全文搜索 热词 获取最大数量限制
     */
    Integer HOT_KEY_MAX_NUM = 20;

    //Es全文搜索索引名 mq 队列、交换机名称
    /**
     * Es全文搜索索引名称
     */
    String FULL_TEXT_SEARCH_INDEX = "full-text-search-index";
    String INDEX_CAR = "index-car";
    /**
     * 社区app主页全文搜索交换机名称
     */
    String APP_SEARCH_EXCHANGE_NAME = "app.search.topic.exchange";
    /**
     * 社区app主页全文搜索队列名称
     */
    String APP_SEARCH_QUEUE_NAME = "app.search.topic.queue";
    /**
     * 社区app主页全文搜索路由key名称
     */
    String APP_SEARCH_ROUTE_KEY = "appSearchFullText";

    //========= 车辆正则 ==========
    /**
     * 普通机动车
     */
    String REGEX_OF_CAR = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
    /**
     * 新能源车
     */
    String REGEX_OF_NEW_ENERGY_CAR = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}(([0-9]{5}[DF])|([DF][A-HJ-NP-Z0-9][0-9]{4}))$";

    //============ 国籍id范围 ===============
    /**
     * 范围最小的ID
     */
    Integer COUNTRY_ID_MIN = 1;
    /**
     * 范围最大的ID
     */
    Integer COUNTRY_ID_MAX = 233;
    //============ 租赁相关 ===============
    /**
     * 用户发布房源最大
     */
    Integer USER_PUBLISH_LEASE_MAX = 10;

    /**
     * 租赁模块 房屋收藏 t_house_favorite  中 favorite_type 商铺类型
     */
    Short SHOP_FAVORITE_TYPE = 1;

    /**
     * 租赁模块 签约倒计时天数
     */
    long COUNTDOWN_DAYS_TO_CONTRACT = 7;

    /**
     * 租赁模块 签约申请倒计时天数
     */
    long COUNTDOWN_TO_CONTRACT_APPLY = 3;

    /**
     * 一天的毫秒数
     */
    long ONE_DAY = 1000 * 60 * 60 * 24;
    
    //============ 用户登录类型 ===============
    /**
     * E到家用户端登录
     */
    String E_HOME = "e_home";
    /**
     * 商家后台
     */
    String SHOP_ADMIN = "shop_admin";
    /**
     * 物业后台
     */
    String PROPERTY_ADMIN = "property_admin";
    /**
     * 小区管理员（目前阶段主要用来区分 物业后台管理员，和小区管理员页面角色）
     */
    String COMMUNITY_ADMIN = "community_admin";
    /**
     * 大后台
     */
    String ULTIMATE_ADMIN = "ultimate_admin";
}
