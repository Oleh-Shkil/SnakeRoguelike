package com.snake.roguelike.model;

import java.util.Objects;

public record Point(int x, int y) {
    public Point move(Direction direction){
        Objects.requireNonNull(direction, "Direction must not be null");
        return new Point(this.x + direction.getDx(), this.y + direction.getDy());
    }

    public Point translate(int deltaX, int deltaY){
        return new Point(this.x + deltaX, this.y + deltaY);
    }

    public int manhattanDistance(Point other){
        Objects.requireNonNull(other, "Other must not be null");
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    public boolean isWithinBounds(int width, int height){
        return (this.x >= 0 && this.x < width) && (this.y >= 0 && this.y <height);
    }
}