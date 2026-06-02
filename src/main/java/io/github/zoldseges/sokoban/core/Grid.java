package io.github.zoldseges.sokoban.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid {
    final int width;
    final int height;

    private final Cell[] cells;

    public static Result from(int width, int height) {
        List<Violation> violations = new ArrayList<>();
        if (width <= 0) {
            violations.add(new Violation.WidthZeroOrLess(width));
        }
        if (height <= 0) {
            violations.add(new Violation.HeightZeroOrLess(height));
        }
        if (!violations.isEmpty()) {
            return new Result.Err(violations);
        }
        return new Result.Ok(new Grid(width, height));
    }

    public Grid copy() {
        Grid newGrid = new Grid(this.width, this.height);
        System.arraycopy(this.cells, 0, newGrid.cells, 0, this.cells.length);
        return newGrid;
    }

    public Cell get(Pos position) {
        int x = position.x(), y = position.y();
        if (x >= 0 && x < this.width
                && y >= 0 && y < this.height) {
            return cells[y * this.width + x];
        }
        return Cell.VOID;
    }

    public boolean set(Pos position, Cell cell) {
        int x = position.x(), y = position.y();
        if (x >= 0 && x < this.width
                && y >= 0 && y < this.height) {
            cells[y * this.width + x] = cell;
            return true;
        }
        return false;
    }

    public void forEach(CellVisitor visitor) {
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                visitor.visit(new Pos(x, y), cells[y * this.width + x]);
            }
        }
    }

    //NOTE: this is a SAM, it's needed so `forEach` can accept lambad as parameters
    @FunctionalInterface
    public interface CellVisitor {
        void visit(Pos pos, Cell cell);
    }

    public sealed interface Result permits
            Result.Ok,
            Result.Err {
        record Ok(Grid grid)                   implements Result {}
        record Err(List<Violation> violations) implements Result {}
    }

    public sealed interface Violation permits
            Violation.WidthZeroOrLess,
            Violation.HeightZeroOrLess {
        record WidthZeroOrLess(int width)   implements Violation {}
        record HeightZeroOrLess(int height) implements Violation {}
    }

    private Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width * height];
        Arrays.fill(this.cells, Cell.FLOOR);
    }
}
