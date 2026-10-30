package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LevelGeneratorTest {
    Point snakeSpawn = new Point(10, 10);
    LevelGenerator level = new LevelGenerator(20, 20);
    Set<Point> obstacles = level.generateObstacles(20, snakeSpawn);


    @Test
    void testObstaclesStayWithinBounds(){
        for(Point p : obstacles){
            assertTrue(p.isWithinBounds(20, 20), "The point have to be in bounds");
        }
        assertFalse(obstacles.isEmpty());
    }

    @Test
    void testSafeZoneRemainsEmpty(){
        for(Point p : obstacles){
            assertTrue(Math.abs(p.x() - snakeSpawn.x()) > 2 || Math.abs(p.y() - snakeSpawn.y()) > 2);
        }
    }

    @Test
    void testDeterministicGenerationWithSeed(){
        LevelGenerator generatorA = new LevelGenerator(20, 20, new Random(42L));
        LevelGenerator generatorB = new LevelGenerator(20, 20, new Random(42L));

        Set<Point> A = generatorA.generateObstacles(20, snakeSpawn);
        Set<Point> B = generatorB.generateObstacles(20, snakeSpawn);

        assertEquals(A, B);
    }

    @Test
    void testZeroOrNegativeObstacleCount(){
        Set<Point> A = level.generateObstacles(0, snakeSpawn);
        Set<Point> B = level.generateObstacles(-1, snakeSpawn);

        assertTrue(A.isEmpty());
        assertTrue(B.isEmpty());
    }

    @Test
    void testCornerSpawn(){
        Point cornerSpawn = new Point(0, 0);
        Set<Point> cornerObstacles = level.generateObstacles(20, cornerSpawn);
        for(Point p : cornerObstacles){
            assertTrue(p.isWithinBounds(20, 20) && (p.x() > 2 || p.y() > 2));
        }
    }

    @Test
    void testImmutability(){
        assertThrows(UnsupportedOperationException.class, () -> obstacles.add(new Point(0, 0)));
    }

    @Test
    void testCleanMapIsFullConnected(){
        assertTrue(level.isFullyConnected(Set.of(), snakeSpawn));
    }

    @Test
    void testDisconnectedMapReturnsFalse(){
        Set<Point> walls = new HashSet<>();
        for(int y = 0; y < 20; y++){
            walls.add(new Point(5, y));
        }

        assertFalse(level.isFullyConnected(walls, snakeSpawn));
    }

    @Test
    void testIsolatedSingleCellReturnsFalse(){
        Set<Point> walls = new HashSet<>();

        walls.add(new Point(1, 2));
        walls.add(new Point(3, 2));
        walls.add(new Point(2, 1));
        walls.add(new Point(2, 3));

        assertFalse(level.isFullyConnected(walls, snakeSpawn));
    }

    @Test
    void testDeadEndDetectionInsideMap(){
        Set<Point> walls = new HashSet<>();

        walls.add(new Point(4, 5));
        walls.add(new Point(6, 5));
        walls.add(new Point(5, 4));

        assertTrue(level.hasDeadEnds(walls));
    }

    @Test
    void testDeadEndDetectionInCorner(){
        Set<Point> walls = new HashSet<>();

        walls.add(new Point(1, 0));

        assertTrue(level.hasDeadEnds(walls));
    }

    @Test
    void testCleanMapHasNoDeadEnds(){
        Set<Point> walls = new HashSet<>();

        assertFalse(level.hasDeadEnds(walls));
    }

     @Test
    void testGenerateValidLevelProducesConsistentArena(){
        Set<Point> validLevel = level.generateValidLevel(20, snakeSpawn, 50);

        assertNotEquals(null, validLevel);
        assertFalse(validLevel.isEmpty());
        assertFalse(level.hasDeadEnds(validLevel));
        assertTrue(level.isFullyConnected(validLevel, snakeSpawn));
     }

     @Test
    void testGenerateValidLevelPerformanceUnder300ms(){
         long start = System.currentTimeMillis();

         for(int i = 0; i < 30; i++)
            level.generateValidLevel(25, snakeSpawn, 30);

         long end = System.currentTimeMillis();

         long res = end - start;

         assertTrue(res < 300);
     }

     @Test
    void testInvalidArgumentsInGenerateValidLevel(){
        assertThrows(NullPointerException.class, () -> level.generateValidLevel(25, null, 30));
        assertThrows(IllegalArgumentException.class, () -> level.generateValidLevel(25, snakeSpawn, 0));
        assertThrows(IllegalArgumentException.class, () -> level.generateValidLevel(25, snakeSpawn, -5));
     }
}