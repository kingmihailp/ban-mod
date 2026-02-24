package com.banmod.mixin.server;

import com.banmod.config.BanScreenConfig;
import com.banmod.data.BanScreenData;
import com.banmod.network.NetworkHandler;
import com.banmod.network.packet.BanScreenConfigPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Server-side mixin that fires just before a player is disconnected.
 *
 * <p>If the player is banned, we send a {@link BanScreenConfigPacket}
 * with the current server config <em>before</em> the disconnect packet
 * so the client can render the custom ban screen.
 *
 * <p>Packet ordering is guaranteed by the underlying Netty pipeline —
 * both packets are queued on the same channel in the same flush cycle,
 * so the config packet always arrives before the disconnect.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {

    @Shadow
    public ServerPlayer player;

    /**
     * Injected at the very start of {@code disconnect(Component)}.
     * Checks if the player is banned and, if so, sends the config packet first.
     */
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void banmod$onDisconnect(Component reason, CallbackInfo ci) {
        try {
            if (player == null) return;

            PlayerList playerList = player.getServer() != null
                    ? player.getServer().getPlayerList()
                    : null;
            if (playerList == null) return;

            // Check if this player is currently banned
            boolean isBanned = playerList.getBans().isBanned(player.getGameProfile())
                    || playerList.getIpBans().isBanned(player.connection.getRemoteAddress());

            if (!isBanned) return;

            // Build the data object from current server config
            BanScreenData data = BanScreenConfig.toBanScreenData();

            // Send the config packet to the client.
            // NetworkHandler.CHANNEL is registered with PLAY_TO_CLIENT, so this
            // only succeeds if the client also has the mod loaded (version check).
            // If not, the packet is silently discarded by Forge's channel negotiation.
            try {
                NetworkHandler.CHANNEL.sendTo(
                        new BanScreenConfigPacket(data),
                        player.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            } catch (Exception ignored) {
                // Client doesn't have the mod — no custom screen, plain disconnect.
            }
        } catch (Exception e) {
            // Never crash the server due to our mixin
            com.banmod.BanMod.LOGGER.error("BanMod: error in disconnect mixin", e);
        }
    }
}
