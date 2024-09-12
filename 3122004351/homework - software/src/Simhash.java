package utils;

import cn.hutool.core.util.StrUtil;
import exceptions.HashException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


public class CalculationUtils {
    //hash码长度为128
    static final int HASH_BIT = 128;

    //将给定的字符串（单词）转换为MD5哈希值，并进一步将MD5哈希值转换为128位二进制字符串。
    public static String wordHash(String word) throws HashException {
        //如果传入词语为null或“”或“ ”
        if (word == null || StrUtil.isBlank(word) || StrUtil.isEmpty(word)) {
            throw new HashException("词语为空");
        }
        try {
            // 采用MD5算法进行hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(word.getBytes(StandardCharsets.UTF_8));
            // hash值转为32位16进制的散列值
            StringBuilder hash = new StringBuilder();
            for (byte b : digest.digest()) {
                hash.append(String.format("%02x", b));
            }
            // 16进制的散列值转为128位二进制码
            StringBuilder finalHash = new StringBuilder();
            String strTemp;
            for (int i = 0; i < hash.length(); i++) {
                // 每一位16进制数加上0000，最后截取后4位，得到便是这位数的二进制
                strTemp = "0000" + Integer.toBinaryString(Integer.parseInt(hash.substring(i, i + 1), 16));
                finalHash.append(strTemp.substring(strTemp.length() - 4));
            }

            return finalHash.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new HashException("MD5算法异常");
        }
    }

//据给定的二进制哈希值和权重，将二进制中的每一位（'0'或'1'）转换为相应的加权整数
    public static int[] hashWeight(String hash, int weight) {
        // 新建一个数组用于存放加权后的二进制哈希值
        int[] hashArray = new int[HASH_BIT];
        // 遍历二进制哈希值，0则是-1，1则是1，将每一位加权后存入数组
        for (int i = 0; i < hash.length(); i++) {
            if (hash.charAt(i) == '1') {
                hashArray[i] = weight;
            } else {
                hashArray[i] = -1 * weight;
            }
        }
        return hashArray;
    }

//根据加权后的哈希数组，生成最终的simHash字符串，其中加权后的正值转换为'1'，负值转换为'0'。
    public static String getSimHash(int[] mergeHash){
        // 使用StringBuilder存储simHash
        StringBuilder simHash = new StringBuilder();
        // 遍历合并后的hash值，大于0则是1，小于0则是0
        for (int hash : mergeHash) {
            if (hash > 0) {
                simHash.append("1");
            } else {
                simHash.append("0");
            }
        }
        return simHash.toString();
    }

