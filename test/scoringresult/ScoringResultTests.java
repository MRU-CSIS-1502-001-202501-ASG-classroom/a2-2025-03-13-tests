package test.scoringresult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import main.blueprint.Blueprint;
import main.building.Building;
import main.building.Die;
import main.scoringresult.ScoringResult;
import main.violations.ViolationList;

public class ScoringResultTests {

    @Nested
    class GetBonusScoreTests {
        @Test
        public void bonus_given_if_all_spaces_filled_exactly() {
            // Given a ScoringResult for a valid Building that
            // fills all expected Blueprint spaces exactly
            Blueprint blueprint = new Blueprint("1X 21 1X");

            Building building = new Building();
            building.add(new Die("W1"), 1, 1);
            building.add(new Die("G2"), 2, 1);
            building.add(new Die("G3"), 2, 1);
            building.add(new Die("R5"), 2, 2);
            building.add(new Die("S2"), 3, 1);

            ScoringResult result = new ScoringResult(blueprint, building);

            // When the result is asked for the bonus score
            int bonus = result.getBonusScore();

            // Then it's the expected glass score
            assertEquals(6, bonus);

        }

        @Test
        public void bonus_not_given_if_all_spaces_not_filled_exactly() {
            // Given a ScoringResult for a valid Building that
            // fills all expected Blueprint spaces exactly
            Blueprint blueprint = new Blueprint("1X 21 1X");

            Building building = new Building();
            building.add(new Die("W1"), 1, 1);
            building.add(new Die("G5"), 2, 1);
            building.add(new Die("G1"), 2, 1);
            building.add(new Die("R5"), 2, 1); // Too many dice for space
            building.add(new Die("S2"), 3, 1);

            ScoringResult result = new ScoringResult(blueprint, building);

            // When the result is asked for the bonus score
            int bonus = result.getBonusScore();

            // Then it's the expected glass score
            assertEquals(0, bonus);

        }

        @Test
        public void bonus_not_given_if_all_spaces_filled_exactly_but_descending_dice_violation_present() {
            // Given a ScoringResult for a valid Building that
            // fills all expected Blueprint spaces exactly
            Blueprint blueprint = new Blueprint("1X 21 1X");

            Building building = new Building();
            building.add(new Die("W1"), 1, 1);
            building.add(new Die("G5"), 2, 1);
            building.add(new Die("G1"), 2, 1); // Descending dice!
            building.add(new Die("R5"), 2, 2);
            building.add(new Die("S2"), 3, 1);

            ScoringResult result = new ScoringResult(blueprint, building);

            // When the result is asked for the bonus score
            int bonus = result.getBonusScore();

            // Then it's the expected glass score
            assertEquals(0, bonus);

        }
    }

    @Nested
    class GetGlassScoreTests {
        @Test
        public void scorer_with_no_violations_reports_normal_glass_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and no violations
            Blueprint blueprint = new Blueprint("11 11 11");
            Building building = new Building();
            building.add(new Die("G2"), 1, 2);
            building.add(new Die("G6"), 3, 2);

            ScoringResult result = new ScoringResult(blueprint, building);

            // When the result is asked for the glass score
            int glassScore = result.getGlassScore();

            // Then it's the expected glass score
            assertEquals(8, glassScore);
        }

        @Test
        public void scorer_with_descending_dice_violation_reports_0_glass_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and descending dice violation
            Blueprint blueprint = new Blueprint("21 11 11");
            Building descendingDiceBuilding = new Building();
            descendingDiceBuilding.add(new Die("G6"), 1, 1);
            descendingDiceBuilding.add(new Die("G2"), 1, 1); // Descending!

            ScoringResult result = new ScoringResult(blueprint, descendingDiceBuilding);

            // When the result is asked for the glass score
            int glassScore = result.getGlassScore();

