package test.blueprint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import main.blueprint.Blueprint;
import main.blueprint.BlueprintFileReader;

public class BlueprintFileReaderTests {
        private static final String ANY_BLUEPRINT_PATH = "blueprint.txt";
        private Path tempBlueprintPath;

        @TempDir
        private Path blueprintTempDir;

        @BeforeEach
        public void setUp() throws Exception {
                tempBlueprintPath = blueprintTempDir.resolve(ANY_BLUEPRINT_PATH);

                String blueprintFileContents = "";
                blueprintFileContents += String.format("X2%n");
                blueprintFileContents += String.format("X1%n");
                blueprintFileContents += String.format("3X%n");

                Files.write(tempBlueprintPath, blueprintFileContents.getBytes());
        }

        @Test
        public void load_returns_expected_blueprint() throws Exception {

                // Given the path to a blueprint file
                String pathToBlueprint = tempBlueprintPath.toString();

                // When an attempt is made to load that blueprint
                Blueprint blueprint = BlueprintFileReader.load(pathToBlueprint);

                // Then the expected blueprint is returned
                assertEquals(0, blueprint.heightTargetAt(1, 1));
                assertEquals(2, blueprint.heightTargetAt(1, 2));

                assertEquals(0, blueprint.heightTargetAt(2, 1));
                assertEquals(1, blueprint.heightTargetAt(2, 2));

                assertEquals(3, blueprint.heightTargetAt(3, 1));
                assertEquals(0, blueprint.heightTargetAt(3, 2));
        }

}
