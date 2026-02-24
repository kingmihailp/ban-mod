package com.banmod.network;

import com.banmod.BanMod;
import com.banmod.network.packet.BanScreenConfigPacket;
import com.banmod.network.packet.PreviewPacket;
import com.banmod.network.packet.UpdateConfigPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BanMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        // S2C – server sends ban screen config just before disconnect
        CHANNEL.messageBuilder(BanScreenConfigPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(BanScreenConfigPacket::encode)
                .decoder(BanScreenConfigPacket::decode)
                .consumerMainThread(BanScreenConfigPacket::handle)
                .add();

        // S2C – server asks admin client to show a live preview
        CHANNEL.messageBuilder(PreviewPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PreviewPacket::encode)
                .decoder(PreviewPacket::decode)
                .consumerMainThread(PreviewPacket::handle)
                .add();

        // C2S – admin GUI sends updated config back to server
        CHANNEL.messageBuilder(UpdateConfigPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UpdateConfigPacket::encode)
                .decoder(UpdateConfigPacket::decode)
                .consumerMainThread(UpdateConfigPacket::handle)
                .add();
    }
}
