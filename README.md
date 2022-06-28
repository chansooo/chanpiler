# Chanpiler

simple c lang의 compiler 역할 중 lexical analyzer와 syntax analyzer를 구현

## 토큰의 정의와 정규표현식

- 사전 설정
    
    정규표현식을 정의하기 위해 기존에 설정한 집합
    
    $digit = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}$
    
    $nonZeroigit = 1,2,3,4,5,6,7,8,9$
    
    $letter = a,b,c,d ... Z$
    
    $blank =$ 
    

1. <COMPARISON> : < | > | == | != | <= | >= 
2. <SEMI> : ;
3. <LBRACE> : {
4. <RBRACE> : }
5. <LPAREN> : )
6. <RPAREN> : )
7. <COMMA>: ,
8. <VTYPE> : int | INT | char | CHAR
9. <INTEGER> : (( - | $\epsilon$ ) (<nonZeroDigit>)( 0 | <nonZeroDigit> )*) | 0 
10. <STRING> : ( “ ) ( <digit> | <letter> | blank ) ( <digit> | <letter> | blank )* (”)
11. <IF> : if | IF
12. <ELSE> : else | ELSE
13. <WHILE> : while | WHILE
14. <RETURN> : return | RETURN
15. <OP> : + | - | * | /
16. <ASSIGN> : =
17. <ID> : <letter>( <letter> | <digit> )*
18. <WHITESPACE> : ( \n | \t | blank)(\n | \t | blank)* 

## Lexical Analyzer 구현

1. main class

```java
public class Main {

    public static void main(String[] args) throws IOException {
        String line;
        String fileInfo = "";

        //파일 확인
        if(args.length == 0){
            System.out.println("파일이 없습니다");
        }
        File f = new File(args[0]);
        String fileName = String.valueOf(args);
        //파일 정보 fileInfo에 저장
        BufferedReader br = new BufferedReader(new FileReader(f));
        while((line = br.readLine()) != null){
            fileInfo += line;
        }
        br.close();

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fileInfo, fileName);
    }
}
```

메인 클래스에서는 파일을 받아서  String 형식으로 반환한 후, 값을 LexicalAnalyzer 클래스에게 넘겨줍니다.

1. Lexical Analyzer

```java
private void setDFA(){
        for(int i=0; i<TOKEN_NUM; i++){
            dfa[i] = new DFA(decodeJSON.getTable(i));
            dfa[i].initDFA();
        }
    }
```

- `setDFA`
    
    lexical analyzer 내부의 setDFA 함수입니다.
    
    토큰의 수만큼 DFA에 JSON으로 구현된 DFA table을 파싱하여 생성해줍니다.
    
    그리고 DFA의 초기 상태를 $T_0$로 초기화시켜줍니다
    

```java
private void run(){
        System.out.println("running");
        int startPosition = 0;
        //끝난 lex들의 길이를 재서 가장 긴 것으로 저장하기 위해서
        int[] lexLength = new int[TOKEN_NUM+1];
        //한글자씩 진행
        while(startPosition < fileInfo.length()){
            //모든 dfa 돌려주기
            for(int i =0;i<TOKEN_NUM;i++){ //모든 dfa 돌리기
                lexLength[i] = -1;
                for(int j = startPosition; j <= fileInfo.length() ;j++){
                    String index;
										//DFA가 끝났는지 확인
                    isFinish = dfa[i].setState("final");
                    //끝까지 돌았는데 안 끝났는 것 처리
                    //dfa돌릴 글자 fileInfo[i]를 어디에다가 저장
                    if(j == fileInfo.length()){ //input file의 끝까지 돌았을 때
                        index = "@#$#@";
                    } else{
                        index = fileInfo.substring(j, j+1);
                    }
                    //- 처리 (boolean으로 변수 하나 만들어두고 이전 token이 =이면 true로 바꿔줘서 처리)
                    if(index.equals("-") && i == 8 && (currentToken.equals("INTEGER")) && j == startPosition){
                        break;
                    }
                    if( index.equals("-") && i == 8 && (currentToken.equals("IDENTIFIER")) && j == startPosition){
                        break;
                    }
                    //dfa.setState()로 다음 state 반환
                    if(!dfa[i].setState(index)){ //setState가 false 반환할 때                        if(isFinish){
                            lexLength[i] = j - startPosition; //토큰 길이 저장
                        }
                        dfa[i].initDFA();
                        break;
                    }
                }
                //dfa init해주기
                dfa[i].initDFA();
            }
            //가장 길게 나온 토큰으로 결정해주기
            int max = -1;
            int maxLengthTableId = -1;
            for(int j = 0; j<TOKEN_NUM;j++){
                //길이가 같은 2개 이상의 토큰이 있다면 우선순위 순서대로 dfa가 돌아가서 우선순위가 높은 토큰이 먼저 들어가게 되므로 >=이 아닌 >로 해서 우선순위 가장 높은 토큰을 받음
                if(lexLength[j]>max){
                    max = lexLength[j];
                    maxLengthTableId = j;
                }
            }

            //dfa 성공한 토큰 없을 때  validation
            if(maxLengthTableId == -1){
                resultInfo += "입력받을 수 없는 문자입니다";
                break;
            }
            //나온 토큰 저장, 이전 token type 저장(- 처리 위해서)
            if(!dfa[maxLengthTableId].getTableName().equals("WHITESPACE")) {
                currentToken = dfa[maxLengthTableId].getTableName();
                currentValue = fileInfo.substring(startPosition, startPosition + max);

                if (currentToken.equals("VTYPE")||currentToken.equals("COMPARISON")||currentToken.equals("INTEGER")||currentToken.equals("OPERATOR")||currentToken.equals("ASSIGNMENT")||currentToken.equals("CHAR")||currentToken.equals("BOOLEAN")||currentToken.equals("STRING")||currentToken.equals("IDENTIFIER")) {
                    resultInfo += ("<"+ currentToken + "," + currentValue + ">");
                } else{
                    resultInfo += ("<"+currentToken+">");
                }
                System.out.println("token: " + currentToken);
                System.out.println("value: " + currentValue);
            }
            startPosition += max;
            //output파일에 저장
            BufferedOutputStream bs = null;
            try {
                bs = new BufferedOutputStream(new FileOutputStream("./" + fileName + "_" + "output.txt"));
                bs.write(resultInfo.getBytes());
                bs.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
```

