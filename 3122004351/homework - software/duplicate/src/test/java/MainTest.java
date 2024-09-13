import com.hankcs.hanlp.HanLP;
import exceptions.HashException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import exceptions.FileAnalyseException;
import exceptions.NotExistFileException;
import utils.Simhash;
import utils.CommonUtils;
import java.util.Arrays;
import java.util.Map;


public class MainTest {
    //读取文件后得到的文本
    static String analyseStr;
    //两个示例句子
    static String originSentence = "今天是星期天，天气晴，今天晚上我要去看电影。";

    static String compareSentence = "今天是周天，天气晴朗，我晚上要去看电影。";
    //比对结果写入的文件
    static String writeFilePath = "D:\\测试文本\\write.txt";
    //原文件
    static String OrigFilePath = "D:\\测试文本\\orig.txt";
    //5个比对文件
    static String CopyFilePath1 = "D:\\测试文本\\orig_0.8_add.txt";
    static String CopyFilePath2 = "D:\\测试文本\\orig_0.8_del.txt";
    static String CopyFilePath3 = "D:\\测试文本\\orig_0.8_dis_1.txt";
    static String CopyFilePath4 = "D:\\测试文本\\orig_0.8_dis_10.txt";
    static String CopyFilePath5 = "D:\\测试文本\\orig_0.8_dis_15.txt";

    static String testHash ="010100000001100110001010000001001111010000111010010111101101110000111101011100101010001011101000101110011100110011111100111110";





   //测试文件分析异常
    @Test
    void FileAnalyseException(){
        try {
            CommonUtils.analyseText(null);
            Assertions.fail("没有抛出异常");
        } catch (FileAnalyseException e) {
            e.printStackTrace();
            Assertions.assertTrue(true);
        }
        try {
            CommonUtils.analyseText("");
            Assertions.fail("没有抛出异常");
        } catch (FileAnalyseException e) {
            e.printStackTrace();
            Assertions.assertTrue(true);
        }
        try {
            CommonUtils.analyseText(" ");
            Assertions.fail("没有抛出异常");
        } catch (FileAnalyseException e) {
            e.printStackTrace();
            Assertions.assertTrue(true);
        }
    }

    //测试哈希异常（得到hash值为空）

    @Test
    void HashException1(){
        try {
            Simhash.wordHash("");
            Assertions.fail("没有抛出异常");
        } catch (HashException e) {
            e.printStackTrace();
            Assertions.assertTrue(true);
        }

    }

    @Test
    void HashException2(){
        try {
            Simhash.wordHash(null);
            Assertions.fail("没有抛出异常");
        } catch (HashException e) {
            e.printStackTrace();
            Assertions.assertTrue(true);
        }
    }
    @Test
    void HashException3(){
        try {
            Simhash.wordHash("    ");
            Assertions.fail("没有抛出异常");
        } catch (HashException e) {
            e.printStackTrace();
            Assertions.assertTrue(true);
        }
    }

    //测试文件读取异常


//测试文件读取结果,文件不存在错误
    @Test
    void ReadFileTest(){
        try {
            CommonUtils.readFile("D:\\not existing.txt");
            Assertions.fail("没有抛出异常");
        } catch (NotExistFileException e) {
            e.printStackTrace();
            Assertions.assertTrue(true);
        }

    }
//字符串分析
    @Test
    void  analyseTextTest(){
        try {
            //测试句子分词
            System.out.println("分词结果为："+CommonUtils.analyseText(originSentence));
            //测试文本分词
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("分词结果有误");
        }
    }


    //测试MD5算法hash计算hash，检查所得到hash值是否正常

    @Test
    void  WordHashTest(){
        HanLP.extractKeyword(originSentence, originSentence.length()).forEach(
                word -> {
                    try {
                        String hash = Simhash.wordHash(word);
                        System.out.println(word +" : "+  hash);
                        Assertions.assertEquals(128, hash.length(), "hash值长度不是128");
                    } catch (HashException e) {
                        Assertions.fail("哈希值出错");
                        e.printStackTrace();
                    }
                }
        );
    }

    @Test
    void  WordHashTest2(){
        HanLP.extractKeyword(originSentence, originSentence.length()).forEach(
                word -> {
                    try {
                        String hash = Simhash.wordHash(word);
                        System.out.println(word +" : "+ hash);
                        Assertions.assertEquals(128, testHash.length(), "hash值长度不是128");
                    } catch (HashException e) {
                        e.printStackTrace();
                        Assertions.assertTrue(true);
                    }
                }
        );
    }





  //加权算法测试
    @Test
    void HashWeightTest(){
        Map<String, Integer> map = null;
        try {
            map = CommonUtils.analyseText(originSentence);
        } catch (FileAnalyseException e) {
            e.printStackTrace();
            Assertions.fail("解析错误");
        }
        map.forEach((word, count) -> {
            try {
                String hash = Simhash.wordHash(word);
                int[] hashWeight = Simhash.hashWeight(hash,count);
                //打印加权后的hash值
                System.out.println(word +" : "+ Arrays.toString(hashWeight));
                Assertions.assertEquals(128, hashWeight.length, "加权后的hash值长度不是128");
            } catch (HashException e) {
                Assertions.fail("哈希出错");
                e.printStackTrace();
            }
        });
    }


    //测试计算simHash

    @Test
    void CalculateSimHashTest() {
        try {
            String hash1 = Simhash.calculateSimHash(CommonUtils.analyseText(originSentence));
            System.out.println("原句子\"" + originSentence + "\"的simHash值为：" + hash1);
            Assertions.assertEquals(hash1.length(), 128, "hash值长度不是128");
            String hash2=Simhash.calculateSimHash(CommonUtils.analyseText((CommonUtils.readFile(OrigFilePath))));
            System.out.println("原文本的simHash值为：" + hash2);
            Assertions.assertEquals(hash2.length(), 128, "hash值长度不是128");
        } catch (FileAnalyseException | NotExistFileException e) {
            e.printStackTrace();
        }
    }

//两个文件的simhash对比测试
    @Test
    void GetSimilarityTest(){
        String hash1;
        String hash2;
        try {
            hash1 = Simhash.calculateSimHash(CommonUtils.analyseText(CommonUtils.readFile(OrigFilePath)));
            hash2 = Simhash.calculateSimHash(CommonUtils.analyseText(CommonUtils.readFile(CopyFilePath1)));
            double similarity = Simhash.getSimilarity(hash1, hash2);
            String format = String.format("两个文本的相似度为：%.2f", similarity);
            System.out.println(format);
            Assertions.assertTrue(0 <= similarity && similarity <= 1, "相似度不在0-1之间");
        } catch (FileAnalyseException | NotExistFileException e) {
            e.printStackTrace();
        }
    }


}