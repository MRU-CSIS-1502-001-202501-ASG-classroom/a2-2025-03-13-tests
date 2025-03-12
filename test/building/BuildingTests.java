package test.building;

import static java.util.Comparator.comparing;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import main.building.Building;
import main.building.Die;
import main.building.Material;

public class BuildingTests {
    private static final Die ANY_DIE = new Die("W3");

    @Nested
    class NoArgConstructorTests {
        @Test
        public void new_building_has_height_0() {
            Building building = new Building();

            assertEquals(0, building.getHeight());
        }

        @Test
        public void new_building_has_no_dice() {
            Building building = new Building();

            assertEquals(0, building.getNumDice());
            assertTrue(building.isEmpty());
        }

        @Test
        public void new_building_is_valid() {
            Building building = new Building();

            assertTrue(building.isValid());
        }

        @Test
        public void new_building_has_no_violations() {
            Building building = new Building();

            assertFalse(building.getViolations().hasViolations());
        }
    }

    @Nested
    class CopyConstructorTests {
        @Test
        public void copy_constructor_works() {
            // Given a building with one die in it
            Building originalBuilding = new Building();
            originalBuilding.add(new Die("G3"), 2, 1);

            // When we store the original's basic properties
            int originalHeight = originalBuilding.getHeight();
            int originalNumDice = originalBuilding.getNumDice();
            boolean originalIsValid = originalBuilding.isValid();
            boolean originalViolations = originalBuilding.getViolations().hasViolations();
            Die originalDie = originalBuilding.getDie(2, 1, 1);

            // And when we make a copy of that building
            Building copyOfBuilding = new Building(originalBuilding);

            // Then they copy has the same basic properties as the original
            assertEquals(originalHeight, copyOfBuilding.getHeight());
            assertEquals(originalNumDice, copyOfBuilding.getNumDice());
            assertEquals(originalIsValid, copyOfBuilding.isValid());
            assertEquals(originalViolations, copyOfBuilding.getViolations().hasViolations());
            assertEquals(originalDie, copyOfBuilding.getDie(2, 1, 1));

            // And when we alter the original
            originalBuilding.add(new Die("W2"), 2, 1); // Descending die!

            // Then the copy hasn't changed.
            assertEquals(originalHeight, copyOfBuilding.getHeight());
            assertEquals(originalNumDice, copyOfBuilding.getNumDice());
            assertEquals(originalIsValid, copyOfBuilding.isValid());
            assertEquals(originalViolations, copyOfBuilding.getViolations().hasViolations());
            assertEquals(originalDie, copyOfBuilding.getDie(2, 1, 1));
            assertNull(copyOfBuilding.getDie(2, 1, 2));
        }
    }

    @Nested
    class AddDieTests {
        private Building building;

        @BeforeEach
        public void setUp() {
            this.building = new Building();
        }

        @Test
        public void adding_dice_in_one_place_increases_height_and_num_dice() {
            building.add(ANY_DIE, 1, 2);

            assertEquals(1, building.getHeight());
            assertEquals(1, building.getNumDice());
            assertFalse(building.isEmpty());

            building.add(ANY_DIE, 1, 2);

            assertEquals(2, building.getHeight());
            assertEquals(2, building.getNumDice());
        }