lexical analyzer의 대부분의 기능을 담당하는 run 함수입니다.

- 전체적인 흐름
    1. 동일한 index(코드상에서 startposition)에서 시작해서 모든 dfa를 진행시킵니다
    2. 그 중에서 가장 긴 토큰을 찾아서 결정을 해줍니다.
        1. 만약 길이가 같은 경우가 있다면 우선순위를 통해 결정을 해줍니다
    3. 토큰을 결정하고 output 파일에 저장해줍니다.
    4. startposition에 토큰의 길이만큼 더해줘서 결정되지 않은 문자들에서 다시 탐색해나갑니다.
- 각 dfa가 끝났는지 확인하기 위해서 dfa에 setState(”final”)을 실행해서 boolean값을 isFinish에 저장합니다. 이 변수가 true라면 각 dfa에서 나온 결과물의 길이를 저장하는 lexLength[]에 저장합니다.
- -처리
    - -는 이전의 토큰이 어떤 것인지에 따라 결정됩니다.
    - currentToken이라는 변수에 저장된 이전 토큰의 lexeme을 받아와서 integer이거나, identifier이라면 지금 인식된 -를 operator로 분류할 수 있도록 integer DFA를 중단시켜줍니다.
- 같은 시작 인덱스에 시작된 dfa들로 나온 값들 중에서 가장 긴 길이를 가진 토큰으로 결정합니다.
- 만약 같은 길이의 토큰이 있다면 `if(lexLength[j]>max)`로 인해서 길이가 같은 값들 중 우선순위가 가장 높은 가장 처음 들어오는 값으로 결정됩니다.
- 만약 최대 길이를 가지는 dfa의 토큰 값의 길이가 처음에 초기화시킨 -1 그대로라면 토큰에서 인식할 수 없는 값이 들어간 것입니다. 이때는 올바른 문법을 입력하라는 문구와 함께 몇번째 글자에서 오류가 났는지를 .out 파일에 입력해줍니다.
- whitespace를 처리
    - 가장 긴 value를 가지는 토큰이 whitespace라면 토큰을 따로 .out 파일에 저장해주지 않고 넘어가게 합니다.
- startposition에 가장 긴 토큰의 value 길이만큼 더해줘서 다음 반복문을 실행할 때 .out에 업데이트한 토큰 뒤부터 탐색을 시작하도록 합니다.
- 가장 긴 토큰의 값은 currentToken, currentValue로 저장해두고 이를 .out 파일에 저장해둡니다.

```java

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
```

DFA 클래스 내부입니다.

- `initDFA`
    - 해당 dfa의 state를 $T_0$으로 초기화시켜줍니다
- `setState`
    - input으로 들어오는 값을 현재 state에서 갈 수 있는 state 경로가 있는지 탐색합니다.
    - 탐색 전 input이 final이라면 현재 state에서 final 값의 value를 반환합니다
    - state 내에 있는 key들을 탐색해서 input을 포함하는 key가 있다면 해당 state로 이동합니다

