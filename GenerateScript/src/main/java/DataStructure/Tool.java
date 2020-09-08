package DataStructure;

import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.List;

/**
 * CheckModule
 * Created by ccwei on 2018/9/19.
 */
public class Tool {

    public static String getAttributeByKey(String key, Element e) {
        List<Attribute> lst = e.attributes();
        String res = "";
        if(lst == null || lst.isEmpty()){
            return res;
        }

        for(org.dom4j.Attribute a : lst){
            if(a.getName().equals(key)){
                return a.getStringValue();
            }
        }
        return res;
    }
}
