package com.toomuchiq.sbp.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toomuchiq.sbp.config.Config;
import com.toomuchiq.sbp.ui.components.CustomCheckbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ConfigGUI extends Screen {

    private static final Component TITLE = new TranslatableComponent("configGui.title");

    public ConfigGUI() {
        super(TITLE);
    }

    @Override
    protected void init() {
        addRenderableWidget(new CustomCheckbox(this.width / 2 - 100, this.height / 2, 50, 50, new TranslatableComponent("configGui.title"), (onPress) -> {
            Config.COMMON.debug.set(!Config.COMMON.debug.get());
        }, Config.COMMON.debug));
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, Color.darkGray.getRGB(), Color.darkGray.getRGB());
        //drawCenteredString(matrixStack, this.font, String text, int x, int y, int color
        // drawCenteredString(matrixStack, Minecraft.getInstance().font, TITLE, this.width/2, 70, Color.RED.getRGB());
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
    }
}
