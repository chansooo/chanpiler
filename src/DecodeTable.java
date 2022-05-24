import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;

public class DecodeTable {
    private JSONObject slrTable;
    private JSONObject transTable;
    private JSONObject ruleTable;
    private JSONParser parser = new JSONParser();

    public DecodeTable(){
        DecodeSLRTable();
        DecodeTransTable();
        DecodeRuleTable();
    }

    public void DecodeSLRTable(){
        try{
            InputStream l = getClass().getResourceAsStream("/LRTable.json");
            slrTable = (JSONObject) parser.parse(new InputStreamReader(l));
        } catch (Exception error){
            System.out.println(error);
        }
    }
    public void DecodeTransTable(){
        try{
            InputStream a = getClass().getResourceAsStream("/transTable.json");
            transTable = (JSONObject) parser.parse(new InputStreamReader(a));
        } catch (Exception error){
            System.out.println(error);
        }
    }

    public  void DecodeRuleTable(){
        try{
            InputStream b = getClass().getResourceAsStream("/rule.json");
            ruleTable = (JSONObject) parser.parse(new InputStreamReader(b));
        } catch (Exception error){
            System.out.println(error);
        }
    }

    public JSONObject getTransTable() {
        return transTable;
    }

    public JSONObject getSlrTable() {
        return slrTable;
    }

    public JSONObject getRuleTable(){
        return ruleTable;
    }
}