```java
public class DecodeJSON {
    private JSONObject table;
    private JSONParser parser = new JSONParser();
    
    public DecodeJSON(){
        try{
            InputStream l = getClass().getResourceAsStream("/dfa/DFATable.json");
            table = (JSONObject) parser.parse(new InputStreamReader(l));
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
```

JSON으로 만들어진 DFA 테이블을 편하게 사용할 수 있는 JSONObject로 변환해주는 클래스입니다.

자바에서 JSON을 편하게 다룰 수 있는 JSON-simple이라는 라이브러리를 사용했습니다.

- `getTable`
    - 변환된 JSONobject에서 우리가 원하는 우선순위를 가진 테이블만 가져와서 반환하는 함수입니다.

```json
{

  "0" : {
    "id" : "COMPARISON",
    "T0" : {"<":"T1", ">":"T2", "!":"T3", "=":"T4"},
    "T1" : {"=":"T5", "final":"true"},
    "T2" : {"=":"T5", "final":"true"},
    "T3" : {"=":"T5"},
    "T4" : {"=":"T5"},
    "T5" : {"final":"true"}
  },

```

우선순위를 key값으로 가지는 value 안에 lexeme을 넣어두고, 테이블을 넣었습니다.

# CFG G

```
S' -> CODE
CODE -> VDECL CODE
CODE -> FDECL CODE
CODE -> ε
VDECL -> vtype id semi
FDECL -> vtype id lparen ARG rparen lbrace BLOCK RETURN rbrace
ARG -> vtype id MOREARGS
ARG -> ε
MOREARGS -> comma vtype id MOREARGS
MOREARGS -> ε 
BLOCK -> STMT BLOCK
BLOCK -> ε  
STMT -> VDECL
STMT -> id assign RHS semi
STMT -> if lparen COND rparen lbrace BLOCK rbrace else lbrace BLOCK rbrace
STMT -> while lparen COND rparen lbrace BLOCK rbrace
RHS -> EXPR
RHS -> literal
EXPR -> TERM addsub EXPR
EXPR -> TERM
TERM -> FACTOR multdiv TERM
TERM -> FACTOR
FACTOR -> lparen EXPR rparen
FACTOR -> id
FACTOR -> num
COND -> FACTOR comp FACTOR
RETURN -> return FACTOR semi
```

요구사항에 주어진 CFG를 그대로 따랐으며, 시작점을 표시하는 S’을 추가했습니다.

# SLR parsing Table

<img width="1080" alt="스크린샷_2022-05-22_20 18 57" src="https://user-images.githubusercontent.com/89574881/176104164-4e1953af-f2ca-442f-8818-19cd4ac61d80.png">

<img width="1073" alt="스크린샷_2022-05-22_20 20 09" src="https://user-images.githubusercontent.com/89574881/176104241-17bc5efc-b083-484a-a3d8-e3ec1eafceef.png">

<img width="1069" alt="스크린샷_2022-05-22_20 21 20" src="https://user-images.githubusercontent.com/89574881/176104288-16a2c401-b4ca-4926-94d0-d0c630cfe3ca.png">

<img width="1067" alt="스크린샷_2022-05-22_20 21 54" src="https://user-images.githubusercontent.com/89574881/176104326-1cf8af0c-253e-4d91-8f27-39050e46e704.png">

요구사항에서 주어진 CFG G를 주어진 링크에 입력해서 Action Table과 GOTO Table을 얻었습니다.

상세한 적용은 뒤에서 설명하겠습니다.

# Syntax Analyzer 구현

## Main

```java
SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexicalAnalyzer.getLexicalResult(), fileName);
```

Lexical Analyzer 결과를 Syntax Analyzer로 전달하기 위해서 lexicalAnalyzer 클래스에 결과를 반환하는 메소드를 만든 후 해당 메소드를 실행해서 syntaxAnalyzer 클래스에 전달해주었습니다.

## JSON

### LRTable.json

```json
{
  "0": {
    "e": "s4",
    "vtype": "s5",
    "CODE": "1",
    "VDECL": "2",
    "FDECL": "3"
  },
  "1": {
    "$": "acc"
  ,
  "72": {
    "e": "r14",
    "vtype": "r14",
    "id": "r14",
    "if": "r14",
    "while": "r14"
  }
}
```

위의 SLR 테이블을 사용하기 위해 JSON 형식으로 변환했습니다.

각 State에 존재할 수 있는 key와 key에 대응되는 value를 넣었습니다.

### transTable.json

