package DataStructure;

import org.dom4j.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import static DataStructure.Tool.getAttributeByKey;

/**
 * CheckModule
 * Created by ccwei on 2018/10/9.
 */
public class Relationship {
    private String id;
    private String name;
    private String code;
    private String objectID;

    private String entity_1 ;
    private String entity_2 ;


    public String getEntity_1() {
        return entity_1;
    }

    public void setEntity_1(String entity_1) {
        this.entity_1 = entity_1;
    }

    public String getEntity_2() {
        return entity_2;
    }

    public void setEntity_2(String entity_2) {
        this.entity_2 = entity_2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public static void initRelationships(Element node) {
        if(node.getParent() == null || !node.getName().equals("Relationships")){
            return;
        }
        String rsID = getAttributeByKey("Id",node.getParent());
        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("Relationship")){//
                Relationship rs = new Relationship();
                String id = getAttributeByKey("Id",e);
                rs.setId(id);

                fillRelationships(e, rs);

                ResourcePool.getRelationships().put(id,rs);
            }
        }

    }

    private static void fillRelationships(Element e, Relationship relationship) {
        for(Object obj : e.elements()){
            Element t = (Element) obj;
            if(t.getName().equals("ObjectID")){
                relationship.setObjectID(t.getStringValue());
            }
            if(t.getName().equals("Name")){
                relationship.setName(t.getStringValue());
            }
            if(t.getName().equals("Code")){
                relationship.setCode(t.getStringValue());
            }
            if(t.getName().equals("Object1")){
                relationship.setEntity_1(getRsObjectRef(t));
            }
            if(t.getName().equals("Object2")){
                relationship.setEntity_2(getRsObjectRef(t));
            }

        }
    }

    private static String getRsObjectRef(Element e) {
        String res = "";
        for(Object obj : e.elements()) {
            Element elm = (Element) obj;
            if(elm.getName().equals("Entity")){
                res = getAttributeByKey("Ref",elm);
                break;
            }
        }
        return res;
    }

    public static void initHub2SatRelationships() {
        HashMap<String,Relationship> relationships = ResourcePool.getRelationships();
        for(String key : relationships.keySet()){
            Relationship rs = relationships.get(key);
            Entity e1 = ResourcePool.getEntitiy(rs.getEntity_1());
            Entity e2 = ResourcePool.getEntitiy(rs.getEntity_2());
            if(e1.getName().startsWith("H_") && e2.getName().startsWith("S_")){
                putIntoMap(e2, e1);
            }
            if(e2.getName().startsWith("H_") && e1.getName().startsWith("S_")){
                putIntoMap(e1, e2);
            }
        }
    }

    private static void putIntoMap(Entity e1, Entity e2) {
        if (ResourcePool.getHub2SatMap().containsKey(e2.getName())) {
            ResourcePool.getHub2SatMap().get(e2.getName()).add(e1.getName());
        } else {
            HashSet<String> set = new HashSet<String>();
            set.add(e1.getName());
            ResourcePool.getHub2SatMap().put(e2.getName(), set);
        }
    }

}
