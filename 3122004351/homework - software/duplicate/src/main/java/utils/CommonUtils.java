package utils;

import cn.hutool.core.io.FileUtil; // 引入Hutool工具包中的FileUtil类用于文件操作
import cn.hutool.core.util.StrUtil; // 引入Hutool工具包中的StrUtil类用于字符串操作
import com.hankcs.hanlp.HanLP; // 引入HanLP库，用于自然语言处理
import com.hankcs.hanlp.seg.common.Term; // 引入HanLP中的Term类，用于分词结果
import exceptions.FileAnalyseException; // 自定义异常，用于处理文件分析过程中的错误
import exceptions.NotExistFileException; // 自定义异常，用于处理文件不存在的情况

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 公共工具类
public class CommonUtils {

    public static String readFile(String path) throws NotExistFileException {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileContent.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            // 这里的异常处理可能不够精确，因为IOException可能是由多种原因引起的
            // 但在实际应用中，我们通常将其视为文件不存在
            throw new NotExistFileException("该绝对路径的文件不存在");
        }
        return fileContent.toString();
    }

    public static Map<String, Integer> analyseText(String text) throws FileAnalyseException {
        if (text == null || StrUtil.isBlank(text) || StrUtil.isEmpty(text)) {
            throw new FileAnalyseException("解析内容不能为空");
        }
        List<String> keyList = HanLP.extractKeyword(text, text.length()); // 提取关键词
        if (keyList.size() <= 3) {
            throw new FileAnalyseException("解析关键词过少");
        }
        List<Term> termList = HanLP.segment(text); // 对文本进行分词
        List<String> allWords = termList.stream().map(term -> term.word).collect(Collectors.toList()); // 获取所有词语
        Map<String, Integer> wordCount = new HashMap<>(keyList.size()); // 存储关键词及其词频
        for (String s : keyList) {
            wordCount.put(s, Collections.frequency(allWords, s)); // 计算每个关键词的词频并存储
        }
        return wordCount;
    }

    public static void writeFile(String filePath, String content) {
        FileUtil.appendString(content, filePath, "utf-8"); // 使用Hutool的FileUtil类将内容追加到文件，如果不存在则创建文件
    }
}