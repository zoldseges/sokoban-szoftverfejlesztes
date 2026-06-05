package io.github.zoldseges.sokoban.core;

import io.github.zoldseges.sokoban.core.Level.Violation.*;
import io.github.zoldseges.sokoban.core.Level.Violation;
import io.github.zoldseges.sokoban.core.Level.Result.*;
import io.github.zoldseges.sokoban.core.Level.Result;

import io.github.zoldseges.persistence.Xsb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {
    static List<TestCase> levelCases() {
        List<TestCase> cases = new ArrayList<>();

        cases.add(TestCase
                .expectResult("""
                                #######
                                #@ $ .#
                                #######
                                """,
                        Ok.class
                )
        );

        cases.add(TestCase
                .expectViolations("""
                                #######
                                #@$ $.#
                                #######
                                """,
                        List.of(new GoalBoxMismatch(
                                        List.of(new Pos(2, 1),
                                                new Pos(4, 1)
                                        ),
                                        List.of(new Pos(5, 1))
                                )
                        )
                )
        );

        cases.add(TestCase
                .expectViolationTypes("""
                                #######
                                # $  .#
                                #######
                                """,
                        List.of(NoPlayer.class)));

        cases.add(TestCase.expectViolationTypes("""
                        #######
                        #@$@$.#
                        #######
                        """,
                List.of(MultiplePlayers.class,
                        GoalBoxMismatch.class)));

        return cases;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("levelCases")
    void from(TestCase testCase) throws Throwable {
        testCase.executable.execute();
    }

    @Test
    void copyGrid() {

    }

    static class TestCase {
        private final Grid grid;
        private final Level.Result levelResult;
        Executable executable;

        private TestCase(String xsb) {
            Grid.Result result = Xsb.gridFrom(xsb);
            Grid.Result.Ok ok = assertInstanceOf(Grid.Result.Ok.class, result,
                    "Should be valid grid:\n" + xsb);
            this.grid = ok.grid();
            this.levelResult = Level.from(this.grid);
        }

        //NOTE needs factory, otherwise List<Class<? extends Violation>> would clash with List<Violation>
        static TestCase expectResult(String xsb, Class<? extends Result> resultType) {
            TestCase tc = new TestCase(xsb);
            tc.executable = () -> assertInstanceOf(resultType, tc.levelResult,
                    "Level.Result should be " + resultType.getName() + ":\n"
                    + xsb
            );
            return tc;
        }

        //NOTE: fail if violation type counts doesn't match up
        static TestCase expectViolationTypes(String xsb, List<Class<? extends Violation>> violationTypes) {
            TestCase tc = new TestCase(xsb);
            Level.Result.Err err = assertInstanceOf(Err.class, tc.levelResult,
                "Level.Result should be: " + Err.class.getName() + ":\n"
                    + xsb);
            tc.executable = () -> {
                Map<Class<?>, Integer> expected = new HashMap<>();
                Map<Class<?>, Integer> actual = new HashMap<>();

                for (Class<? extends Violation> type : violationTypes) {
                    expected.put(type, expected.getOrDefault(type, 0) + 1);
                }

                for (Violation violation : err.violations()) {
                    Class<?> type = violation.getClass();
                    actual.put(type, actual.getOrDefault(type, 0) + 1);
                }
                assertEquals(expected, actual,
                        "Violations should match the expected types (ignoring order):\n"
                                + xsb);
            };
            return tc;
        }

        //NOTE: uses a normalizing helper function so order should not matter
        static TestCase expectViolations(String xsb, List<Violation> expectedViolations) {
            TestCase tc = new TestCase(xsb);
            Level.Result.Err err = assertInstanceOf(Err.class, tc.levelResult,
                    "Level.Result should be: " + Err.class.getName() + ":\n"
                            + xsb);
            tc.executable = () -> {
                List<Violation> remainingExpected = new ArrayList<>();
                for (Violation expected: expectedViolations) {
                    remainingExpected.add(normalize(expected));
                }

                for (Violation actual : err.violations()) {
                    assertTrue(remainingExpected.remove(normalize(actual)),
                            "Unexpected violation: " + actual + "\n" + xsb
                    );
                }
                assertTrue(remainingExpected.isEmpty(),
                "Missing violations: " + remainingExpected + "\n" + xsb
                );
            };
            return tc;
        }

        private static Violation normalize(Violation violation) {
            return switch(violation) {
                case NoPlayer nP -> nP;
                case MultiplePlayers mP ->
                        new MultiplePlayers(sorted(mP.playerPositions()));
                case GoalBoxMismatch gBM ->
                        new GoalBoxMismatch(
                                sorted(gBM.boxPositions()),
                                sorted(gBM.goalPositions())
                        );
            };
        }

        private static final Comparator<Pos> BY_POSITION =
                Comparator.comparingInt(Pos::x).thenComparingInt(Pos::y);

        private static List<Pos> sorted(List<Pos> positions) {
            List<Pos> copy = new ArrayList<>(positions);
            copy.sort(BY_POSITION);
            return copy;
        }
    }
}