        @Test
        public void adding_dice_but_not_stacked_increases_num_dice_but_doesnt_increase_height() {
            building.add(ANY_DIE, 1, 1);
            building.add(ANY_DIE, 1, 2);
            assertEquals(2, building.getNumDice());
            assertEquals(1, building.getHeight());

            building.add(ANY_DIE, 2, 1);
            building.add(ANY_DIE, 2, 2);
            assertEquals(4, building.getNumDice());
            assertEquals(1, building.getHeight());

            building.add(ANY_DIE, 3, 1);
            building.add(ANY_DIE, 3, 2);
            assertEquals(6, building.getNumDice());
            assertEquals(1, building.getHeight());
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "1, 2",
                "2, 1",
                "2, 2",
                "3, 1",
                "3, 2"
        })
        public void adding_desending_dice_creates_an_invalid_building_with_descending_violation(int row, int col) {
            building.add(new Die("S4"), row, col);
            building.add(new Die("W2"), row, col);

            assertFalse(building.isValid());
            assertEquals("[DESCENDING_DICE]", building.getViolations().toString());
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "1, 2",
                "2, 1",
                "2, 2",
                "3, 1",
                "3, 2"
        })
        public void adding_too_many_dice_to_location_creates_an_invalid_building_with_overlarge_violations(int row,
                int col) {
            building.add(ANY_DIE, row, col);
            building.add(ANY_DIE, row, col);
            building.add(ANY_DIE, row, col);
            building.add(ANY_DIE, row, col);
            building.add(ANY_DIE, row, col);
            building.add(ANY_DIE, row, col);
            building.add(ANY_DIE, row, col);

            assertFalse(building.isValid());
            assertEquals("[BUILDING_OVERLARGE, STACK_OVERLARGE]", building.getViolations().toString());
        }
    }

    @Nested
    class HeightTests {
        @Test
        public void building_with_one_die_has_height_1() {
            for (int row = 1; row <= 3; row++) {
                for (int col = 1; col <= 2; col++) {
                    Building building = new Building();
                    building.add(new Die("G1"), row, col);

                    assertEquals(1, building.getHeight());
                }
            }
        }

        @Test
        public void building_with_one_die_in_each_location_has_height_1() {
            Building building = new Building();
            for (int row = 1; row <= 3; row++) {
                for (int col = 1; col <= 2; col++) {

                    building.add(new Die("G1"), row, col);

                }
            }
            assertEquals(1, building.getHeight());
        }

        @Test
        public void building_with_one_die_in_each_location_but_one_with_two_has_height_2() {
            Building building = new Building();
            building.add(new Die("W4"), 2, 2);
            building.add(new Die("R6"), 2, 2);

            for (int row = 1; row <= 3; row++) {
                for (int col = 1; col <= 2; col++) {
                    if (row == 2 && col == 2) {
                        continue;
                    }
                    building.add(new Die("R5"), row, col);
                }
            }
            assertEquals(2, building.getHeight());
        }

        @Test
        public void building_with_stacks_of_heights_3_2_1_has_height_3() {
            Building building = new Building();

            building.add(new Die("W4"), 3, 2);
            building.add(new Die("R6"), 3, 2);
            building.add(new Die("W6"), 3, 2);

            building.add(new Die("W4"), 1, 2);
            building.add(new Die("R6"), 1, 2);

            building.add(new Die("W4"), 2, 1);

            assertEquals(3, building.getHeight());
        }

        @Test
        // Yes, it's invalid, but it's still possible!
        public void building_with_2_stacks_of_6_has_height_6() {
            Building building = new Building();

            for (int count = 1; count <= 6; count++) {
                building.add(new Die("S" + count), 1, 2);
                building.add(new Die("R" + count), 3, 1);
            }

            assertEquals(6, building.getHeight());
        }
    }

    @Nested
    class AllDiceOfGivenMaterialTests {
        private Building building;

        private String sorted(ArrayList<Die> dice) {
            dice.sort(comparing(Die::toString));
            return dice.toString();
        }

        @BeforeEach
        public void setUp() {
            this.building = new Building();
        }

        @Test
        public void empty_building_will_return_empty_list() {
            assertEquals("[]", building.all(Material.GLASS).toString());
            assertEquals("[]", building.all(Material.WOOD).toString());
            assertEquals("[]", building.all(Material.RECYCLED).toString());
            assertEquals("[]", building.all(Material.STONE).toString());
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "1, 2",
                "2, 1",
                "2, 2",
                "3, 1",
                "3, 2"
        })
        public void building_with_1_glass_will_return_list_with_glass_but_others_will_return_empty(int row, int col) {

            building.add(new Die("G1"), row, col);

            assertEquals("[G1]", building.all(Material.GLASS).toString());
            assertEquals("[]", building.all(Material.WOOD).toString());
            assertEquals("[]", building.all(Material.RECYCLED).toString());
            assertEquals("[]", building.all(Material.STONE).toString());
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "1, 2",
                "2, 1",
                "2, 2",
                "3, 1",
                "3, 2"
        })
        public void building_with_1_stone_will_return_list_with_stone_but_others_will_return_empty(int row, int col) {

            building.add(new Die("S2"), row, col);

            assertEquals("[S2]", building.all(Material.STONE).toString());
            assertEquals("[]", building.all(Material.WOOD).toString());
            assertEquals("[]", building.all(Material.RECYCLED).toString());
            assertEquals("[]", building.all(Material.GLASS).toString());
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "1, 2",
                "2, 1",
                "2, 2",
                "3, 1",
                "3, 2"
        })
        public void building_with_1_recycled_will_return_list_with_recycled_but_others_will_return_empty(int row,
                int col) {

            building.add(new Die("R4"), row, col);

            assertEquals("[R4]", building.all(Material.RECYCLED).toString());
            assertEquals("[]", building.all(Material.WOOD).toString());
            assertEquals("[]", building.all(Material.STONE).toString());
            assertEquals("[]", building.all(Material.GLASS).toString());
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1",
                "1, 2",
                "2, 1",
                "2, 2",
                "3, 1",
                "3, 2"
        })
        public void building_with_1_wood_will_return_list_with_wood_but_others_will_return_empty(int row,
                int col) {

            building.add(new Die("W6"), row, col);

            assertEquals("[W6]", building.all(Material.WOOD).toString());
            assertEquals("[]", building.all(Material.RECYCLED).toString());
            assertEquals("[]", building.all(Material.STONE).toString());
            assertEquals("[]", building.all(Material.GLASS).toString());
        }

        @Test
        public void valid_building_with_mix_of_materials_returns_expected_lists() {
            building.add(new Die("G1"), 1, 1);

            building.add(new Die("S3"), 1, 2);
            building.add(new Die("G5"), 1, 2);

            building.add(new Die("R6"), 2, 1);

            building.add(new Die("R3"), 3, 1);
            building.add(new Die("W5"), 3, 1);

            assertEquals("[G1, G5]", sorted(building.all(Material.GLASS)));
            assertEquals("[S3]", sorted(building.all(Material.STONE)));
            assertEquals("[R3, R6]", sorted(building.all(Material.RECYCLED)));
            assertEquals("[W5]", sorted(building.all(Material.WOOD)));
        }

        @Test
        public void invalid_building_with_mix_of_materials_returns_expected_lists() {
            building.add(new Die("G1"), 1, 1);
            building.add(new Die("G2"), 1, 1);
            building.add(new Die("G3"), 1, 1);
            building.add(new Die("G4"), 1, 1);
            building.add(new Die("G5"), 1, 1);
            building.add(new Die("G6"), 1, 1);

            building.add(new Die("S3"), 1, 2);
            building.add(new Die("S3"), 1, 2);
            building.add(new Die("S3"), 1, 2);
            building.add(new Die("G1"), 1, 2); // descending!

            building.add(new Die("R1"), 2, 1);
            building.add(new Die("R3"), 2, 1);
            building.add(new Die("R6"), 2, 1);

            building.add(new Die("R2"), 3, 1);
            building.add(new Die("W5"), 3, 1);
            building.add(new Die("W6"), 3, 1);

            building.add(new Die("W2"), 3, 2);

            assertEquals("[G1, G1, G2, G3, G4, G5, G6]", sorted(building.all(Material.GLASS)));
            assertEquals("[S3, S3, S3]", sorted(building.all(Material.STONE)));
            assertEquals("[R1, R2, R3, R6]", sorted(building.all(Material.RECYCLED)));
            assertEquals("[W2, W5, W6]", sorted(building.all(Material.WOOD)));
        }

    }

    @Nested
    class InvalidBuildingBehaviour {
        @Test
        public void building_with_invalid_stack_and_two_dice_has_numDice_2() {
            Building building = new Building();

            building.add(new Die("R4"), 2, 1);
            building.add(new Die("W3"), 2, 1);

            assertEquals(2, building.getNumDice());
        }

        @Test
        public void building_with_7_dice_is_invalid() {
            Building building = new Building();

            for (int count = 1; count <= 7; count++) {
                building.add(new Die("W3"), 3, 2);
            }

            assertFalse(building.isValid());
        }
    }
}
