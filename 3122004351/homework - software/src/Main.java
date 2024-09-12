import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
      String originFile=readFile("resources/测试文本/orig.txt");

    }

   static String readFile(String path){
       // 用于存储文件内容的字符串
       StringBuilder fileContent = new StringBuilder();

       try (BufferedReader br = new BufferedReader(new FileReader(path))) {
           String line;
           // 逐行读取文件内容
           while ((line = br.readLine()) != null) {
               fileContent.append(line).append(System.lineSeparator());
           }
       } catch (IOException e) {
           e.printStackTrace();
       }

       // 返回文件内容
       return fileContent.toString();
   }
}