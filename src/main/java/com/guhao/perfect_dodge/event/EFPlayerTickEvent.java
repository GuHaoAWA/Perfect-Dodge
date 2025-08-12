package com.guhao.perfect_dodge.event;

import com.guhao.perfect_dodge.PDMod;
import com.guhao.perfect_dodge.efm.PDAnimations;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod.EventBusSubscriber

public class EFPlayerTickEvent {
    private static final UUID EVENT_UUID = UUID.fromString("36a396ea-0461-11ee-be56-0292ac114514");
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            execute(event,event.player);
        }
    }
    public static void execute(Player player) {
        execute(null,player);
    }
    private static void execute(@Nullable Event event, Player player) {
        PlayerPatch<?> pp = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
        if (pp == null) return;
        pp.getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (e) -> {
            Entity livingEntity = e.getDamageSource().getDirectEntity();
            LivingEntityPatch<?> livingEntityPatch = EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
            if (e.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation() == PDAnimations.PERFECT_DODGE) return;
            e.getPlayerPatch().playAnimationInstantly(PDAnimations.PERFECT_DODGE);
        });

    }

}