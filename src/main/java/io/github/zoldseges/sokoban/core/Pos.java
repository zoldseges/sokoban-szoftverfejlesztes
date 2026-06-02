package io.github.zoldseges.sokoban.core;

// IDEA: This choice resulted in unneeded memory allocations. Maybe a class with accessible x, y would have been better.
//       With that being said, these allocations are _said to be_ optimized away by the JIT, but still...
//       (see: search on HotSpot escape analysis if you want to know more)

/** Position on the grid. */
public record Pos(int x, int y) {
    public Pos neighbour(Direction dir) {
        return new Pos(x + dir.dx, y + dir.dy);
    }
}