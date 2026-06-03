package io.github.zoldseges.persistence;

import io.github.zoldseges.sokoban.core.Cell;
import io.github.zoldseges.sokoban.core.Grid;
import io.github.zoldseges.sokoban.core.Pos;

import java.util.Arrays;

// xsb format: http://sokoban.org/about_sokoban.php
class Xsb {

    private static Cell cellFrom(char chr) {
        return switch (chr) {
            case '#' -> Cell.WALL;
            case '@' -> Cell.PLAYER;
            case '+' -> Cell.PLAYER_ON_GOAL;
            case '$' -> Cell.BOX;
            case '*' -> Cell.BOX_ON_GOAL;
            case '.' -> Cell.GOAL;
            //TODO: file should not contain mixed floor encoding
            case ' ', '-', '_' -> Cell.FLOOR;
            //TODO: unknown chars shouldn't just simply default to VOID
            default -> Cell.VOID;
        };
    }

    static Grid.Result gridFrom(String text) {
        String[] textRows = text.lines().toArray(String[]::new);
        int height = textRows.length;
        int width = Arrays.stream(textRows)
                .mapToInt(String::length)
                .max()
                .orElse(0);
        return switch (Grid.from(width, height)) {
            case Grid.Result.Err err -> err;
            case Grid.Result.Ok ok -> {
                Grid grid = ok.grid();
                for (int y = 0; y < height; ++y) {
                    String row = textRows[y];
                    for (int x = 0; x < width; ++x) {
                        Cell cell = x < row.length()
                                ? cellFrom(row.charAt(x))
                                : Cell.VOID;
                        grid.setCell(new Pos(x, y), cell);
                    }
                }
                yield ok;
            }
        };
    }
}
