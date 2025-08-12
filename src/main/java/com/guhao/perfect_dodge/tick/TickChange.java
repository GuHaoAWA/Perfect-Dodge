package com.guhao.perfect_dodge.tick;

import com.guhao.perfect_dodge.PDMod;
import com.guhao.perfect_dodge.network.TickChangePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class TickChange {
    public static float PERCENT = 20.0F;
    public static double millisF = 0.0;
    public static long millis = 0L;
    public static Timer timer;
    public static ScheduledExecutorService service;

    public static synchronized void init() {
        create();
    }



    public static void create() {
        if (service == null) {
            service = Executors.newSingleThreadScheduledExecutor();
        }

        if (timer == null) {
            timer = new Timer();
        }

        try {
            timer.schedule(new TimerTask() {
                public void run() {
                    PDMod.service.scheduleAtFixedRate(TickChange::update, 1L, 1L, TimeUnit.MILLISECONDS);
                }
            }, 1L);
        } catch (Exception var1) {
            var1.printStackTrace();
        }
    }

    static void update() {
        float p = PERCENT / 20.0F;
        millisF += p;
        millis = (long)millisF;
    }

    // 服务器端调用此方法
    public static void changeAll(float percent) {
        PERCENT = percent;
        // 只从服务器端发送同步包
        if (!Minecraft.getInstance().isLocalServer()) {
            PDMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new TickChangePacket(percent));
        }
    }

    // 客户端调用此方法会发送请求到服务器
    public static void requestChange(float percent) {
        PDMod.PACKET_HANDLER.sendToServer(new TickChangePacket(percent));
    }

    public static void jump(int ticks) {
        millisF += (double)((long)ticks * 50L);
    }
}