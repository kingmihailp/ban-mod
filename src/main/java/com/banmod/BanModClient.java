package com.banmod;

import com.banmod.data.BanScreenData;
import com.banmod.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Client-side entry point for BanMod.
 * Stores the pending ban screen configuration sent by the server
 * so it can be picked up by the DisconnectScreen mixin.
 */
@Mod.EventBusSubscriber(modid = BanMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BanModClient {

    /**
     * Pending ban screen data received from the server just before disconnect.
     * The MixinDisconnectScreen reads and clears this field when showing the ban screen.
     */
    public static volatile BanScreenData pendingBanConfig = null;

    /**
     * Whether we're currently showing a preview (triggered by /banscreen preview).
     */
    public static volatile boolean isPreview = false;

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(BanModClient::onClientSetup);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        BanMod.LOGGER.info("BanMod client initialized");
    }
}
