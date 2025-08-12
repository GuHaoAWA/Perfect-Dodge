package com.guhao.perfect_dodge.mixins;


import com.guhao.perfect_dodge.tick.TickChange;
import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Timer.class})
public class TimerMixin {
    @Inject(
            method = {"advanceTime"},
            at = {@At("HEAD")}
    )
    public void at(long p_92526_, CallbackInfoReturnable<Integer> cir) {
        p_92526_ = TickChange.millis;
    }
}
