package io.github.zoldseges.sokoban.core;

public class State {

    private final Grid grid;
    private Pos playerPosition;

    public State(Level level) {
        this.grid = level.copyGrid();
        this.playerPosition = level.playerStartPosition;
    }

    /** @return {@code true} if move could be applied */
    public boolean apply(Direction dir) {
        Pos landingPos = playerPosition.neighbour(dir);
        Cell currentCell = this.grid.get(this.playerPosition);
        Cell landingCell = this.grid.get(landingPos);

        if (landingCell.isBlocking()) {
            return false;
        } else {
            if (landingCell.hasBox()) {
                Pos beyondBoxPos = landingPos.neighbour(dir);
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
}
