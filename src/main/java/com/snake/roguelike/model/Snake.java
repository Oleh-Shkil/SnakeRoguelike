package com.snake.roguelike.model;

import java.util.*;

public class Snake {
    private final Deque<Point> body;
    private final Set<Point> bodySet;
    private Direction direction;

    public Snake(Point initialHead, int initialLength, Direction direction){
        Objects.requireNonNull(initialHead);
        Objects.requireNonNull(direction);
        if (initialLength < 1)
            throw new IllegalArgumentException("Initial length must be at least 1");
        
        this.body = new ArrayDeque<>();
        this.bodySet = new HashSet<>();
        this.direction = direction;

        Point current = initialHead;
        Direction inverted = direction.inverted();

        for (int i = 0; i < initialLength; i++) {
            body.addLast(current);
            bodySet.add(current);

            current = current.move(inverted);
        }
    }

    public Point getHead(){
        return body.getFirst();
    }

    public Point getTail(){
        return body.getLast();
    }

    public int getLength(){
        return body.size();
    }

    public Direction getDirection(){
        return this.direction;
    }

    public boolean occupies(Point point) {
        return bodySet.contains(point);
    }

    public List<Point> getSegments(){
        return List.copyOf(body);
    }

    public void setDirection(Direction newDirection){
        if(newDirection == null)
            throw new NullPointerException("Direction must not be null");
        if(!direction.isOpposite(newDirection))
            this.direction = newDirection;
    }

    public void move(Point newHead, boolean grow){
        Objects.requireNonNull(newHead, "New head cannot be null");
        if(!grow){
            Point removedTail = body.removeLast();
            bodySet.remove(removedTail);
        }
        body.addFirst(newHead);
        bodySet.add(newHead);
    }

    public boolean willCollideWithSelf(Point nextHead, boolean willGrow){
        if (!occupies(nextHead)) {
            return false;
        }
        if (nextHead.equals(getTail())) {
            return willGrow;
        }
        return true;
    }

    public void shrink(int segmentsToRemove, int minLength){
        for(int i = 1; i <= segmentsToRemove; i++){
            if(body.size() > minLength){
                Point removed = body.removeLast();
                bodySet.remove(removed);
            }
        }
    }
}