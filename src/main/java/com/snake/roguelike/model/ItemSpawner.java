package com.snake.roguelike.model;

import java.util.*;

public class ItemSpawner {
    private static final int MAX_ATTEMPTS = 30;

    private static final int NORMAL_APPLE_WEIGHT = 70;
    private static final int CURSED_APPLE_WEIGHT = 15;
    private static final int BONUS_BERRY_WEIGHT = 10;
    private static final int MYSTERY_ORB_WEIGHT = 5;

    private final Random random;

    public ItemSpawner(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    public  Optional<Point> findSpawnPosition(int width, int height, Snake snake, Set<Point> obstacles, Set<Point> activeItems) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }

        Objects.requireNonNull(snake);
        Objects.requireNonNull(obstacles);
        Objects.requireNonNull(activeItems);

        java.util.function.Predicate<Point> occupiedCheck = point -> snake.occupies(point)
                                                                            || obstacles.contains(point)
                                                                            || activeItems.contains(point);
        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            Point candidate = new Point(random.nextInt(width), random.nextInt(height));

            if (!occupiedCheck.test(candidate)) {
                return Optional.of(candidate);
            }
        }

        List<Point> freeCells = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point point = new Point(x, y);

                if (!occupiedCheck.test(point))
                    freeCells.add(point);
            }
        }

        if (freeCells.isEmpty())
            return Optional.empty();

        return Optional.of(freeCells.get(random.nextInt(freeCells.size())));
    }

    public Optional<Item> spawnRandomItem(int width, int height, Snake snake, Set<Point> obstacles, Set<Point> activeItems) {
        Optional<Point> position = findSpawnPosition(width, height, snake, obstacles, activeItems);

        if (position.isEmpty())
            return Optional.empty();

        return Optional.of(createRandomItem(position.get()));
    }

    private Item createRandomItem(Point position) {
        int roll = random.nextInt(100);

        if (roll < NORMAL_APPLE_WEIGHT)
            return new Item.NormalApple(position);

        if (roll < NORMAL_APPLE_WEIGHT + CURSED_APPLE_WEIGHT) {
            List<DebuffType> commands = List.of(
                    DebuffType.INVERTED_CONTROLS,
                    DebuffType.ICE_DRIFT
            );

            DebuffType debuff = commands.get(random.nextInt(2));

            return new Item.CursedApple(position, debuff, 40);
        }

        if (roll < NORMAL_APPLE_WEIGHT + CURSED_APPLE_WEIGHT + BONUS_BERRY_WEIGHT) {
            List<BonusType> commands = List.of(
                    BonusType.GHOST_MODE,
                    BonusType.CHRONO_SLOW
            );

            BonusType bonus = commands.get(random.nextInt(2));

            return new Item.BonusBerry(position, bonus, 50);
        }

        return new Item.MysteryOrb(position);
    }
}