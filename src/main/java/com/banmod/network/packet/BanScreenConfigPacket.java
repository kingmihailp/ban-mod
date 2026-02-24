package com.banmod.network.packet;

import com.banmod.BanModClient;
import com.banmod.data.BanScreenData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * S2C packet: server sends the full ban screen configuration to the client
 * just before issuing the disconnect (ban) packet.
 * The client stores it in {@link BanModClient#pendingBanConfig} and the
 * {@code MixinDisconnectScreen} picks it up when the disconnect screen opens.
 */
public class BanScreenConfigPacket {

    private final String configJson;

    public BanScreenConfigPacket(BanScreenData data) {
        this.configJson = data.toJson();
    }

    private BanScreenConfigPacket(String json) {
        this.configJson = json;
    }

    // ── Serialisation ────────────────────────────────────────────────────────

    public static void encode(BanScreenConfigPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.configJson, 32767);
    }

    public static BanScreenConfigPacket decode(FriendlyByteBuf buf) {
        return new BanScreenConfigPacket(buf.readUtf(32767));
    }

    // ── Client-side handler ──────────────────────────────────────────────────

    public static void handle(BanScreenConfigPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            // Parse and store; the DisconnectScreen mixin will read this.
            BanModClient.pendingBanConfig = BanScreenData.fromJson(pkt.configJson);
            BanModClient.isPreview = false;
        });
        ctx.setPacketHandled(true);
    }
}
