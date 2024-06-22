package com.tmiq.dev

import com.tmiq.utils.NumberUtils.format
import com.tmiq.utils.TimeUnit
import com.tmiq.utils.Utils
import com.tmiq.utils.render.RenderUtils
import com.tmiq.utils.time.TimeMarker
import com.tmiq.utils.time.Timer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import javax.management.timer.TimerMBean

class BlockBrokenFeature {

    var blocksBroken = mutableMapOf<BlockPos, TimeMarker>()

    fun init() {
        blocksBroken.clear()
        PlayerBlockBreakEvents.BEFORE.register { world, player, pos, state, entity ->

            val marker = TimeMarker.now()

            if(!blocksBroken.containsKey(pos)) blocksBroken[pos] = marker

            true
        }

        WorldRenderEvents.AFTER_TRANSLUCENT.register { context ->

            for (block in blocksBroken) {

                val time = block.value.passedSince()
                val formattedTime = time.format(TimeUnit.MINUTE)

                RenderUtils.renderText(
                    context,
                    Text.literal(Utils.c("&4${formattedTime}", '&')),
                    block.key,
                    false
                )
            }


        }

    }

}