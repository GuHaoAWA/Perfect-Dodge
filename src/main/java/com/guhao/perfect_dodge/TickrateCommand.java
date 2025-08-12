package com.guhao.perfect_dodge;


import com.guhao.perfect_dodge.camera.CameraAnimation;
import com.guhao.perfect_dodge.camera.CameraEvents;
import com.guhao.perfect_dodge.efm.PDAnimations;
import com.guhao.perfect_dodge.tick.TickChange;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//@Mod.EventBusSubscriber
public class TickrateCommand {
    public TickrateCommand() {
    }

    @SubscribeEvent
    public static void registerTickrateCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("tickrate").then(Commands.literal("change").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F)).executes((s) -> changeAndSend(s, FloatArgumentType.getFloat(s, "value"))))));
    }

    private static int changeAndSend(CommandContext<CommandSourceStack> s, float tick) {
//        CameraEvents.SetAnim(CameraAnimation.createCombinedAnimationWithTransition(PDAnimations.NB,0.5f), Minecraft.getInstance().player, true);

        tick = Float.parseFloat(String.valueOf(tick));
        float finalTick = tick;
        s.getSource().sendSuccess(() -> Component.translatable("tickrate changed to ").append(Component.translatable(String.valueOf(finalTick))).withStyle(ChatFormatting.DARK_GRAY), false);
        TickChange.changeAll(tick);
        return 0;
    }

}
