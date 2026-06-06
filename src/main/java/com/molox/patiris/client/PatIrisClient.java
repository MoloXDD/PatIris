package com.molox.patiris.client;

import com.molox.patiris.PatIris;
import com.molox.patiris.PatIrisConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.utils.EMFEntity;

import java.lang.reflect.Method;
import java.util.List;

@EventBusSubscriber(modid = PatIris.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class PatIrisClient {

    private static boolean emoteChecked = false;
    private static Method isPlayingEmoteMethod = null;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModList.get().getModContainerById(PatIris.MOD_ID).ifPresent(container ->
                container.registerExtensionPoint(IConfigScreenFactory.class,
                        (mc, parent) -> AutoConfig.getConfigScreen(PatIrisConfig.class, parent).get())
        );

        if (isModLoaded("entity_model_features")) {
            event.enqueueWork(() ->
                    EMFAnimationApi.registerVanillaModelCondition(PatIrisClient::shouldUseVanillaModel)
            );
        }
    }

    private static boolean shouldUseVanillaModel(EMFEntity entity) {
        return isHoldingBlockedItem(entity) || isPlayerEmoting(entity);
    }

    private static boolean isHoldingBlockedItem(EMFEntity entity) {
        if (!(entity instanceof AbstractClientPlayer player)) {
            return false;
        }
        List<String> patterns = PatIris.getConfig().vanillaAnimationItems;
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        return matchesAnyPattern(player.getMainHandItem(), patterns)
                || matchesAnyPattern(player.getOffhandItem(), patterns);
    }

    private static boolean matchesAnyPattern(ItemStack stack, List<String> patterns) {
        if (stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) return false;
        String idStr = id.toString();
        for (String pattern : patterns) {
            if (matchesPattern(idStr, pattern)) return true;
        }
        return false;
    }

    private static boolean matchesPattern(String itemId, String pattern) {
        if (pattern.endsWith(":*")) {
            String namespace = pattern.substring(0, pattern.length() - 2);
            return itemId.startsWith(namespace + ":");
        }
        if (pattern.startsWith("*:")) {
            String path = pattern.substring(2);
            int colon = itemId.indexOf(':');
            return colon >= 0 && itemId.substring(colon + 1).equals(path);
        }
        if (pattern.contains("*")) {
            String regex = "\\Q" + pattern.replace("*", "\\E.*\\Q") + "\\E";
            return itemId.matches(regex);
        }
        return itemId.equals(pattern);
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