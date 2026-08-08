package com.moulberry.lattice.mixin.v12111.multiversion;

import com.moulberry.lattice.multiversion.LatticeMultiversion;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Function;

@Mixin(LatticeMultiversion.class)
public class MixinNewCycleButton {

    /**
     * @author Moulberry
     * @reason Implementation
     */
    @Overwrite
    public static CycleButton.Builder<Boolean> newCycleButtonOnOffBuilder(boolean initial) {
        return CycleButton.onOffBuilder(initial);
    }

    /**
     * @author Moulberry
     * @reason Implementation
     */
    @Overwrite
    public static <T> CycleButton.Builder<T> newCycleButtonBuilder(Function<T, Component> function, T initial) {
        return CycleButton.<T>builder(function, initial);
    }

}
