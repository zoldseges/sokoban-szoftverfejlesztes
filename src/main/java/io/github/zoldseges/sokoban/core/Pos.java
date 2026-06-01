package io.github.zoldseges.sokoban.core;

// TODO: This choice resulted in unneeded memory allocations.
//       With that being said, these allocations are probably optimized away by the JIT, but still...
//       (search on HotSpot escape analysis if you want to know more)
/** Position on the grid. */
public record Pos(int x, int y) {}