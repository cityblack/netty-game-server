package com.lzh.game.start.util;

import com.lzh.game.common.serialization.JsonUtil;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Random utils
 */
public class RandomUtils {
    // 默认的单位(万)
    private static final int DEFAULT_DENOMINATOR = 10000;

    private static ThreadLocal<Random> random = new ThreadLocal<>();

    static {
        random.set(new Random());
    }

    /**
     * 随机抽一个
     * 允许有0的值存在
     * @param probabilityList -- 概率集合
     * @return
     */
    public static int randList(final List<Integer> probabilityList) {
        if (CollectionUtils.isEmpty(probabilityList)) {
            throw new IllegalArgumentException("概率集合为空");
        }
        int sum = probabilityList.stream().reduce(0, Integer::sum);
        if (sum <= 0) {
            throw new IllegalArgumentException("概率总和小于或则等于0");
        }
        int value = randomValue(1, sum);
        int lastProb = 0;
        for (int i = 0; i < probabilityList.size(); i++) {
            lastProb += probabilityList.get(i);
            if (value <= lastProb) {
                return i;
            }
        }
        throw new IllegalArgumentException("未命中任何随机数." + probabilityList);
    }

    /**
     * 增强型随机抽取
     * @param data -- 需要随机的数据
     * @param probabilityTransfer -- 内部转换函数
     * @param <T>
     * @return
     */
    public static <T>T enhanceRandList(final List<T> data, ToIntFunction<T> probabilityTransfer) {
        int sum = data.stream().mapToInt(probabilityTransfer).sum();
        if (sum <= 0) {
            throw new IllegalArgumentException("概率总和小于或则等于0");
        }
        int value = randomValue(1, sum);
        int lastProb = 0;
        for (int i = 0; i < data.size(); i++) {
            int probability = probabilityTransfer.applyAsInt(data.get(i));
            lastProb += probability;
            if (value <= lastProb) {
                return data.get(i);
            }
        }
        throw new IllegalArgumentException("未命中任何对象." + JsonUtil.toJSON(data));
    }

    /**
     * 随机抽取 每个元素获取的概率均等
     * @param data
     * @param <T>
     * @return
     */
    public static <T>T enhanceRandList(final List<T> data) {
        if (CollectionUtils.isEmpty(data)) {
            throw new IllegalArgumentException("概率集合为空");
        }
        int value = randomValue(1, data.size()) - 1;
        return data.get(value);
    }

    public static boolean isHit(int value, int denominator) {

        if (denominator <= 0 || value < 0) {
            throw new IllegalArgumentException("value or denominator can't low zero" + denominator + "," + value);
        }

        long random = randomValue(1, denominator);

        return random <= value;
    }
    /**
     * 是否命中 万分单位
     * @param value
     * @return
     */
    public static boolean isHit(int value) {
        return isHit(value, DEFAULT_DENOMINATOR);
    }

    public static long randomValue(long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        return min + getRandom().nextLong() * (max - min + 1);
    }

    public static int randomValue(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        int temp = max - min;

        return getRandom().nextInt(temp) + 1;
    }

    protected static Random getRandom() {
        return random.get();
    }

    private RandomUtils() {}

    public static void main(String[] args) {
        //IntStream.range(0,100).forEach(e -> System.out.println(getRandom().nextInt(10 + 1) ));

        Map<Boolean, List<Boolean>> map = IntStream.range(0, 100000)
                .mapToObj(e -> isHit(3000))
                .collect(Collectors.groupingBy(e -> e));
        double trueFlag = map.get(Boolean.TRUE).size();
        double falseFlag = map.get(Boolean.FALSE).size();
        System.out.println(trueFlag / 100000);
        System.out.println(falseFlag / 100000);
    }
}
