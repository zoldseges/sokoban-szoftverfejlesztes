package io.github.zoldseges.sokoban.core;

public class State {
    Grid grid;
    Pos playerPosition;

    public State(Level level) {
        this.grid = level.copyGrid();
        this.playerPosition = level.playerStartPosition;
    }

    public Grid getGrid() {
        return grid;
    }

    public boolean apply(Command command) {
        return switch (command) {
            case LEFT  -> movePlayer(Direction.LEFT);
            case RIGHT -> movePlayer(Direction.RIGHT);
            case UP    -> movePlayer(Direction.UP);
            case DOWN  -> movePlayer(Direction.DOWN);
        };
    }

    public enum Command {
        LEFT,
        RIGHT,
        UP,
        DOWN,
    }

    /** @return {@code true} if move could be applied */
    private boolean movePlayer(Direction dir) {
        Pos landingPos = neighbour(this.playerPosition, dir);
        Cell currentCell = this.grid.get(this.playerPosition);
        Cell landingCell = this.grid.get(landingPos);
        if (landingCell.isBlocking()) {
            return false;
        } else {
            if (landingCell.hasBox()) {
                Pos beyondBoxPos = neighbour(landingPos, dir);
                Cell beyondBoxCell = this.grid.get(beyondBoxPos);
                if (beyondBoxCell.isBlocking()
                        || beyondBoxCell.hasBox()) {
                    return false;
                } else {
                    this.grid.set(beyondBoxPos, beyondBoxCell.withBox());
                }
            }
            this.grid.set(this.playerPosition, currentCell.withoutPlayerOrBox());
            this.grid.set(landingPos, landingCell.withPlayer());
            this.playerPosition = landingPos;
            return true;
        }
    }

    private static Pos neighbour(Pos from, Direction dir) {
        return new Pos(from.x() + dir.dx,
                from.y() + dir.dy
        );
    }

    private enum Direction {
        LEFT(-1, 0),
        RIGHT(1, 0),
        UP(0, -1),
        DOWN(0, 1);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}
