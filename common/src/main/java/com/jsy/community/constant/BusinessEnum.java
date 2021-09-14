package com.jsy.community.constant;

import com.jsy.community.entity.HouseLeaseConstEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author chq459799974
 * @description 业务相关枚举(不会变动的数据)(如有改变，提到表里做后台配置)
 * @since 2020-11-28 13:47
 **/
public interface BusinessEnum {
	
	Logger log = LoggerFactory.getLogger(BusinessEnum.class);
	
	/**
	 * 资源类查询类型大字典
	 */
	Map<String,List<Map<String, Object>>> sourceMap = new HashMap<>();
	
	/**
	* @Description: 省市区查询类型(type 1.带参 2.不带参 ; classType 带参类型)
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
	enum RegionQueryTypeEnum {
		/**
		 * 省市区查询类型
		 */
		SUB("getSubRegion", 1, 1, Integer.class),
		CITY_MAP("getCityMap", 2, 2, null),
		HOT_CITY("getHotCityList", 3,2, null),
		VAGUE_QUERY_CITY("vagueQueryCity", 4,1, String.class),
		CITY_LIST("getCityList", 5,2, null),
		VAGUE_QUERY_AREA("vagueQueryArea", 6,1, Object.class);
		private String name;
		private Integer code;
		private Integer type;
		private Class classType;
		RegionQueryTypeEnum(String name, Integer code, Integer type, Class classType) {
			this.name = name;
			this.code = code;
			this.type = type;
			this.classType = classType;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		public Integer getType() {
			return type;
		}
		
		public void setType(Integer type) {
			this.type = type;
		}
		
		public Class getClassType() {
			return classType;
		}
		
		public void setClassType(Class classType) {
			this.classType = classType;
		}
		
		@Override
		public String toString() {
			return this.name+"_"+this.code+"_"+this.type+"_"+this.classType;
		}
		
		public static final Map<Integer, String> regionQueryNameMap = new HashMap<>();
		public static final Map<Integer, Integer> regionQueryTypeMap = new HashMap<>();
		public static final Map<Integer, Class> regionQueryClassTypeMap = new HashMap<>();
		static {
			for(RegionQueryTypeEnum regionQueryTypeEnum : RegionQueryTypeEnum.values()){
				regionQueryNameMap.put(regionQueryTypeEnum.getCode(), regionQueryTypeEnum.getName());
				regionQueryTypeMap.put(regionQueryTypeEnum.getCode(), regionQueryTypeEnum.getType());
				regionQueryClassTypeMap.put(regionQueryTypeEnum.getCode(), regionQueryTypeEnum.getClassType());
			}
		}
	}
	
