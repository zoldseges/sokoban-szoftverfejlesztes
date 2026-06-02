package io.github.zoldseges.sokoban;

import io.github.zoldseges.sokoban.core.Direction;
import io.github.zoldseges.sokoban.core.State;
import io.github.zoldseges.sokoban.core.Level;

import java.util.List;

public class GameSession {
    private final Level level;
    private final State gameState;
    private Direction playerDirection;

    private final List<Command.Move> gameMoveHistory = new java.util.ArrayList<>();

    //IDEA: we might need to derive from `Level` later on the `storage` persistence layer so we can associate attributes
    //      associated with a level - e.g. ID, best-score, or _maybe even_ (smells bad, but) unfinished sessions!
    public GameSession(Level level) {
        this.level = level;
        this.gameState = new State(level);
        this.playerDirection = Direction.DOWN;
    }

    public State getGameState() {
        return gameState;
    }

    public Direction getPlayerDirection() {
        return playerDirection;
    }

    public boolean dispatchCommand(Command cmd) {
        return switch (cmd) {
            case Command.Move moveCmd: {
                playerDirection = moveCmd.dir;
                yield gameState.apply(moveCmd.dir);
            }
            // TODO: session history traversal not implemented
            case Command.Time timeCmd: {
                yield switch (timeCmd) {
                case UNDO:
                case REDO:
                case RESTART:
                    throw new UnsupportedOperationException("TODO");
                default: yield false;
                };
            }
        };
    }

    public sealed interface Command {
        record Move(Direction dir) implements Command {}

        enum Time implements Command {
            UNDO,
            REDO,
            RESTART
        }
    }
}
