package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GameWorldTest {

    @Test
    void testInitialState(){
        LevelGenerator level = new LevelGenerator(20, 20);
        GameWorld world = GameWorld.createDefault(20, 20, level, 30);

        assertEquals(20, world.getWidth());
        assertEquals(20, world.getHeight());
        assertEquals(new Point(10, 10), world.getSnake().getHead());
        assertEquals(0, world.getScore());
    }

    @Test
    void testFreeMovement(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        while(world.getTicksElapsed() < 5){
            world.tick(Direction.RIGHT);
            assertNotEquals(GameState.GAME_OVER, world.getState());
        }
    }

    @Test
    void testOutOfBoundsAllWalls(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        while(world.getState() == GameState.PLAYING ){
            world.tick(Direction.RIGHT);
        }
        assertEquals(GameState.GAME_OVER, world.getState());
    }

    @Test
    void testObstacleCollision(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        obstacles.add(new Point(11, 10));
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        world.tick(Direction.RIGHT);

        assertEquals(GameState.GAME_OVER, world.getState());
    }

    @Test
    void testSelfCollision(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 5, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20 , snake, obstacles, GameState.READY);

        world.start();

        world.tick(Direction.DOWN);
        world.tick(Direction.LEFT);
        world.tick(Direction.UP);

        assertEquals(GameState.GAME_OVER, world.getState());
    }

    @Test
    void testTailChasingAllowed(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 4, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        world.tick(Direction.DOWN);
        world.tick(Direction.LEFT);
        world.tick(Direction.UP);

        assertEquals(GameState.PLAYING, world.getState());
    }

    @Test
    void testReverseDirectionIgnored(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        world.tick(Direction.LEFT);

        assertEquals(new Point(11, 10), world.getSnake().getHead());
    }

    @Test
    void testPauseFreezesTick(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        world.togglePause();
        world.tick(Direction.RIGHT);

        assertEquals(snakeSpawn, world.getSnake().getHead());
        assertEquals(0, world.getTicksElapsed());
        assertEquals(GameState.PAUSED, world.getState());

        world.togglePause();

        assertEquals(GameState.PLAYING, world.getState());
    }

    @Test
    void testImmutabilityOfObstacles(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        assertThrows(UnsupportedOperationException.class, () -> world.getObstacles().add(new Point(1, 1)));
    }

    @Test
    void testNullDirectionKeepsCurrent(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        world.tick(null);

        assertEquals(new Point(11, 10), world.getSnake().getHead());
        assertEquals(1, world.getTicksElapsed());
        assertEquals(GameState.PLAYING, world.getState());
    }

    @Test
    void testConstructionException(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();

        assertThrows(IllegalArgumentException.class, () -> new GameWorld(9, 20, snake, obstacles, GameState.READY));
        assertThrows(NullPointerException.class, () -> new GameWorld(20, 20, null, obstacles, GameState.READY));
        assertThrows(NullPointerException.class, () -> new GameWorld(20, 20, snake, null, GameState.READY));
        assertThrows(NullPointerException.class, () -> new GameWorld(20, 20, snake, obstacles, null));
    }

    @Test
    void testGameOverStopsSimulation(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        Set<Point> obstacles = new HashSet<>();
        obstacles.add(new Point(12, 10));
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        world.start();

        world.tick(Direction.RIGHT);
        world.tick(Direction.RIGHT);
        world.tick(Direction.UP);
        world.togglePause();

        assertEquals(GameState.GAME_OVER, world.getState());
        assertEquals(1, world.getTicksElapsed());
    }

    @Test
    void testRestart(){
        Point snakeSpawn = new Point(10, 10);
        Snake snake = new Snake(snakeSpawn, 3, Direction.RIGHT);
        LevelGenerator level = new LevelGenerator(20, 20);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = new GameWorld(20, 20, snake, obstacles, GameState.READY);

        GameWorld newWorld = world.restart(level, 10);
        newWorld.start();

        assertNotSame(newWorld, world);
        assertSame(GameState.PLAYING, newWorld.getState());
    }
}