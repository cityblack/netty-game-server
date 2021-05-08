package com.lzh.game.framework.rank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class RankTest {

    public static void main(String[] args) {

        SortedSet<Rank> set = new ConcurrentSkipListSet<>();
        long time = System.currentTimeMillis();
        Rank first = new Rank(1, 100, time + 1);
        Rank second = new Rank(2, 100, time - 1);
        Rank tree = new Rank(3, 200, time + 1);
        Rank four = new Rank(4, 50, time + 1);

        set.add(first);
        set.add(second);
        set.add(tree);
        set.add(four);

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Rank implements Comparable<Rank> {
        private int id;
        private long score;
        private long time;

        @Override
        public int compareTo(Rank o) {
            if (this.score == o.score) {
                return (int) (this.time - o.time);
            }
            return (int)(o.score - this.score);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rank rank = (Rank) o;
            return id == rank.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
