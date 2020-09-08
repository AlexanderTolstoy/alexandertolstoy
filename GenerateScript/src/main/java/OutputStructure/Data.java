package OutputStructure;

/**
 * CheckModule
 * Created by ccwei on 2018/9/19.
 */
public class Data {

    String name;
    String code;
    String type;

    String Schemas;
    String TableCode;
    String TableName;

    boolean isFK = false;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchemas() {
        return Schemas;
    }

    public void setSchemas(String schemas) {
        Schemas = schemas;
    }

    public String getTableCode() {
        return TableCode;
    }

    public void setTableCode(String tableCode) {
        TableCode = tableCode;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }

    public boolean isFK() {
        return isFK;
    }

    public void setFK(boolean FK) {
        isFK = FK;
    }
}
