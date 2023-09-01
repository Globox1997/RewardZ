package net.rewardz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.rewardz.config.RewardzConfig;

public class ConfigInit {

    public static RewardzConfig CONFIG = new RewardzConfig();

    public static void init() {
        AutoConfig.register(RewardzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(RewardzConfig.class).getConfig();
    }
}
