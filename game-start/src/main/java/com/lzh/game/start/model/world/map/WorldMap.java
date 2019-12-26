package com.lzh.game.start.model.world.map;

import com.lzh.game.start.model.world.Position;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class WorldMap {

    private byte[][] map;

    private int mapId;

    private int width;

    private int length;

    @Getter
    private List<Position> noBlock;

    private WorldMap(byte[][] map, int mapId) {
        this.map = map;
        this.mapId = mapId;
    }

    public byte getState(int x, int y) {
        return map[x][y];
    }

    public int getMapId() {
        return mapId;
    }

    public boolean isBlock(int x, int y) {
        return isBlock(getState(x,y));
    }

    private boolean isBlock(byte state) {
        return state == 1;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public static WorldMap of(byte[][] map, int mapId, int width, int length) {
        WorldMap worldMap = new WorldMap(map, mapId);
        worldMap.width = width;
        worldMap.length = length;
        worldMap.noBlock = worldMap.parseNoBlock(map);
        return worldMap;
    }

    private List<Position> parseNoBlock(byte[][] map) {
        List<Position> positions = new ArrayList<>();

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (!isBlock(x,y)) {
                    positions.add(Position.of(x,y));
                }
            }
        }
        return positions;
    }
}