```json
{
  "VTYPE": "vtype",
  "INTEGER": "num",
  "CHAR": "character",
  "BOOLEAN": "boolstr",
  "STRING": "literal",
  "IDENTIFIER": "id",
  "IF": "if",
  "ELSE": "else",
  "WHILE": "while",
  "RETURN": "return",
  "CLASS": "class",
  "OPERATOR": {
    "+": "addsub",
    "-": "addsub",
    "*": "multdiv",
    "/": "multdiv"
  },
  "ASSIGNMENT": "assign",
  "COMPARISON": "comp",
  "SEMI": "semi",
  "COMMA": "comma",
  "LPAREN": "lparen",
  "RPAREN": "rparen",
  "LBRACE": "lbrace",
  "RBRACE": "rbrace"
}
```

또한 Lexical Analyzer의 결과가 모두 대문자이기 때문에 terminal과 nonterminal을 가시화하기 위해 terminal들을 소문자로 바꿔줄 수 있도록 하기 위해 사용되는 JSON 파일을 만들었습니다.

### rule.json

```json
{
  "0": {
    "LHS": "S'",
    "RHS": 1
  },
  "1": {
    "LHS": "CODE",
    "RHS": 2
  },
  "2": {
    "LHS": "CODE",
    "RHS": 2
  },
...
  "26": {
    "LHS": "RETURN",
    "RHS": 3
  }
}
```

Syntax Analyzing을 진행할 때 상황에 따라 pop되는 갯수와 LHS를 인식하기 쉽게 하기 위해 따로 Table을 만들었습니다.

## Syntax Analyzer

### transformLexical()

```java
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
```

lexical analyzer에서 전달받은 String으로 된 결과값을 parsing해서 tokenList에 저장하는 과정입니다.

현재 lexical analyzer는 각 토큰 사이를 띄어쓰기로, 토큰의 key와value를 콤마(,)로 구별하도록 했기 때문에 “ “(띄어쓰기)로 잘라서 tokenList에 삽입하고, 각 토큰 내부에서 ,로 한번 더 잘라줬습니다.

operator의 경우 인자가 2개에서 1개로 변경되기 때문에 위에 나온 transTable.json을 이용하여 따로 처리를 해주었습니다.

### run()

```java
for (int i = 0; i < transformedTokenList.length;) {
...
//action shift
	if (rule.charAt(0) == 's') {
             ...
	if(!epsilonMove){
	i++;
	}
}
```

run메소드에서 tokenlist의 끝까지 갈 때까지 진행하는 반복문을 만들었습니다. 해당 반복문의 iterator는 epsilon move가 아닌 shift에서만 1씩 늘어납니다.

```java
curState = (JSONObject)slrTable.get(stack.peek());   //현재상태 테이블 로딩
rule = (String)curState.get(transformedTokenList[i]);    //다음 액션 로딩
```

stack의 가장 위에 있는 index를 참조해서 slrtable에서 해당하는 state를 curState에 저장합니다.

curState에서 현재 보고있는 token에 해당하는 값을 rule에 저장합니다.

```java
if (rule == null) {
	rule = (String) curState.get("e");
	epsilonMove = true;
}
if (rule == null){
	System.out.println("ERROR: "+i+"번째 "+"token - "+transformedTokenList[i]);
	lexicalInfo += "\nERROR: " + i + "번째 token - " + transformedTokenList[i];
	return false;
}
if (rule.equals("acc"))
	return true;
```

만약 rule이 null이라면 현재 state에 우리가 보고 있는 token에 해당하는 값이 없다는 것입니다. 

이때는 eplison move이거나, syntax error이기 때문에 현재 state에 epsilon이 포함되어있는지 확인을 하고 있다면 rule의 값을 변경하고, eplisonMove를 true로 변경해줍니다.

그 후에도 rule이 null이라면 해당 token list는 올바르지 않기 때문에 오류를 뱉습니다.

만약 rule이 acc까지 도착했다면 올바른 token list라고 판단하고 true를 반환합니다.

```java
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
```

shift의 경우 splitter를 한 칸 오른쪽으로 보내야할 때 진행됩니다.

rule의 첫번째 글자가 shift를 의미하는 s라면, shift를 진행합니다.

rule에 저장된 state index를 추출해서 shiftCount에 저장을 합니다.

그리고 현재 보고 있는 token과 index를 stack에 push합니다.

여기서 state 뿐만 아니라 token의 값까지 stack에 push를 해줬는데, 

<img width="1413" alt="Screen_Shot_2022-05-26_at_2 37 21_PM" src="https://user-images.githubusercontent.com/89574881/176104392-a87118c9-e2fa-4822-8d21-991f96f3605f.png">

