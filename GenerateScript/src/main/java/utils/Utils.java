package utils;

import net.sourceforge.pinyin4j.*;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CheckModule
 * Created by ccwei on 2018/9/2.
 */
public class Utils {

    private static Pattern number1 = Pattern.compile("NUMBER\\((\\d*),(\\d*)\\)");
    private static Pattern number2 = Pattern.compile("NUMBER\\((\\d*)\\)");
    private static Pattern varchar = Pattern.compile("VARCHAR2\\((\\d*)\\)");
    private static Pattern ch = Pattern.compile("CHAR\\((\\d*)\\)");

    public static String convertPinyin2Char(String src,String value){
        String res = ToFirstChar(src);

        return ((res==null || res.length() == 0 )? value : res).toUpperCase();
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


    public static String ToFirstChar(String chinese){
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();  //转为单个字符
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat);
                    if(temp == null || temp.length == 0){
                        continue;
                    }
                    pinyinStr += temp[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

    /**
     * 汉字转为拼音
     * @param chinese
     * @return
     */
    public static String ToPinyin(String chinese){
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

    public static String convert3Dimension(String res) {
        String[] tmp = res.split("_");
        if(tmp.length > 3){

            if(Constants.Duty.containsKey(tmp[tmp.length-1].trim())){
                tmp[tmp.length-1] = Constants.Duty.get(tmp[tmp.length-1].trim());
            }
            if(Constants.Industry.containsKey(tmp[tmp.length-2].trim())){
                tmp[tmp.length-2] = Constants.Industry.get(tmp[tmp.length-2].trim());
            }
            if(Constants.Obj.containsKey(tmp[tmp.length-3].trim())){
                tmp[tmp.length-3] = Constants.Obj.get(tmp[tmp.length-3].trim());
            }
        }
        res = tmp[0];
        for(int i = 1 ;i < tmp.length;i++){
            res += ("_" + tmp[i]);
        }
        return res;
    }

    /**
     * create by: ccwei
     * create time: 23:46 2018/11/15
     * description: 根据字符串转换oracle类型到hive
     * @return
     */
    public static String convertTypeOrcl2Hive(String data_type){

        String type = "";

        if(data_type.contains("NUMBER")){
            Matcher m1 = number1.matcher(data_type);
            Matcher m2 = number2.matcher(data_type);
            Integer precision = null;
            Integer scale = null;
            if(data_type.equals("NUMBER")){
                type = "double";
                return type;
            }else if (m1.find()) {
                precision = Integer.parseInt(m1.group(1));
                scale = Integer.parseInt(m1.group(2));
            }else if(m2.find()){
                precision = Integer.parseInt(m2.group(1));
                scale = 0;
            }
            if (precision == null && scale == null) {
                type = "double";
            } else if (precision > 9 && scale == 0) {
                type = "bigint";
            } else if (precision > 0 && scale > 0) {
                type = "decimal(" + precision + "," + scale + ")";
            } else if (precision <= 9 && scale == 0) {
                type = "int";
            } else if (precision > 0 && scale != 0) {
                type = "double";
            }

        }else if(data_type.equals("BLOB")||data_type.equals("LONG RAW")||data_type.equals("RAW")){
            type = "binary";
        }else if(data_type.equals("DATE") || data_type.equals("TIMESTAMP(6)")){
            type = "timestamp";
        }else if(data_type.equals("INTEGER")){
            type = "int";
        }else if(data_type.equals("FLOAT")){
            type = "double";
        }else if(data_type.equals("CLOB")||data_type.equals("LONG")||data_type.equals("XMLTYPE")){
            type = "string";
        }else if(data_type.contains("VARCHAR2")){
            Matcher m = varchar.matcher(data_type);
            if(m.find()){
                int length = Integer.parseInt(m.group(1));
                type = length > 2000 ? "string" : "varchar(" + length + ")";
            }
        }else if((data_type.contains("CHAR"))){
            Matcher m = ch.matcher(data_type);
            if(m.find()){
                int length = Integer.parseInt(m.group(1));
                type = "varchar(" + length + ")";
            }
        }
        if(type.equals("")){
            System.out.println(data_type + " : has empty type!");
        }
        return type;
    }
}
