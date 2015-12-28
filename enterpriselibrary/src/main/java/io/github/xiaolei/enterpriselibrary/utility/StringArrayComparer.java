package io.github.xiaolei.enterpriselibrary.utility;


import io.github.xiaolei.enterpriselibrary.logging.Logger;

/**
 * 听写与综合填空通用的单词数组比较辅助类
 * 原算法为C++实现，此类根据原算法翻译成 Java 实现
 */
public class StringArrayComparer {
    public static final String TAG = StringArrayComparer.class.getSimpleName();

    /**
     * 比较单词数组
     *
     * @param targetList 正确答案单词数组
     * @param userList   用户输入单词数组
     * @param ignoreCase 是否区分大小写
     * @return 匹配结果。长度与 userList 相同
     */
    public static boolean[] compare(String[] targetList, String[] userList, boolean ignoreCase) {

        int tlen = targetList.length;
        int ulen = userList.length;

        double[][] Ptable = new double[tlen][ulen];
        double[][] Stable = new double[tlen][ulen];
        int[][] Pathtable = new int[tlen][ulen];

        for (int i = 0; i < tlen; i++) {
            for (int r = 0; r < ulen; r++) {
                Ptable[i][r] = compareWords(targetList[i], userList[r], ignoreCase);
            }
        }

        //start
        for (int i = 0; i < ulen; i++) {
            Stable[0][i] = Ptable[0][i];
        }

        for (int x = 1; x < tlen; x++) {
            for (int y = 0; y < ulen; y++) {
                double max = -1;
                int index = -1;

                for (int z = 0; z <= y; z++) {
                    if (z == y && Ptable[x - 1][y] == 1) {
                        if (z != ulen - 1) {
                            continue;
                        }
                    }

                    if (Stable[x - 1][z] > max) {
                        max = Stable[x - 1][z];
                        index = z;
                    }
                }

                Stable[x][y] = max + Ptable[x][y];
                Pathtable[x][y] = index;
            }
        }

        double resultmax = -1;
        int resultlastindex = -1;

        for (int y = ulen - 1; y >= 0; y--) {
            if (Stable[tlen - 1][y] >= resultmax) {
                resultlastindex = y;
                resultmax = Stable[tlen - 1][y];
            }
        }

        boolean[] result = new boolean[ulen];

        int xx = tlen - 1;
        while (xx >= 0) {

            if (Ptable[xx][resultlastindex] == 1) {
                result[resultlastindex] = true;
            }

            Logger.d(TAG,
                    String.format("%s  --->   %s  \t  (%d)-(%d) \n", String.valueOf(targetList[xx]), String.valueOf(userList[resultlastindex]), xx, resultlastindex));
            resultlastindex = Pathtable[xx][resultlastindex];
            xx--;
        }

        return result;
    }

    /**
     * 单词比较
     *
     * @param str1       正确答案单词
     * @param str2       用户输入单词
     * @param ignoreCase true: 不区分大小写, false: 区分大小写
     * @return 1: 相同, 0: 不相同
     */
    private static int compareWords(String str1, String str2, boolean ignoreCase) {
        if (str1 == str2) {
            return 1;
        }

        if (str1 == null || str2 == null) {
            return 0;
        }

        if (ignoreCase) {
            return str1.equalsIgnoreCase(str2) ? 1 : 0;
        } else {
            return str1.equals(str2) ? 1 : 0;
        }
    }
}
