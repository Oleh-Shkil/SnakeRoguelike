package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

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
}
