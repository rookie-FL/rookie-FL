import cn.hutool.core.date.DateUtil;
import exceptions.FileAnalyseException;
import exceptions.NotExistFileException;
import utils.Simhash;
import utils.CommonUtils;
import java.util.Map;

public class Main {

    public static void main(String[] args){
        // 读取并解析参数,如果参数不为3个则抛出错误
        if (args.length != 3) {
            throw new IllegalArgumentException("参数个数不正确!!!");
        }
        // 源文件映射
        Map<String, Integer> originFile = null;
        //比较文件映射
        Map<String, Integer> compareFile = null;
        try {
            //得到原文本的关键词和词频
            originFile = CommonUtils.analyseText(CommonUtils.readFile(args[0]));
            //以及比对文本的关键词的关键词和词频
            compareFile = CommonUtils.analyseText(CommonUtils.readFile(args[1]));
        } catch (FileAnalyseException | NotExistFileException e) {
            e.printStackTrace();
        }
        // 计算两个文件simHash值
        String simHash1 = Simhash.calculateSimHash(originFile);
        String simHash2 = Simhash.calculateSimHash(compareFile);
        //对比两个文件simHash值,保留两位小数
        double result = Simhash.getSimilarity(simHash1, simHash2);
        String format = String.format("相似度为：%.2f", result);
        System.out.println(format);

        //写入文件
        String writeFileContent = "---------------------------------------" + "\n" +
                "原文件：" + args[0] + "\n" +
                "对比文件：" + args[1] + "\n" +
                format + "\n" +
                "比较时间为：" + DateUtil.now() + "\n";
        ;

        CommonUtils.writeFile(args[2],writeFileContent);
    }
}
