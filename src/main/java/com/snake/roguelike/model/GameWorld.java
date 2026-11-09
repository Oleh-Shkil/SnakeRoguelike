package com.snake.roguelike.model;

import java.util.*;

import static com.snake.roguelike.model.GameState.*;
import static java.lang.Math.max;
import static java.util.Collections.emptySet;

public class GameWorld {
    private final int width;
    private final int height;
    private final Snake snake;
    private final Set<Point> obstacles;
    private GameState state;
    private int score;
    private long ticksElapsed;
    private int pendingGrowth;
    private Item currentItem;
    private final ItemSpawner itemSpawner;

    GameWorld(int width, int height, Snake snake, Set<Point> obstacles, GameState initialState, ItemSpawner itemSpawner) {
        if (width < 10)
            throw new IllegalArgumentException("width must be at least 10");

        if (height < 10)
            throw new IllegalArgumentException("height must be at least 10");

        this.snake = Objects.requireNonNull(snake, "snake must not be null");
        this.obstacles = Set.copyOf(Objects.requireNonNull(obstacles, "obstacles must not be null"));
        this.state = Objects.requireNonNull(initialState, "initial state must not be null");
        this.width = width;
        this.height = height;
        this.itemSpawner = itemSpawner;
    }

    GameWorld(int width, int height, Snake snake, Set<Point> obstacles, GameState initialState) {
        this(width, height, snake, obstacles, initialState, new ItemSpawner(new Random()));
    }

    public static GameWorld createDefault(int width, int height, LevelGenerator generator, int obstaclesCount) {
        Point center = new Point(width / 2, height / 2);

        Snake snake = new Snake(center, 3, Direction.RIGHT);
        Set<Point> validObstacles = generator.generateValidLevel(obstaclesCount, center, 100);
        GameWorld world = new GameWorld(width, height, snake, validObstacles, GameState.READY);

        world.spawnInitialItem();
        return world;
    }

    public void tick(Direction requestedDirection) {
        if (this.state != PLAYING)
            return;

        Direction effectiveDirection = snake.getDirection();
        if (requestedDirection != null && !snake.getDirection().isOpposite(requestedDirection)){
            effectiveDirection = requestedDirection;
            snake.setDirection(requestedDirection);
        }

        Point currentHead = snake.getHead();
        Point nextHead = currentHead.move(effectiveDirection);
        if (!nextHead.isWithinBounds(this.width, this.height)) {
            this.state = GAME_OVER;
            return;
        }

        if (this.obstacles.contains(nextHead)) {
            this.state = GAME_OVER;
            return;
        }

        boolean isItemCollected = (currentItem != null && nextHead.equals(currentItem.position()));

        boolean willGrow = false;

        if (isItemCollected){
            if (currentItem.growthSegments() > 0) {
                willGrow = true;
            }
        } else if (pendingGrowth > 0) {
            willGrow = true;
        }

        if (snake.willCollideWithSelf(nextHead, willGrow)) {
            this.state = GAME_OVER;
            return;
        }

        snake.move(nextHead, willGrow);
        this.ticksElapsed = this.ticksElapsed + 1;

        if (isItemCollected) {
            score += currentItem.scoreBonus();

            int extraGrowth = currentItem.growthSegments() - (willGrow ? 1 : 0);
            pendingGrowth += max(0, extraGrowth);

            Set<Point> activeItemsSet = new HashSet<>();

            Optional<Item> newItem = this.itemSpawner.spawnRandomItem(width, height, snake, obstacles, activeItemsSet);

            if (newItem.isPresent()) {
                currentItem = newItem.get();
            } else {
                currentItem = null;
            }
        } else if (willGrow && pendingGrowth > 0) {
            pendingGrowth--;
        }
    }

    public void start() {
        if (this.state == READY)
            this.state = PLAYING;
    }

    public void togglePause() {
        if (this.state == PLAYING)
            this.state = PAUSED;
        else if(this.state == PAUSED)
            this.state = PLAYING;
    }

    public GameWorld restart(LevelGenerator generator, int obstacleCount) {
        GameWorld newWorld = createDefault(width, height, generator, obstacleCount);
        newWorld.start();
        return newWorld;
    }

    void  spawnInitialItem() {
        Optional<Item> item = this.itemSpawner.spawnRandomItem(width, height, snake, obstacles, emptySet());
        this.currentItem = item.orElse(null);
    }

    void setItemForTesting(Item item) {
        this.currentItem = item;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public GameState getState() {
        return this.state;
    }

    public int getScore() {
        return this.score;
    }

    public long getTicksElapsed() {
        return this.ticksElapsed;
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Set<Point> getObstacles() {
        return Collections.unmodifiableSet(this.obstacles);
    }

    public Optional<Item> getCurrentItem() {
        return Optional.ofNullable(this.currentItem);
    }
}