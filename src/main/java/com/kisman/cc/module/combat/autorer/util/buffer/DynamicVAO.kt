package com.kisman.cc.module.combat.autorer.util.buffer

import com.kisman.cc.module.combat.autorer.math.MathUtilKt
import i.gishreloaded.gishcode.utils.TimerUtils
import net.minecraft.client.renderer.GLAllocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import java.nio.ByteBuffer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract as contract1

enum class DynamicVAO(private val vertexSize: Int, private val buildVAO: () -> Unit) {
    POS2_COLOR(12, {
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 12, 0L)
        glVertexAttribPointer(1, 4, GL_UNSIGNED_BYTE, true, 12, 8L)

        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
    }),
    POS3_COLOR(16, {
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 16, 0L)
        glVertexAttribPointer(1, 4, GL_UNSIGNED_BYTE, true, 16, 12L)

        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
    });

    private var bufferSize = 0

    init {
        allocateBuffer(64)
    }

    fun upload(size: Int) {
        buffer.flip()
        ensureCapacity(size * vertexSize)

        useVbo ({
            glBufferSubData(GL_ARRAY_BUFFER, 0, buffer)
        }, this)

        buffer.clear()
    }

    private fun ensureCapacity(size: Int) {
        if (size > bufferSize) {
            allocateBuffer(MathUtilKt.ceilToInt(size / 64.0) * 64)
        }
    }

    fun useVao(unit: DynamicVAO.() -> Unit) {
        useVao (unit, this)
    }

    private fun allocateBuffer(size: Int) {
        if (size == bufferSize) return

        useVao ({
            useVbo ({
                glBufferData(GL_ARRAY_BUFFER, size.toLong(), GL_DYNAMIC_DRAW)
                buildVAO.invoke()
            }, this)
        }, this)
        bufferSize = size
    }

    companion object {
        val buffer: ByteBuffer = GLAllocation.createDirectByteBuffer(0x800000)
        private val timer = TimerUtils()

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent fun onTick(event: TickEvent.ClientTickEvent) {
            if (timer.passedTicks(5)) {
                timer.reset()
                values().forEach {
                    it.allocateBuffer(64)
                }
            }
        }
    }
}

val vaoID = glGenVertexArrays()
val vboID = glGenBuffers()

@UseExperimental(ExperimentalContracts::class)
inline fun useVbo(block: DynamicVAO.() -> Unit, parent: DynamicVAO) {
    contract1 {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    glBindBuffer(GL_ARRAY_BUFFER, vboID)
    block.invoke(parent)
    glBindBuffer(GL_ARRAY_BUFFER, 0)
}

@UseExperimental(ExperimentalContracts::class)
inline fun useVao(block: DynamicVAO.() -> Unit, parent: DynamicVAO) {
    contract1 {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    glBindVertexArray(vaoID)
    block.invoke(parent)
    glBindVertexArray(0)
}