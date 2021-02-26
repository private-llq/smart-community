package com.jsy.community.constant;

import com.jsy.community.entity.HouseLeaseConstEntity;

import java.util.*;

/**
 * @author chq459799974
 * @description 业务相关枚举(不会变动的数据)(如有改变，提到表里做后台配置)
 * @since 2020-11-28 13:47
 **/
public interface BusinessEnum {
	
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
		CITY_LIST("getCityList", 5,2, null);
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
	 * @Description: 亲属关系枚举
	 * @Author: chq459799974
	 * @Date: 2020/12/01
	 **/
	enum RelationshipEnum {
		SPOUSE("夫妻", 1),
		FATHER_AND_SON("父子", 2),
		MOTHER_AND_SON("母子", 3),
		FATHER_AND_DAUGHTER("父女", 4),
		MOTHER_AND_DAUGHTER("母女", 5),
		RELATIVES("亲属", 6);
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

		public static String getDirectionName(String code){
			Map<Integer, String> kv = getKv();
			return kv.get(Integer.parseInt(code));
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

}
