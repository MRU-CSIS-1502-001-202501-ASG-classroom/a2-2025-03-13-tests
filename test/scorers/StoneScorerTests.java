package test.scorers;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import main.building.Building;
import main.building.Die;
import main.building.Material;
import main.scorers.StoneScorer;

public class StoneScorerTests {

    // We don't care about stone die faces, so we'll use
    // this as a stand-in for any old stone die.
    private static final Die ANY_STONE_DIE = new Die(Material.STONE, 6);

    private Building building;

    @BeforeEach
    public void setup() {
        building = new Building();
    }

    @Nested
    class EmptyBuildingTests {
        @Test
        public void new_building_has_stone_score_0() {
            // Given an empty building

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }
    }

    @Nested
    class OneDieBuildingTests {
        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void one_stone_scores_2(int row, int col) {
            // Given a building composed of a single stone die
            building.add(ANY_STONE_DIE, row, col);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 2
            assertEquals(2, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1,W4",
                "1,2,G1",
                "2,1,R6",
                "2,2,R2",
                "3,1,G3",
                "3,2,W5"
        })
        public void no_stone_scores_0(int row, int col, String diceText) {
            // Given a building composed of a single non-stone die
            Die notStoneDie = new Die(diceText);
            building.add(notStoneDie, row, col);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }
    }

    @Nested
    class TwoDiceBuildingTests {
        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void two_stone_together_scores_5(int row, int col) {
            // Given a building composed of 2 stacked stone dice
            building.add(ANY_STONE_DIE, row, col);
            building.add(ANY_STONE_DIE, row, col);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 5
            assertEquals(5, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void two_stone_apart_scores_4(int row, int col) {
            // Given a building composed of 2 separate stone dice
            int secondStoneDieRow = ((row + 1) % 3) + 1;
            int secondStoneDieCol = ((col + 1) % 2) + 1;

            building.add(ANY_STONE_DIE, row, col);
            building.add(ANY_STONE_DIE, secondStoneDieRow, secondStoneDieCol);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 4
            assertEquals(4, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void stone_on_top_of_other_material_stacked_together_scores_3(int row, int col) {
            // Given a building composed of a stone stacked on another material
            building.add(new Die(Material.GLASS, 1), row, col);
            building.add(ANY_STONE_DIE, row, col);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 2
            assertEquals(3, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void stone_under_other_material_stacked_together_scores_2(int row, int col) {
            // Given a building composed of a stone stacked under another material
            building.add(ANY_STONE_DIE, row, col);
            building.add(new Die(Material.WOOD, 6), row, col);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 2
            assertEquals(2, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void stone_and_other_material_apart_scores_2(int row, int col) {
            // Given a building composed of 2 separated stone and non-stone dice
            int separateRow = ((row + 1) % 3) + 1;
            int separateCol = ((col + 1) % 2) + 1;

            building.add(ANY_STONE_DIE, row, col);
            building.add(new Die("W2"), separateRow, separateCol);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 2
            assertEquals(2, score);
        }

    }

    @Nested
    class SixStoneTests {
        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void stack_of_all_stone_scores_34(int row, int col) {
            // Given a building composed of a stack of 6 stone dice
            for (int count = 1; count <= 6; count++) {
                building.add(ANY_STONE_DIE, row, col);
            }

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 30
            assertEquals(34, score);
        }

        @Test
        public void six_single_stone_stacks_scores_12() {
            // Given a building composed of 6 separated stone dice
            building.add(ANY_STONE_DIE, 1, 1);
            building.add(ANY_STONE_DIE, 1, 2);
            building.add(ANY_STONE_DIE, 2, 1);
            building.add(ANY_STONE_DIE, 2, 2);
            building.add(ANY_STONE_DIE, 3, 1);
            building.add(ANY_STONE_DIE, 3, 2);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 30
            assertEquals(12, score);
        }

        @Test
        public void three_double_stone_stacks_scores_15() {
            // Given a building composed of 3 separated stone stacks of 2
            building.add(ANY_STONE_DIE, 1, 1);
            building.add(ANY_STONE_DIE, 1, 1);

            building.add(ANY_STONE_DIE, 2, 2);
            building.add(ANY_STONE_DIE, 2, 2);

            building.add(ANY_STONE_DIE, 3, 1);
            building.add(ANY_STONE_DIE, 3, 1);

            // When you get the stone score for that building
            StoneScorer scorer = new StoneScorer(building);
            int score = scorer.score();

            // Then that score should be 15
            assertEquals(15, score);
        }
    }

    @Nested
    class InvalidBuildingTests {
        @Test
        public void building_with_descending_stack() {
            // Given a StoneScorer for a building with a descending stack
            building.add(new Die("S4"), 3, 2);
            building.add(new Die("S3"), 3, 2);

            StoneScorer scorer = new StoneScorer(building);

            // When you get the stone score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack() {
            // Given a StoneScorer for a building with an overlarge stack
            for (int count = 1; count <= 7; count++) {
                building.add(ANY_STONE_DIE, 1, 1);
            }

            StoneScorer scorer = new StoneScorer(building);

            // When you get the stone score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack() {
            // Given a StoneScorer for an overlarge building without an overlarge stack
            building.add(ANY_STONE_DIE, 1, 1);

            building.add(ANY_STONE_DIE, 1, 2);
            building.add(ANY_STONE_DIE, 1, 2);

            building.add(ANY_STONE_DIE, 2, 2);
            building.add(ANY_STONE_DIE, 2, 2);

            building.add(ANY_STONE_DIE, 3, 1);

            building.add(ANY_STONE_DIE, 3, 2);

            StoneScorer scorer = new StoneScorer(building);

            // When you get the stone score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_thats_descending() {
            // Given a StoneScorer for a building with an overlarge stack that descends
            for (int count = 1; count <= 7; count++) {
                building.add(new Die("R6"), 2, 1);
            }
            building.add(new Die("R1"), 2, 1);

            StoneScorer scorer = new StoneScorer(building);

            // When you get the stone score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_and_another_stack_descending() {
            // Given a StoneScorer for a building with an overlarge stack
            // and a different stack that's descending
            for (int count = 1; count <= 7; count++) {
                building.add(ANY_STONE_DIE, 1, 1);
            }

            building.add(new Die("W3"), 3, 1);
            building.add(new Die("R1"), 3, 1);

            StoneScorer scorer = new StoneScorer(building);

            // When you get the stone score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack_has_descending_stack() {
            // Given a StoneScorer for an overlarge building without an overlarge stack
            building.add(new Die("G6"), 1, 1);

            building.add(new Die("G6"), 1, 2);
            building.add(new Die("W2"), 1, 2); // Descending stack

            building.add(new Die("G6"), 2, 2);
            building.add(new Die("G6"), 2, 2);

            building.add(new Die("G6"), 3, 1);
            building.add(new Die("G6"), 3, 2);

            StoneScorer scorer = new StoneScorer(building);

            // When you get the stone score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }
    }
}
