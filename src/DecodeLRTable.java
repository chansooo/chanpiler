import dfa.DecodeJSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;

public class DecodeLRTable {
    private JSONObject slrTable;
    private JSONObject table;
    private JSONParser parser = new JSONParser();
    DecodeLRTable(){
        DecodeTable();

    }

    public void DecodeTable(){
        try{
            InputStream l = getClass().getResourceAsStream("/LRTable.json");
            slrTable = (JSONObject) parser.parse(new InputStreamReader(l));
        } catch (Exception error){
            System.out.println(error);
        }
    }

    public JSONObject getTable(int tableNum){
        String tableID = Integer.toString(tableNum);
        JSONObject parsedTable = (JSONObject) table.get(tableID);
        return parsedTable;
    }
}
