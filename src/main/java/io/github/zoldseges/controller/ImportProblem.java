package io.github.zoldseges.controller;

import io.github.zoldseges.sokoban.core.Grid;
import io.github.zoldseges.sokoban.core.Level;

import java.util.List;

//TODO: it lives here, because I made this for the importProblemsController,
//      but it would probably simplify persistence as well.
//      consider - with a new name - to move this to sokoban.core

public sealed interface ImportProblem {

    record GridProblems(List<Grid.Violation> violations) implements
            ImportProblem {}

    record LevelProblems(Grid grid, List<Level.Violation> violations)
            implements ImportProblem {}
}


