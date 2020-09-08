package function;

import utils.ConfigCenter;
import utils.Constants;

import java.util.HashMap;

/**
 * CheckModule
 * Created by ccwei on 2018/11/20.
 */
public class Tools {

    /**
     * create by: ccwei
     * create time: 17:03 2018/11/20
     * description: 获取配置中的列数
     * @return
     */
    public static int getColNum(String type,int value) {
        if(ConfigCenter.config.get(type) != null && ConfigCenter.config.get(type).get(Constants.INDEX_COLNUM) != null){
            return Integer.parseInt(ConfigCenter.config.get(type).get(Constants.INDEX_COLNUM).get(0));
        }
        return value;
    }
    
    
    /**
     * create by: ccwei
     * create time: 16:38 2019/2/28
     * description:
     * @return 
     */
    private static int classify(HashMap<String, String> m){
        String desColCode = m.get(Constants.DES_COL_CODE);
        if((desColCode.contains("ROW_MD5")||desColCode.contains("row_md5")) && m.get(Constants.DES_IS_PRIMARY).equals(":key")) {
            return 0;
        }
        if(m.get(Constants.DES_IS_PRIMARY).equals(":key")) {
            return 1;
        }
        if(desColCode.startsWith("SK_")||desColCode.startsWith("sk_")){
            return 2;
        }
        if(desColCode.startsWith("BK_")||desColCode.startsWith("bk_")){
            return 3;
        }
        if(desColCode.startsWith("LOG_")||desColCode.startsWith("log_")){
            return 5;
        }
        if(desColCode.startsWith("DEL_")||desColCode.startsWith("del_")){
            return 6;
        }
        //普通列
        return 4;

    }
    
    /**
     * create by: ccwei
     * create time: 16:38 2019/2/28
     * description:
     * @return 
     */
    public static boolean isGE(HashMap<String, String> a,HashMap<String, String> b){
        if(classify(a) != classify(b)){
            return classify(a) > classify(b);
        }else {
            return compareString(a.get(Constants.DES_COL_CODE),b.get(Constants.DES_COL_CODE)) == 1;
        }
    }

    /**
     * create by: ccwei
     * create time: 16:38 2019/2/28
     * description: null < (length = 0) < (length > 0)
     * @return 
     */
    private static int compareString(String a,String b){
        if(a == null){
            return b == null ? 0 : -1;
        }
        if(a.length() == 0){
            if(b == null ) return -1;
            if(b.length() == 0) return 0;
            return 1;
        }

        for(int i = 0 ; i < a.length(); ++i){
            if(i>b.length()-1){
                return 1;
            }
            if(a.charAt(i) != b.charAt(i)){
                return a.charAt(i) > b.charAt(i) ? 1 : -1;
            }
        }
        return 0;
    }
}
