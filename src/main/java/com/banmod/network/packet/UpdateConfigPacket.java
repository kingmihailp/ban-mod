package com.banmod.network.packet;

import com.banmod.config.BanScreenConfig;
import com.banmod.data.BanScreenData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * C2S packet: admin GUI sends a full {@link BanScreenData} to the server
 * to update the live server config values.
 */
public class UpdateConfigPacket {

    private final BanScreenData data;

    public UpdateConfigPacket(BanScreenData data) {
        this.data = data;
    }

    // ── Serialisation ────────────────────────────────────────────────────────

    public static void encode(UpdateConfigPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.data.toJson(), 32767);
    }

    public static UpdateConfigPacket decode(FriendlyByteBuf buf) {
        return new UpdateConfigPacket(BanScreenData.fromJson(buf.readUtf(32767)));
    }

    // ── Server-side handler ──────────────────────────────────────────────────

    public static void handle(UpdateConfigPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null) return;

            // Only operators (permission level 3+) may update the config.
            if (!sender.hasPermissions(3)) {
                sender.sendSystemMessage(
                        net.minecraft.network.chat.Component.literal("§cYou do not have permission to modify the ban screen config."));
                return;
            }

            BanScreenData d = pkt.data;
            applyToConfig(d);
        });
        ctx.setPacketHandled(true);
    }

    /** Writes every field of the incoming {@link BanScreenData} into the live Forge config. */
    private static void applyToConfig(BanScreenData d) {
        BanScreenConfig.BG_COLOR.set(d.bgColor);
        BanScreenConfig.BG_COLOR_BOTTOM.set(d.bgColorBottom);
        BanScreenConfig.BG_GRADIENT.set(d.bgGradient);
        BanScreenConfig.BG_OPACITY.set((double) d.bgOpacity);

        BanScreenConfig.TITLE_TEXT.set(d.titleText);
        BanScreenConfig.TITLE_COLOR.set(d.titleColor);
        BanScreenConfig.TITLE_X.set((double) d.titleX);
        BanScreenConfig.TITLE_Y.set((double) d.titleY);
        BanScreenConfig.TITLE_BOLD.set(d.titleBold);
        BanScreenConfig.TITLE_ITALIC.set(d.titleItalic);
        BanScreenConfig.TITLE_SHADOW.set(d.titleShadow);
        BanScreenConfig.TITLE_SCALE.set(d.titleScale);
        BanScreenConfig.TITLE_VISIBLE.set(d.titleVisible);

        BanScreenConfig.REASON_PREFIX.set(d.reasonPrefix);
        BanScreenConfig.REASON_COLOR.set(d.reasonColor);
        BanScreenConfig.REASON_X.set((double) d.reasonX);
        BanScreenConfig.REASON_Y.set((double) d.reasonY);
        BanScreenConfig.REASON_BOLD.set(d.reasonBold);
        BanScreenConfig.REASON_ITALIC.set(d.reasonItalic);
        BanScreenConfig.REASON_SHADOW.set(d.reasonShadow);
        BanScreenConfig.REASON_VISIBLE.set(d.reasonVisible);

        BanScreenConfig.EXPIRY_VISIBLE.set(d.expiryVisible);
        BanScreenConfig.EXPIRY_PREFIX.set(d.expiryPrefix);
        BanScreenConfig.NEVER_BAN_TEXT.set(d.neverBanText);
        BanScreenConfig.EXPIRY_COLOR.set(d.expiryColor);
        BanScreenConfig.EXPIRY_X.set((double) d.expiryX);
        BanScreenConfig.EXPIRY_Y.set((double) d.expiryY);
        BanScreenConfig.EXPIRY_SHADOW.set(d.expiryShadow);

        BanScreenConfig.PLAYER_VISIBLE.set(d.playerVisible);
        BanScreenConfig.PLAYER_PREFIX.set(d.playerPrefix);
        BanScreenConfig.PLAYER_COLOR.set(d.playerColor);
        BanScreenConfig.PLAYER_X.set((double) d.playerX);
        BanScreenConfig.PLAYER_Y.set((double) d.playerY);

        BanScreenConfig.DIVIDER_VISIBLE.set(d.dividerVisible);
        BanScreenConfig.DIVIDER_COLOR.set(d.dividerColor);
        BanScreenConfig.DIVIDER_Y.set((double) d.dividerY);
        BanScreenConfig.DIVIDER_THICKNESS.set(d.dividerThickness);
        BanScreenConfig.DIVIDER_WIDTH_PCT.set(d.dividerWidthPct);

        BanScreenConfig.FOOTER_VISIBLE.set(d.footerVisible);
        BanScreenConfig.FOOTER_TEXT.set(d.footerText);
        BanScreenConfig.FOOTER_COLOR.set(d.footerColor);
        BanScreenConfig.FOOTER_X.set((double) d.footerX);
        BanScreenConfig.FOOTER_Y.set((double) d.footerY);
        BanScreenConfig.FOOTER_SHADOW.set(d.footerShadow);

        BanScreenConfig.APPEAL_VISIBLE.set(d.appealVisible);
        BanScreenConfig.APPEAL_TEXT.set(d.appealText);
        BanScreenConfig.APPEAL_COLOR.set(d.appealColor);
        BanScreenConfig.APPEAL_X.set((double) d.appealX);
        BanScreenConfig.APPEAL_Y.set((double) d.appealY);
        BanScreenConfig.APPEAL_SHADOW.set(d.appealShadow);

        BanScreenConfig.BUTTON_TEXT.set(d.buttonText);
        BanScreenConfig.BUTTON_COLOR.set(d.buttonColor);
        BanScreenConfig.BUTTON_TEXT_COLOR.set(d.buttonTextColor);
        BanScreenConfig.BUTTON_X.set((double) d.buttonX);
        BanScreenConfig.BUTTON_Y.set((double) d.buttonY);

        // Persist changes to disk
        BanScreenConfig.SPEC.save();
    }
}
