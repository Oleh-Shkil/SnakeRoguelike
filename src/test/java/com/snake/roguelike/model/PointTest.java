package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

public class PointTest {
    @Test
    void shouldMoveInAllDirections() {
        Point start = new Point(5, 5);
        assertEquals(new Point(5, 4), start.move(Direction.UP));

        assertEquals(new Point(5, 6), start.move(Direction.DOWN));

        assertEquals(new Point(4, 5), start.move(Direction.LEFT));

        assertEquals(new Point(6, 5), start.move(Direction.RIGHT));
    }

    @Test
    void shouldThrowExceptionMovingWithNull() {
        Point point = new Point(0, 0);
        assertThrows(NullPointerException.class, () -> point.move(null));
    }

    @Test
    void shouldTranslateByDeltas() {
        Point point = new Point(10, 10);
        assertEquals(new Point(13,6),
        point.translate(3, -4));
    }

    @Test
    void shouldCalculateManhattanDistance() {
        Point A = new Point(1, 2);
        Point B = new Point(4, 6);

        assertEquals(7, A.manhattanDistance(B));
        assertThrows(NullPointerException.class, () -> A.manhattanDistance(null));
    }

    @Test
    void shouldValidateBoundsCorrectly(){
        int width = 20;
        int height = 10;

        assertTrue(new Point(0, 0).isWithinBounds(width, height));
        assertTrue(new Point(19, 9).isWithinBounds(width, height));

        assertFalse(new Point(-1, 5).isWithinBounds(width, height));
        assertFalse(new Point(5, -1).isWithinBounds(width, height));

        assertFalse(new Point(20, 7).isWithinBounds(width, height));
        assertFalse(new Point(7, 10).isWithinBounds(width, height));

        assertFalse(new Point(15, 13).isWithinBounds(width, height));
    }

    @Test
    void shouldVerifyEqualityAndHashCode(){
        Point p1 = new Point(3, 3);
        Point p2 = new Point(3, 3);

        assertEquals(p1, p2);

        Set<Point> set = new HashSet<>();

        set.add(p1);
        set.add(p2);

        assertEquals(1, set.size());
    }
}