다음과 같이 현재 보고 있는 token 값의 확인을 용이하게 하기 위해서 stack에 함께 삽입하였습니다.

또한 epsilon move일 경우 stack에 push만 진행을 하고, shift를 하면 안되므로 조건문을 통해 제어했습니다.

```java
else if (rule.charAt(0) == 'r') {   //action -reduce
	String temp = rule.substring(1);
	reduce = (JSONObject) ruleTable.get(temp);
	//reduce = (JSONObject) ruleTable.get(rule);   //rule Table 로딩
	pop_count = ((Long) reduce.get("RHS")).intValue();

	for (int j= 0; j < pop_count; j++) {    //불러온 글자 수*2 만큼 pop&reduce
		//System.out.println("popping: " + stack.peek());
		stack.pop();
		stack.pop();
	}
	//System.out.println("after pop stack top: " + stack.peek());
	curState = (JSONObject)slrTable.get(stack.peek());
	//System.out.println("after pop token: " + temp[0]);
	String LHS =(String) reduce.get("LHS");
	goto_state = (String)curState.get(LHS);
	
	stack.push(LHS);
	stack.push(goto_state);
	// System.out.println("pushed goto state: " + goto_state);
}
```

reduce의 경우에는 현재 진행되고 있는 token들을 치환할 수 있을 때 발생됩니다.

치환될 수 있는 stack 내에 있는 token들을 pop하고 치환된 값을 stack에 다시 넣어줄 것입니다.

reduce에 해당하는 코드입니다.

rule에 담긴 state index의 값을 ruleTable에서 찾아서 reduce에 저장합니다.

따라서 reduce에는 LHS와 RHS의 정보가 담기게 됩니다.

RHS에는 terminal과 nonterminal의 개수가 들어있습니다. 

이 값의 *2만큼 pop()을 진행하게 되는데 2개씩 pop을 하는 이유는 아까 stack에 state index와 token을 넣어줬기 때문입니다.

pop을 진행하고 나서 stack의 가장 위에 있는 state index를 받아와서 slrTable에서 해당하는 state의 정보를 curState에 받아옵니다.

curState에서 reduce의 nonterminal값에 해당하는 값을 가져오면 그 값이 reduce를 한 후 가야하는 state index가 됩니다.

LHS와 goto_state를 stack에 삽입해줍니다.

## Test

### Test1

```c
int a;
int b;
b = (12 * number) + 12;
while (a > 56) {
    b = a+25;
}
```

다음과 같은 예시 코드는 맞는 것 같지만 주어진 문법에 의하면 틀렸다.

3번째 줄에서 b의 값을 정해주는데, 값을 정해주는 행위는 block 내부에서 진행되어야하기 때문이다.

### Test2

```c
int a;
int soo;

int main(char chan, int soo) {
    int res;

    soo = -45;
    a = chan + soo / 3;
    if ((soo / 3) == 0) {
        res = -1;
    }
    else {
        res = soo + a;
    }
    while(a!=0){
        a = a - 12;
    }

    return res;
}
```

다음과 같은 코드는 문제 없이 Accept 된다.

### Test3

여기서 else를 빼고

```c
int a;
int soo;

int main(char chan, int soo) {
    int res;

    soo = -45;
    a = chan + soo / 3;
    if ((soo / 3) == 0) {
        res = -1;
    }
    else {
        res = soo + a;
    }
    while(a!=0){
        a = a - 12;
    }

    return res;
}
```

다음과 같은 코드를 실행시키면 else가 없기 때문에 reject된다.

### Test4

```c
int chansoo(char num, int d) {
        while (1<d){
             int a = d+2;
             int asdfggh = -23 * a / 3 -2;
             int c = (a + b123) - 3 ;
             int else123 = 0;
             char awdw3334 = "dfsadkfjbl sadfi eiuhweiui1234  3";
             if (a == bs123) {
                return a;
             }
            }
        }
}

```

다음과 같은 예시에서는 주어진 문법에서 무조건 선언 후 값을 지정해줘야하기 때문에 reject된다.

### Test5

```c
int a;
int soo;

int main(char chan, int soo) {
    int res;

    soo = -45;
    a = chan + soo / 3;
    if ((soo / 3) == 0) {
        res = -1;
    }
    else {
        res = soo + a;
    }
    while(a!=0){
        a = a - 12;
    }

}

```

test2와 동일한 코드에서 return을 빼고 실행하면 reject되게 된다.

## 실행

jar 파일로 첨부를 했습니다.

```json
java -jar analyzer.jar <testfile이름>
```

터미널에서 다음과 같이 실행할 수 있습니다.
