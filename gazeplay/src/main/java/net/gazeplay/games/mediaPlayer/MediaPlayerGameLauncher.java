package net.gazeplay.games.mediaPlayer;

import javafx.scene.Scene;
import net.gazeplay.GameContext;
import net.gazeplay.GameLifeCycle;
import net.gazeplay.GameSpec;
import net.gazeplay.commons.utils.stats.Stats;
import net.gazeplay.games.pet.PetStats;

public class MediaPlayerGameLauncher implements GameSpec.GameLauncher<Stats, GameSpec.DimensionGameVariant> {
    @Override
    public Stats createNewStats(Scene scene) {
        return new PetStats(scene);
    }

    @Override
    public GameLifeCycle createNewGame(GameContext gameContext,
                                       GameSpec.DimensionGameVariant gameVariant, Stats stats) {
        return new GazeMediaPlayer(gameContext, stats);
    }

}