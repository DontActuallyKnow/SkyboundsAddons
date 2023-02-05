package com.toomuchiq.sbp.config;

import com.toomuchiq.sbp.ui.components.CustomCheckbox;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.Optional;

public enum UIComponents {
    // NAME | ID | TEXT | FUNCTION | WIDTH | HEIGHT | X | Y | CONFIG VALUE | COMPONENT TYPE
    DEBUG(1, new TranslatableComponent("configGui.title"), Config.COMMON.debug, ComponentType.CHECKBOX),
    A(2, new TranslatableComponent("configGui.title"), Config.COMMON.a, ComponentType.CHECKBOX),
    B(2, new TranslatableComponent("configGui.title"), Config.COMMON.b, ComponentType.CHECKBOX),
    C(2, new TranslatableComponent("configGui.title"), Config.COMMON.c, ComponentType.CHECKBOX),
    D(2, new TranslatableComponent("configGui.title"), Config.COMMON.d, ComponentType.CHECKBOX),
    E(2, new TranslatableComponent("configGui.title"), Config.COMMON.e, ComponentType.CHECKBOX),
    F(2, new TranslatableComponent("configGui.title"), Config.COMMON.f, ComponentType.CHECKBOX),
    G(2, new TranslatableComponent("configGui.title"), Config.COMMON.g, ComponentType.CHECKBOX),
    H(2, new TranslatableComponent("configGui.title"), Config.COMMON.h, ComponentType.CHECKBOX),
    I(2, new TranslatableComponent("configGui.title"), Config.COMMON.i, ComponentType.CHECKBOX);

    private final int id;
    private final TranslatableComponent text;
    private final ForgeConfigSpec.ConfigValue configValue;
    private final ComponentType componentType;

    UIComponents(int id, TranslatableComponent text, ForgeConfigSpec.ConfigValue configValue, ComponentType componentType) {
        this.id = id;
        this.text = text;
        this.configValue = configValue;
        this.componentType = componentType;
    }

    public int getId() {
        return id;
    }

    public TranslatableComponent getText() {
        return text;
    }

    public ForgeConfigSpec.ConfigValue getRelatedConfigValue() {
        return configValue;
    }

    public static Optional<UIComponents> getComponentById(int id) {
        return Arrays.stream(UIComponents.values())
                .filter(component -> component.id == id)
                .findFirst();
    }

    public enum ComponentType {
        CHECKBOX(CustomCheckbox.class),
        SWITCH(CustomCheckbox.class),
        TEXT(CustomCheckbox.class),
        PARAGRAPH(CustomCheckbox.class),
        PERCENT_SLIDER(CustomCheckbox.class),
        DECIMAL_SLIDER(CustomCheckbox.class),
        SLIDER(CustomCheckbox.class),
        NUMBER(CustomCheckbox.class),
        COLOR(CustomCheckbox.class),
        SELECTOR(CustomCheckbox.class),
        BUTTON(CustomCheckbox.class);

        private static Class customClass;
        private final Class componentClass;

        ComponentType(Class customClass) {
            this.componentClass = customClass;
        }

        public Class getComponentClass() {
            return componentClass;
        }

    }

}
