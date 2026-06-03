package io.github.zoldseges.persistence;

import io.github.zoldseges.sokoban.core.Grid;
import io.github.zoldseges.sokoban.core.Level;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LevelLoader {

    public static Level load(Path path) throws LevelLoadException, IOException {
        String xsbString = Files.readString(path);
        Grid grid = switch (Xsb.gridFrom(xsbString)) {
            case Grid.Result.Err err -> throw new GridException(err.violations());
            case Grid.Result.Ok ok -> ok.grid();
        };
        return switch(Level.from(grid)) {
            case Level.Result.Err err -> throw new LevelException(err.violations());
            case Level.Result.Ok ok -> ok.level();
        };
    }

    public sealed abstract static class LevelLoadException extends Exception permits
            GridException,
            LevelException {
        LevelLoadException(String msg) {
            super(msg);
        }

    }

    public static final class GridException extends LevelLoadException {
        private final List<Grid.Violation> violations;

        GridException(List<Grid.Violation> violations) {
            super("invalid grid: %s".formatted(violations));
            this.violations = violations;
        }

        List<Grid.Violation> getViolations() {
            return this.violations;
        }
    }

    public static final class LevelException extends LevelLoadException {
        private final List<Level.Violation> violations;

        LevelException(List<Level.Violation> violations) {
            super("invalid level: %s".formatted(violations));
            this.violations = violations;
        }

        List<Level.Violation> getViolations() {
            return this.violations;
        }
    }
}
