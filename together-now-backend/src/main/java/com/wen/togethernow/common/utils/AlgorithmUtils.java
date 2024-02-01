package com.wen.togethernow.common.utils;

import java.util.List;
import java.util.Objects;

/**
 * 算法工具类
 *
 * @author wen
 */
public class AlgorithmUtils {
    // 私有化构造方法
    private AlgorithmUtils(){}

    /**
     * 编辑距离算法，用于计算两组标签的相似度
     *
     * @param tagList1 标签列表1
     * @param tagList2 标签列表2
     * @return 返回两组标签的距离（越小越相似）
     */
    public static int editDistance(List<String> tagList1,List<String> tagList2) {
        // 创建二维数组来存储中间结果
        int[][] dp = new int[tagList1.size() + 1][tagList2.size() + 1];

        for (int i = 0; i <= tagList1.size(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= tagList2.size(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= tagList1.size(); i++) {
            for (int j = 1; j <= tagList2.size(); j++) {
                if (Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
            }
        }

        return dp[tagList1.size()][tagList2.size()];
    }
}
