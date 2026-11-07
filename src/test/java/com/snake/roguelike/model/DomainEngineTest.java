package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class DomainEngineTest {
    private static final int RUNS_COUNT = 500;
    private static final int width = 20;
    private static final int height = 20;
    private static final int obstacles = 25;
    private static final Point spawn = new Point(10, 10);
    private static final int MAX_RETRIES = 100;
    private static final long MASTER_SEED = 0xCAFEBABEL;

    private static final int steps = 100;

    private static final int THREAD_COUNT = 4;
    private static final int TICKS_PER_THREAD = 1000;

    @Test
    void stressBenchmark500Maps() {
        Random master = new Random(MASTER_SEED);

        long startTime = System.currentTimeMillis();

        for(int i = 1; i <= RUNS_COUNT; i++) {
            long seed = master.nextLong();

            LevelGenerator generator = new LevelGenerator(width, height, new Random(seed));
            Set<Point> walls = generator.generateValidLevel(obstacles, spawn, MAX_RETRIES);

            assertNotNull(walls, "Iteration " + i + ": walls is null");
            assertFalse(walls.isEmpty(), "Iteration " + i + ": empty map");
            assertFalse(generator.hasDeadEnds(walls), "Iteration " + i + ":dead ends detected");
            assertTrue(generator.isFullyConnected(walls, spawn), "Iteration " + i + ": map is disconnected");

            for(Point wall : walls) {
                int dx = wall.x() - spawn.x();
                int dy = wall.y() - spawn.y();

                boolean outsideSafeZone = Math.abs(dx) > LevelGenerator.SAFE_ZONE_RADIUS || Math.abs(dy) > LevelGenerator.SAFE_ZONE_RADIUS;

                assertTrue(outsideSafeZone, "Iteration " + i + ": wall inside spawn safe zone: " + wall);
            }
        }
        long totalTime = System.currentTimeMillis() - startTime;
        double avgTime = (double) totalTime / RUNS_COUNT;

        assertTrue(avgTime < 5, "Average generation time exceeded 5 ms");
        assertTrue(totalTime < 3000, "Total generation time exceeded 3000 ms");
    }

    @Test
    void SimulationDeterminismTest() {
        Set<Point> obstacles = Set.of(
                new Point(3, 3),
                new Point(5, 5),
                new Point(7, 7),
                new Point(9, 9),
                new Point(11, 11),
                new Point(13, 13),
                new Point(15, 15),
                new Point(4, 12),
                new Point(8, 14),
                new Point(16, 4)
        );

        Snake snake1 = new Snake(new Point(10, 10), 3, Direction.RIGHT);
        Snake snake2 = new Snake(new Point(10, 10), 3, Direction.RIGHT);

        GameWorld worldA = new GameWorld(width, height, snake1, obstacles, GameState.READY);
        GameWorld worldB = new GameWorld(width, height, snake2, obstacles, GameState.READY);

        worldA.start();
        worldB.start();

        assertEquals(worldA.getState(), worldB.getState());

        List<Direction> commands = List.of(
                Direction.UP,
                Direction.RIGHT,
                Direction.DOWN,
                Direction.LEFT
        );

        for(int i = 0; i < steps; i++) {
            Direction direction = commands.get(i % commands.size());

            worldA.tick(direction);
            worldB.tick(direction);

            assertEquals(worldA.getState(), worldB.getState(), "State mismatch at tick " + (i + 1));
            assertEquals(worldA.getSnake().getHead(), worldB.getSnake().getHead());
            assertEquals(worldA.getTicksElapsed(), worldB.getTicksElapsed());
            assertEquals(worldA.getScore(), worldB.getScore());
            assertEquals(worldA.getSnake().getSegments(), worldB.getSnake().getSegments());
            assertEquals(GameState.PLAYING, worldA.getState());
        }
    }

    @Test
    void shouldSurvive10000TicksWithRepeatedRestarts() {
        LevelGenerator generator = new LevelGenerator(width, height);
        GameWorld world = GameWorld.createDefault(width, height, generator, 20);

        world.start();

        int deathCount = 0;
        int totalTicks = 0;
        List<Direction> commands = List.of(
                Direction.UP,
                Direction.RIGHT,
                Direction.DOWN,
                Direction.LEFT
        );

        while(totalTicks < 10000) {
            Direction direction = commands.get(((totalTicks * deathCount) % commands.size()));

            world.tick(direction);
            totalTicks++;

            if(world.getState() == GameState.GAME_OVER){
                deathCount++;
                world = world.restart(generator, 20);
                world.start();
            }
        }
        assertTrue(deathCount > 0);
        assertEquals(10000, totalTicks);
        assertTrue(world.getTicksElapsed() > 0);
        assertEquals(GameState.PLAYING, world.getState());
    }

    @Test
    void multiThreadedWorldIsolationTest() throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startGun = new CountDownLatch(1);

        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadIndex = i;

                Runnable task = () -> {
                    try {
                        startGun.await();

                        Random localRandom = new Random(MASTER_SEED + threadIndex * 1000L);
                        LevelGenerator generator = new LevelGenerator(width, height, localRandom);
                        GameWorld world = GameWorld.createDefault(width, height, generator, 15);

                        world.start();

                        Direction[] commands = {
                                Direction.UP,
                                Direction.RIGHT,
                                Direction.DOWN,
                                Direction.LEFT
                        };

                        for (int tick = 1; tick <= TICKS_PER_THREAD; tick++) {
                            Direction direction = commands[
                                    localRandom.nextInt(commands.length)
                                    ];

                            world.tick(direction);

                            if (world.getState() == GameState.GAME_OVER) {
                                world = world.restart(generator, 15);
                                world.start();
                            }

                            assertNotNull(world.getSnake().getHead());
                            assertEquals(width, world.getWidth());
                            assertEquals(height, world.getHeight());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(
                                "Simulation thread was interrupted", e
                        );
                    }
                };

                futures.add(executor.submit(task));
            }

            startGun.countDown();

            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {

            executor.shutdown();

            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
}
