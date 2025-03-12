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
import main.scorers.RecycledScorer;

public class RecycledScorerTests {

    // We don't care about recycled die faces, so we'll use
    // this as a stand-in for any old recycled die.
    private static final Die ANY_RECYCLED_DIE = new Die(Material.RECYCLED, 3);

    private Building building;

    @BeforeEach
    public void setup() {
        building = new Building();
    }

    @Nested
    class EmptyBuildingTests {
        @Test
        public void new_building_has_recycled_score_0() {
            // Given an empty building

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
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
        public void one_recycled_scores_2(int row, int col) {
            // Given a building composed of a single recycled die
            building.add(ANY_RECYCLED_DIE, row, col);

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
            int score = scorer.score();

            // Then that score should be 2
            assertEquals(2, score);

        }

        @ParameterizedTest
        @CsvSource({
                "1,1,W4",
                "1,2,G1",
                "2,1,S6",
                "2,2,S2",
                "3,1,G3",
                "3,2,W5"
        })
        public void no_recycled_scores_0(int row, int col, String diceText) {
            // Given a building composed of a single non-recycled die
            Die notRecycledDie = new Die(diceText);
            building.add(notRecycledDie, row, col);

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
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
        public void two_recycled_together_scores_5(int row, int col) {
            // Given a building composed of 2 recycled die stacked on each other
            building.add(ANY_RECYCLED_DIE, row, col);
            building.add(ANY_RECYCLED_DIE, row, col);

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
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
        public void two_recycled_apart_scores_5(int row, int col) {
            // Given a building composed of 2 separated recycled dice
            int separateRow = ((row + 1) % 3) + 1;
            int separateCol = ((col + 1) % 2) + 1;

            building.add(ANY_RECYCLED_DIE, row, col);
            building.add(ANY_RECYCLED_DIE, separateRow, separateCol);

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
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
        public void recycled_and_other_material_stacked_together_scores_2(int row, int col) {
            // Given a building composed of a stacked recycled and other material
            building.add(new Die(Material.GLASS, 1), row, col);
            building.add(ANY_RECYCLED_DIE, row, col);

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
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
        public void recycled_and_other_material_apart_scores_2(int row, int col) {
            // Given a building composed of 2 separated recycled and non-recycled dice
            int separateRow = ((row + 1) % 3) + 1;
            int separateCol = ((col + 1) % 2) + 1;

            building.add(ANY_RECYCLED_DIE, row, col);
            building.add(new Die("W2"), separateRow, separateCol);

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
            int score = scorer.score();

            // Then that score should be 2
            assertEquals(2, score);
        }

    }

    @Nested
    class SixRecycledTests {
        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1",
                "2,2",
                "3,1",
                "3,2"
        })
        public void stack_of_all_recycled_scores_30(int row, int col) {
            // Given a building composed of a stack of 6 recycled dice
            for (int count = 1; count <= 6; count++) {
                building.add(ANY_RECYCLED_DIE, row, col);
            }

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
            int score = scorer.score();

            // Then that score should be 30
            assertEquals(30, score);
        }

        @Test
        public void all_separated_recycled_scores_30() {
            // Given a building composed of 6 separated recycled dice
            building.add(ANY_RECYCLED_DIE, 1, 1);
            building.add(ANY_RECYCLED_DIE, 1, 2);
            building.add(ANY_RECYCLED_DIE, 2, 1);
            building.add(ANY_RECYCLED_DIE, 2, 2);
            building.add(ANY_RECYCLED_DIE, 3, 1);
            building.add(ANY_RECYCLED_DIE, 3, 2);

            // When you get the recycled score for that building
            RecycledScorer scorer = new RecycledScorer(building);
            int score = scorer.score();

            // Then that score should be 30
            assertEquals(30, score);
        }
    }

    @Nested
    class InvalidBuildingTests {
        @Test
        public void building_with_descending_stack() {
            // Given a Recycled for a building with a descending stack
            building.add(new Die("R4"), 3, 2);
            building.add(new Die("R3"), 3, 2);

            RecycledScorer scorer = new RecycledScorer(building);

            // When you get the recycled score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack() {
            // Given a RecycledScorer for a building with an overlarge stack
            for (int count = 1; count <= 7; count++) {
                building.add(ANY_RECYCLED_DIE, 1, 1);
            }

            RecycledScorer scorer = new RecycledScorer(building);

            // When you get the recycled score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack() {
            // Given a RecycledScorer for an overlarge building without an overlarge stack
            building.add(ANY_RECYCLED_DIE, 1, 1);

            building.add(ANY_RECYCLED_DIE, 1, 2);
            building.add(ANY_RECYCLED_DIE, 1, 2);

            building.add(ANY_RECYCLED_DIE, 2, 2);
            building.add(ANY_RECYCLED_DIE, 2, 2);

            building.add(ANY_RECYCLED_DIE, 3, 1);

            building.add(ANY_RECYCLED_DIE, 3, 2);

            RecycledScorer scorer = new RecycledScorer(building);

            // When you get the recycled score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_thats_descending() {
            // Given a RecycledScorer for a building with an overlarge stack that descends
            for (int count = 1; count <= 7; count++) {
                building.add(new Die("R6"), 2, 1);
            }
            building.add(new Die("R1"), 2, 1);

            RecycledScorer scorer = new RecycledScorer(building);

            // When you get the recycled score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_and_another_stack_descending() {
            // Given a RecycledScorer for a building with an overlarge stack
            // and a different stack that's descending
            for (int count = 1; count <= 7; count++) {
                building.add(ANY_RECYCLED_DIE, 1, 1);
            }

            building.add(new Die("W3"), 3, 1);
            building.add(new Die("R1"), 3, 1);

            RecycledScorer scorer = new RecycledScorer(building);

            // When you get the recycled score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack_has_descending_stack() {
            // Given a RecycledScorer for an overlarge building without an overlarge stack
            building.add(new Die("G6"), 1, 1);

            building.add(new Die("G6"), 1, 2);
            building.add(new Die("W2"), 1, 2); // Descending stack

            building.add(new Die("G6"), 2, 2);
            building.add(new Die("G6"), 2, 2);

            building.add(new Die("G6"), 3, 1);
            building.add(new Die("G6"), 3, 2);

            RecycledScorer scorer = new RecycledScorer(building);

            // When you get the recycled score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }
    }
}
