package DataStructure;

import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;

import static DataStructure.Tool.getAttributeByKey;

/**
 * CheckModule
 * Created by ccwei on 2018/9/20.
 */
public class DiagramLink {

    String id;
    String SourceEntity;
    String DestinationEntity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceEntity() {
        return SourceEntity;
    }

    public void setSourceEntity(String sourceEntity) {
        SourceEntity = sourceEntity;
    }

    public String getDestinationEntity() {
        return DestinationEntity;
    }

    public void setDestinationEntity(String destinationEntity) {
        DestinationEntity = destinationEntity;
    }

    public static void initDiagramLinks(Element node) {
        if(node.getParent() == null || !node.getParent().getName().equals("ConceptualDiagram") || !node.getName().equals("Symbols") ){
            return;
        }
        String diagramID = getAttributeByKey("Id",node.getParent());
        HashMap<String,DiagramLink> diagramLinkHashMap = new HashMap<String, DiagramLink>();
        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("ExtendedDependencySymbol")){//
                DiagramLink diagramLink = new DiagramLink();
                String id = getAttributeByKey("Id",e);
                diagramLink.setId(id);

                fillDiagramLinks(e, diagramLink);

                diagramLinkHashMap.put(id,diagramLink);
            }
        }
        if(diagramLinkHashMap.size() > 0){
            ResourcePool.getDiagramLinks().put(diagramID,diagramLinkHashMap);
        }

    }

    private static void fillDiagramLinks(Element e, DiagramLink diagramLink) {
        for(Object obj : e.elements()){
            Element t = (Element) obj;
            if(t.getName().equals("SourceSymbol")){
                diagramLink.setSourceEntity(getDiagramLinkRef(t));
            }
            if(t.getName().equals("DestinationSymbol")){
                diagramLink.setDestinationEntity(getDiagramLinkRef(t));
            }

        }
    }
    private static String getDiagramLinkRef(Element e) {
        String res = "";
        for(Object obj : e.elements()) {
            Element elm = (Element) obj;
            if(elm.getName().equals("EntitySymbol")){
                res = getAttributeByKey("Ref",elm);
                break;
            }
        }
        return res;
    }
}
