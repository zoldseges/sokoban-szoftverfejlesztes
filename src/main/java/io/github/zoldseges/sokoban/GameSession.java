package io.github.zoldseges.sokoban;

import io.github.zoldseges.sokoban.core.Direction;
import io.github.zoldseges.sokoban.core.State;
import io.github.zoldseges.sokoban.core.Level;

import java.util.List;

//TODO:
//  - UNITTESTING:
//    - Implement undo -> gives you move records
//    - I'm plement resume play -> persistent game session
//    => you just have to play, and if you find something weird,
//       just grab the persistent game session and test against that
public class GameSession {
    private final Level level;
    private final State gameState;
    private Direction playerDirection;

    private final List<Command.Move> gameMoveHistory = new java.util.ArrayList<>();

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
