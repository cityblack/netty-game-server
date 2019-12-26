package com.lzh.game.start.util;

import com.lzh.game.start.model.world.Position;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapUtils {
    /**
     * Calculate distance two points
     * @param position1
     * @param position2
     * @return
     */
    public static double distance(Position position1, Position position2) {
        double powerX = Math.pow(position1.getX() - position2.getX(), 2);
        double powerY = Math.pow(position1.getY() - position2.getY(), 2);
        return Math.sqrt(powerX + powerY);
    }

    public static void main(String[] args) {
        int test = 1000000;
        int range = 60000;
        Random random = new Random();
        List<Position> positionList = IntStream.range(0, test)
                .mapToObj(e -> Position.of(random.nextInt(range), random.nextInt(range)))
                .collect(Collectors.toList());
        long time = System.currentTimeMillis();
        for (int i = 1; i < test; i++) {
            MapUtils.distance(positionList.get(i - 1), positionList.get(i));
        }
//        System.out.println(positionList);
        System.out.println((System.currentTimeMillis() - time));
        // System.out.println(MapUtils.distance(p1, p2));
    }
}
