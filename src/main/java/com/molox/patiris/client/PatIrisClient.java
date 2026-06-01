package com.molox.patiris.client;

import com.molox.patiris.PatIris;
import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.LoadingModList;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.utils.EMFEntity;

import java.lang.reflect.Method;

@EventBusSubscriber(modid = PatIris.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class PatIrisClient {

    private static boolean emoteChecked = false;
    private static Method isPlayingEmoteMethod = null;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!isModLoaded("entity_model_features")) {
            return;
        }
        event.enqueueWork(() ->
                EMFAnimationApi.registerVanillaModelCondition(PatIrisClient::isPlayerEmoting)
        );
    }

    private static boolean isPlayerEmoting(EMFEntity entity) {
        if (!(entity instanceof AbstractClientPlayer player)) {
            return false;
        }
        Method method = getIsPlayingEmoteMethod();
        if (method == null) {
            return false;
        }
        try {
            return (boolean) method.invoke(player);
        } catch (Throwable t) {
            return false;
        }
    }

    private static Method getIsPlayingEmoteMethod() {
        if (emoteChecked) {
            return isPlayingEmoteMethod;
        }
        emoteChecked = true;
        try {
            Class<?> clazz = Class.forName("io.github.kosmx.emotes.executor.emotePlayer.IEmotePlayerEntity");
            isPlayingEmoteMethod = clazz.getMethod("isPlayingEmote");
        } catch (Throwable t) {
            isPlayingEmoteMethod = null;
        }
        return isPlayingEmoteMethod;
    }

    private static boolean isModLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}