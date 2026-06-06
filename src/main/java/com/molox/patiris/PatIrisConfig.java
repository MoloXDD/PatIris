package com.molox.patiris;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name = "patiris")
public class PatIrisConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public List<String> vanillaAnimationItems = new ArrayList<>(List.of(
            "tacz:*"
    ));
}