package com.snake.roguelike.model;

import java.util.*;


public class LevelGenerator {
    private final int width;
    private final int height;
    private final Random random;
    public static final int SAFE_ZONE_RADIUS = 2;

    public LevelGenerator(int width, int height, Random random){
        if(width < 10 || height < 10)
            throw new IllegalArgumentException("Width and height must be more then 10");
        Objects.requireNonNull(random, "Random must not be null");

        this.width = width;
        this.height = height;
        this.random = random;
    }

    public LevelGenerator(int width, int height){
        this(width, height, new Random());
    }

    public Set<Point> generateObstacles(int obstacleCount, Point snakeSpawn){
        if(obstacleCount <= 0)
            return Set.of();

        Objects.requireNonNull(snakeSpawn, "snakeSpawn can not be null");

        if(!snakeSpawn.isWithinBounds(width, height))
            throw new IllegalArgumentException("snakeSpawn must be within field bounds");

        Set<Point> obstacles = new HashSet<>();

        int maxAllowedDensity = (int) (width * height * 0.3);
        int effectiveTarget = Math.min(obstacleCount, maxAllowedDensity);
        int maxAttempts = Math.max(100, effectiveTarget * 40);
        int attempts = 0;

        while (obstacles.size() < effectiveTarget && attempts < maxAttempts) {
            attempts++;
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            Point candidate = new Point(x, y);

            int length = 2 + random.nextInt(3);
            int shapeType = random.nextInt(3);

            List<Point> shapePoints = createCandidateShape(candidate, shapeType, length);

            if(canPlaceShape(shapePoints, snakeSpawn, obstacles)){
                  for(Point shapePoint : shapePoints)
                      if(obstacles.size() < effectiveTarget)
                        obstacles.add(shapePoint);
            }
        }
        return Collections.unmodifiableSet(obstacles);
    }

    private boolean isInsideSafeZone(Point point, Point snakeSpawn){
        return Math.abs(point.x() - snakeSpawn.x()) <= SAFE_ZONE_RADIUS && Math.abs(point.y() - snakeSpawn.y()) <= SAFE_ZONE_RADIUS;
    }

    private List<Point> createCandidateShape(Point origin, int shapeType, int length){
        List<Point> points = new ArrayList<>();

        switch(shapeType){
            case 0:// Single point
                points.add(new Point(origin.x(), origin.y()));
                break;

            case 1:// Line
                for(int i = 0; i < length; i++){
                    points.add(new Point(origin.x() + i, origin.y()));
                }
                break;

            case 2:// Angle (L-shape)
                for(int i = 0; i < length; i++){
                    points.add(new Point(origin.x() + i, origin.y()));
                }
                for(int i = 1; i < length; i++){
                    points.add(new Point(origin.x() + length - 1, origin.y() + i));
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown primitive type: " + shapeType);
        }
        return points;
    }

    private boolean canPlaceShape(List<Point> shape, Point snakeSpawn, Set<Point> currentWalls){
        for(Point point : shape){
            if(!point.isWithinBounds(width, height))
                return false;

            if(isInsideSafeZone(point, snakeSpawn))
                return false;

            if(currentWalls.contains(point))
                return false;
        }

        return true;
    }
}
