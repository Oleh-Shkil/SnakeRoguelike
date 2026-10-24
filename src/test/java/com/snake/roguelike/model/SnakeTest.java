package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



public class SnakeTest {
    Point head = new Point(5, 5);

    @Test
    void testInitialization(){
        Snake snake = new Snake(head, 3, Direction.RIGHT);

        assertEquals(3, snake.getLength());
        assertEquals(new Point(5, 5), snake.getHead());
        assertEquals(new Point(3, 5), snake.getTail());

        assertTrue(snake.occupies(new Point(5, 5)));
        assertTrue(snake.occupies(new Point(4, 5)));
        assertTrue(snake.occupies(new Point(3, 5)));
        assertFalse(snake.occupies(new Point(6, 5)));
    }

    @Test
    void testConstructorValidation(){
        assertThrows(NullPointerException.class, () -> new Snake(null, 3, Direction.RIGHT));
        assertThrows(NullPointerException.class, () -> new Snake(head, 3, null));
        assertThrows(IllegalArgumentException.class, () -> new Snake(head, 0, Direction.RIGHT));
    }

    @Test
    void testMoveWithoutGrowth(){
        Snake snake = new Snake(head, 3, Direction.RIGHT);

        snake.move(new Point(6, 5), false);

        assertEquals(3, snake.getLength());
        assertEquals(new Point(6, 5), snake.getHead());
        assertEquals(new Point(4, 5), snake.getTail());
        assertFalse(snake.occupies(new Point(3, 5)));
        assertTrue(snake.occupies(new Point(6, 5)));
    }

    @Test
    void testMoveWithGrowth(){
        Snake snake = new Snake(head, 3, Direction.RIGHT);

        snake.move(new Point(6, 5), true);

        assertEquals(4, snake.getLength());
        assertTrue(snake.occupies(new Point(6, 5)));
        assertTrue(snake.occupies(new Point(3, 5)));
    }

    @Test
    void testDirectionControlAnd180Reversal(){
        Snake snake = new Snake(head, 3, Direction.UP);

        //90°
        snake.setDirection(Direction.RIGHT);
        assertEquals(Direction.RIGHT, snake.getDirection());

        //180°
        snake.setDirection(Direction.LEFT);
        assertEquals(Direction.RIGHT, snake.getDirection());

        assertThrows(NullPointerException.class, () -> snake.setDirection(null));
    }

    @Test
    void testSelfCollisionDetection(){
        Snake snake = new Snake(head, 4, Direction.RIGHT);

        assertFalse(snake.willCollideWithSelf(new Point(6, 5), false));
        assertTrue(snake.willCollideWithSelf(new Point(4, 5), false));
        assertFalse(snake.willCollideWithSelf(new Point(2, 5), false));
        assertTrue(snake.willCollideWithSelf(new Point(2, 5), true));
    }

    @Test
    void testShrink(){
        Snake snake = new Snake(head, 5, Direction.RIGHT);

        snake.shrink(3, 3);

        assertEquals(3, snake.getLength());
        assertFalse(snake.occupies(new Point(1, 5)));
        assertFalse(snake.occupies(new Point(2, 5)));
    }

    @Test
    void testImmutabilityOfExposedSegments(){
        Snake snake = new Snake(head, 3, Direction.UP);

        List<Point> segments = snake.getSegments();

        assertThrows(UnsupportedOperationException.class, () -> segments.clear());

        assertThrows(UnsupportedOperationException.class, () -> segments.add(new Point(0, 0)));
    }
}