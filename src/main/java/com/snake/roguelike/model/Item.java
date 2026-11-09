package com.snake.roguelike.model;

import java.util.Objects;

public sealed interface Item permits Item.NormalApple, Item.CursedApple, Item.BonusBerry, Item.MysteryOrb{
    Point position();

    int scoreBonus();

    int growthSegments();

    record NormalApple(Point position) implements Item {

        public NormalApple {
            Objects.requireNonNull(position, "position cannot be null");
        }

        @Override
        public int scoreBonus() {
            return 10;
        }

        @Override
        public int growthSegments() {
            return 1;
        }
    }

    record CursedApple(Point position, DebuffType debuff, int durationTicks) implements Item {

        public CursedApple {
            Objects.requireNonNull(position, "position cannot be null");
        }

        @Override
        public int scoreBonus() {
            return 30;
        }

        @Override
        public int growthSegments() {
            return 2;
        }
    }

    record BonusBerry(Point position, BonusType bonus, int durationTicks) implements Item {

        public BonusBerry {
            Objects.requireNonNull(position, "position cannot be null");
        }

        @Override
        public int scoreBonus() {
            return 15;
        }

        @Override
        public int growthSegments() {
            return 0;
        }
    }

    record MysteryOrb(Point position) implements Item {

        public MysteryOrb {
            Objects.requireNonNull(position, "position cannot be null");
        }

        @Override
        public int scoreBonus() {
            return 100;
        }

        @Override
        public int growthSegments() {
            return 1;
        }
    }
}
