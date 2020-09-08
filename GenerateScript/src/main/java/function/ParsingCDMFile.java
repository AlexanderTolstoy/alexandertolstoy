package function;

import DataStructure.*;
import OutputStructure.Mapping;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static DataStructure.Tool.getAttributeByKey;

/**
 * CheckModule
 * Created by ccwei on 2018/9/18.
 */
public class ParsingCDMFile {
    public static void scanNodes( Element node) {

        //初始化基础数据
        Model.initModel(node);
        initEntities(node);
        initDataItems(node);
        initLinks(node);
        initDiagrams(node);
        DiagramLink.initDiagramLinks(node);
        Relationship.initRelationships(node);


        // 当前节点下面子节点迭代器
        Iterator<Element> it = node.elementIterator();
        // 遍历
        while (it.hasNext()) {
            Element e = it.next();
            // 对子节点进行遍历
            scanNodes(e);
        }
    }

    private static void initDiagrams(Element node){
        if(!node.getName().equals("ConceptualDiagrams")){
            return;
        }

        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("ConceptualDiagram")){//
                Diagram diagram = new Diagram();
                String id = getAttributeByKey("Id",e);
                diagram.setId(id);

                initDiagram(e, diagram);
                ResourcePool.putDiagrams(id,diagram);
            }
        }
    }

    public static void initAdditionalRelationShips() {

        Set<String> keySet = ResourcePool.getDiagramLinks().keySet();

        for(String key : keySet){
            if(null == key || key.trim().length() == 0){
                continue;
            }
            ArrayList<Mapping> mapping = ConvertODS2DVModel.initMappingTables(key.trim());
            ResourcePool.putDiagramMappingMaps(key,mapping);
        }

    }

    private static void initLinks(Element node) {
        if(!node.getName().equals("ChildTraceabilityLinks")){
            return;
        }
        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("ExtendedDependency")){//
                Link link = new Link();
                String id = getAttributeByKey("Id",e);
                link.setId(id);

                fillLinks(e, link);
                ResourcePool.putLink(id,link);
            }
        }
    }


    private static void initDataItems(Element node) {
        //
        if(!node.getName().equals("DataItems")){
            return;
        }
        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("DataItem")){//
                Item item = new Item();
                String id = getAttributeByKey("Id",e);
                item.setId(id);
                fillDataItem(e, item);
                ResourcePool.putItem(id,item);
            }
        }

    }

    private static void initEntities(Element node) {
        //
        if(!node.getName().equals("Entities")){
            return;
        }
        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("Entity")){//
                Entity entity = new Entity();
                String id = getAttributeByKey("Id",e);
                entity.setId(id);

                fillEntity(e, entity);
                ResourcePool.putEntity(id,entity);
            }
        }

    }

    private static void fillEntity(Element e, Entity entity) {
        for(Object obj : e.elements()){
            Element t = (Element) obj;
            if(t.getName().equals("ObjectID")){
                entity.setObjectID(t.getStringValue());
            }
            if(t.getName().equals("Name")){
                entity.setName(t.getStringValue());
            }
            if(t.getName().equals("Code")){
                entity.setCode(t.getStringValue());
            }
            if(t.getName().equals("Attributes")){
//                String aID = getAttributeByKey("Id",t);
                getAttributes(t,entity);


            }
        }
    }

    private static void initDiagram(Element e, Diagram diagram) {
        for(Object obj : e.elements()){
            Element t = (Element) obj;
            if(t.getName().equals("ObjectID")){
                diagram.setObjectID(t.getStringValue());
            }
            if(t.getName().equals("Name")){
                diagram.setName(t.getStringValue());
            }
            if(t.getName().equals("Code")){
                diagram.setCode(t.getStringValue());
            }
            if(t.getName().equals("Symbols")){
                EntitySymbol.initEntitySymbols(t,diagram);
            }

        }
    }

    private static void fillLinks(Element e, Link link) {
        for(Object obj : e.elements()){
            Element t = (Element) obj;
            if(t.getName().equals("ObjectID")){
                link.setObjectID(t.getStringValue());
            }
            if(t.getName().equals("Object1")){
                link.setObject1(getLinkRef(t));
            }
            if(t.getName().equals("Object2")){
                link.setObject2(getLinkRef(t));
            }

        }
    }

    private static void fillDataItem(Element e, Item item) {
        for(Object obj : e.elements()){
            Element t = (Element) obj;
            if(t.getName().equals("ObjectID")){
                item.setObjectID(t.getStringValue());
            }
            if(t.getName().equals("Name")){
                item.setName(t.getStringValue());
            }
            if(t.getName().equals("Code")){
                item.setCode(t.getStringValue());
            }
            if(t.getName().equals("DataType")){
                item.setDataType(t.getStringValue());
            }
            if(t.getName().equals("Length")){
                item.setLength(t.getStringValue());
            }

        }
    }

    private static void getAttributes(Element t,Entity entity) {
        ArrayList<Attribute> attributes = new ArrayList<DataStructure.Attribute>();
        for(Object obj : t.elements()){
            Element elm = (Element) obj;
            if(!elm.getName().equals("EntityAttribute")){
                continue;
            }
            String id = getAttributeByKey("Id", elm);
            DataStructure.Attribute attribute = new DataStructure.Attribute();
            attribute.setId(id);

            fillAttribute(elm, attribute);

            attributes.add(attribute);
        }
        if(!attributes.isEmpty()){
            entity.setAttributes(attributes);
        }
    }

    private static void fillAttribute(Element elm, DataStructure.Attribute attribute) {
        for(Object obj : elm.elements()){
            Element e = (Element) obj;
            if(e.getName().equals("ObjectID")){
                attribute.setObjectID(e.getStringValue());
            }
            if(e.getName().equals("DataItem")){
                attribute.setRef(getItemRef(e));
            }
        }
    }

    private static String getItemRef(Element e) {
        String res = "";
        for(Object obj : e.elements()) {
            Element elm = (Element) obj;
            if(elm.getName().equals("DataItem")){
                res = getAttributeByKey("Ref",elm);
                break;
            }
        }
        return res;
    }

    private static String getLinkRef(Element e) {
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


    public static void setNodeValue(Element e,String key,String value) {
        for(Object obj : e.elements()) {
            Element elm = (Element) obj;
            if(elm.getName().equals(key)){
                elm.setText(value);
            }
        }
    }


}
