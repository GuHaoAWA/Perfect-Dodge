package com.guhao.perfect_dodge.ef;

import com.guhao.perfect_dodge.PDMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.AnimationManager;

@Mod.EventBusSubscriber(
        modid = PDMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class PDAnimations {
    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(PDMod.MODID, PDAnimations::build);
    }
    public static void build(AnimationManager.AnimationBuilder builder) {

    }
}
