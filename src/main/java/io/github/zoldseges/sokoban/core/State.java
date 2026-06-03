package io.github.zoldseges.sokoban.core;

public class State {

    private final Grid grid;
    private Pos playerPosition;

    public State(Level level) {
        this.grid = level.copyGrid();
        this.playerPosition = level.playerStartPosition;
    }

    public Grid getGrid() {
        return this.grid;
    }

    public Pos getPlayerPosition() {
        return this.playerPosition;
    }

    /** @return {@code true} if move could be applied */
    public boolean apply(Direction dir) {
        Pos landingPos = playerPosition.neighbour(dir);
        Cell currentCell = this.grid.getCell(this.playerPosition);
        Cell landingCell = this.grid.getCell(landingPos);

        if (landingCell.isBlocking()) {
            return false;
        } else {
            if (landingCell.hasBox()) {
                Pos beyondBoxPos = landingPos.neighbour(dir);
                Cell beyondBoxCell = this.grid.getCell(beyondBoxPos);
                if (beyondBoxCell.isBlocking()
                        || beyondBoxCell.hasBox()) {
                    return false;
                } else {
                    this.grid.setCell(beyondBoxPos, beyondBoxCell.withBox());
                }
            }
            this.grid.setCell(this.playerPosition, currentCell.withoutPlayerOrBox());
            this.grid.setCell(landingPos, landingCell.withPlayer());
            this.playerPosition = landingPos;
            return true;
        }
    }
}
