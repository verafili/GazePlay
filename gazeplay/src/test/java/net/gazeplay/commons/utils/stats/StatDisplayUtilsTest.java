package net.gazeplay.commons.utils.stats;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.gazeplay.GazePlay;
import net.gazeplay.commons.utils.FixationPoint;
import net.gazeplay.commons.utils.HomeButton;
import net.gazeplay.stats.ShootGamesStats;
import net.gazeplay.ui.scenes.stats.StatsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.testfx.framework.junit5.ApplicationExtension;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
@RunWith(MockitoJUnitRunner.class)
class StatDisplayUtilsTest {

    @Mock
    private GazePlay mockGazePlay;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private StatsContext mockStatsContext;

    @Mock
    private Stats mockStats;

    @Mock
    private ShootGamesStats mockShootStats;

    @Mock
    private Region mockRegion;

    @Captor
    private ArgumentCaptor<Cursor> captor;

    @BeforeEach
    void setup() {
        initMocks();
    }

    void initMocks() {
        MockitoAnnotations.initMocks(this);

        DoubleProperty mockWidth = new SimpleDoubleProperty(100);
        DoubleProperty mockHeight = new SimpleDoubleProperty(100);

        when(mockRegion.widthProperty()).thenReturn(mockWidth);
        when(mockRegion.heightProperty()).thenReturn(mockHeight);
        when(mockRegion.getWidth()).thenReturn(500d);
        when(mockRegion.getHeight()).thenReturn(500d);
    }

