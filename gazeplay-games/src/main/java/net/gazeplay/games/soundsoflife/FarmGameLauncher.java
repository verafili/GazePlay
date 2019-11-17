package net.gazeplay.games.soundsoflife;

import javafx.scene.Scene;
import net.gazeplay.GameLifeCycle;
import net.gazeplay.GameSpec;
import net.gazeplay.IGameContext;
import net.gazeplay.commons.utils.stats.Stats;

public class FarmGameLauncher implements GameSpec.GameLauncher {
    @Override
    public Stats createNewStats(Scene scene) {
        return new SoundsOfLifeStats(scene, "Farm");
    }

    @Override
    public GameLifeCycle createNewGame(IGameContext gameContext, GameSpec.GameVariant gameVariant,
                                       Stats stats) {
        return new SoundsOfLife(gameContext, stats, 0);
    }
}
