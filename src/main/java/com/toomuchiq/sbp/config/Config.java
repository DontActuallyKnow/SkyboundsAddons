package com.toomuchiq.sbp.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static class Common {
        private static final boolean defaultDebug = false;
        private static final boolean defaultA = false;
        private static final boolean defaultB = false;
        private static final boolean defaultC = false;
        private static final boolean defaultD = false;
        private static final boolean defaultE = false;
        private static final boolean defaultF = false;
        private static final boolean defaultG = false;
        private static final boolean defaultH = false;
        private static final boolean defaultI = false;

        public final ForgeConfigSpec.ConfigValue<Boolean> debug;
        public final ForgeConfigSpec.ConfigValue<Boolean> a;
        public final ForgeConfigSpec.ConfigValue<Boolean> b;
        public final ForgeConfigSpec.ConfigValue<Boolean> c;
        public final ForgeConfigSpec.ConfigValue<Boolean> d;
        public final ForgeConfigSpec.ConfigValue<Boolean> e;
        public final ForgeConfigSpec.ConfigValue<Boolean> f;
        public final ForgeConfigSpec.ConfigValue<Boolean> g;
        public final ForgeConfigSpec.ConfigValue<Boolean> h;
        public final ForgeConfigSpec.ConfigValue<Boolean> i;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Dev");
            this.debug = builder.comment("Enable / disable debug mode").define("debug", defaultDebug);
            this.a = builder.comment("TEST VALUE").define("debug", defaultA);
            this.b = builder.comment("TEST VALUE").define("debug", defaultB);
            this.c = builder.comment("TEST VALUE").define("debug", defaultC);
            this.d = builder.comment("TEST VALUE").define("debug", defaultD);
            this.e = builder.comment("TEST VALUE").define("debug", defaultE);
            this.f = builder.comment("TEST VALUE").define("debug", defaultF);
            this.g = builder.comment("TEST VALUE").define("debug", defaultG);
            this.h = builder.comment("TEST VALUE").define("debug", defaultH);
            this.i = builder.comment("TEST VALUE").define("debug", defaultI);
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
