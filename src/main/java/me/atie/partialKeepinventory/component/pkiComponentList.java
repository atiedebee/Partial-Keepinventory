package me.atie.partialKeepinventory.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryV2;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.TeamComponentFactoryV2;
import me.atie.partialKeepinventory.PartialKeepInventory;
import net.minecraft.util.Identifier;

public class pkiComponentList implements ScoreboardComponentInitializer {
    public static final ComponentKey<pkiScoreboardComponent> configKey = ComponentRegistry.getOrCreate(
            new Identifier(PartialKeepInventory.getID(), "config"),
            pkiScoreboardComponent.class);

    public static final ComponentKey<pkiTeamComponent> savedPlayersKey = ComponentRegistry.getOrCreate(
            new Identifier(PartialKeepInventory.getID(), "saved-players"),
            pkiTeamComponent.class
    );

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        ScoreboardComponentFactoryV2<pkiScoreboardComponent> scf = pkiScoreboardComponent::new;
        TeamComponentFactoryV2<pkiTeamComponent> tcf = (team, scoreboard, server) -> new pkiTeamComponent(team);

        registry.registerScoreboardComponent(configKey, scf);
        registry.registerTeamComponent(savedPlayersKey, tcf);
    }
}
