package com.tmiq.mixin.accessors;

import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Frustum.class)
public interface FrustumInvoker {

    @Invoker
    boolean invokeIsVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

}
