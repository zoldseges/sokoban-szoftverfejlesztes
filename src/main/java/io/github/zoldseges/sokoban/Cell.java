package io.github.zoldseges.sokoban;

public enum Cell {
    VOID(Terrain.VOID, DynamicElement.NONE), //TODO: we might not even need this.
    FLOOR(Terrain.FLOOR, DynamicElement.NONE),
    WALL(Terrain.WALL, DynamicElement.NONE),
    GOAL(Terrain.GOAL, DynamicElement.NONE),
    BOX(Terrain.FLOOR, DynamicElement.BOX),
    BOX_ON_GOAL(Terrain.GOAL, DynamicElement.BOX),
    PLAYER(Terrain.FLOOR, DynamicElement.PLAYER),
    PLAYER_ON_GOAL(Terrain.GOAL, DynamicElement.PLAYER);

    private final Terrain terrain;
    private final DynamicElement dynamicElement;

    Cell(Terrain terrain, DynamicElement dynamicElement) {
        this.terrain = terrain;
        this.dynamicElement = dynamicElement;
    }


    boolean isBlocking() {
        return (this.terrain == Terrain.WALL
                || this.terrain == Terrain.VOID);
    }
    boolean isGoal() {
        return this.terrain == Terrain.GOAL;
    }
    boolean hasPlayer() {
        return this.dynamicElement == DynamicElement.PLAYER;
    }
    boolean hasBox() {
        return this.dynamicElement == DynamicElement.BOX;
    }

    Cell withoutPlayerOrBox() {
        return this.withDynamicElement(DynamicElement.NONE);
    }

    Cell withPlayer() {
        return this.withDynamicElement(DynamicElement.PLAYER);
    }

    Cell withBox() {
        return this.withDynamicElement(DynamicElement.BOX);
    }

    private enum Terrain {
        VOID,
        FLOOR,
        WALL,
        GOAL,
    }

    private enum DynamicElement {
        NONE,
        BOX,
        PLAYER
    }

    private Cell withDynamicElement(DynamicElement dynamicElement) {
        //NOTE: we can't just `new Cell(this.terrain, dynamicElement)` here. Enums can't be instantiated.
        if (this.terrain == Terrain.FLOOR || this.terrain == Terrain.GOAL) {
            return switch (dynamicElement) {
                case NONE: {
                    yield this.isGoal() ? GOAL : FLOOR;
                }
                case BOX: {
                    yield this.isGoal() ? BOX_ON_GOAL : BOX;
                }
                case PLAYER: {
                    yield this.isGoal() ? PLAYER_ON_GOAL : PLAYER;
                }
            };
        } else {
            throw new IllegalStateException(
                    "no Cell for (%s, %s)".formatted(this.terrain, dynamicElement)
            );
        }
    }

}
