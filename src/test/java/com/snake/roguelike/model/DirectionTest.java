package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {
    @Test
    void shouldIdentifyOppositeDirections(){
        assertTrue(Direction.UP.isOpposite(Direction.DOWN));
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT));

        assertFalse(Direction.UP.isOpposite(Direction.LEFT));

        assertFalse(Direction.UP.isOpposite(Direction.UP));
    }

    @Test
    void shouldReturnFalseWhenCheckingOppositeWithNull(){
        assertFalse(Direction.UP.isOpposite(null));
    }

    @Test
    void shouldInvertDirections(){
        assertEquals(Direction.DOWN, Direction.UP.inverted());
        assertEquals(Direction.UP, Direction.DOWN.inverted());
        assertEquals(Direction.LEFT, Direction.RIGHT.inverted());
        assertEquals(Direction.RIGHT, Direction.LEFT.inverted());
    }

    @Test
    void shouldMaintainIdempotencyOfDoubleInversion(){
        for(Direction dir : Direction.values()){
            assertEquals(dir, dir.inverted().inverted());
        }
    }
}
