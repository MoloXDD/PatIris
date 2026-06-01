package com.molox.patiris;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PatIris.MOD_ID)
public class PatIris {

    public static final String MOD_ID = "patiris";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public PatIris(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("PatIris initializing...");
    }
}
