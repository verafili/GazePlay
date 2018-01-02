package net.gazeplay.commons.gaze;

import com.theeyetribe.clientsdk.GazeManager;
import com.theeyetribe.clientsdk.IGazeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import lombok.extern.slf4j.Slf4j;
import net.gazeplay.commons.gaze.configuration.Configuration;
import net.gazeplay.commons.gaze.configuration.ConfigurationBuilder;
import net.gazeplay.commons.utils.stats.Stats;
import tobii.Tobii;

import java.util.ArrayList;

/**
 * Created by schwab on 16/08/2016.
 */
@Slf4j
public class GazeUtils {

    static ArrayList<GazeInfos> nodesEventFilter = new ArrayList<>(100);

    static ArrayList<GazeInfos> nodesEventHandler = new ArrayList<>(100);

    static final GazeManager gm = GazeManager.getInstance();
    static boolean success = gm.activate();
    static final IGazeListener gazeListener = createGazeListener();

    static Stats stats;

    static Scene scene = null;

    private static IGazeListener createGazeListener() {

        Configuration config = ConfigurationBuilder.createFromPropertiesResource().build();

        final String eyetracker = config.getEyetracker();
        log.info("Eye-tracker = " + eyetracker);

        if (eyetracker.equals(EyeTrackers.tobii_eyeX_4C.toString())) {
            Tobii.execProg(new TobiiGazeListener(nodesEventFilter, nodesEventHandler));
        } else if (eyetracker.equals(EyeTrackers.eyetribe.toString()))
            return new EyeTribeGazeListener(nodesEventFilter, nodesEventHandler);
        // else
        // return new FuzzyGazeListener(nodesEventFilter, nodesEventHandler);
        return null;
    }

    public static void addStats(Stats newStats) {

        stats = newStats;
    }

    public static void addEventFilter(Scene gazeScene) {

        scene = gazeScene;
    }

    public static void addEventFilter(Node gs) {

        gm.addGazeListener(gazeListener);
        final int listenersCount = gm.getNumGazeListeners();
        log.info("Gaze Event Filters Count = {}", listenersCount);

        nodesEventFilter.add(new GazeInfos(gs));
        final int nodesEventFilterListSize = nodesEventFilter.size();
        log.info("nodesEventFilterListSize = {}", nodesEventFilterListSize);
    }

    public static void addEventHandler(Node gs) {

        gm.addGazeListener(gazeListener);

        nodesEventHandler.add(new GazeInfos(gs));
    }

    public static void removeEventFilter(Node gs) {

        int i;

        try {
            for (i = 0; i < nodesEventFilter.size() && nodesEventFilter.get(i).getNode() != null
                    && !nodesEventFilter.get(i).getNode().equals(gs); i++)
                ;

            if (i < nodesEventFilter.size()) {

                nodesEventFilter.remove(i);
            }
        } catch (Exception e) {

            log.debug(e.getMessage());
            System.exit(0);
        }
    }

    public static void removeEventHandler(Node gs) {

        int i;

        for (i = 0; i < nodesEventHandler.size() && !nodesEventHandler.get(i).getNode().equals(gs); i++)
            ;

        if (i < nodesEventHandler.size()) {

            nodesEventHandler.remove(i);
        }
    }

    /**
     *
     * Clear all Nodes in both EventFilter and EventHandler. There is no more gaze event after this function is called
     *
     */
    public static void clear() {

        nodesEventFilter.clear();

        nodesEventHandler.clear();
    }

    public static boolean isOn() {
        return Tobii.isInit() || success;
    }
}