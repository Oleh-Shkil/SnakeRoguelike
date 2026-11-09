package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ItemSpawnerTest {

    @Test
    void testItemSpawn() {
        Snake snake = new Snake(new Point(5, 5), 3, Direction.RIGHT);
        LevelGenerator level = new LevelGenerator(10, 10);
        Set<Point> obstacles = level.generateValidLevel(10, new Point(5, 5), 50);
        GameWorld world = GameWorld.createDefault(10, 10, level, 10);
        ItemSpawner itemSpawner = new ItemSpawner(new Random());
        Set<Point> activeItems = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            Optional<Item> item = itemSpawner.spawnRandomItem(10, 10, snake, obstacles, activeItems);

            assertTrue(item.isPresent());

            Point point = item.get().position();

            assertTrue(point.isWithinBounds(10, 10));
            assertFalse(snake.occupies(point));
            assertFalse(obstacles.contains(point));

            activeItems.add(point);
        }
    }

    @Test
    void testItemInOnePoint() {
        Snake snake = new Snake(new Point(5, 5), 3, Direction.RIGHT);
        LevelGenerator level = new LevelGenerator(10, 10);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = GameWorld.createDefault(10, 10, level, 10);
        ItemSpawner itemSpawner = new ItemSpawner(new Random());
        Set<Point> activeItems = new HashSet<>();

        for (int x = 0; x < world.getWidth(); x++){
            for (int y = 0; y < world.getHeight(); y++){
                if(x == 3 && y == 3)
                    continue;
                obstacles.add(new Point(x, y));
            }
        }

        Optional<Item> item = itemSpawner.spawnRandomItem(10, 10, snake, obstacles, activeItems);

        assertEquals(new Point(3, 3), item.get().position());
    }

    @Test
    void testNoSpace() {
        Snake snake = new Snake(new Point(5, 5), 3, Direction.RIGHT);
        LevelGenerator level = new LevelGenerator(10, 10);
        Set<Point> obstacles = new HashSet<>();
        GameWorld world = GameWorld.createDefault(10, 10, level, 10);
        ItemSpawner itemSpawner = new ItemSpawner(new Random());
        Set<Point> activeItems = new HashSet<>();

        for (int x = 0; x < world.getWidth(); x++){
            for (int y = 0; y < world.getHeight(); y++){
                obstacles.add(new Point(x, y));
            }
        }

        Optional<Item> item = itemSpawner.spawnRandomItem(10, 10, snake, obstacles, activeItems);

        assertEquals(Optional.empty(), item);
    }
}
