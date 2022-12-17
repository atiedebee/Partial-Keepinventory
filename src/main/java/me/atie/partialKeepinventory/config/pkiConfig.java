package me.atie.partialKeepinventory.config;


import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;
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

    @SectionHeader("Rarity based")
    @RangeConstraint(min = 0, max = 100)
    public int commonDroprate = 100;

    @RangeConstraint(min = 0, max = 100)
    public int uncommonDroprate = 100;

    @RangeConstraint(min = 0, max = 100)
    public int rareDroprate = 100;

    @RangeConstraint(min = 0, max = 100)
    public int epicDroprate = 100;

//    public List<String> perPlayerKeepinventory = new ArrayList<>();


}