	/**
	* @Description: 车辆类型
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
	enum CarTypeEnum {
		/**
		 * 车辆枚举
		 */
		TINY("微型车", 1),
		SMALL("小型车", 2),
		COMPACT("紧凑型车",3),
		MIDDLE("中型车",4),
		ML("中大型车",5),
		OTHER("其他车型",6);
		private String name;
		private Integer code;
		CarTypeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code+"_"+this.name;
		}
		
		public static final List<Map<String, Object>> CAR_TYPE_LIST = new ArrayList<>();
		public static final Map<Integer, String> CAR_TYPE_MAP = new HashMap<>();
		public static  final  int CAR_TYPE_MAX = 6;
		public static  final  int CAR_TYPE_MIN = 1;

		public static String getCode(Integer code){
			CarTypeEnum[] values = CarTypeEnum.values();
			for(CarTypeEnum c : values){
				if( c.code.equals(code) ){
					return c.name;
				}
			}
			return OTHER.name;
		}
		/**
		 * 上述6种类型 含汽车的绝大多数种类
		 * 拿到包含
		 * @author YuLF
		 * @since  2021/2/25 9:42
		 */
		public static CarTypeEnum getContainsType(String carType){
			int matchStrLength = 3;
			if( Objects.isNull(carType) || carType.length() < matchStrLength ){
				return CarTypeEnum.OTHER;
			}
			CarTypeEnum[] values = CarTypeEnum.values();
			for( CarTypeEnum carTypeEnum : values ){
				if( carTypeEnum.name.contains(carType.substring(0, 2)) ){
					return carTypeEnum;
				}
			}
			return CarTypeEnum.OTHER;
		}

		static {
			for(CarTypeEnum regionQueryTypeEnum : CarTypeEnum.values()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", regionQueryTypeEnum.getCode());
				map.put("name", regionQueryTypeEnum.getName());
				CAR_TYPE_LIST.add(map);
				CAR_TYPE_MAP.put(regionQueryTypeEnum.getCode(), regionQueryTypeEnum.getName());
			}
			sourceMap.put("carType", CAR_TYPE_LIST);
		}
	}

	/**
	 * 身份证件类型
	 * @author YuLF
	 * @since  2021/2/25 11:29
	 */
	enum IdentificationType {
		/**
		 * 身份证件类型
		 */
		ID_CARD("居民身份证",1),
		PASSPORT("护照",2);

		public static final Integer MIN = 1;
		public static final Integer MAX = 2;

		private final String name;
		private final Integer code;

		IdentificationType(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public Integer getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		private volatile static Map<Integer, String> kv = null;

		public static Map<Integer, String> getKv(){
			if( kv == null ){
				synchronized (IdentificationType.class){
					if(kv == null){
						IdentificationType[] values = values();
						kv = new HashMap<>(values.length);
						for(IdentificationType everyOne : values){
							kv.put(everyOne.code, everyOne.name);
						}
					}
				}
			}
			return kv;
		}
	}
	/**
	* @Description: 审核状态枚举
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
	enum CheckStatusEnum {
		/**
		 * 审核状态枚举
		 */
		UN_CHECK("未审核", 0),
		PASS("通过", 1),
		UNPASS("拒绝", 2);
		private String name;
		private Integer code;
		
		CheckStatusEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> checkStatusList = new ArrayList<>();
		public static final Map<Integer, String> checkStatusMap = new HashMap<>();

		static {
			for (CheckStatusEnum checkStatusEnum : CheckStatusEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", checkStatusEnum.getCode());
				map.put("name", checkStatusEnum.getName());
				checkStatusList.add(map);
				checkStatusMap.put(checkStatusEnum.getCode(), checkStatusEnum.getName());
			}
			sourceMap.put("checkStatus",checkStatusList);
		}
	}
	
	/**
	* @Description: 来访事由枚举
	 * @Author: chq459799974
	 * @Date: 2020/11/28
	**/
	enum VisitReasonEnum {
		NORMAL("一般来访", 1),
		APPLICATOIN("应聘来访", 2),
		FRIEND("走亲访友", 3),
		BUSINESS("客户来访", 4);
		private String name;
		private Integer code;
		
		VisitReasonEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> visitReasonList = new ArrayList<>();
		public static final Map<Integer, String> visitReasonMap = new HashMap<>();
		
		static {
			for (VisitReasonEnum visitReasonEnum : VisitReasonEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", visitReasonEnum.getCode());
				map.put("name", visitReasonEnum.getName());
				visitReasonList.add(map);
				visitReasonMap.put(visitReasonEnum.getCode(), visitReasonEnum.getName());
			}
			sourceMap.put("visitReason",visitReasonList);
		}
	}
	
	/**
	 * @Description: 社区门禁授权类型
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum CommunityAccessEnum {
		NONE("无", 0),
		QR_CODE("二维码通行证", 1),
		FACE("人脸识别", 2);
		private String name;
		private Integer code;
		
		CommunityAccessEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> communityAccessList = new ArrayList<>();
		public static final Map<Integer, String> communityAccessMap = new HashMap<>();
		
		static {
			for (CommunityAccessEnum communityAccessEnum : CommunityAccessEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", communityAccessEnum.getCode());
				map.put("name", communityAccessEnum.getName());
				communityAccessList.add(map);
				communityAccessMap.put(communityAccessEnum.getCode(), communityAccessEnum.getName());
			}
			sourceMap.put("communityAccess",communityAccessList);
		}
	}
	
	/**
	 * @Description: 楼栋门禁授权类型
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum BuildingAccessEnum {
		NONE("无", 0),
		QR_CODE("二维码通行证", 1),
		COMMUNICATION("可视对讲", 2);
		private String name;
		private Integer code;
		
		BuildingAccessEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> buildingAccessList = new ArrayList<>();
		public static final Map<Integer, String> buildingAccessMap = new HashMap<>();
		
		static {
			for (BuildingAccessEnum buildingAccessEnum : BuildingAccessEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", buildingAccessEnum.getCode());
				map.put("name", buildingAccessEnum.getName());
				buildingAccessList.add(map);
				buildingAccessMap.put(buildingAccessEnum.getCode(), buildingAccessEnum.getName());
			}
			sourceMap.put("buildingAccess",buildingAccessList);
		}
	}
	
	/**
	 * @Description: 门禁类型
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum EntryTypeEnum {
		COMMUNITY("小区", 1),
		BUILDING("楼栋", 2);
		private String name;
		private Integer code;
		
		EntryTypeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> entryTypeList = new ArrayList<>();
		
		static {
			for (EntryTypeEnum entryTypeEnum : EntryTypeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", entryTypeEnum.getCode());
				map.put("name", entryTypeEnum.getName());
				entryTypeList.add(map);
			}
			sourceMap.put("entryType",entryTypeList);
		}
	}

	/**
	 * @Description: 户口类型
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum FamilyTypeEnum {
		COUNTRY("农村户口", 1),
		CILTES("城镇户口", 2);
		private String name;
		private Integer code;

		FamilyTypeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> familyTypeList = new ArrayList<>();

		static {
			for (FamilyTypeEnum entryTypeEnum : FamilyTypeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", entryTypeEnum.getCode());
				map.put("name", entryTypeEnum.getName());
				familyTypeList.add(map);
			}
			sourceMap.put("familyType",familyTypeList);
		}
	}

	/**
	 * @Description: 户口类型
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum MaritalStatusEnum {
		SECRECY("保密", 0),
		MARRIED("已婚", 1),
		SPINSTERHOOD("未婚", 2);
		private String name;
		private Integer code;

		MaritalStatusEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> maritalStatusList = new ArrayList<>();

		static {
			for (MaritalStatusEnum entryTypeEnum : MaritalStatusEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", entryTypeEnum.getCode());
				map.put("name", entryTypeEnum.getName());
				maritalStatusList.add(map);
			}
			sourceMap.put("maritalStatus",maritalStatusList);
		}
	}
	
	/**
	 * @Description: 亲属关系枚举
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum RelationshipEnum {
		TEMPORARY("临时",0),
		PROPRIETOR("业主", 1),
		RELATIVES("亲属", 6),
		TENANT("租客", 7);
		private String name;
		private Integer code;
		
		RelationshipEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}
		
		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}
		
		public static final List<Map<String, Object>> relationshipList = new ArrayList<>();
		public static final Map<Integer, String> relationshipMap = new HashMap<>();

		public static String getCodeName(Integer code){
			RelationshipEnum[] values = RelationshipEnum.values();
			for(RelationshipEnum c : values){
				if( c.code.equals(code) ){
					return c.name;
				}
			}
			return null;
		}

		public static Integer getNameCode(String name){
			RelationshipEnum[] values = RelationshipEnum.values();
			for(RelationshipEnum c : values){
				if( c.name.equals(name) ){
					return c.code;
				}
			}
			return null;
		}

		static {
			for (RelationshipEnum relationshipEnum : RelationshipEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", relationshipEnum.getCode());
				map.put("name", relationshipEnum.getName());
				relationshipList.add(map);
				relationshipMap.put(relationshipEnum.getCode(), relationshipEnum.getName());
			}
			sourceMap.put("relationship",relationshipList);
		}
	}

	/**
	 * @Description: 业主标签
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum MemberTallyEnum {
		SOLITUDE("独居",1),
		ORPHANS("孤寡", 2),
		DISABILITY("残疾", 3),
		LEFT("留守", 4);
		private String name;
		private Integer code;

		MemberTallyEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> tallyList = new ArrayList<>();
		public static final Map<Integer, String> tallyMap = new HashMap<>();

		public static String getCode(Integer code){
			MemberTallyEnum[] values = MemberTallyEnum.values();
			for(MemberTallyEnum c : values){
				if( c.code.equals(code) ){
					return c.name;
				}
			}
			return null;
		}
		static {
			for (MemberTallyEnum tallyEnum : MemberTallyEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", tallyEnum.getCode());
				map.put("name", tallyEnum.getName());
				tallyList.add(map);
				tallyMap.put(tallyEnum.getCode(), tallyEnum.getName());
			}
			sourceMap.put("memberTally",tallyList);
		}
	}
	
	/**
	 * @Description: 空气质量枚举
	 * @Author: chq459799974
	 * @Date: 2020/2/25
	 **/
	enum AQIEnum {
		LV1("优", 0, 50),
		LV2("良", 51, 100),
		LV3("轻度污染", 101, 150),
		LV4("中度污染", 151, 200),
		LV5("重度污染", 201, 250),
		LV6("严重污染", 300, 999);
		private String name;
		private int min;
		private int max;
		
		AQIEnum(String name, int min, int max) {
			this.name = name;
			this.min = min;
			this.max = max;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getMin() {
			return min;
		}
		public void setMin(int min) {
			this.min = min;
		}
		public int getMax() {
			return max;
		}
		public void setMax(int max) {
			this.max = max;
		}
		
		public static String getAQIName(int value, Double lon, Double lat, String cityId){
			for(AQIEnum qQIEnum : AQIEnum.values()){
				if(value > qQIEnum.min && value < qQIEnum.max){
					return qQIEnum.getName();
				}
			}
			log.error("空气质量指数异常 value：" + value + " 经度：" + lon + " 纬度：" + lat + " 天气接口城市ID：" + cityId);
			return "";
		}
	}


	/**
	 * 房屋租售 房屋朝向 常量
	 * @author YuLF
	 * @since  2020/12/10 10:02
	 */
	enum HouseDirectionEnum {
		//1.东.2.西 3.南 4.北. 5.东南 6.东北 7.西北 8.西南
		EAST("东", 1),
		WEST("西", 2),
		SOUTH("南", 3),
		NORTH("北", 4),
		SOUTHEAST("东南", 5),
		NORTHEAST("东北", 6),
		NORTHWEST("西北", 7),
		SOUTHWEST("西南", 8);
		private final String name;
		private final Integer code;

		HouseDirectionEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public static final int min = 1;

		public static final int max = 8;

		private volatile static Map<Integer, String> kv = null;
		/**
		 * 返回当前常量的Key Val  由于当使用HouseDirectionEnum的时候 不一定会用到kv
		 * 例：{东南=5, 东北=6, 南=3, 北=4, 西北=7, 西南=8, 东=1, 西=2}
		 */
		public static Map<Integer, String> getKv(){
			if( kv == null ){
				synchronized (HouseDirectionEnum.class){
					if(kv == null){
						HouseDirectionEnum[] values = values();
						kv = new HashMap<>(values.length);
						for(HouseDirectionEnum everyOne : values){
							kv.put(everyOne.code, everyOne.name);
						}
					}
				}
			}
			return kv;
		}

		public static String getDirectionName(Integer code){
			Map<Integer, String> kv = getKv();
			return kv.get(code);
		}

	}

	/**
	 * 房屋租售 房屋用途 常量
	 * @author YuLF
	 * @since  2020/12/10 10:02
	 *  1住宅、2工商业、3仓库
	 */
	enum HouseUsageEnum {
		RESIDENCE("住宅", 1),
		INDUSTRY_AND_COMMERCE("工商业", 2),
		WAREHOUSE("仓库", 3);
		private final String name;
		private final Integer code;

		HouseUsageEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public static final int min = 1;

		public static final int max = 3;

		private volatile static Map<String, Integer> kv = null;

		public static Map<String, Integer> getKv(){
			if( kv == null ){
				synchronized (HouseDirectionEnum.class){
					if(kv == null){
						HouseUsageEnum[] values = values();
						kv = new HashMap<>(values.length);
						for(HouseUsageEnum everyOne : values){
							kv.put(everyOne.name, everyOne.code);
						}
					}
				}
			}
			return kv;
		}
	}

	/**
	 * 房屋租售 房屋种类 常量
	 * @author YuLF
	 * @since  2020/12/10 10:02
	 *  1.商品房、2.经济适用房、3.央产房、4.军产房、5.公房、6.小产权房、7.自建住房
	 */
	enum HouseKindEnum {
		COMMERCIAL_HOUSE("商品房", 1),
		ECONOMIC_HOUSE("经济房", 2),
		CENTER_PRODUCTION_HOUSE("央产房", 3),
		ARMY_PRODUCTION_HOUSE("军产房", 4),
		COMMON_HOUSE("公房", 5),
		SMALL_PROPERTY_HOUSE("小产权房", 6),
		BUILD_BY_ONESELF_HOUSE("自建住房", 7);
		private final String name;
		private final Integer code;

		HouseKindEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public static final int min = 1;

		public static final int max = 7;

		private volatile static Map<String, Integer> kv = null;

		public static Map<String, Integer> getKv(){
			if( kv == null ){
				synchronized (HouseDirectionEnum.class){
					if(kv == null){
						HouseKindEnum[] values = values();
						kv = new HashMap<>(values.length);
						for(HouseKindEnum everyOne : values){
							kv.put(everyOne.name, everyOne.code);
						}
					}
				}
			}
			return kv;
		}
	}

	/**
	 * 房屋租售常量类，所有常量在第一次调用时 从数据库获取 保持在内存
	 */
	class HouseLeaseSaleEnum {

		public static List<HouseLeaseConstEntity> houseLeaseConstEntityList = null;


	}
	
	
	
	/**
	 * 商铺租售 商铺发布源 常量
	 * @author lihao
	 * @since  2020/12/10 10:02
	 *  1业主、2物业、3未知
	 */
	enum SourceEnum {
		PROPRIETOR(1,"业主"),
		PROPERTY(2,"物业" ),
		UNKNOWN(3,"不限");
		private final String name;
		private final Integer code;
		
		SourceEnum(Integer code,String name) {
			this.name = name;
			this.code = code;
		}
		
		public static final int min = 1;
		
		public static final int max = 3;
		
		private volatile static Map<Integer,String > kv = null;
		
		public static Map<Integer,String> getKv(){
			if( kv == null ){
				synchronized (HouseDirectionEnum.class){
					if(kv == null){
						SourceEnum[] values = values();
						kv = new HashMap<>(values.length);
						for(SourceEnum everyOne : values){
							kv.put(everyOne.code,everyOne.name);
						}
					}
				}
			}
			return kv;
		}
	}

	/**
	 * @Description: 投诉类型枚举
	 * @author: Hu
	 * @since: 2021/3/17 14:22
	 * @Param:
	 * @return:
	 */
	enum ComplainTypeEnum {
		QUALITY("质量投诉", 1),
		MAINTAIN("维修投诉", 2),
		DISTURBING("扰民投诉", 3),
		SAFETY("安全投诉", 4),
		PARK("停车管理投诉", 5),
		ENVIRONMENT("环境投诉", 6),
		EQUIPMENT("设备设施", 7),
		SERVE("服务投诉", 8),
		COST("费用投诉", 9),
		OTHER("其他投诉", 10);
		private String name;
		private Integer code;

		ComplainTypeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public static List toList() {
			ComplainTypeEnum[] values = ComplainTypeEnum.values();
			List<Map<String, Object>> list = new ArrayList();
			Map map=null;
			for (ComplainTypeEnum value : values) {
				map=new HashMap();
				map.put("type", value.code);
				map.put("typeName", value.name);
				list.add(map);
			}
			return list;
		}

		public static final List<Map<String, Object>> Complain_TYPE_LIST = new ArrayList<>();
		public static final Map<Integer, String> Complain_TYPE_MAP = new HashMap<>();
		public static final int Complain_TYPE_MAX = 10;
		public static final int Complain_TYPE_MIN = 1;

		public static String getCode(Integer code) {
			ComplainTypeEnum[] values = ComplainTypeEnum.values();
			for (ComplainTypeEnum c : values) {
				if (c.code.equals(code)) {
					return c.name;
				}
			}
			return OTHER.name;
		}
	}

	/**
	 *@Author: Pipi
	 *@Description: 预约看房入住时间枚举
	 *@Date: 2021/3/27 11:41
	 **/
	enum CheckInTimeEnum {
		WITHIN_WEEK("1周内", 1),
		WITHIN_ONE_TO_TWO_WEEKS("1-2周内", 2),
		WITHIN_TWO_TO_FOUR_WEEKS("2-4周内", 3),
		ONE_MONTH_LATER("1个月之后", 4);
		private final String name;
		private final Integer code;

		CheckInTimeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public static String getName(Integer code) {
			CheckInTimeEnum[] values = CheckInTimeEnum.values();
			for (CheckInTimeEnum c : values) {
				if (c.code.equals(code)) {
					return c.name;
				}
			}
			return null;
		}

		public static Integer getCode(String name) {
			CheckInTimeEnum[] values = CheckInTimeEnum.values();
			for (CheckInTimeEnum c : values) {
				if (c.name.equals(name)) {
					return c.code;
				}
			}
			return null;
		}
	}

	/**
	 * @Description:  缴费项目计费方式
	 * @author: Hu
	 * @since: 2021/7/30 9:28
	 * @Param: 
	 * @return: 
	 */
	enum ChargeModeEnum {
		AREA("按面积", 1),
		AUANTITY("按量", 2),
		FAMILY("按户", 3);
		private String name;
		private Integer code;

		ChargeModeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> maritalStatusList = new ArrayList<>();

		static {
			for (ChargeModeEnum entryTypeEnum : ChargeModeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", entryTypeEnum.getCode());
				map.put("name", entryTypeEnum.getName());
				maritalStatusList.add(map);
			}
			sourceMap.put("chargeMode",maritalStatusList);
		}
	}

	/**
	 * @Description: 缴费项目类型
	 * @author: Hu
	 * @since: 2021/7/30 9:28
	 * @Param:
	 * @return:
	 */
	enum ProjectTypeEnum {
		PERIOD("周期", 1),
		TEMPORARY("临时", 2);
		private String name;
		private Integer code;

		ProjectTypeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> maritalStatusList = new ArrayList<>();

		static {
			for (ProjectTypeEnum entryTypeEnum : ProjectTypeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", entryTypeEnum.getCode());
				map.put("name", entryTypeEnum.getName());
				maritalStatusList.add(map);
			}
			sourceMap.put("projectType",maritalStatusList);
		}
	}

	/**
	 * @Description: 物业缴费项目生成周期
	 * @author: Hu
	 * @since: 2021/7/30 9:28
	 * @Param:
	 * @return:
	 */
	enum FeeRulePeriodEnum {
		MONTH("月", 1),
		QUARTER("季度", 2),
		SEMI_ANNUAL("半年", 3),
		YEAR("一年", 4);
		private String name;
		private Integer code;

		FeeRulePeriodEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> maritalStatusList = new ArrayList<>();

		public static String getName(Integer code) {
			FeeRulePeriodEnum[] values = FeeRulePeriodEnum.values();
			for (FeeRulePeriodEnum c : values) {
				if (c.code.equals(code)) {
					return c.name;
				}
			}
			return null;
		}
		static {
			for (FeeRulePeriodEnum entryTypeEnum : FeeRulePeriodEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", entryTypeEnum.getCode());
				map.put("name", entryTypeEnum.getName());
				maritalStatusList.add(map);
			}
			sourceMap.put("feeRulePeriod",maritalStatusList);
		}
	}

	/**
	 * @Description: 物业缴费项目生成周期
	 * @author: Hu
	 * @since: 2021/7/30 9:28
	 * @Param:
	 * @return:
	 */
	enum FeeRuleNameEnum {
		FITMENT("装修管理费", 1),
		HIGH_RISE("高层物业服务费", 2),
		LOWER("低层物业服务费", 3),
		VILLA("别墅物业服务费", 4),
		BUSINESS("底商物业服务费", 5),
		HEATING("供暖费", 6),
		CUSTOM("自定义名称", 7),
		ARCHITECTURE("建筑面积", 8),
		PLEDGE("装修押金", 9),
		IMMOBILIZATION("固定金额", 10),
		VEHICLE("车辆管理费", 11),
		STALL("车位租金", 12),
		FAMILY("按户", 13),
		MAINTENANCE("电梯维护", 14),
		EMPLOY("电梯使用", 15),
		SANITATION("公共卫生费", 16);
		private String name;
		private Integer code;

		FeeRuleNameEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> maritalStatusList = new ArrayList<>();

		public static String getName(Integer code) {
			FeeRuleNameEnum[] values = FeeRuleNameEnum.values();
			for (FeeRuleNameEnum c : values) {
				if (c.code.equals(code)) {
					return c.name;
				}
			}
			return null;
		}
		static {
			for (FeeRuleNameEnum feeRuleNameEnum : FeeRuleNameEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", feeRuleNameEnum.getCode());
				map.put("name", feeRuleNameEnum.getName());
				maritalStatusList.add(map);
			}
			sourceMap.put("feeRuleName",maritalStatusList);
		}
	}

	/**
	 * @Description: 房屋类型枚举
	 * @Author: chq459799974
	 * @Date: 2021/3/12
	 **/
	enum HouseTypeEnum{
		SHOP("商铺",1),
		HOUSE("住宅",2);
		private String name;
		private Integer code;
		HouseTypeEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getCode() {
			return code;
		}
		public void setCode(Integer code) {
			this.code = code;
		}
		public static final Map<Integer, String> HOUSE_TYPE_MAP = new HashMap<>();
		static{
			for (PropertyEnum.HouseTypeEnum houseTypeEnum : PropertyEnum.HouseTypeEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", houseTypeEnum.getCode());
				map.put("name", houseTypeEnum.getName());
				HOUSE_TYPE_MAP.put(houseTypeEnum.getCode(), houseTypeEnum.getName());
			}
		}

		public static Integer getCode(String name) {
			for (PropertyEnum.HouseTypeEnum value : PropertyEnum.HouseTypeEnum.values()) {
				if (value.getName().equals(name)) {
					return value.getCode();
				}
			}
			return null;
		}

		public static String getName(Integer code) {
			for (PropertyEnum.HouseTypeEnum value : PropertyEnum.HouseTypeEnum.values()) {
				if (value.getCode().equals(code)) {
					return value.getName();
				}
			}
			return null;
		}
	}

	/**
	 * 租赁签约进程状态枚举
	 */
	enum ContractingProcessStatusEnum {
		INITIATE_CONTRACT("(租客)申请签约", 1),
		ACCEPTING_APPLICATIONS("接受申请", 2),
		CONTRACT_PREPARATION("拟定合同", 3),
		WAITING_TO_PAY_RENT("等待支付房租", 4),
		PAYMENT_COMPLETED("支付完成", 5),
		COMPLETE_CONTRACT("完成签约", 6),
		CANCELLATION_REQUEST("取消申请", 7),
		REJECTION_OF_APPLICATION("拒绝申请", 8),
		REAPPLY("重新申请", 9),
		LANDLORD_INITIATED_CONTRACT("(房东)发起签约", 31),
		CANCEL_LAUNCH("取消发起", 32)
		;
		private String name;
		private Integer code;

		ContractingProcessStatusEnum(String name, Integer code) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		@Override
		public String toString() {
			return this.code + "_" + this.name;
		}

		public static final List<Map<String, Object>> ORDER_STATUS_LIST = new ArrayList<>();
		public static final Map<Integer, String> ORDER_STATUS_MAP = new HashMap<>();

		static {
			for (ContractingProcessStatusEnum processStatusEnum : ContractingProcessStatusEnum.values()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("code", processStatusEnum.getCode());
				map.put("name", processStatusEnum.getName());
				ORDER_STATUS_LIST.add(map);
				ORDER_STATUS_MAP.put(processStatusEnum.getCode(), processStatusEnum.getName());
			}
		}
	}

}
