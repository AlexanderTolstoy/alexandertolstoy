package OutputStructure;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * CheckModule
 * Created by ccwei on 2018/9/20.
 */
public class Mapping {

    String srcTable;
    HashSet<String> desTable;

    ArrayList<ArrayList<Line>> res = new ArrayList<ArrayList<Line>>();

    public String getSrcTable() {
        return srcTable;
    }

    public void setSrcTable(String srcTable) {
        this.srcTable = srcTable;
    }

    public HashSet<String> getDesTable() {
        return desTable;
    }

    public void setDesTable(HashSet<String> desTable) {
        this.desTable = desTable;
    }

    public ArrayList<ArrayList<Line>> getRes() {
        return res;
    }

    public void setRes(ArrayList<ArrayList<Line>> res) {
        this.res = res;
    }
}
