package me.atie.partialKeepinventory.api;

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
        return null;
    }

    @Nullable
    public pkiSettingsApi getSettings(){
        return null;
    }

    public abstract String getModId();
}

