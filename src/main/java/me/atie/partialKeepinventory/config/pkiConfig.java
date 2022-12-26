package me.atie.partialKeepinventory.config;


import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static me.atie.partialKeepinventory.partialKeepinventory.KeepinvMode;


@Modmenu(modId = "partial-keepinv")
@Sync(Option.SyncMode.OVERRIDE_CLIENT)
@Config(name = "pki-config", wrapperName = "pkiConfigClass")
public class pkiConfig  {
    public boolean enableMod = true;
    public KeepinvMode partialKeepinvMode = KeepinvMode.PERCENTAGE;

    @SectionHeader("Percentage based")
    @RangeConstraint(min = 0, max = 100)
    public int inventoryDroprate = 100;

    @Nest
    @SectionHeader("Rarity based")
    @RangeConstraint(min = 0, max = 100)
    public int commonDroprate = 100;

    @RangeConstraint(min = 0, max = 100)
    public int uncommonDroprate = 100;

    @RangeConstraint(min = 0, max = 100)
    public int rareDroprate = 100;

    @RangeConstraint(min = 0, max = 100)
    public int epicDroprate = 100;

    public List<String> perPlayerKeepinventory = new ArrayList<>();

    @SectionHeader("Custom droprate")
    //TODO: add predicate constraint to check if the given expression is valid
    //TODO: add comment that shows up in config
    @Comment(
        """
                Custom expressions aren't checked on correctness yet. Please test them out in a separate world before adding them.\n
                Percentages are from 0.0 - 1.0
                Variables:
                        - spawnDistance:                distance from player to spawnpoint 
                        - spawnX, spawnY, spawnZ:       spawn coordinates
                        - playerX, playerY, playerZ:    player coordinates
                        - rarityPercent:                get droprate from rarity as set in the config.                                
                        - isEpic, isRare, isCommon, isUncommon:
                                                        return 1.0 if true
                        - dropPercent:                  inventory droprate as set in the config
                 """
    )
    public String expression = new String();



}
