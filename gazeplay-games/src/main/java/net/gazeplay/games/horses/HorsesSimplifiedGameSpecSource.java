package net.gazeplay.games.horses;

import net.gazeplay.GameCategories;
import net.gazeplay.GameSpec;
import net.gazeplay.GameSpecSource;
import net.gazeplay.GameSummary;

public class HorsesSimplifiedGameSpecSource implements GameSpecSource {
    @Override
    public GameSpec getGameSpec() {
        return new GameSpec(
            GameSummary.builder().nameCode("Horses Simplified").gameThumbnail("data/Thumbnails/horsesSimplified.png").category(GameCategories.Category.ACTION_REACTION).build(),
            new HorsesSimplifiedGameVariantGenerator(), new HorsesSimplifiedGameLauncher());
    }
}
