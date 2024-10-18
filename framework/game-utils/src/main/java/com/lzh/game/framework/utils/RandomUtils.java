package com.lzh.game.framework.utils;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zehong.l
 * @since 2024-10-15 17:42
 **/
public class RandomUtils {

    private static final int TEN_THOUSAND_INT = 10000;

    /**
     * Return random number
     *
     * @param min - min value
     * @param max - max value
     * @return [min, max]
     */
    public static int randomValue(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        if (max == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("max value need < Integer.MAX");
        }
        if (min == max) {
            return max;
        }
        return getRandom().nextInt(min, max + 1);
    }

    /**
     * Return random number
     *
     * @param min - min value
     * @param max - max value
     * @return [min, max]
     */
    public static long randomValue(long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException(min + ">" + max);
        }
        if (max == Long.MAX_VALUE) {
            throw new IllegalArgumentException("max value need < Long.MAX");
        }
        if (min == max) {
            return max;
        }
        return getRandom().nextLong(min, max + 1);
    }

    /**
     * Did it hit, Use a thousand point unit
     *
     * @param value - current probability
     * @return Is hit
     */
    public static boolean isHist(int value) {
        return isHit(value, TEN_THOUSAND_INT);
    }

    /**
     * Did it hit
     *
     * @param probCurr - current probability
     * @param probAll  - sum probability
     * @return Is hit
     */
    public static boolean isHit(int probCurr, int probAll) {
        if (probCurr < 0 || probAll <= 0) {
            throw new IllegalArgumentException("probCurr or prob <= 0, curr:" + probCurr + " all:" + probAll);
        }
        if (probCurr == 0) {
            return false;
        }
        if (probCurr >= probAll) {
            return true;
        }
        int hitValue = randomValueFromOne(probAll);
        return probCurr >= hitValue;
    }

    /**
     * Randomly select an element from the collection with equal probability
     *
     * @param list - All elements
     * @param <T>  - Element type
     * @return - Element
     */
    public static <T> T randomSelectOne(List<T> list) {
        checkList(list);
        int random = randomValue(0, list.size() - 1);
        return list.get(random);
    }

    /**
     * Randomly select some elements from the collection with equal probability
     *
     * @param list    - All elements
     * @param num     - Select num
     * @param putBack - Whether to put back the element
     * @param <T>     - Element type
     * @return - Elements
     */
    public static <T> List<T> randomSelect(List<T> list, int num, boolean putBack) {
        checkList(list);
        checkSelectNum(putBack, num, list.size());
        if (num <= 0) {
            return Collections.emptyList();
        }
        if (!putBack && num >= list.size()) {
            return List.copyOf(list);
        }
        return randomSelect(list, v -> 1, num, putBack);
    }

    /**
     * Randomly select some elements from the collection
     *
     * @param list     - All elements
     * @param probFunc - Extraction probability replacement function of elements
     * @param num      - Select num
     * @param putBack  - Whether to put back the element
     * @param <T>      - Element type
     * @return - Elements
     */
    public static <T> List<T> randomSelect(List<T> list, Function<T, Integer> probFunc, int num, boolean putBack) {
        checkList(list);
        checkSelectNum(putBack, num, list.size());
        if (num <= 0) {
            return Collections.emptyList();
        }
        if (!putBack && num >= list.size()) {
            return List.copyOf(list);
        }
        List<Integer> probList = list.stream()
                .map(probFunc)
                .collect(Collectors.toList());
        List<T> hits = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            int index = randomProbabilityIndex(probList);
            hits.add(list.get(index));
            if (!putBack) {
                probList.set(index, 0);
            }
        }

        return hits;
    }

    private static void checkSelectNum(boolean putBack, int num, int listSize) {
        if (putBack) {
            return;
        }
        Assert.isTrue(num <= listSize, "Random error, Draw num > contain size");
    }

    /**
     * Extract an element from the probability collection
     *
     * @param probList - Probability collection
     * @return - The index of element in collection
     */
    public static int randomProbabilityIndex(List<Integer> probList) {
        int sum = 0;
        for (Integer value : probList) {
            if (value < 0) {
                throw new IllegalArgumentException("probability value < 0");
            }
            sum += value;
        }
        if (sum <= 0) {
            throw new IllegalArgumentException("sum probability <= 0");
        }
        int random = randomValueFromOne(sum);
        int computeValue = 0;
        for (int i = 0; i < probList.size(); i++) {
            computeValue += probList.get(i);
            if (random <= computeValue) {
                return i;
            }
        }
        // Never execute
        throw new IllegalArgumentException("Get random index error");
    }

    private static int randomValueFromOne(int max) {
        return randomValue(1, max);
    }

    private static <T> void checkList(List<T> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            throw new IllegalArgumentException("Random error, list is empty.");
        }
    }

    private static ThreadLocalRandom getRandom() {
        var random = ThreadLocalRandom.current();
        int next = random.nextInt(TEN_THOUSAND_INT);
        if (next <= 1000) {
            // Change seed
            random.setSeed(next);
        }
        return random;
    }

    private RandomUtils() {
    }
}
