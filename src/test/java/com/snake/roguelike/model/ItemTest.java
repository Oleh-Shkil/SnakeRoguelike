package com.snake.roguelike.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    private final Point p = new Point(5, 5);

    @Test
    void normalAppleContractTest() {
        Item item = new Item.NormalApple(p);

        assertEquals(10, item.scoreBonus());
        assertEquals(1, item.growthSegments());
        assertEquals(p, item.position());
    }

    @Test
    void cursedAppleContractTest() {
        Item item = new Item.CursedApple(p, DebuffType.ICE_DRIFT, 40);

        assertEquals(30, item.scoreBonus());
        assertEquals(2, item.growthSegments());
        assertEquals(p, item.position());
    }

    @Test
    void bonusBerryContractTest() {
        Item item = new Item.BonusBerry(p, BonusType.CHRONO_SLOW, 50);

        assertEquals(15, item.scoreBonus());
        assertEquals(0, item.growthSegments());
        assertEquals(p, item.position());
    }

    @Test
    void mysteryOrbContractTest() {
        Item item = new Item.MysteryOrb(p);

        assertEquals(100, item.scoreBonus());
        assertEquals(1, item.growthSegments());
        assertEquals(p, item.position());
    }

    public static String getItemType(Item item) {

        return switch (item) {
            case Item.NormalApple a -> "NORMAL";
            case Item.CursedApple c -> "CURSED";
            case Item.BonusBerry b -> "BONUS";
            case Item.MysteryOrb m -> "MYSTERY";
        };
    }
    @Test
    void shouldResolveNormalApple() {
        Item item = new Item.NormalApple(p);

        assertEquals("NORMAL", getItemType(item));
    }

    @Test
    void shouldResolveCursedApple() {
        Item item = new Item.CursedApple(p, DebuffType.ICE_DRIFT, 40);

        assertEquals("CURSED", getItemType(item));
    }

    @Test
    void shouldResolveBonusBerry() {
        Item item = new Item.BonusBerry(p, BonusType.CHRONO_SLOW, 30);

        assertEquals("BONUS", getItemType(item));
    }

    @Test
    void shouldResolveMysteryOrb() {
        Item item = new Item.MysteryOrb(p);

        assertEquals("MYSTERY", getItemType(item));
    }

    @Test
    void nullPointerExceptionNormalApple() {
        assertThrows(NullPointerException.class, () -> new Item.NormalApple(null));
    }

    @Test
    void nullPointerExceptionCursedApple() {
        assertThrows(NullPointerException.class, () -> new Item.CursedApple(null, DebuffType.ICE_DRIFT, 40));
    }

    @Test
    void nullPointerExceptionBonusBerry() {
        assertThrows(NullPointerException.class, () -> new Item.BonusBerry(null, BonusType.CHRONO_SLOW, 30));
    }

    @Test
    void nullPointerExceptionMysteryOrb() {
        assertThrows(NullPointerException.class, () -> new Item.MysteryOrb(null));
    }
}
