package com.toomuchiq.sbp.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CustomButton extends Button {

    public ResourceLocation buttonTexture;
    
    public CustomButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, ResourceLocation buttonTexture) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
        this.buttonTexture = buttonTexture;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, buttonTexture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int isHovered = this.getYImage(this.isHoveredOrFocused()); //0 not active, 1 is active, 2 active and hovered
        this.blit(matrixStack, this.x, this.y, 0, (isHovered * this.height) - this.height, this.width, this.height);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }

}
