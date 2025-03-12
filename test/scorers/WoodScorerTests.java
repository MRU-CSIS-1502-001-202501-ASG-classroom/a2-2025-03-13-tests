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
import main.scorers.WoodScorer;

public class WoodScorerTests {

    // We don't care about wood die faces, so we'll use
    // this as a stand-in for any old wood die.
    private static final Die ANY_WOOD_DIE = new Die(Material.WOOD, 1);

    private Building building;

    @BeforeEach
    public void setup() {
        building = new Building();
    }

    @Nested
    class EmptyBuilding {
        @Test
        public void new_building_has_wood_score_0() {
            // Given an empty building

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
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
        public void one_wood_scores_0(int row, int col) {
            // Given a building composed of a single wood die
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1,G4",
                "1,2,R1",
                "2,1,S6",
                "2,2,S2",
                "3,1,R3",
                "3,2,G5"
        })
        public void no_wood_scores_0(int row, int col, String diceText) {
            // Given a building composed of a single non-wood die
            Building building = new Building();
            Die die = new Die(diceText);
            building.add(die, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
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
        public void two_wood_stacked_scores_4(int row, int col) {
            // Given a building composed of 2 wood die stacked on each other
            building.add(ANY_WOOD_DIE, row, col);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 5
            assertEquals(4, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "2,1",
                "1,2",
                "2,2"
        })
        public void two_wood_touching_in_same_column_scores_4(int row, int col) {
            // Given a building composed of 2 wood die stacked on each other
            building.add(ANY_WOOD_DIE, row, col);
            building.add(ANY_WOOD_DIE, row + 1, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 5
            assertEquals(4, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "2,1",
                "3,1"
        })
        public void two_wood_touching_in_same_row_scores_4(int row, int col) {
            // Given a building composed of 2 wood die stacked on each other
            building.add(ANY_WOOD_DIE, row, col);
            building.add(ANY_WOOD_DIE, row, col + 1);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 5
            assertEquals(4, score);
        }

        @ParameterizedTest
        @CsvSource({
                "2,2",
                "3,1",
                "3,2"
        })
        public void one_wood_r1_c1_other_die_disjoint_scores_0(int row, int col) {
            // Given a building composed of a wood die at (1,1) and another disjoint die
            building.add(ANY_WOOD_DIE, 1, 1);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }

        @ParameterizedTest
        @CsvSource({
                "2,1",
                "3,1",
                "3,2"
        })
        public void one_wood_r1_c2_other_die_disjoint_scores_0(int row, int col) {
            // Given a building composed of a wood die at (1,2) and another disjoint die
            building.add(ANY_WOOD_DIE, 1, 2);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,2",
                "3,2"
        })
        public void one_wood_r2_c1_other_die_disjoint_scores_0(int row, int col) {
            // Given a building composed of a wood die at (2,1) and another disjoint die
            building.add(ANY_WOOD_DIE, 2, 1);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "3,1"
        })
        public void one_wood_r2_c2_other_die_disjoint_scores_0(int row, int col) {
            // Given a building composed of a wood die at (2,2) and another disjoint die
            building.add(ANY_WOOD_DIE, 2, 2);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,2"
        })
        public void one_wood_r3_c1_other_die_disjoint_scores_0(int row, int col) {
            // Given a building composed of a wood die at (3,1) and another disjoint die
            building.add(ANY_WOOD_DIE, 3, 1);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1",
                "1,2",
                "2,1"
        })
        public void one_wood_r3_c2_other_die_disjoint_scores_0(int row, int col) {
            // Given a building composed of a wood die at (3,2) and another disjoint die
            building.add(ANY_WOOD_DIE, 3, 2);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
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
        public void wood_on_top_of_other_material_stacked_together_scores_2(int row, int col) {
            // Given a building composed of a stacked wood and other material
            building.add(new Die(Material.GLASS, 1), row, col);
            building.add(ANY_WOOD_DIE, row, col);

            // When you get the wood score for that building
            WoodScorer scorer = new WoodScorer(building);
            int score = scorer.score();

            // Then that score should be 2
            assertEquals(2, score);
        }
    }

    @Nested
    class SixWoodTests {
        //
    }

    @Nested
    class InvalidBuildingTests {
        @Test
        public void building_with_descending_stack() {
            // Given a WoodScorer for a building with a descending stack
            building.add(new Die("S4"), 3, 2);
            building.add(new Die("S3"), 3, 2);

            WoodScorer scorer = new WoodScorer(building);

            // When you get the wood score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack() {
            // Given a WoodScorer for a building with an overlarge stack
            for (int count = 1; count <= 7; count++) {
                building.add(ANY_WOOD_DIE, 1, 1);
            }

            WoodScorer scorer = new WoodScorer(building);

            // When you get the wood score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack() {
            // Given a WoodScorer for an overlarge building without an overlarge stack
            building.add(ANY_WOOD_DIE, 1, 1);

            building.add(ANY_WOOD_DIE, 1, 2);
            building.add(ANY_WOOD_DIE, 1, 2);

            building.add(ANY_WOOD_DIE, 2, 2);
            building.add(ANY_WOOD_DIE, 2, 2);

            building.add(ANY_WOOD_DIE, 3, 1);

            building.add(ANY_WOOD_DIE, 3, 2);

            WoodScorer scorer = new WoodScorer(building);

            // When you get the wood score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_thats_descending() {
            // Given a WoodScorer for a building with an overlarge stack that descends
            for (int count = 1; count <= 7; count++) {
                building.add(new Die("R6"), 2, 1);
            }
            building.add(new Die("R1"), 2, 1);

            WoodScorer scorer = new WoodScorer(building);

            // When you get the wood score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_and_another_stack_descending() {
            // Given a WoodScorer for a building with an overlarge stack
            // and a different stack that's descending
            for (int count = 1; count <= 7; count++) {
                building.add(ANY_WOOD_DIE, 1, 1);
            }

            building.add(new Die("W3"), 3, 1);
            building.add(new Die("R1"), 3, 1);

            WoodScorer scorer = new WoodScorer(building);

            // When you get the wood score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack_has_descending_stack() {
            // Given a WoodScorer for an overlarge building without an overlarge stack
            building.add(new Die("G6"), 1, 1);

            building.add(new Die("G6"), 1, 2);
            building.add(new Die("W2"), 1, 2); // Descending stack

            building.add(new Die("G6"), 2, 2);
            building.add(new Die("G6"), 2, 2);

            building.add(new Die("G6"), 3, 1);
            building.add(new Die("G6"), 3, 2);

            WoodScorer scorer = new WoodScorer(building);

            // When you get the wood score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }
    }
}
