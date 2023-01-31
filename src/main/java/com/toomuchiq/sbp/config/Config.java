package com.toomuchiq.sbp.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static class Common {
        private static final boolean defaultDebug = false;

        public final ForgeConfigSpec.ConfigValue<Boolean> debug;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Dev");
            this.debug = builder.comment("Enable / disable debug mode").define("debug", defaultDebug);
            builder.pop();
        }

    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }

}
