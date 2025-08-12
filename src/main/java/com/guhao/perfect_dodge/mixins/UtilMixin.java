package com.guhao.perfect_dodge.mixins;

import com.guhao.perfect_dodge.tick.TickChange;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
        value = {Util.class},
        priority = Integer.MAX_VALUE
)
public abstract class UtilMixin {
    public UtilMixin() {
    }

    @Overwrite
    public static long getMillis() {
        return TickChange.millis;
    }
}
