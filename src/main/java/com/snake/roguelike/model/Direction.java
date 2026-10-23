package com.snake.roguelike.model;

public enum Direction {
    UP (0, -1),
    DOWN (0, 1),
    LEFT (-1, 0),
    RIGHT (1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
    
    public boolean isOpposite(Direction other){
        if(other == null)
            return false;
        else return this.dx + other.dx == 0 && this.dy + other.dy == 0;
    }

    public Direction inverted(){
        return switch(this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
