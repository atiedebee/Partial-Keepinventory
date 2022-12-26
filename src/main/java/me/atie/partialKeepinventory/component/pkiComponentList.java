package me.atie.partialKeepinventory.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryV2;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import me.atie.partialKeepinventory.partialKeepinventory;
import net.minecraft.util.Identifier;

public class pkiComponentList implements ScoreboardComponentInitializer {
    public static final ComponentKey<pkiComponent> config = ComponentRegistry.getOrCreate(
            new Identifier(partialKeepinventory.getID(), "config"),
            pkiComponent.class);


    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        ScoreboardComponentFactoryV2<pkiComponent> scf = (scoreboard, minecraftServer) -> new pkiComponent();

        registry.registerScoreboardComponent(config, scf);
    }
}
