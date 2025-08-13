package com.guhao.perfect_dodge.network;

import com.guhao.perfect_dodge.PDMod;
import com.guhao.perfect_dodge.tick.TickChange;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

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
            // 仅服务器处理
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer sender = ctx.get().getSender();
                if (sender != null) {
                    // 修改 tick rate 并广播给所有客户端
                    TickChange.PERCENT = this.percent;
                    PDMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new TickChangePacket(this.percent));
                }
            }
            // 客户端仅接收同步
            else {
                TickChange.PERCENT = this.percent;
            }
        });
        ctx.get().setPacketHandled(true);
    }
}