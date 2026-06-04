package io.github.zoldseges.sokoban.core;

import java.util.ArrayList;
import java.util.List;

/** intended to be immutable as opposed to {@link Grid} */
public class Level {
    final Pos playerStartPosition;

    private final Grid grid;

    public static Result from(Grid source) {
        List<Violation> violations = new ArrayList<>();

        List<Pos> boxPositions = new ArrayList<>();
        List<Pos> goalPositions = new ArrayList<>();
        List<Pos> playerPositions = new ArrayList<>();

        source.forEach((pos, cell) -> {
            if (cell.hasBox())    boxPositions.add(pos);
            if (cell.isGoal())    goalPositions.add(pos);
            if (cell.hasPlayer()) playerPositions.add(pos);
        });

        if (playerPositions.isEmpty()) {
            violations.add(new Violation.NoPlayer());
        } else if (playerPositions.size() > 1) {
            violations.add(new Violation.MultiplePlayers(playerPositions));
        }
        if (boxPositions.size() != goalPositions.size()) {
            violations.add(new Violation.GoalBoxMismatch(
                    boxPositions, goalPositions));
        }

        if (! (violations.isEmpty())) {
            return new Result.Err(violations);
        } else {
            return new Result.Ok(new Level(source, playerPositions.getFirst()));
        }
    }

    public Grid copyGrid() {
        return this.grid.copy();
    }

    private Level(Grid grid, Pos playerStartPosition) {
        this.grid = grid.copy();
        this.playerStartPosition = playerStartPosition;
    }

    public sealed interface Result permits
            Result.Ok,
            Result.Err {
        record Ok(Level level)                 implements Result {}
        record Err(List<Violation> violations) implements Result {}
    }

    public sealed interface Violation permits
            //TODO: enclosement check
            //Violation.NotEnclosed,
            Violation.NoPlayer,
            Violation.MultiplePlayers,
            Violation.GoalBoxMismatch {
        record NoPlayer()
                implements Violation {}

        record MultiplePlayers(
                List<Pos> playerPositions
        ) implements Violation {}

        record GoalBoxMismatch(
                List<Pos> boxPositions,
                List<Pos> goalPositions
        ) implements Violation {}
        // record NotEnclosed(List<Pos> gaps)   implements Violation {}
    }
}
