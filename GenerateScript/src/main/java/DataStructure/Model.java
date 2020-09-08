package DataStructure;

import org.dom4j.Element;

import java.util.Iterator;

import static DataStructure.Tool.getAttributeByKey;

/**
 * CheckModule
 * Created by ccwei on 2018/9/23.
 */
public class Model {
    String id;
    String objectID;
    String name;
    String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static void initModel(Element node){
        if(!node.getName().equals("Model")){
            return;
        }
        String id = getAttributeByKey("Id",node);
        ResourcePool.getModel().setId(id);

        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("ObjectID")){//
                ResourcePool.getModel().setObjectID(e.getStringValue());
            }
            if(e.getName().equals("Name")){//
                ResourcePool.getModel().setName(e.getStringValue());
            }
            if(e.getName().equals("Code")){//
                ResourcePool.getModel().setCode(e.getStringValue());
            }
        }
    }
}
