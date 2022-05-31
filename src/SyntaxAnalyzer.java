import org.json.simple.JSONObject;

import java.io.*;
import java.util.Stack;
import java.util.Arrays;

public class SyntaxAnalyzer {
    int position = 0;
    String lexicalInfo;
    String fileName;
    JSONObject transTable;
    JSONObject slrTable;
    JSONObject ruleTable;
    String[] transformedTokenList;
    boolean result;


    private Stack<String> stack;
    public SyntaxAnalyzer(String lexicalResult,String fileName){
        this.fileName = fileName;
        this.lexicalInfo = lexicalResult;
        stack = new Stack<String>();
        DecodeTable jsonDecoder = new DecodeTable();
        slrTable = jsonDecoder.getSlrTable();
        transTable = jsonDecoder.getTransTable();
        ruleTable = jsonDecoder.getRuleTable();
        stack.push("0");
        transformedTokenList = transformLexical();
        result = run();
        saveTokenList();
    }

    //lexical analyzer에서 받은 결과물을 syantax analyzer의 문법에 맞도록 변환
    public String[] transformLexical(){
        String[] tokenList;
        String[] curToken;
        int position = 0;
        String result;

        if(lexicalInfo.contains("ERROR"))
            System.out.println("올바르지 않은 문법입니다");
        tokenList = this.lexicalInfo.split(" ");
        for(int tokenIndex =0; tokenIndex <tokenList.length; tokenIndex++){
            curToken = tokenList[tokenIndex].split(",");
            if(curToken[0].equals("OPERATOR")){
                JSONObject translated = (JSONObject) this.transTable.get(curToken[0]);
                result = String.valueOf(translated.get(curToken[1]));
            }else {
                result = String.valueOf(this.transTable.get(curToken[0]));
                if (result.equals("null")) {
                    continue;
                }
            }
            tokenList[position] = result;    //인식한 토큰을 TokenList에 하나씩 저장
            position++;
        }
        tokenList = Arrays.copyOf(tokenList, position + 1);
        tokenList[position] = "$";
        return tokenList;
    }

    public boolean run() {
        String rule = "";
        JSONObject reduce ;
        String shiftCount = "";
        String goto_state = "";
        int pop_count = 0;
        JSONObject curState;
        boolean epsilonMove = false;

        for (int i = 0; i < transformedTokenList.length;) {
            curState = (JSONObject)slrTable.get(stack.peek());   //현재상태 테이블 로딩
            rule = (String)curState.get(transformedTokenList[i]);
            //rule이 null이라면 epsilon move인지 확인
            if (rule == null) {
                rule = (String) curState.get("e");
                epsilonMove = true;
            }
            //epsilon move도 아니라면 오류로 인식
            if (rule == null){
                int k = i + 1;
                System.out.println("ERROR: "+k+"번째 "+"token - "+transformedTokenList[i]);
                lexicalInfo += "\nERROR: " + k + "번째 token - " + transformedTokenList[i];
                return false;
            }
            //끝났다면 true반환
            if (rule.equals("acc"))
                return true;
            //shift 진행
            if (rule.charAt(0) == 's') {
                //epsilon일 경우 shift X
                shiftCount = rule.substring(1);
                //System.out.println("Shift to " + shiftCount);
                stack.push(transformedTokenList[i]);
                stack.push(shiftCount);
                if(!epsilonMove){
                    i++;

                }
            }
            //reduce 진행
            else if (rule.charAt(0) == 'r') {   //action -reduce
                String temp = rule.substring(1);
                reduce = (JSONObject) ruleTable.get(temp);
                pop_count = ((Long) reduce.get("RHS")).intValue();

                for (int j= 0; j < pop_count; j++) {    //불러온 글자 수만큼 pop&reduce
                    stack.pop();
                    stack.pop();
                }
                curState = (JSONObject)slrTable.get(stack.peek());
                String LHS =(String) reduce.get("LHS");
                goto_state = (String)curState.get(LHS);
                stack.push(LHS);
                stack.push(goto_state);
            }
            epsilonMove = false;
        }
        return false;
    }

    public void saveTokenList(){
        BufferedOutputStream bs = null;
        if(result == true){
            System.out.println("Accepted");
            lexicalInfo += "\nResult: Accepted";
            try {
                bs = new BufferedOutputStream(new FileOutputStream("./" + fileName + ".out"));
                bs.write(lexicalInfo.getBytes());
                bs.close();
//                System.out.println("파일이 생성되었습니다");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            System.out.println("Rejected");
            lexicalInfo += "\nResult: Rejected";
            try {
                bs = new BufferedOutputStream(new FileOutputStream("./" + fileName + ".out"));
                bs.write(lexicalInfo.getBytes());
                bs.close();
//                System.out.println("파일이 생성되었습니다");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
