package OutputStructure;

/**
 * CheckModule
 * Created by ccwei on 2018/9/19.
 */
public class Line implements Comparable {

    String srcSchemas;
    String srcTableCode;
    String srcTableName;
    String srcColumnCode;
    String srcColumnName;
    String srcDateType;

    String convertRule;

    String desSchemas;
    String desTableCode;
    String desTableName;
    String desColumnCode;
    String desColumnName;
    String desDateType;

    Boolean isFK = false;
    Boolean isPartition = false;

    String selfDifine;

    public String getByIndex(int idx){
        String res = "";
        switch (idx){
            case 0:res=getSrcSchemas();break;
            case 1:res=getSrcTableName();break;
            case 2:res=getSrcTableCode();break;
            case 3:res=getSrcColumnName();break;
            case 4:res=getSrcColumnCode();break;
            case 5:res=getSrcDateType();break;
            case 6:res=getConvertRule();break;
            case 7:res=getDesSchemas();break;
            case 8:res=getDesTableName();break;
            case 9:res=getDesTableCode();break;
            case 10:res=getDesColumnName();break;
            case 11:res=getDesColumnCode();break;
            case 12:res=getDesDateType();break;
            case 13:res=isFK().toString();break;
            case 14:res=isPartition().toString();break;
            case 15:res=getSelfDifine();break;
            default:break;
        }
        return res;
    }

    public String getSelfDifine() {
        return selfDifine;
    }

    public void setSelfDifine(String selfDifine) {
        this.selfDifine = selfDifine;
    }

    public String getSrcSchemas() {
        return srcSchemas;
    }

    public void setSrcSchemas(String srcSchemas) {
        this.srcSchemas = srcSchemas;
    }

    public String getSrcTableCode() {
        return srcTableCode;
    }

    public void setSrcTableCode(String srcTableCode) {
        this.srcTableCode = srcTableCode;
    }

    public String getSrcTableName() {
        return srcTableName;
    }

    public void setSrcTableName(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    public String getSrcColumnCode() {
        return srcColumnCode;
    }

    public void setSrcColumnCode(String srcColumnCode) {
        this.srcColumnCode = srcColumnCode;
    }

    public String getSrcColumnName() {
        return srcColumnName;
    }

    public void setSrcColumnName(String srcColumnName) {
        this.srcColumnName = srcColumnName;
    }

    public String getSrcDateType() {
        return srcDateType;
    }

    public void setSrcDateType(String srcDateType) {
        this.srcDateType = srcDateType;
    }

    public String getConvertRule() {
        return convertRule;
    }

    public void setConvertRule(String convertRule) {
        this.convertRule = convertRule;
    }

    public String getDesSchemas() {
        return desSchemas;
    }

    public void setDesSchemas(String desSchemas) {
        this.desSchemas = desSchemas;
    }

    public String getDesTableCode() {
        return desTableCode;
    }

    public void setDesTableCode(String desTableCode) {
        this.desTableCode = desTableCode;
    }

    public String getDesTableName() {
        return desTableName;
    }

    public void setDesTableName(String desTableName) {
        this.desTableName = desTableName;
    }

    public String getDesColumnCode() {
        return desColumnCode;
    }

    public void setDesColumnCode(String desColumnCode) {
        this.desColumnCode = desColumnCode;
    }

    public String getDesColumnName() {
        return desColumnName;
    }

    public void setDesColumnName(String desColumnName) {
        this.desColumnName = desColumnName;
    }

    public String getDesDateType() {
        return desDateType;
    }

    public void setDesDateType(String desDateType) {
        this.desDateType = desDateType;
    }

    public Boolean isFK() {
        return isFK;
    }

    public void setFK(boolean FK) {
        isFK = FK;
    }

    public Boolean isPartition() {
        return isPartition;
    }

    public void setPartition(boolean partition) {
        isPartition = partition;
    }

    public int compareTo(Object o) {
        Line line = (Line) o;
        if(getHeadType(this) < getHeadType(line)){
            return -1;
        }
        if(getHeadType(this) > getHeadType(line)){
            return 1;
        }

        return 0;
    }

    private int getHeadType(Line line){
        if(line.getDesColumnName() == null || line.getDesColumnName().length() == 0){
            return 0;
        }
        if(line.getDesColumnName().startsWith("SK_")){
            return 1;
        }
        if(line.getDesColumnName().startsWith("BK_")){
            return 2;
        }
        if(line.getDesColumnName().startsWith("LOG_")){
            return 4;
        }
        return 3;
    }
}
