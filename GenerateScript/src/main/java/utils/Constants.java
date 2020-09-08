package utils;

import java.util.HashMap;

/**
 * CheckModule
 * Created by ccwei on 2018/9/21.
 */
public class Constants {

    //索引常量
    public static int INDEX_SCHEMA = 1001;
    public static int INDEX_COLNUM = 1002;
    //字段索引
    public static String DES_SCHEMA_PREFIX = "模式前缀";
    public static String DES_SCHEMA_POSTFIX = "模式后缀";
    public static String SRC_DOMAIN = "域";

    public static String SRC_TABLE_NAME = "源表名";
    public static String SRC_TABLE_CODE = "源表代码";
    public static String SRC_TABLE_COMMENT = "源表注释";
    public static String SRC_COL_NAME = "源表列名";
    public static String SRC_COL_CODE = "源表列代码";
    public static String SRC_COL_TYPE = "源表列类型";
    public static String SRC_COL_COMMENT = "源表列注释";

    public static String MAP_SRC_DES = "映射规则";

    public static String DES_TABLE_NAME = "目标表名";
    public static String DES_TABLE_CODE = "目标表代码";
    public static String DES_COL_NAME = "目标列名";
    public static String DES_COL_CODE = "目标列代码";
    public static String DES_COL_TYPE = "目标列类型";
    public static String DES_COL_COMMENT = "目标列注释";
    public static String DES_TABLE_COMMENT = "目标表注释";

    public static String DES_IS_PRIMARY = "是否主键";
    public static String DES_COL_SEQ= "目标列编号";


    public enum DV_TYPE {
        TINYINT, SMALLINT, INT, BIGINT, BOOLEAN, FLOAT, DOUBLE, DECIMAL, TIMESTAMP, DATE, INTERVAL, BINARY, CHAR, STRING, VARCHAR
    }

    public static HashMap<String,String> Industry;
    public static HashMap<String,String> Obj;
    public static HashMap<String,String> Duty;
    public static HashMap<String,String> Fixed;
    public static HashMap<String,String> Log;

    static {
        Industry = new HashMap<String, String>();
        Industry.put("铁路交通","TL");
        Industry.put("公路交通","GL");
        Industry.put("水路交通","SL");
        Industry.put("道路交通","DL");
        Industry.put("城市交通","CS");
        Industry.put("民用航空","MH");
        Industry.put("邮政管理","YZ");
        Industry.put("综合管理","ZH");
        Industry.put("其他行业","QT");
        Industry.put("通用","TY");

        Obj = new HashMap<String, String>();
//        Obj.put("人员","0A");
//        Obj.put("组织","0B");
//        Obj.put("运输装备","0C");
//        Obj.put("基础设施","0D");
//        Obj.put("货物","0E");
//        Obj.put("环境","0F");
//        Obj.put("项目","0G");
//        Obj.put("资金","0H");
//        Obj.put("制度","0I");
//        Obj.put("事件","0J");
//        Obj.put("通用","0Z");
        Obj.put("人员","OA");
        Obj.put("组织","OB");
        Obj.put("运输装备","OC");
        Obj.put("基础设施","OD");
        Obj.put("货物","OE");
        Obj.put("环境","OF");
        Obj.put("项目","OG");
        Obj.put("资金","OH");
        Obj.put("制度","OI");
        Obj.put("事件","OJ");
        Obj.put("通用","OZ");

        Duty = new HashMap<String, String>();
        Duty.put("行政许可事项","TA");
        Duty.put("行政执法事项","TB");
        Duty.put("行政征收事项","TC");
        Duty.put("公众服务事项","TD");
        Duty.put("运行管理事项","TE");
        Duty.put("基本建设事项","TF");
        Duty.put("行业监管事项","TG");
        Duty.put("信用管理事项","TH");
        Duty.put("应急管理事项","TI");
        Duty.put("一般政务管理","TJ");
        Duty.put("企业业务","2B");
        Duty.put("政务","2G");
        Duty.put("公共业务","2P");

        Fixed = new HashMap<String, String>();
        Fixed.put("LOG_LOAD_TIME",DV_TYPE.TIMESTAMP.toString());
        Fixed.put("LOG_SRC_NODE",DV_TYPE.STRING.toString());
        Fixed.put("LOG_INIT_TIME",DV_TYPE.TIMESTAMP.toString());
        Fixed.put("LOG_SRC_TABLE",DV_TYPE.STRING.toString());
        Fixed.put("LOG_DIFF_MD5",DV_TYPE.STRING.toString());
        Fixed.put("LOG_END_TIME",DV_TYPE.TIMESTAMP.toString());

        Log = new HashMap<String, String>();
        Log.put("LOG_LOAD_TIME","current_timestamp()");
        Log.put("LOG_SRC_NODE",DV_TYPE.STRING.toString());
        Log.put("LOG_INIT_TIME","current_timestamp()");
        Log.put("LOG_SRC_TABLE",DV_TYPE.STRING.toString());
        Log.put("LOG_DIFF_MD5",DV_TYPE.STRING.toString());
        Log.put("LOG_END_TIME","to_timestamp('9999-01-01 00:00:00')");

    }
}
