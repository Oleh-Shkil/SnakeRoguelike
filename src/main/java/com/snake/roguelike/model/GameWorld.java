package com.snake.roguelike.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.snake.roguelike.model.GameState.*;

public class GameWorld {
    private final int width;
    private final int height;
    private final Snake snake;
    private final Set<Point> obstacles;
    private GameState state;
    private int score;
    private long ticksElapsed;

    GameWorld(int width, int height, Snake snake, Set<Point> obstacles, GameState initialState){
        if(width < 10)
            throw new IllegalArgumentException("width must be at least 10");

        if(height < 10)
            throw new IllegalArgumentException("height must be at least 10");

        this.snake = Objects.requireNonNull(snake, "snake must not be null");
        this.obstacles = Set.copyOf(Objects.requireNonNull(obstacles, "obstacles must not be null"));
        this.state = Objects.requireNonNull(initialState, "initial state must not be null");
        this.width = width;
        this.height = height;
    }

    public static GameWorld createDefault(int width, int height, LevelGenerator generator, int obstaclesCount){
        Point center = new Point(width / 2, height / 2);

        Snake snake = new Snake(center, 3, Direction.RIGHT);
        Set<Point> validObstacles = generator.generateValidLevel(obstaclesCount, center, 100);

        return new GameWorld(width, height, snake, validObstacles, GameState.READY);
    }

    public void tick(Direction requestedDirection){
        if(this.state != PLAYING)
            return;

        Direction effectiveDirection = snake.getDirection();
        if(requestedDirection != null && !snake.getDirection().isOpposite(requestedDirection)){
            effectiveDirection = requestedDirection;
            snake.setDirection(requestedDirection);
        }

        Point currentHead = snake.getHead();
        Point nextHead = currentHead.move(effectiveDirection);
        if(!nextHead.isWithinBounds(this.width, this.height)){
            this.state = GAME_OVER;
            return;
        }

        if(this.obstacles.contains(nextHead)){
            this.state = GAME_OVER;
            return;
        }

        if(snake.willCollideWithSelf(nextHead, false)){
            this.state = GAME_OVER;
            return;
        }

        snake.move(nextHead, false);
        this.ticksElapsed = this.ticksElapsed + 1;
    }

    public void start(){
        if(this.state == READY)
            this.state = PLAYING;
    }

    public void togglePause(){
        if(this.state == PLAYING)
            this.state = PAUSED;
        else if(this.state == PAUSED)
            this.state = PLAYING;
    }

    public GameWorld restart(LevelGenerator generator, int obstacleCount){
        GameWorld newWorld = createDefault(width, height, generator, obstacleCount);
        newWorld.start();
        return newWorld;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public GameState getState(){
        return this.state;
    }

    public int getScore(){
        return this.score;
    }

    public long getTicksElapsed(){
        return this.ticksElapsed;
    }

    public Snake getSnake(){
        return this.snake;
    }

    public Set<Point> getObstacles(){
        return Collections.unmodifiableSet(this.obstacles);
    }
}