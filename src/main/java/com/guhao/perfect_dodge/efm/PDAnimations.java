package com.guhao.perfect_dodge.efm;

import com.guhao.perfect_dodge.PDMod;
import com.guhao.perfect_dodge.camera.CameraAnimation;
import com.guhao.perfect_dodge.camera.CameraEvents;
import com.guhao.perfect_dodge.camera.OjangUtils;
import com.guhao.perfect_dodge.tick.TickChange;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.gameasset.Armatures;

@Mod.EventBusSubscriber(
        modid = PDMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class PDAnimations {
    public static AnimationManager.AnimationAccessor<DodgeAnimation> PERFECT_DODGE;
    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(PDMod.MODID, PDAnimations::build);
    }
    public static void build(AnimationManager.AnimationBuilder builder) {
        PERFECT_DODGE = builder.nextAccessor("biped/perfect_dodge", (accessor) -> (new DodgeAnimation(0.1F, 2.0F,accessor, 0.6F, 0.8F, Armatures.BIPED))
//                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.SimpleEvent.create((entitypatch, animation, params) -> CameraEvents.SetAnim(NB, Minecraft.getInstance().player, true), AnimationEvent.Side.CLIENT))

                .addEvents(AnimationEvent.InTimeEvent.create(0.333F, ((livingEntityPatch, assetAccessor, animationParameters) -> {
                    TickChange.changeAll(3.0f);
                }), AnimationEvent.Side.SERVER),
                        AnimationEvent.InTimeEvent.create(0.167F, ((livingEntityPatch, assetAccessor, animationParameters) -> {
                            livingEntityPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,37,254,false,false,false));
                        }), AnimationEvent.Side.SERVER),

                        AnimationEvent.InTimeEvent.create(0.7167F, ((livingEntityPatch, assetAccessor, animationParameters) -> {
                            TickChange.changeAll(20.0f);
                        }), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1, v2) -> 1.15F)));
    }

    public static CameraAnimation NB;

    public static void LoadCamAnims() {
        NB = CameraAnimation.load(OjangUtils.newRL(PDMod.MODID, "camera_animation/perfect_dodge.json"));
    }

}
