package com.lzh.game.start.model.world;

import lombok.ToString;

import java.util.Objects;

@ToString
public class Position implements Cloneable {

    private int x;

    private int y;

    public int getX() {
        return x;
    }

    private void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    private void setY(int y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        int resource = 17;
        return resource + Objects.hashCode(x) + Objects.hashCode(y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Position)) {
            return false;
        }
        Position o = (Position)obj;
        if (o.x == this.x && o.y == this.y) {
            return true;
        }
        return false;
    }

    public static Position of(int x, int y) {
        Position position = new Position();
        position.setX(x);
        position.setY(y);
        return position;
    }

    @Override
    protected Position clone() {
        Position position = new Position();
        position.setX(this.x);
        position.setY(this.y);
        return position;
    }
}
