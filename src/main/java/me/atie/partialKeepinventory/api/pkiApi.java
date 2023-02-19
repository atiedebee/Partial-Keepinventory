package me.atie.partialKeepinventory.api;

import me.atie.partialKeepinventory.settings.Settings;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

// TODO: Entrypoint documentation
public abstract class pkiApi {
    public Pair<Double, InventoryUtil.DropAction> getDropBehaviour(PlayerEntity player, ItemStack itemStack){
        return null;
    }


    public List<ItemStack> getInventorySlots(PlayerEntity player){
        return List.of();
    }

    @Nullable
    public Settings getSettings(){
        return null;
    }

    public Supplier<Screen> configScreenSupplier;

    public abstract String getModId();
}

