package test.scoringresult;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import main.blueprint.Blueprint;
import main.building.Building;
import main.building.Die;
import main.scoringresult.ScoringResult;
import main.scoringresult.ScoringResultFileWriter;

public class ScoringResultFileWriterTests {
    private static final String ANY_SCORING_RESULTS_PATH = "scoring-results.txt";
    private Path tempScoringResultsPath;

    @TempDir
    private Path scoringResultTempDir;

    @BeforeEach
    public void setUp() throws Exception {
        tempScoringResultsPath = scoringResultTempDir.resolve(ANY_SCORING_RESULTS_PATH);
    }

    @Test
    public void instructions_example_1() throws Exception {
        Blueprint blueprint = new Blueprint("X1 31 1X");

        Building building = new Building();
        building.add(new Die("W3"), 1, 2);

        building.add(new Die("G5"), 2, 1);
        building.add(new Die("R5"), 2, 1);
        building.add(new Die("S6"), 2, 1);

        building.add(new Die("W4"), 2, 2);

        building.add(new Die("R1"), 3, 1);

        ScoringResult result = new ScoringResult(blueprint, building);
        ScoringResultFileWriter writer = new ScoringResultFileWriter(result);

        writer.write(tempScoringResultsPath.toString());

        List<String> expectedLines = List.of(
                "X1",
                "31",
                "1X",
                "",
                "--|--",
                "--|--",
                "--|W3",
                "==+==",
                "S6|--",
                "R5|--",
                "G5|W4",
                "==+==",
                "--|--",
                "--|--",
                "R1|--",
                "",
                "+-----------+----+",
                "| glass     |  5 |",
                "| recycled  |  5 |",
                "| stone     |  5 |",
                "| wood      |  6 |",
                "| **bonus** |  6 |",
                "+===========+====+",
                "| total     | 27 |",
                "+-----------+----+",
                "",
                "Rule violations: NONE");
        List<String> actualLines = Files.readAllLines(tempScoringResultsPath);

        assertIterableEquals(expectedLines, actualLines);

    }

    @Test
    public void instructions_example_2() throws Exception {
        Blueprint blueprint = new Blueprint("X1 31 1X");

        Building building = new Building();
        building.add(new Die("W3"), 1, 2);
        building.add(new Die("W4"), 1, 2);

        building.add(new Die("R5"), 2, 1);
        building.add(new Die("S6"), 2, 1);

        building.add(new Die("R1"), 3, 1);
        building.add(new Die("G5"), 3, 1);

        ScoringResult result = new ScoringResult(blueprint, building);
        ScoringResultFileWriter writer = new ScoringResultFileWriter(result);

        writer.write(tempScoringResultsPath.toString());

        List<String> expectedLines = List.of(
                "X1",
                "31",
                "1X",
                "",
                "--|W4",
                "--|W3",
                "==+==",
                "S6|--",
                "R5|--",
                "==+==",
                "G5|--",
                "R1|--",
                "",
                "+-----------+----+",
                "| glass     |  5 |",
                "| recycled  |  5 |",
                "| stone     |  3 |",
                "| wood      |  4 |",
                "| **bonus** |  0 |",
                "+===========+====+",
                "| total     | 17 |",
                "+-----------+----+",
                "",
                "Rule violations: NONE");
        List<String> actualLines = Files.readAllLines(tempScoringResultsPath);

        assertIterableEquals(expectedLines, actualLines);

    }

    @Test
    public void instructions_example_3() throws Exception {
        Blueprint blueprint = new Blueprint("X1 31 1X");

        Building building = new Building();
        building.add(new Die("W4"), 1, 1);
        building.add(new Die("W3"), 1, 1);

        building.add(new Die("R1"), 1, 2);
        building.add(new Die("R5"), 1, 2);
        building.add(new Die("G5"), 1, 2);
        building.add(new Die("S6"), 1, 2);

        ScoringResult result = new ScoringResult(blueprint, building);
        ScoringResultFileWriter writer = new ScoringResultFileWriter(result);

        writer.write(tempScoringResultsPath.toString());

        List<String> expectedLines = List.of(
                "X1",
                "31",
                "1X",
                "",
                "--|S6",
                "--|G5",
                "W3|R5",
                "W4|R1",
                "==+==",
                "--|--",
                "--|--",
                "--|--",
                "--|--",
                "==+==",
                "--|--",
                "--|--",
                "--|--",
                "--|--",
                "",
                "+-----------+----+",
                "| glass     |  0 |",
                "| recycled  |  0 |",
                "| stone     |  0 |",
                "| wood      |  0 |",
                "| **bonus** |  0 |",
                "+===========+====+",
                "| total     |  0 |",
                "+-----------+----+",
                "",
                "Rule violations: [DESCENDING_DICE, INVALID_PLACEMENT]");
        List<String> actualLines = Files.readAllLines(tempScoringResultsPath);

        assertIterableEquals(expectedLines, actualLines);

    }

    @Test
    public void instructions_example_4() throws Exception {
        Blueprint blueprint = new Blueprint("X1 31 1X");

        Building building = new Building();

        ScoringResult result = new ScoringResult(blueprint, building);
        ScoringResultFileWriter writer = new ScoringResultFileWriter(result);

        writer.write(tempScoringResultsPath.toString());

        List<String> expectedLines = List.of(
                "X1",
                "31",
                "1X",
                "",
                "<< EMPTY BUILDING >>",
                "",
                "+-----------+----+",
                "| glass     |  0 |",
                "| recycled  |  0 |",
                "| stone     |  0 |",
                "| wood      |  0 |",
                "| **bonus** |  0 |",
                "+===========+====+",
                "| total     |  0 |",
                "+-----------+----+",
                "",
                "Rule violations: NONE");
        List<String> actualLines = Files.readAllLines(tempScoringResultsPath);

        assertIterableEquals(expectedLines, actualLines);

    }

}
