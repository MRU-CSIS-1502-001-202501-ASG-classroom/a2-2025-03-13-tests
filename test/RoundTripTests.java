package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.BlueprintsScoringApp;

public class RoundTripTests {

    @ParameterizedTest
    @ValueSource(strings = { "01", "02", "03", "04", "05", "06", "07", "08", "09" })
    public void level_50(String testNum) throws Exception {
        BlueprintsScoringApp app = new BlueprintsScoringApp(
                buildingPath(50, testNum),
                blueprintPath(50, testNum),
                actualResultPath(50, testNum));
        app.run();

        List<String> expectedLines = only(expectedFileLines(50, testNum), "glass", "recycled");
        List<String> actualLines = only(actualFileLines(50, testNum), "glass", "recycled");

        assertIterableEquals(expectedLines, actualLines);
    }

    @ParameterizedTest
    @ValueSource(strings = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23" })
    public void level_66(String testNum) throws Exception {
        BlueprintsScoringApp app = new BlueprintsScoringApp(
                buildingPath(66, testNum),
                blueprintPath(66, testNum),
                actualResultPath(66, testNum));
        app.run();

        List<String> expectedLines = expectedFileLines(66, testNum);
        List<String> actualLines = actualFileLines(66, testNum);

        // Confirm that the Blueprint is present in the first three lines of the
        // produced scoring report.
        List<String> expectedBlueprintLines = expectedBlueprintLines(66, testNum);
        List<String> actualBlueprintLines = actualLines.subList(0, 3);

        assertIterableEquals(expectedBlueprintLines, actualBlueprintLines);

        // Confirm that the scores (including format) for glass, recycled, and bonus are
        // as expected.
        List<String> expectedScoringLines = only(expectedLines, "glass", "recycled", "bonus");
        List<String> actualScoringLines = only(actualLines, "glass", "recycled", "bonus");

        assertIterableEquals(expectedScoringLines, actualScoringLines);

        // Confirm that the last line of the generated report has the expected rules
        // violation message.
        int expectedLinesSize = expectedLines.size();
        int actualLinesSize = actualLines.size();

        String expectedLastLine = expectedLines.get(expectedLinesSize - 1);
        String actualLastLine = actualLines.get(actualLinesSize - 1);

        assertEquals(expectedLastLine, actualLastLine);
    }

    @ParameterizedTest
    @ValueSource(strings = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23" })
    public void level_77(String testNum) throws Exception {
        BlueprintsScoringApp app = new BlueprintsScoringApp(
                buildingPath(77, testNum),
                blueprintPath(77, testNum),
                actualResultPath(77, testNum));
        app.run();

        List<String> expectedLines = not(expectedFileLines(77, testNum), "wood", "total");
        List<String> actualLines = not(actualFileLines(77, testNum), "wood", "total");

        assertIterableEquals(expectedLines, actualLines);
    }

    @ParameterizedTest
    @ValueSource(strings = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23" })
    public void level_90(String testNum) throws Exception {
        BlueprintsScoringApp app = new BlueprintsScoringApp(
                buildingPath(90, testNum),
                blueprintPath(90, testNum),
                actualResultPath(90, testNum));
        app.run();

        List<String> expectedLines = expectedFileLines(90, testNum);
        List<String> actualLines = actualFileLines(90, testNum);

        assertIterableEquals(expectedLines, actualLines);
    }

    private String buildingPath(int level, String testNum) {
        return String.format("res/marking/LEVEL-%d/building-%s.txt", level, testNum);
    }

    private String blueprintPath(int level, String testNum) {
        return String.format("res/marking/LEVEL-%d/blueprint-%s.txt", level, testNum);
    }

    private String expectedResultPath(int level, String testNum) {
        return String.format("res/marking/LEVEL-%d/expected-scoring-result-%s.txt", level, testNum);
    }

    private String actualResultPath(int level, String testNum) {
        return String.format("res/marking/LEVEL-%d/scoring-result-%s.txt", level, testNum);
    }

    private List<String> expectedBlueprintLines(int level, String testNum) throws Exception {
        Path expectedBlueprintPath = Paths.get(blueprintPath(level, testNum));
        return Files.readAllLines(expectedBlueprintPath);
    }

    private List<String> expectedFileLines(int level, String testNum) throws Exception {
        Path expectedResultPath = Paths.get(expectedResultPath(level, testNum));
        return Files.readAllLines(expectedResultPath);
    }

    private List<String> actualFileLines(int level, String testNum) throws Exception {
        Path actualResultPath = Paths.get(actualResultPath(level, testNum));
        return Files.readAllLines(actualResultPath);
    }

    /**
     * Return only those lines of text that contain a given set of filters.
     * 
     * Used for round-trip tests where you want to be more flexible with scoring -
     * for example, you could just focus on scoring for glass or wood and ignore
     * everything else.
     * 
     * Note if filters is empty, then all lines of text are returned as-is.
     */
    private List<String> only(List<String> linesOfText, String... filters) {
        if (filters.length == 0) {
            return linesOfText;
        }

        List<String> result = new ArrayList<>();
        for (String lineOfText : linesOfText) {
            if (containsFiltered(lineOfText, filters)) {
                result.add(lineOfText);
            }
        }

        return result;
    }

    /**
     * Return only those lines of text that DON'T contain a given set of filters.
     * 
     * Used for round-trip tests where you want to be more flexible with scoring -
     * for example, you could just focus on scoring for glass or wood and ignore
     * everything else.
     * 
     * Note if filters is empty, then all lines of text are returned as-is.
     */
    private List<String> not(List<String> linesOfText, String... filters) {
        if (filters.length == 0) {
            return linesOfText;
        }

        List<String> result = new ArrayList<>();
        for (String lineOfText : linesOfText) {
            if (!containsFiltered(lineOfText, filters)) {
                result.add(lineOfText);
            }
        }

        return result;
    }

    private boolean containsFiltered(String text, String[] filters) {
        for (String filter : filters) {
            if (text.contains(filter)) {
                return true;
            }
        }
        return false;
    }

}
