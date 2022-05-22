import java.io.File;

public class SyntaxAnalyzer {
    int position = 0;
    String lexicalInfo;
    String fileName;
    public SyntaxAnalyzer(String lexicalResult,String fileName){
        this.fileName = fileName;
        this.lexicalInfo = lexicalResult;
        transformLexical();
    }

    public String[] transformLexical(){
        String[] tokenList;
        String[] curToken;

        if(lexicalInfo.contains("ERROR"))
            System.out.println("올바르지 않은 문법입니다");
        tokenList = this.lexicalInfo.split(" ");

    }



}
