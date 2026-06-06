package io.github.zoldseges.view;

import io.github.zoldseges.sokoban.core.*;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Objects;

// TODO: screen zoom + screen move for big maps
public class GridRenderer {
    public static void render(Canvas canvas, Grid grid, Direction playerDirection) {
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        // NOTE: not sure what it does, might worth a try flipping it if you see visual artifacts
        // ctx.setImageSmoothing(false);

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double gridCols = grid.getCols();
        double gridRows = grid.getRows();

        // NOTE: there were some artifacts on tile borders.
        //  It had something to do with pixel represented as doubles i guess
        double canvasTileSize = Math.floor(
            Math.min((canvasWidth / gridCols),
                    (canvasHeight / gridRows)
            )
        );

        //NOTE: centering
        double canvasOriginOfsX = Math.floor((canvasWidth - (gridCols * canvasTileSize)) / 2);
        double canvasOriginOfsY = Math.floor((canvasHeight - (gridRows * canvasTileSize)) / 2);

        ctx.clearRect(0, 0, canvasWidth, canvasHeight);
        grid.forEach((gridPos, cell) -> {
            for (Tile tileLayer : getTilesFor(cell, playerDirection)) {
                ctx.drawImage(Tile.ATLAS,
                        tileLayer.colIdx * Tile.DIM_PX,
                        tileLayer.rowIdx * Tile.DIM_PX,
                        Tile.DIM_PX,
                        Tile.DIM_PX,
                        canvasOriginOfsX + (gridPos.x() * canvasTileSize),
                        canvasOriginOfsY + (gridPos.y() * canvasTileSize),
                        canvasTileSize,
                        canvasTileSize
                );
            }
        });
    }

    //TODO: shares lot of coude with the full canvas render method, might need refactor
    public static void highlight(Canvas canvas, Grid grid, List<Pos> positions, Color color) {
        double border_px = 10.0;
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double gridCols = grid.getCols();
        double gridRows = grid.getRows();

        double canvasTileSize = Math.floor(
                Math.min((canvasWidth / gridCols),
                        (canvasHeight / gridRows)
                )
        );

        double canvasOriginOfsX = Math.floor((canvasWidth - (gridCols * canvasTileSize)) / 2);
        double canvasOriginOfsY = Math.floor((canvasHeight - (gridRows * canvasTileSize)) / 2);

        ctx.setFill(color);
        ctx.setStroke(color);
        //NOTE: draws border: makes highlight bigger then actual tile.
        ctx.setLineWidth(border_px);

        for (Pos pos : positions) {
            double x = canvasOriginOfsX + (pos.x() * canvasTileSize);
            double y = canvasOriginOfsY + (pos.y() * canvasTileSize);

            ctx.fillRect(x, y, canvasTileSize, canvasTileSize);
            ctx.strokeRect(x, y, canvasTileSize, canvasTileSize);
        }
    }

    private static List<Tile> getTilesFor(Cell cell, Direction playerDir) {
        Tile playerTile = switch(playerDir) {
            case LEFT -> Tile.PLAYER_LEFT;
            case RIGHT -> Tile.PLAYER_RIGHT;
            case UP -> Tile.PLAYER_UP;
            case DOWN -> Tile.PLAYER_DOWN;
        };

        return switch(cell) {
            case VOID -> List.of(Tile.VOID);
            case FLOOR -> List.of(Tile.FLOOR);
            case WALL -> List.of(
                    Tile.FLOOR,
                    Tile.WALL
            );
            case GOAL -> List.of(Tile.GOAL_FLOOR);
            case BOX -> List.of(
                    Tile.FLOOR,
                    Tile.BOX_BRIGHT
            );
            case BOX_ON_GOAL -> List.of(
                    Tile.FLOOR,
                    Tile.BOX_DARK
            );
            case PLAYER -> List.of(
                    Tile.FLOOR,
                    playerTile
            );
            case PLAYER_ON_GOAL -> List.of(
                    Tile.FLOOR,
                    playerTile,
                    Tile.GOAL_OVERLAY
            );
        };
    }

    private enum Tile {
        VOID(0, 0),
        FLOOR(11, 6),
        WALL(6, 6),
        GOAL_FLOOR(11, 7),
        GOAL_OVERLAY(0, 3),
        BOX_BRIGHT(1, 0),
        BOX_DARK(1, 1),
        PLAYER_DOWN(0, 5),
        PLAYER_RIGHT(0, 7),
        PLAYER_UP(3, 5),
        PLAYER_LEFT(3, 7);

        // NOTE: initialized compile time;
        private static final int DIM_PX = 128;

        private final int colIdx;
        private final int rowIdx;

        Tile(int colIdx, int rowIdx) {
            this.colIdx = colIdx;
            this.rowIdx = rowIdx;
        }

        private static final Image ATLAS = new Image(
                Objects.requireNonNull(
                        GridRenderer.class.getResource("tilesheet.png"),
                        "Habibi, can't find the tilesheet asset."
                ).toExternalForm()
        );

    }
}