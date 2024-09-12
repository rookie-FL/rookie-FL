package utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import exception.FileAnalyseException;
import exception.NotExistFileException;

import java.util.*;

import static utils.CalculationUtils.calculateMath;
import static utils.CalculationUtils.toSuffixMathematic;

public class CommonUtils {
    static final String OPERATIONS="+-*÷";
    /**
     * 从文件中读取文本
     * @param filePath 文件路径
     * @return 读取出的文本
     */
    public static String readFileToStr(String filePath) throws NotExistFileException {
        try {
            return FileUtil.readUtf8String(filePath);//返回读取的文本
        } catch (Exception e) {
            throw new NotExistFileException("该绝对路径的文件不存在");
        }
    }
    /**
     * 解析文本内容，转换为表达式
     * @param fileContent 文件内容
     * @return 表达式
     */
    public static List<List<Object>> analyseMathematics(String fileContent)throws FileAnalyseException{
        //文本内容为null或“”或“ ”时，文件解析异常
        if (fileContent == null || StrUtil.isBlank(fileContent) || StrUtil.isEmpty(fileContent)) {
            throw new FileAnalyseException("文件解析异常，解析内容为空");
        }
        List<List<Object>> lists = new ArrayList<>();
        String[] split = fileContent.split("\n");
        for (int i = 0; i < split.length - 1; i++) {
            String question = split[i].split("\\.")[1];//分解题目
            List<Object> list = new ArrayList<>();
            for (int j = 0; j < question.length(); j++) {
                if (question.charAt(j) == '+') {
                    list.add("+");
                } else if (question.charAt(j) == '-') {
                    list.add("-");
                } else if (question.charAt(j) == '*') {
                    list.add("*");
                } else if (question.charAt(j) == '÷') {
                    list.add("÷");
                } else if (question.charAt(j) == '(') {
                    list.add("(");
                } else if (question.charAt(j) == ')') {
                    list.add(")");
                } else {
                    //标记运算符的索引
                    int index1 = j;
                    int index2 = -1;
                    int index3 = -1;
                    int index4 = -1;
                    boolean flag = false;
                    for (int k = j; k < question.length(); k++) {
                        if (question.charAt(k) == '\''){
                            index2 = k;
                        }
                        if (question.charAt(k) == '/'){
                            index3 = k;
                        }
                        if (question.charAt(k) == '+' || question.charAt(k) == '-' || question.charAt(k) == '*' || question.charAt(k) == '÷' || question.charAt(k) == '(' || question.charAt(k) == ')') {
                            index4 = k;
                            j = k - 1;
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        index4 = question.length();
                        j = question.length() - 1;
                    }
                    if (index1 == index2){
                        break;
                    }
                    if (index2 != -1 && index3 != -1) {
                        int num1  = Integer.parseInt(question.substring(index1, index2));
                        int num2 = Integer.parseInt(question.substring(index2 + 1, index3));
                        int num3 = Integer.parseInt(question.substring(index3 + 1, index4));
                        Fraction rational1 = new Fraction(num1*num3 + num2, num3);
                        list.add(rational1);
                    } else if (index2 == -1 && index3 != -1) {
                        int num1 = Integer.parseInt(question.substring(index1, index3));
                        int num3 = Integer.parseInt(question.substring(index3 + 1, index4));
                        Fraction rational1 = new Fraction(num1, num3);
                        list.add(rational1);
                    } else {
                        int num1 = Integer.parseInt(question.substring(index1, index4));
                        Fraction rational1 = new Fraction(num1, 1);
                        list.add(rational1);
                    }
                }
            }
            lists.add(list);
        }
        return lists;
    }

    /**
     * 写入文件
     * @param filePath 写入的文件路径
     * @param content 写入内容
     */
    public static void writeFile(String filePath,String content) {
        FileUtil.writeString(content,filePath,"utf-8");
    }

    /**
     * 检查题目是否重复
     *
     * @param mathematic1 题目1
     * @param mathematic2 题目2
     * @return 题目是否重复
     */
    public static boolean checkRepeat(List<Object> mathematic1,List<Object> mathematic2){
        boolean flag=true;
        //判断运算结果是否一致,不一致则不重复
        if (!calculateMath(mathematic1).toString().equals(calculateMath(mathematic2).toString())){
            return false;
        }
        //将中缀表达式转换为后缀表达式
        List<Object> suffixMathematic1=toSuffixMathematic(mathematic1);
        List<Object> suffixMathematic2=toSuffixMathematic(mathematic2);
        //判断后缀表达式的长度是否一致，不一致则不重复
        if(suffixMathematic1.size()!=suffixMathematic2.size()){
            return false;
        }
        //判断后缀表达式的元素是否一致，一致则重复
        for (int i = 0; i < suffixMathematic1.size(); i++) {
            if (!suffixMathematic1.get(i).toString().equals(suffixMathematic2.get(i).toString())) {
                flag = false;
                break;
            }
        }
        if (flag)  return true;//如果运算结果一致，长度一致，后缀表达式元素完全一致，则重复

        Object[] suffixArray1=suffixMathematic1.toArray();
        //统计加号和乘号的数量
        Map<String,Integer> countOperation=new HashMap<>(2);
        countOperation.put("+",0);
        countOperation.put("*",0);
        for (Object o : suffixMathematic1) {
            if (o.equals("+") || o.equals("*")) {
                countOperation.put(o.toString(), countOperation.get(o.toString()) + 1);
            }
        }
        //如果只有一个乘号
        if (countOperation.get("*")==1){
            //将加号两侧的数字交换
            for (int i = 0; i < suffixMathematic1.size(); i++) {
                if (suffixMathematic1.get(i).equals("*")){
                    //中缀表达式中在加号两侧的数字，在后缀表达式中在加号左侧
                    suffixArray1[i-2]=suffixMathematic1.get(i-1);
                    suffixArray1[i-2]=suffixMathematic1.get(i-2);
                }
            }
            for (int i = 0; i < suffixMathematic2.size(); i++) {
                if (!suffixArray1[i].toString().equals(suffixMathematic2.get(i).toString())){
                    return false;
                }
            }
        }
        //如果只有一个加号
        if (countOperation.get("+")==1){
            //将加号两侧的数字交换
            for (int i = 0; i < suffixMathematic1.size(); i++) {
                if (suffixMathematic1.get(i).equals("+")){
                    //中缀表达式中在加号两侧的数字，在后缀表达式中在加号左侧
                    suffixArray1[i-2]=suffixMathematic1.get(i-1);
                    suffixArray1[i-1]=suffixMathematic1.get(i-2);
                }
            }
            int count=0;//加号两侧数字交换后，元素是否完全一致
            for (int i = 0; i < suffixMathematic2.size(); i++) {
                if (!suffixArray1[i].toString().equals(suffixMathematic2.get(i).toString())) {
                    count = 1; //count=1,标志运算符只有一个加号，但交换后还是不一致，所以不重复
                    return false;
                }
            }
            if(count == 0) return true;//count=0,标志运算符只有一个加号，交换后完全一致，所以重复了
        }

        //如果加号或乘号的个数大于1
        int index=0;//记录加号或乘号的位置
        if(countOperation.get("+")==2) {
            for (int i = 0; i < suffixMathematic1.size(); i++) {
                if (suffixMathematic1.get(i).equals("+")) {
                    index = i;
                    suffixArray1[i - 2] = suffixMathematic1.get(i - 1);
                    suffixArray1[i - 1] = suffixMathematic1.get(i - 2);
                    break;
                }
            }
            flag = true;
            for (int i = 0; i < suffixMathematic2.size(); i++) {
                if (!suffixArray1[i].toString().equals(suffixMathematic2.get(i).toString())) {
                    flag = false;
                    break;
                }
            }
            if (flag){
                return true;//如果交换一次后一致，则重复
            }

            //不一致，继续交换
            if (suffixArray1[index + 1].equals("+")) {
                suffixArray1[index - 3] = suffixMathematic1.get(index - 2);
                suffixArray1[index - 2] = suffixMathematic1.get(index - 1);
                suffixArray1[index - 1] = suffixMathematic1.get(index);
                suffixArray1[index] = suffixMathematic1.get(index - 3);
            } else {
                suffixArray1[index - 2] = suffixMathematic1.get(index + 1);
                suffixArray1[index - 1] = suffixMathematic1.get(index - 2);
                suffixArray1[index] = suffixMathematic1.get(index - 1);
                suffixArray1[index + 1] = suffixMathematic1.get(index);
            }
            flag = true;
            for (int i = 0; i < suffixMathematic2.size(); i++) {
                if (!suffixArray1[i].toString().equals(suffixMathematic2.get(i).toString())) {
                    flag = false;
                    break;
                }
            }
            if (flag){
                return true;
            }
            flag = true;
            if (suffixArray1[index + 1].equals("+")) {
                if(index-3!=-1){
                    suffixArray1[index - 3] = suffixMathematic1.get(index - 1);
                    suffixArray1[index] = suffixMathematic1.get(index - 3);
                }
                suffixArray1[index - 2] = suffixMathematic1.get(index - 2);
                suffixArray1[index - 1] = suffixMathematic1.get(index);
            }else {
                suffixArray1[index - 2] = suffixMathematic1.get(index + 1);
                suffixArray1[index - 1] = suffixMathematic1.get(index-1);
                suffixArray1[index] = suffixMathematic1.get(index - 2);
                suffixArray1[index + 1] = suffixMathematic1.get(index);
            }

            // 比较一下
            for (int i = 0; i < suffixMathematic2.size(); i++) {
                if (!suffixMathematic2.get(i).toString().equals(suffixArray1[i].toString())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return true;
            }
        }

        index=0;//记录加号或乘号的位置
        if(countOperation.get("*")==2){
            for (int i=0;i < suffixMathematic1.size() ; i++){
                if(suffixMathematic1.get(i).equals("*")){
                index=i;
                suffixArray1[i-2]=suffixMathematic1.get(i-1);
                suffixArray1[i-1]=suffixMathematic1.get(i-2);
                break;
            }
        }
        flag=true;
        for(int i=0;i<suffixMathematic2.size();i++){
            if(!suffixArray1[i].toString().equals(suffixMathematic2.get(i).toString())){
                flag=false;
                break;
            }
        }
        if(flag)  return true;//如果交换一次后一致，则重复
        //不一致，继续交换
        if(suffixArray1[index+1].equals("*")){
            suffixArray1[index-3]=suffixMathematic1.get(index-2);
            suffixArray1[index-2]=suffixMathematic1.get(index-1);
            suffixArray1[index-1]=suffixMathematic1.get(index);
            suffixArray1[index]=suffixMathematic1.get(index-3);
        }else{
            suffixArray1[index-2]=suffixMathematic1.get(index+1);
            suffixArray1[index-1]=suffixMathematic1.get(index-2);
            suffixArray1[index]=suffixMathematic1.get(index-1);
            suffixArray1[index+1]=suffixMathematic1.get(index);
        }
        flag = true;
        for(int i=0;i<suffixMathematic2.size();i++){
            if(!suffixArray1[i].toString().equals(suffixMathematic2.get(i).toString())){
                flag=false;
                break;
            }
        }
        return flag;
    }
        return false;
    }
    /**
     * 生成题目
     *
     * @param number  题目数量
     * @param range 题目范围
     * @return 题目列表
     */
    public static List<List<Object>> generateQuestions(int number,int range){
        if (number<=0 || range<=0){
            throw new RuntimeException(("请正确输入参数，参数必须大于0"));
        }
        //存放题目的集合
        List<List<Object>> list=new ArrayList<>();
        for (int i = 0; i < number; i++) {
            //题目的运算符个数和数字个数
            int operationNum = (int) (Math.random() * 3 + 1);//1~3
            int mathNum = operationNum + 1;//2~4,比运算符数多一
            //生成运算符
            String[] operations=new String[operationNum];//存放运算符
                //根据integers数组的元素随机生成运算符
            int[] integers = NumberUtil.generateRandomNumber(0, 4, operationNum);
            boolean flag=true;//还没有加括号？
            for(int j = 0; j < operationNum ; j++){
                // 如果是加法,随机加括号,且只能加一次括号
                if(flag && ((OPERATIONS.charAt(integers[j])+"").equals("+"))){
                    int isNeed=(int)(Math.random()*2);//是否加括号
                    if(isNeed==1){
                        operations[j]="("+OPERATIONS.charAt(integers[j])+")";
                        flag=false;//已经加了括号
                    }
                    else{
                        operations[j]=String.valueOf(OPERATIONS.charAt(integers[j]));
                    }
                }else{
                    operations[j]=OPERATIONS.charAt(integers[j])+"";
                }
            }
            //生成数字
            Fraction[] numbers=new Fraction[mathNum];
            for(int j=0;j<mathNum;j++){
                //每次生成的数字的范围
                int min=0;
                int max=range;
                //检查第二、三、。。。个数的范围
                if (j>0){
                    //如果是除法，后一个数比前一个大
                    if(operations[j-1].equals("÷")){
                        min=numbers[j-1].getNumerator()/numbers[j-1].getDenominator()+1;
                    }
                    //如果是减法，后一个比前一个小
                    if(operations[j-1].equals("-")){
                        max=numbers[j-1].getNumerator()/numbers[j-1].getDenominator();
                    }
                }
                //生成随机数
                int isFraction=(int)(Math.random()*2);//判断是否生成分数
                if (isFraction==1){
                    int denominator = (int) (Math.random() * 100 + 1);//1~100
                    int numerator = (int) (Math.random() * (max * denominator - min * denominator + 1) + min * denominator);
                    numbers[j]=new Fraction(numerator,denominator);
                }else{
                    numbers[j]=new Fraction((int) (Math.random() * (max - min + 1) + min),1);
                }
            }
            //将运算符和数字组成题目
            List<Object> question=new ArrayList<>();
            for(int j=0;j<operationNum+mathNum;j++){
                if(j%2==0){
                    //放入数字和有括号组合的运算符
                    if (j / 2 < operations.length && operations[j / 2].length() == 3) {
                        question.add(operations[j / 2].charAt(0));
                        question.add(numbers[j / 2]);
                        question.add(operations[j / 2].charAt(1));
                        question.add(numbers[j / 2+1]);
                        question.add(operations[j / 2].charAt(2));
                        if (j / 2 + 1 < numbers.length - 1) {
                            question.add(operations[j / 2 + 1]);
                        }
                        j += 3;
                    } else {
                            question.add(numbers[j / 2]);
                    }
                }else{
                    //放入单个运算符
                    question.add(operations[j/2]);
                }
            }
            //将题目加入题目集合中
            boolean isSame=false;
            //遍历题目列表，检查题目是否重复
            for (List<Object> objectList : list) {
                if(checkRepeat(objectList,question)){
                    isSame = true;
                    break;
                }
            }
            if(calculateMath(question).getDenominator()>0&&calculateMath(question).getNumerator()>0&&!isSame){
                list.add(question);
            }else{
                i--;
            }
        }
        System.out.println("成功生成"+number+"道题目！");
        return list;
    }
}