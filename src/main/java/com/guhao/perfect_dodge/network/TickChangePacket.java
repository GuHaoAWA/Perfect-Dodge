package com.guhao.perfect_dodge.network;

import com.guhao.perfect_dodge.tick.TickChange;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class TickChangePacket {
    private final float percent;

    public TickChangePacket(float percent) {
        this.percent = percent;
    }

    public TickChangePacket(FriendlyByteBuf buf) {
        this.percent = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(percent);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer sender = ctx.get().getSender();
                if (sender != null) {
                    TickChange.changeAll(percent);
                }
            }
            // 客户端处理
            else {
                TickChange.PERCENT = this.percent;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}