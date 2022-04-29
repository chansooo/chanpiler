package dfa;

import org.json.simple.JSONObject;

public class DFA {
  //  private String tableName;
    private JSONObject state;
    private JSONObject table;

    public DFA(JSONObject table){
        this.table = table;
    //    this.tableName = (String) this.table.get("id");
    }

    public void initDFA(){
        this.state = (JSONObject) this.table.get("T0");
    }

    public String getTableName() {
        return (String) this.table.get("id");
    }

    public JSONObject getState(){
        return state;
    }

    public boolean setState(String input){
        String nextState = "";
        //들어온 값이 final이면 return true
        if(input == "final"){
            nextState = (String) this.state.get(input);
            if (nextState == null) {
                nextState = "false";
            }
//            System.out.println("set state: " + nextState);
            if(nextState.equals("true")){
                return true;
            } else {
                return false;
            }
        }
        //for문으로 state에 있는 keyset에 대해 같은 것이 있으면 그 키에 대한 value를 nextstate에 저장
        for(Object a: this.state.keySet()){
            String k = a.toString();
//            System.out.println(input);
//            System.out.println("index: " + k.indexOf(input));
            if(k.indexOf(input) > -1){
                nextState = (String) this.state.get(k);
//                System.out.println(nextState);
                break;
            }
        }
        //nextstate의 값에 해당하는 Tn의 값을 state에 넣어줌
        JSONObject tempState = (JSONObject) this.table.get(nextState);
        if(tempState == null){
            return false;
        } else {
            this.state = tempState;
            return true;
        }
    }
}