    MouseEvent mouseClickEvent(EventTarget target) {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED, 1, 1, 1, 1, MouseButton.PRIMARY, 1, false, false, false, false,
            false, false, false, false, false, false, new PickResult(target, 0d, 0d));
    }

    @Test
    void shouldCreateHomeButton() {
        HomeButton button = StatDisplayUtils.createHomeButtonInStatsScreen(mockGazePlay, mockStatsContext);
        assert button.isVisible();
    }

    @Test
    void shouldSetTheCursorWhenPressed() {
        StatDisplayUtils.returnToMenu(mockGazePlay, mockStatsContext);

        verify(mockStatsContext.getRoot(), times(2)).setCursor(captor.capture());
        assertTrue(captor.getAllValues().contains(Cursor.WAIT));
        assertTrue(captor.getAllValues().contains(Cursor.DEFAULT));
    }

    @Test
    void shouldBuildLineChartForNormalGame() {
        List<Long> mockShots = new ArrayList<>(List.of(1L, 2L, 3L));

        when(mockStats.getOriginalDurationsBetweenGoals()).thenReturn(mockShots);
        when(mockStats.computeRoundsDurationStandardDeviation()).thenReturn(0d);
        when(mockStats.computeRoundsDurationAverageDuration()).thenReturn(2L);

        LineChart<String, Number> lineChart = StatDisplayUtils.buildLineChart(mockStats, mockRegion);

        assertEquals(4, lineChart.getData().size());
        assertEquals(5, lineChart.getData().get(0).getData().size());
        assertEquals(5, lineChart.getData().get(1).getData().size());
        assertEquals(5, lineChart.getData().get(2).getData().size());
        assertEquals(3, lineChart.getData().get(3).getData().size());
    }

    @Test
    void shouldZoomInToLineChart() {
        List<Long> mockShots = new ArrayList<>(List.of(1L, 2L, 3L));

        when(mockStats.getOriginalDurationsBetweenGoals()).thenReturn(mockShots);
        when(mockStats.computeRoundsDurationStandardDeviation()).thenReturn(0d);
        when(mockStats.computeRoundsDurationAverageDuration()).thenReturn(2L);

        LineChart<String, Number> lineChart = StatDisplayUtils.buildLineChart(mockStats, mockRegion);
        VBox parent = new VBox();
        parent.getChildren().add(lineChart);

        lineChart.fireEvent(mouseClickEvent(lineChart));

        // These assertions are a bit rubbish - need to find a way to set the parent bounds.
        assertEquals( 0, lineChart.getTranslateX());
        assertEquals( 0, lineChart.getTranslateY());
    }

    @Test
    void shouldZoomOutOfLineChart() {
        List<Long> mockShots = new ArrayList<>(List.of(1L, 2L, 3L));

        when(mockStats.getOriginalDurationsBetweenGoals()).thenReturn(mockShots);
        when(mockStats.computeRoundsDurationStandardDeviation()).thenReturn(0d);
        when(mockStats.computeRoundsDurationAverageDuration()).thenReturn(2L);

        LineChart<String, Number> lineChart = StatDisplayUtils.buildLineChart(mockStats, mockRegion);
        VBox parent = new VBox();
        parent.getChildren().add(lineChart);

        lineChart.fireEvent(mouseClickEvent(lineChart));
        lineChart.fireEvent(mouseClickEvent(lineChart));

        assertEquals( 1, lineChart.getScaleX());
        assertEquals( 1, lineChart.getScaleY());
    }

    @Test
    void shouldBuildLineChartForShootingGame() {
        List<Long> mockShots = new ArrayList<>(List.of(1L, 2L, 3L));

        when(mockShootStats.getSortedDurationsBetweenGoals()).thenReturn(mockShots);
        when(mockShootStats.computeRoundsDurationStandardDeviation()).thenReturn(0d);
        when(mockShootStats.computeRoundsDurationAverageDuration()).thenReturn(2L);

        LineChart<String, Number> lineChart = StatDisplayUtils.buildLineChart(mockShootStats, mockRegion);

        assertEquals(4, lineChart.getData().size());
        assertEquals(5, lineChart.getData().get(0).getData().size());
        assertEquals(5, lineChart.getData().get(1).getData().size());
        assertEquals(5, lineChart.getData().get(2).getData().size());
        assertEquals(3, lineChart.getData().get(3).getData().size());
    }

    @Test
    void shouldBuildAreaChartWithPoints() {
        LinkedList<FixationPoint> mockPoints = new LinkedList<>(List.of(
            new FixationPoint(50, 50, 1, 2),
            new FixationPoint(100, 100, 2, 3),
            new FixationPoint(150, 150, 4, 5)
        ));

        AreaChart<Number, Number> areaChart = StatDisplayUtils.buildAreaChart(mockPoints, mockRegion);

        assertEquals(2, areaChart.getData().size());
        assertEquals(3, areaChart.getData().get(0).getData().size());
        assertEquals("X coordinate", areaChart.getData().get(0).getName());
        assertEquals(3, areaChart.getData().get(1).getData().size());
        assertEquals("Y coordinate", areaChart.getData().get(1).getName());
    }

    @Test
    void shouldZoomInToAreaChart() {
        LinkedList<FixationPoint> mockPoints = new LinkedList<>(List.of(
            new FixationPoint(50, 50, 1, 2),
            new FixationPoint(100, 100, 2, 3),
            new FixationPoint(150, 150, 4, 5)
        ));

        AreaChart<Number, Number> areaChart = StatDisplayUtils.buildAreaChart(mockPoints, mockRegion);
        VBox parent = new VBox();
        parent.getChildren().add(areaChart);

        areaChart.fireEvent(mouseClickEvent(areaChart));

        // These assertions are a bit rubbish - need to find a way to set the parent bounds.
        assertEquals( 0, areaChart.getTranslateX());
        assertEquals( 0, areaChart.getTranslateY());
    }

    @Test
    void shouldZoomOutOfAreaChart() {
        LinkedList<FixationPoint> mockPoints = new LinkedList<>(List.of(
            new FixationPoint(50, 50, 1, 2),
            new FixationPoint(100, 100, 2, 3),
            new FixationPoint(150, 150, 4, 5)
        ));

        AreaChart<Number, Number> areaChart = StatDisplayUtils.buildAreaChart(mockPoints, mockRegion);
        VBox parent = new VBox();
        parent.getChildren().add(areaChart);

        areaChart.fireEvent(mouseClickEvent(areaChart));
        areaChart.fireEvent(mouseClickEvent(areaChart));

        assertEquals( 1, areaChart.getScaleX());
        assertEquals( 1, areaChart.getScaleY());
    }

    @Test
    void shouldReturnNullBuildAreaWithNoPoints() {
        LinkedList<FixationPoint> mockPoints = new LinkedList<>();

        AreaChart<Number, Number> areaChart = StatDisplayUtils.buildAreaChart(mockPoints, mockRegion);

        assertEquals(null, areaChart);
    }

    @Test
    void shouldBuildGazeMetrics() {
        File mockFile = new File("bear.jpg");
        SavedStatsInfo mockSavedStatsInfo = new SavedStatsInfo(mockFile, mockFile, mockFile, mockFile);

        when(mockStats.getSavedStatsInfo()).thenReturn(mockSavedStatsInfo);

        ImageView imageView = StatDisplayUtils.buildGazeMetrics(mockStats, mockRegion);

        assertTrue(imageView.getImage().getUrl().contains("bear.jpg"));
    }

    @Test
    void shouldZoomInToGazeMetrics() {
        File mockFile = new File("bear.jpg");
        SavedStatsInfo mockSavedStatsInfo = new SavedStatsInfo(mockFile, mockFile, mockFile, mockFile);

        when(mockStats.getSavedStatsInfo()).thenReturn(mockSavedStatsInfo);

        ImageView imageView = StatDisplayUtils.buildGazeMetrics(mockStats, mockRegion);

        VBox parent = new VBox();
        parent.getChildren().add(imageView);
        imageView.fireEvent(mouseClickEvent(imageView));

        // These assertions are a bit rubbish - need to find a way to set the parent bounds.
        assertEquals( 0, imageView.getTranslateX());
        assertEquals( 0, imageView.getTranslateY());
    }

    @Test
    void shouldZoomOutOfGazeMetrics() {
        File mockFile = new File("bear.jpg");
        SavedStatsInfo mockSavedStatsInfo = new SavedStatsInfo(mockFile, mockFile, mockFile, mockFile);

        when(mockStats.getSavedStatsInfo()).thenReturn(mockSavedStatsInfo);

        ImageView imageView = StatDisplayUtils.buildGazeMetrics(mockStats, mockRegion);
        VBox parent = new VBox();
        parent.getChildren().add(imageView);

        imageView.fireEvent(mouseClickEvent(imageView));
        imageView.fireEvent(mouseClickEvent(imageView));

        assertEquals( 1, imageView.getScaleX());
        assertEquals( 1, imageView.getScaleY());
    }

    @Test
    void shouldConvertTimeToString() {
        Calendar cal = Calendar.getInstance();
        cal.set(2020, Calendar.JANUARY, 28, 13, 25, 20);
        long input = cal.getTimeInMillis();

        String result = StatDisplayUtils.convert(input);

        assertTrue(result.contains("28 d 13 h 25 m 20 s"));
    }
}