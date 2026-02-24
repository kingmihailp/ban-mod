package com.banmod.network.packet;

import com.banmod.BanModClient;
import com.banmod.data.BanScreenData;
import com.banmod.screen.CustomBanScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * S2C packet: server asks the requesting admin's client to display
 * a preview of the current ban screen configuration.
 */
public class PreviewPacket {

    private final String configJson;

    public PreviewPacket(BanScreenData data) {
        this.configJson = data.toJson();
    }

    private PreviewPacket(String json) {
        this.configJson = json;
    }

    // ── Serialisation ────────────────────────────────────────────────────────

    public static void encode(PreviewPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.configJson, 32767);
    }

    public static PreviewPacket decode(FriendlyByteBuf buf) {
        return new PreviewPacket(buf.readUtf(32767));
    }

    // ── Client-side handler ──────────────────────────────────────────────────

    public static void handle(PreviewPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            BanScreenData data = BanScreenData.fromJson(pkt.configJson);
            BanModClient.isPreview = true;
            // Show the custom ban screen as a preview overlay.
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new CustomBanScreen(
                    Component.literal("§cYou have been banned"),
                    Component.literal("Test reason: Hacking"),
                    Component.literal("2024-12-31 23:59:59"),
                    Component.literal(mc.getUser().getName()),
                    data,
                    true  // isPreview = true so the screen can show a "Close Preview" button
            ));
        });
        ctx.setPacketHandled(true);
    }
}
