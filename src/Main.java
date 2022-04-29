import java.io.*;
import java.util.*;

public class Main {
    // java Main.class test.txt

    public static void main(String[] args) throws IOException {
        String line;
        String fileInfo = "";

        //파일 확인
        if(args.length == 0){
            System.out.println("파일이 없습니다");
            return;
        }
        File f = new File(args[0]);
        String fileName = args[0];
        //파일 정보 fileInfo에 저장
        BufferedReader br = new BufferedReader(new FileReader(f));
        while((line = br.readLine()) != null){
            fileInfo += line;
        }
        br.close();

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fileInfo, fileName);
    }
}