            // Then it's 0
            assertEquals(0, glassScore);
        }

        @Test
        public void scorer_with_overlarge_building_violation_reports_0_glass_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and overlarge building violation
            Blueprint blueprint = new Blueprint("11 11 12");
            Building overlargeBuilding = new Building();
            overlargeBuilding.add(new Die("G6"), 1, 1);
            overlargeBuilding.add(new Die("G2"), 1, 2);

            overlargeBuilding.add(new Die("G6"), 2, 1);
            overlargeBuilding.add(new Die("G2"), 2, 2);

            overlargeBuilding.add(new Die("G6"), 3, 1);
            overlargeBuilding.add(new Die("G2"), 3, 2);

            overlargeBuilding.add(new Die("G6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeBuilding);

            // When the result is asked for the glass score
            int glassScore = result.getGlassScore();

            // Then it's 0
            assertEquals(0, glassScore);
        }

        @Test
        public void scorer_with_overlarge_stack_violation_reports_0_glass_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and overlarge stack violation
            Blueprint blueprint = new Blueprint("71 11 12");
            Building overlargeStackBuilding = new Building();
            overlargeStackBuilding.add(new Die("G1"), 1, 1);
            overlargeStackBuilding.add(new Die("G2"), 1, 2);
            overlargeStackBuilding.add(new Die("G3"), 2, 1);
            overlargeStackBuilding.add(new Die("G4"), 2, 2);
            overlargeStackBuilding.add(new Die("G5"), 3, 1);
            overlargeStackBuilding.add(new Die("G6"), 3, 2);
            overlargeStackBuilding.add(new Die("G6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeStackBuilding);

            // When the result is asked for the glass score
            int glassScore = result.getGlassScore();

            // Then it's 0
            assertEquals(0, glassScore);
        }

        @Test
        public void scorer_with_invalid_placement_violation_reports_0_glass_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and invalid placement violation
            Blueprint blueprint = new Blueprint("1X 11 12");
            Building invalidPlacementBuilding = new Building();
            invalidPlacementBuilding.add(new Die("G1"), 1, 1);

            invalidPlacementBuilding.add(new Die("G2"), 1, 2); // Invalid placement!

            ScoringResult result = new ScoringResult(blueprint, invalidPlacementBuilding);

            // When the result is asked for the glass score
            int glassScore = result.getGlassScore();

            // Then it's 0
            assertEquals(0, glassScore);
        }
    }

    @Nested
    class GetRecycledScoreTests {
        @Test
        public void scorer_with_no_violations_reports_normal_recycled_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero recycled score and no violations
            Blueprint blueprint = new Blueprint("11 11 11");
            Building building = new Building();
            building.add(new Die("R2"), 1, 2);
            building.add(new Die("R6"), 3, 2);

            ScoringResult result = new ScoringResult(blueprint, building);

            // When the result is asked for the recycled score
            int recycledScore = result.getRecycledScore();

            // Then it's the expected glass score
            assertEquals(5, recycledScore);
        }

        @Test
        public void scorer_with_descending_dice_violation_reports_0_recycled_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and descending dice violation
            Blueprint blueprint = new Blueprint("21 11 11");
            Building descendingDiceBuilding = new Building();
            descendingDiceBuilding.add(new Die("R6"), 1, 1);
            descendingDiceBuilding.add(new Die("R2"), 1, 1); // Descending!

            ScoringResult result = new ScoringResult(blueprint, descendingDiceBuilding);

            // When the result is asked for the glass score
            int recycledScore = result.getRecycledScore();

            // Then it's 0
            assertEquals(0, recycledScore);
        }

        @Test
        public void scorer_with_overlarge_building_violation_reports_0_recycled_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and overlarge building violation
            Blueprint blueprint = new Blueprint("11 11 12");
            Building overlargeBuilding = new Building();
            overlargeBuilding.add(new Die("R6"), 1, 1);
            overlargeBuilding.add(new Die("R2"), 1, 2);

            overlargeBuilding.add(new Die("R6"), 2, 1);
            overlargeBuilding.add(new Die("R2"), 2, 2);

            overlargeBuilding.add(new Die("R6"), 3, 1);
            overlargeBuilding.add(new Die("R2"), 3, 2);

            overlargeBuilding.add(new Die("R6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeBuilding);

            // When the result is asked for the glass score
            int recycledScore = result.getRecycledScore();

            // Then it's 0
            assertEquals(0, recycledScore);
        }

        @Test
        public void scorer_with_overlarge_stack_violation_reports_0_recycled_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and overlarge stack violation
            Blueprint blueprint = new Blueprint("71 11 12");
            Building overlargeStackBuilding = new Building();
            overlargeStackBuilding.add(new Die("R1"), 1, 1);
            overlargeStackBuilding.add(new Die("R2"), 1, 2);
            overlargeStackBuilding.add(new Die("R3"), 2, 1);
            overlargeStackBuilding.add(new Die("R4"), 2, 2);
            overlargeStackBuilding.add(new Die("R5"), 3, 1);
            overlargeStackBuilding.add(new Die("R6"), 3, 2);
            overlargeStackBuilding.add(new Die("R6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeStackBuilding);

            // When the result is asked for the glass score
            int recycledScore = result.getRecycledScore();

            // Then it's 0
            assertEquals(0, recycledScore);
        }

        @Test
        public void scorer_with_invalid_placement_violation_reports_0_recycled_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero glass score and invalid placement violation
            Blueprint blueprint = new Blueprint("1X 11 12");
            Building invalidPlacementBuilding = new Building();
            invalidPlacementBuilding.add(new Die("R1"), 1, 1);

            invalidPlacementBuilding.add(new Die("R2"), 1, 2); // Invalid placement!

            ScoringResult result = new ScoringResult(blueprint, invalidPlacementBuilding);

            // When the result is asked for the glass score
            int recycledScore = result.getRecycledScore();

            // Then it's 0
            assertEquals(0, recycledScore);
        }
    }

    @Nested
    class GetStoneScoreTests {
        @Test
        public void scorer_with_no_violations_reports_normal_stone_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero stone score and no violations
            Blueprint blueprint = new Blueprint("22 11 11");
            Building building = new Building();
            building.add(new Die("S2"), 1, 2);
            building.add(new Die("S6"), 1, 2);

            ScoringResult result = new ScoringResult(blueprint, building);

            // When the result is asked for the stone score
            int stoneScore = result.getStoneScore();

            // Then it's the expected stone score
            assertEquals(5, stoneScore);
        }

        @Test
        public void scorer_with_descending_dice_violation_reports_0_stone_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero stone score and descending dice violation
            Blueprint blueprint = new Blueprint("21 11 11");
            Building descendingDiceBuilding = new Building();
            descendingDiceBuilding.add(new Die("S6"), 1, 1);
            descendingDiceBuilding.add(new Die("S2"), 1, 1); // Descending!

            ScoringResult result = new ScoringResult(blueprint, descendingDiceBuilding);

            // When the result is asked for the stone score
            int stoneScore = result.getStoneScore();

            // Then it's 0
            assertEquals(0, stoneScore);
        }

        @Test
        public void scorer_with_overlarge_building_violation_reports_0_stone_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero stone score and overlarge building violation
            Blueprint blueprint = new Blueprint("11 11 12");
            Building overlargeBuilding = new Building();
            overlargeBuilding.add(new Die("S6"), 1, 1);
            overlargeBuilding.add(new Die("S2"), 1, 2);

            overlargeBuilding.add(new Die("S6"), 2, 1);
            overlargeBuilding.add(new Die("S2"), 2, 2);

            overlargeBuilding.add(new Die("S6"), 3, 1);
            overlargeBuilding.add(new Die("S2"), 3, 2);

            overlargeBuilding.add(new Die("S6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeBuilding);

            // When the result is asked for the stone score
            int stoneScore = result.getStoneScore();

            // Then it's 0
            assertEquals(0, stoneScore);
        }

        @Test
        public void scorer_with_overlarge_stack_violation_reports_0_stone_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero stone score and overlarge stack violation
            Blueprint blueprint = new Blueprint("71 11 12");
            Building overlargeStackBuilding = new Building();
            overlargeStackBuilding.add(new Die("S1"), 1, 1);
            overlargeStackBuilding.add(new Die("S2"), 1, 2);
            overlargeStackBuilding.add(new Die("S3"), 2, 1);
            overlargeStackBuilding.add(new Die("S4"), 2, 2);
            overlargeStackBuilding.add(new Die("S5"), 3, 1);
            overlargeStackBuilding.add(new Die("S6"), 3, 2);
            overlargeStackBuilding.add(new Die("S6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeStackBuilding);

            // When the result is asked for the stone score
            int stoneScore = result.getStoneScore();

            // Then it's 0
            assertEquals(0, stoneScore);
        }

        @Test
        public void scorer_with_invalid_placement_violation_reports_0_stone_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero stone score and invalid placement violation
            Blueprint blueprint = new Blueprint("1X 11 12");
            Building invalidPlacementBuilding = new Building();
            invalidPlacementBuilding.add(new Die("S1"), 1, 1);

            invalidPlacementBuilding.add(new Die("S2"), 1, 2); // Invalid placement!

            ScoringResult result = new ScoringResult(blueprint, invalidPlacementBuilding);

            // When the result is asked for the stone score
            int stoneScore = result.getStoneScore();

            // Then it's 0
            assertEquals(0, stoneScore);
        }
    }

    @Nested
    class GetWoodScoreTests {
        @Test
        public void scorer_with_no_violations_reports_normal_wood_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero wood score and no violations
            Blueprint blueprint = new Blueprint("22 11 11");
            Building building = new Building();
            building.add(new Die("W2"), 1, 2);
            building.add(new Die("W6"), 1, 2);

            ScoringResult result = new ScoringResult(blueprint, building);

            // When the result is asked for the wood score
            int woodScore = result.getWoodScore();

            // Then it's the expected wood score
            assertEquals(4, woodScore);
        }

        @Test
        public void scorer_with_descending_dice_violation_reports_0_wood_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero wood score and descending dice violation
            Blueprint blueprint = new Blueprint("21 11 11");
            Building descendingDiceBuilding = new Building();
            descendingDiceBuilding.add(new Die("W6"), 1, 1);
            descendingDiceBuilding.add(new Die("W2"), 1, 1); // Descending!

            ScoringResult result = new ScoringResult(blueprint, descendingDiceBuilding);

            // When the result is asked for the wood score
            int woodScore = result.getWoodScore();

            // Then it's 0
            assertEquals(0, woodScore);
        }

        @Test
        public void scorer_with_overlarge_building_violation_reports_0_wood_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero wood score and overlarge building violation
            Blueprint blueprint = new Blueprint("11 11 12");
            Building overlargeBuilding = new Building();
            overlargeBuilding.add(new Die("W6"), 1, 1);
            overlargeBuilding.add(new Die("W2"), 1, 2);

            overlargeBuilding.add(new Die("W6"), 2, 1);
            overlargeBuilding.add(new Die("W2"), 2, 2);

            overlargeBuilding.add(new Die("W6"), 3, 1);
            overlargeBuilding.add(new Die("W2"), 3, 2);

            overlargeBuilding.add(new Die("W6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeBuilding);

            // When the result is asked for the wood score
            int woodScore = result.getWoodScore();

            // Then it's 0
            assertEquals(0, woodScore);
        }

        @Test
        public void scorer_with_overlarge_stack_violation_reports_0_wood_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero wood score and overlarge stack violation
            Blueprint blueprint = new Blueprint("71 11 12");
            Building overlargeStackBuilding = new Building();
            overlargeStackBuilding.add(new Die("W1"), 1, 1);
            overlargeStackBuilding.add(new Die("W2"), 1, 2);
            overlargeStackBuilding.add(new Die("W3"), 2, 1);
            overlargeStackBuilding.add(new Die("W4"), 2, 2);
            overlargeStackBuilding.add(new Die("W5"), 3, 1);
            overlargeStackBuilding.add(new Die("W6"), 3, 2);
            overlargeStackBuilding.add(new Die("W6"), 3, 2); // Too many dice!

            ScoringResult result = new ScoringResult(blueprint, overlargeStackBuilding);

            // When the result is asked for the wood score
            int woodScore = result.getWoodScore();

            // Then it's 0
            assertEquals(0, woodScore);
        }

        @Test
        public void scorer_with_invalid_placement_violation_reports_0_wood_score() {
            // Given a ScoringResult for a blueprint/building combo
            // with a non-zero wood score and invalid placement violation
            Blueprint blueprint = new Blueprint("1X 11 12");
            Building invalidPlacementBuilding = new Building();
            invalidPlacementBuilding.add(new Die("W1"), 1, 1);

            invalidPlacementBuilding.add(new Die("W2"), 1, 2); // Invalid placement!

            ScoringResult result = new ScoringResult(blueprint, invalidPlacementBuilding);

            // When the result is asked for the wood score
            int woodScore = result.getWoodScore();

            // Then it's 0
            assertEquals(0, woodScore);
        }
    }

    @Nested
    class GetViolationsTests {

        @Nested
        class ValidPlacementCases {
            private static final Blueprint ANY_BLUEPRINT = new Blueprint("77 77 77");

            @Test
            public void no_violations_present_for_any_blueprint_and_empty_building() {
                // Given a ScoringResult made from any Blueprint and empty Building
                Building emptyBuilding = new Building();
                ScoringResult result = new ScoringResult(ANY_BLUEPRINT, emptyBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be no violations
                assertFalse(violations.hasViolations());
            }

            @Test
            public void no_violations_present_for_any_blueprint_and_valid_building() {
                // Given a ScoringResult made from any Blueprint and valid Building
                Building validBuilding = new Building();
                validBuilding.add(new Die("G3"), 1, 1);
                validBuilding.add(new Die("W3"), 3, 2);

                ScoringResult result = new ScoringResult(ANY_BLUEPRINT, validBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be no violations
                assertFalse(violations.hasViolations());
            }

            @Test
            public void descending_dice_violation_present_for_any_blueprint_but_building_with_descending_dice() {
                // Given a ScoringResult made from any Blueprint and Building with descending
                // dice
                Building descDiceBuilding = new Building();
                descDiceBuilding.add(new Die("S5"), 1, 2);
                descDiceBuilding.add(new Die("R4"), 1, 2);

                ScoringResult result = new ScoringResult(ANY_BLUEPRINT, descDiceBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be a DESCENDING_DICE violation
                assertEquals("[DESCENDING_DICE]", violations.toString());
            }

            @Test
            public void building_overlarge_violation_present_for_any_blueprint_but_overlarge_building() {
                // Given a ScoringResult made from any Blueprint and Building with too many
                // dice (but no overlarge stacks)
                Building overlargeBuilding = new Building();
                overlargeBuilding.add(new Die("S5"), 1, 1);
                overlargeBuilding.add(new Die("R5"), 1, 1);

                overlargeBuilding.add(new Die("S5"), 2, 2);
                overlargeBuilding.add(new Die("R5"), 2, 2);

                overlargeBuilding.add(new Die("S5"), 3, 1);
                overlargeBuilding.add(new Die("R5"), 3, 1);
                overlargeBuilding.add(new Die("G6"), 3, 1); // Too many dice!

                ScoringResult result = new ScoringResult(ANY_BLUEPRINT, overlargeBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be a BUILDING_OVERLARGE violation
                assertEquals("[BUILDING_OVERLARGE]", violations.toString());
            }

            @Test
            public void stack_overlarge_violation_present_for_any_blueprint_but_building_with_overlarge_stack() {
                // Given a ScoringResult made from any Blueprint and Building with too many
                // dice in one stack
                Building overlargeStackBuilding = new Building();
                overlargeStackBuilding.add(new Die("S5"), 1, 1);
                overlargeStackBuilding.add(new Die("R5"), 1, 1);
                overlargeStackBuilding.add(new Die("S5"), 1, 1);
                overlargeStackBuilding.add(new Die("R5"), 1, 1);
                overlargeStackBuilding.add(new Die("S5"), 1, 1);
                overlargeStackBuilding.add(new Die("R5"), 1, 1);
                overlargeStackBuilding.add(new Die("G6"), 1, 1); // Too many dice in one stack!

                ScoringResult result = new ScoringResult(ANY_BLUEPRINT, overlargeStackBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be BUILDING_OVERLARGE, STACK_OVERLARGE violations
                assertEquals("[BUILDING_OVERLARGE, STACK_OVERLARGE]", violations.toString());
            }
        }

        @Nested
        class InvalidBlueprintCases {
            @ParameterizedTest
            @CsvSource({
                    "'X3 3X XX',1,1",
                    "'X3 3X XX',2,2",
                    "'X3 3X XX',3,1",
                    "'X3 3X XX',3,2"
            })
            public void ivalid_placement_violation_present_when_valid_building_but_invalid_placement(
                    String blueprintText, int badRow, int badCol) {
                // Given a ScoringResult made from any Blueprint and a building that plays on an
                // invalid space
                Blueprint blueprint = new Blueprint(blueprintText);
                Building badPlacementBuilding = new Building();
                badPlacementBuilding.add(new Die("S3"), badRow, badCol);
                ScoringResult result = new ScoringResult(blueprint, badPlacementBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be an INVALID_PLACEMENT violation
                assertEquals("[INVALID_PLACEMENT]", violations.toString());
            }

            @Test
            public void invalid_placement_and_building_overlarge_violations() {
                // Given a ScoringResult made from any Blueprint and Building with too many
                // dice (but no overlarge stacks) and that plays on an invalid space
                Blueprint blueprint = new Blueprint("33 3X 33");

                Building overlargeBuilding = new Building();
                overlargeBuilding.add(new Die("S5"), 1, 1);
                overlargeBuilding.add(new Die("R5"), 1, 1);

                overlargeBuilding.add(new Die("S5"), 2, 2); // Invalid space!
                overlargeBuilding.add(new Die("R5"), 2, 2); // Invalid space!

                overlargeBuilding.add(new Die("S5"), 3, 1);
                overlargeBuilding.add(new Die("R5"), 3, 1);
                overlargeBuilding.add(new Die("G6"), 3, 1); // Too many dice!

                ScoringResult result = new ScoringResult(blueprint, overlargeBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be BUILDING_OVERLARGE, INVALID_PLACEMENT violations
                assertEquals("[BUILDING_OVERLARGE, INVALID_PLACEMENT]", violations.toString());
            }

            @Test
            public void invalid_placement_and_stack_overlarge_violations() {
                // Given a ScoringResult made from any Blueprint and Building with too many
                // dice in one stack
                Blueprint blueprint = new Blueprint("X1 11 11");

                Building overlargeStackBuilding = new Building();
                overlargeStackBuilding.add(new Die("S5"), 1, 1); // Invalid placement!
                overlargeStackBuilding.add(new Die("R5"), 1, 1);
                overlargeStackBuilding.add(new Die("S5"), 1, 1);
                overlargeStackBuilding.add(new Die("R5"), 1, 1);
                overlargeStackBuilding.add(new Die("S5"), 1, 1);
                overlargeStackBuilding.add(new Die("R5"), 1, 1);
                overlargeStackBuilding.add(new Die("G6"), 1, 1); // Too many dice in one stack!

                ScoringResult result = new ScoringResult(blueprint,
                        overlargeStackBuilding);

                // When we get the violations for the ScoringResult
                ViolationList violations = result.getViolations();

                // Then there should be BUILDING_OVERLARGE, STACK_OVERLARGE violations
                assertEquals("[BUILDING_OVERLARGE, INVALID_PLACEMENT, STACK_OVERLARGE]", violations.toString());
            }
        }

    }

}
