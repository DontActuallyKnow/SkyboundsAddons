package com.toomuchiq.sbp.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.toomuchiq.sbp.ui.components.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ConfigGUI extends Screen {

    private static final Component TITLE = new TranslatableComponent("configGui.title");

    public ConfigGUI() {
        super(TITLE);
    }

    @Override
    protected void init() {
        addRenderableWidget(new CustomButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, new TranslatableComponent("configGui.title"), (widget) -> {
            //Code to execute when button is clicked
            this.onClose();
        },new ResourceLocation("sbp:textures/gui/woah.png")));
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        //drawCenteredString(matrixStack, this.font, String text, int x, int y, int color
        drawCenteredString(matrixStack, Minecraft.getInstance().font, TITLE, this.width/2, 70, Color.RED.getRGB());
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

}
