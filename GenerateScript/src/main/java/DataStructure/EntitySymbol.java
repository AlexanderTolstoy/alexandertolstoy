package DataStructure;

import org.dom4j.Element;

import java.util.Iterator;

import static DataStructure.Tool.getAttributeByKey;

/**
 * CheckModule
 * Created by ccwei on 2018/9/19.
 */
public class EntitySymbol {

    String id;
    String Rect;
    String IconMode;
    String GradientFillMode;
    String BrushStyle;
    String LineColor;
    String FillColor;
    String ShadowColor;

    String ref;

    public static void initEntitySymbols(Element node, Diagram diagram){
        if(!node.getName().equals("Symbols")){
            return;
        }
        Iterator<Element> it = node.elementIterator();
        while (it.hasNext()) {
            Element e = it.next();
            if(e.getName().equals("EntitySymbol")){
                EntitySymbol entitySymbol = new EntitySymbol();
                String id = getAttributeByKey("Id",e);
                entitySymbol.setId(id);

                initEntitySymbol(e, entitySymbol);
                diagram.putEntitySymbol(id,entitySymbol);
            }
        }
    }

    private static void initEntitySymbol(Element e, EntitySymbol symbol) {
        for(Object obj : e.elements()){
            Element t = (Element) obj;
            if(t.getName().equals("Object")){
                symbol.setRef(getEntitySymbolRef(t));
            }
        }
    }

    private static String getEntitySymbolRef(Element e) {
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getRect() {
        return Rect;
    }

    public void setRect(String rect) {
        Rect = rect;
    }

    public String getIconMode() {
        return IconMode;
    }

    public void setIconMode(String iconMode) {
        IconMode = iconMode;
    }

    public String getGradientFillMode() {
        return GradientFillMode;
    }

    public void setGradientFillMode(String gradientFillMode) {
        GradientFillMode = gradientFillMode;
    }

    public String getBrushStyle() {
        return BrushStyle;
    }

    public void setBrushStyle(String brushStyle) {
        BrushStyle = brushStyle;
    }

    public String getLineColor() {
        return LineColor;
    }

    public void setLineColor(String lineColor) {
        LineColor = lineColor;
    }

    public String getFillColor() {
        return FillColor;
    }

    public void setFillColor(String fillColor) {
        FillColor = fillColor;
    }

    public String getShadowColor() {
        return ShadowColor;
    }

    public void setShadowColor(String shadowColor) {
        ShadowColor = shadowColor;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
