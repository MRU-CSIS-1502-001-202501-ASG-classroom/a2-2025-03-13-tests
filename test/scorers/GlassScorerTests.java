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
import main.scorers.GlassScorer;

public class GlassScorerTests {

    private Building building;

    @BeforeEach
    public void setUp() {
        building = new Building();
    }

    @Nested
    class EmptyBuildingTests {
        @Test
        public void new_building_has_glass_score_0() {
            // Given an empty building

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);
        }
    }

    @Nested
    class OneDieBuildingTests {
        @ParameterizedTest
        @CsvSource({
                "1,1,4",
                "1,2,1",
                "2,1,6",
                "2,2,2",
                "3,1,3",
                "3,2,5"
        })
        public void one_glass_scores_face_value(int row, int col, int face) {
            // Given a building composed of a single glass die
            Die die = new Die(Material.GLASS, face);
            building.add(die, row, col);

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be the face value of the die
            assertEquals(face, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1,W4",
                "1,2,R1",
                "2,1,S6",
                "2,2,S2",
                "3,1,R3",
                "3,2,W5"
        })
        public void no_glass_scores_0(int row, int col, String diceText) {
            // Given a building composed of a single non-glass die
            Die die = new Die(diceText);
            building.add(die, row, col);

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be 0
            assertEquals(0, score);

        }
    }

    @Nested
    class TwoDiceBuildingTests {
        @ParameterizedTest
        @CsvSource({
                "1,1,3",
                "1,2,6",
                "2,1,1",
                "2,2,5",
                "3,1,4",
                "3,2,2"
        })
        public void two_glass_together_scores_face_values(int row, int col, int faceValue) {
            // Given a building composed of 2 glass die stacked on each other
            building.add(new Die(Material.GLASS, faceValue), row, col);
            building.add(new Die(Material.GLASS, faceValue), row, col);

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be the sum of the face values
            assertEquals(2 * faceValue, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1,3",
                "1,2,6",
                "2,1,1",
                "2,2,5",
                "3,1,4",
                "3,2,2"
        })
        public void two_glass_apart_scores_face_values(int row, int col, int faceValue) {
            // Given a building composed of 2 separated glass dice
            int separateRow = ((row + 1) % 3) + 1;
            int separateCol = ((col + 1) % 2) + 1;

            building.add(new Die(Material.GLASS, faceValue), row, col);
            building.add(new Die(Material.GLASS, faceValue), separateRow, separateCol);

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be the sum of the face values
            assertEquals(2 * faceValue, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1,3",
                "1,2,6",
                "2,1,1",
                "2,2,5",
                "3,1,4",
                "3,2,2"
        })
        public void glass_and_other_material_stacked_together_scores_face_value_of_glass_only(int row, int col,
                int faceValue) {
            // Given a building composed of a stacked glass and other material
            building.add(new Die(Material.GLASS, faceValue), row, col);
            building.add(new Die("R6"), row, col);

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be the face value of the glass
            assertEquals(faceValue, score);
        }

        @ParameterizedTest
        @CsvSource({
                "1,1,3",
                "1,2,6",
                "2,1,1",
                "2,2,5",
                "3,1,4",
                "3,2,2"
        })
        public void glass_and_other_material_apart_scores_face_value_of_glass_only(int row, int col, int faceValue) {
            // Given a building composed of 2 separated glass and non-glass dice

            int separateRow = ((row + 1) % 3) + 1;
            int separateCol = ((col + 1) % 2) + 1;

            building.add(new Die(Material.GLASS, faceValue), row, col);
            building.add(new Die("W2"), separateRow, separateCol);

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be the face value of the glass
            assertEquals(faceValue, score);
        }

    }

    @Nested
    class SixGlassTests {
        @ParameterizedTest
        @CsvSource({
                "1,1,3",
                "1,2,6",
                "2,1,1",
                "2,2,5",
                "3,1,4",
                "3,2,2"
        })
        public void stack_of_all_glass(int row, int col, int faceValue) {
            // Given a building composed of a stack of 6 glass dice with the same face
            for (int count = 1; count <= 6; count++) {
                building.add(new Die(Material.GLASS, faceValue), row, col);
            }

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be the sum of the face values of the glass
            assertEquals(6 * faceValue, score);
        }

        @Test
        public void all_separated_glass() {
            // Given a building composed of 6 separated glass dice
            building.add(new Die("G1"), 1, 1);
            building.add(new Die("G2"), 1, 2);
            building.add(new Die("G3"), 2, 1);
            building.add(new Die("G4"), 2, 2);
            building.add(new Die("G5"), 3, 1);
            building.add(new Die("G6"), 3, 2);

            // When you get the glass score for that building
            GlassScorer scorer = new GlassScorer(building);
            int score = scorer.score();

            // Then that score should be the sum of the face values of the glass
            assertEquals(21, score);
        }
    }

    @Nested
    class InvalidBuildingTests {
        @Test
        public void building_with_descending_stack() {
            // Given a GlassScorer for a building with a descending stack
            building.add(new Die("G4"), 3, 2);
            building.add(new Die("G3"), 3, 2);

            GlassScorer scorer = new GlassScorer(building);

            // When you get the glass score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack() {
            // Given a GlassScorer for a building with an overlarge stack
            for (int count = 1; count <= 7; count++) {
                building.add(new Die("G6"), 1, 1);
            }

            GlassScorer scorer = new GlassScorer(building);

            // When you get the glass score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack() {
            // Given a GlassScorer for an overlarge building without an overlarge stack
            building.add(new Die("G6"), 1, 1);
            building.add(new Die("G6"), 1, 2);
            building.add(new Die("G6"), 1, 2);
            building.add(new Die("G6"), 2, 2);
            building.add(new Die("G6"), 2, 2);
            building.add(new Die("G6"), 3, 1);
            building.add(new Die("G6"), 3, 2);

            GlassScorer scorer = new GlassScorer(building);

            // When you get the glass score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_thats_descending() {
            // Given a GlassScorer for a building with an overlarge stack that descends
            for (int count = 1; count <= 7; count++) {
                building.add(new Die("G6"), 2, 1);
            }
            building.add(new Die("G1"), 2, 1);

            GlassScorer scorer = new GlassScorer(building);

            // When you get the glass score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void building_with_overlarge_stack_and_another_stack_descending() {
            // Given a GlassScorer for a building with an overlarge stack
            // and a different stack that's descending
            for (int count = 1; count <= 7; count++) {
                building.add(new Die("G6"), 1, 1);
            }

            building.add(new Die("W3"), 3, 1);
            building.add(new Die("R1"), 3, 1);

            GlassScorer scorer = new GlassScorer(building);

            // When you get the glass score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }

        @Test
        public void overlarge_building_without_overlarge_stack_has_descending_stack() {
            // Given a GlassScorer for an overlarge building without an overlarge stack
            building.add(new Die("G6"), 1, 1);

            building.add(new Die("G6"), 1, 2);
            building.add(new Die("W2"), 1, 2); // Descending stack

            building.add(new Die("G6"), 2, 2);
            building.add(new Die("G6"), 2, 2);

            building.add(new Die("G6"), 3, 1);
            building.add(new Die("G6"), 3, 2);

            GlassScorer scorer = new GlassScorer(building);

            // When you get the glass score for that building
            int score = scorer.score();

            // Then it should be 0, because the building is not valid
            assertEquals(0, score);
        }
    }
}
