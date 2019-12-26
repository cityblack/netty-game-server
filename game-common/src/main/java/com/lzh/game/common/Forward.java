package com.lzh.game.common;

public enum Forward {

    UP,
    DOWN,
    LEFT,
    RIGHT,
    ;

    /**
     * 反方向
     * @param forward
     * @return
     */
    public static Forward oppositeForward(Forward forward) {

        switch (forward) {
            case UP: return Forward.DOWN;
            case DOWN: return Forward.UP;
            case LEFT: return Forward.RIGHT;
            case RIGHT: return Forward.LEFT;
            default:
                return forward;
        }
    }
}
