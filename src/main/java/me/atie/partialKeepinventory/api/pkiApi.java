package me.atie.partialKeepinventory.api;

import me.atie.partialKeepinventory.impl.Impl;
import me.atie.partialKeepinventory.util.InventoryUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public abstract class pkiApi {
    @Nullable
    public Pair<Double, InventoryUtil.DropAction> getDropBehaviour(PlayerEntity player, ItemStack itemStack){
        return null;
    }
    @Nullable
    public pkiSettingsApi getSettings(){
        return null;
    }

    @Nullable
    public List<ItemStack> getInventorySlots(PlayerEntity player){
        return null;
    }

    @NotNull
    public abstract String getModId();

    protected Function<Screen, Screen> screenFactoriesAdd(Function<Screen, Screen> screenFactory){
        if( screenFactory != null ){
            Impl.screenFactories.put(this.getModId(), screenFactory);
        }
        return screenFactory;
    }
}

