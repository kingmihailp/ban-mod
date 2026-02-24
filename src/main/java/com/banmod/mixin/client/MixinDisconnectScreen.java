package com.banmod.mixin.client;

import com.banmod.BanModClient;
import com.banmod.data.BanScreenData;
import com.banmod.screen.CustomBanScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side mixin that replaces {@link DisconnectScreen} with
 * {@link CustomBanScreen} when a ban screen config has been received
 * from the server via {@link com.banmod.network.packet.BanScreenConfigPacket}.
 *
 * <p>The replacement happens in {@code init()} — by the time the screen
 * initialises, both the reason text and our stored config are available.
 */
@OnlyIn(Dist.CLIENT)
@Mixin(DisconnectScreen.class)
public abstract class MixinDisconnectScreen {

    @Shadow @Final private Component reason;

    /**
     * If a pending ban config is waiting (sent by the server just before kick),
     * replace the vanilla disconnect screen with our {@link CustomBanScreen}.
     */
    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void banmod$onInit(CallbackInfo ci) {
        BanScreenData config = BanModClient.pendingBanConfig;
        if (config == null) return;

        // Consume the pending config so it isn't reused
        BanModClient.pendingBanConfig = null;
        BanModClient.isPreview = false;

        // Parse the disconnect reason to extract ban details.
        // The vanilla ban message format is:
        //   "You are banned from this server.\nReason: <reason>\nExpires: <date>"
        String fullReason = reason.getString();
        Component banReason  = extractReason(fullReason, config);
        Component expiryText = extractExpiry(fullReason, config);
        Component playerName = Component.literal(
                Minecraft.getInstance().getUser().getName()
        );

        // Replace the vanilla screen
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new CustomBanScreen(reason, banReason, expiryText, playerName, config, false));

        ci.cancel();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Tries to extract the ban reason from the vanilla disconnect message.
     * Falls back to the full reason if parsing fails.
     */
    private static Component extractReason(String full, BanScreenData cfg) {
        // Vanilla format: "You are banned from this server.\nReason: <x>\nExpires: <y>"
        String reasonMarker = "Reason: ";
        int start = full.indexOf(reasonMarker);
        if (start == -1) {
            // Try to use whatever text comes after the first newline
            int nl = full.indexOf('\n');
            return Component.literal(nl == -1 ? full : full.substring(nl + 1).split("\n")[0].trim());
        }
        start += reasonMarker.length();
        int end = full.indexOf('\n', start);
        String reason = end == -1 ? full.substring(start) : full.substring(start, end);
        return Component.literal(reason.trim());
    }

    /**
     * Tries to extract the expiry date from the vanilla disconnect message.
     * Returns the configured "permanent" text if no date is found.
     */
    private static Component extractExpiry(String full, BanScreenData cfg) {
        String[] markers = {"Expires: ", "Until: "};
        for (String marker : markers) {
            int start = full.indexOf(marker);
            if (start != -1) {
                start += marker.length();
                int end = full.indexOf('\n', start);
                String exp = end == -1 ? full.substring(start) : full.substring(start, end);
                return Component.literal(exp.trim());
            }
        }
        return Component.literal(cfg.neverBanText);
    }
}
