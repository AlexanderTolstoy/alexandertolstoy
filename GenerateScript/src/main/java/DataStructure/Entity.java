package DataStructure;

import java.util.Collection;

/**
 * CheckModule
 * Created by ccwei on 2018/9/2.
 */
public class Entity {

    private String id;
    private String name;
    private String code;
    private String objectID;

    private String refId;

    Collection<Attribute> attributes;

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<Attribute> attributes) {
        this.attributes = attributes;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
