package DataStructure;

import java.util.HashMap;

/**
 * CheckModule
 * Created by ccwei on 2018/9/19.
 */
public class Diagram {

    String id;
    String objectID;
    String name;
    String code;

    private HashMap<String,EntitySymbol> entitySymbolHashMap = new HashMap<String, EntitySymbol>();
    private HashMap<String,RelationshipSymbol> relationshipSymbolHashMap = new HashMap<String, RelationshipSymbol>();

    public void putEntitySymbol(String key, EntitySymbol entitySymbol){
        entitySymbolHashMap.put(key,entitySymbol);
    }

    public EntitySymbol getEntitySymbol(String key){
        return entitySymbolHashMap.get(key);
    }

    public void putRelationshipSymbol(String key, RelationshipSymbol entitySymbol){
        relationshipSymbolHashMap.put(key,entitySymbol);
    }

    public HashMap<String, EntitySymbol> getEntitySymbolHashMap() {
        return entitySymbolHashMap;
    }

    public void setEntitySymbolHashMap(HashMap<String, EntitySymbol> entitySymbolHashMap) {
        this.entitySymbolHashMap = entitySymbolHashMap;
    }

    public RelationshipSymbol getRelationshipSymbol(String key){
        return relationshipSymbolHashMap.get(key);
    }

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
}
