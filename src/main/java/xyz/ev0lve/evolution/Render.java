package xyz.ev0lve.evolution;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Render {
    private final Tesselator ts;
    private final BufferBuilder buffer;
    private PoseStack poseStack;
    private final Vec3 cameraPos;

    public static class Color
    {
        public final float r, g, b, a;

        public Color(int r, int g, int b) {
            this.r = r / 255.f;
            this.g = g / 255.f;
            this.b = b / 255.f;
            this.a = 1.f;
        }

        public Color(int r, int g, int b, int a) {
            this.r = r / 255.f;
            this.g = g / 255.f;
            this.b = b / 255.f;
            this.a = a / 255.f;
        }
    }

    public Render() {
        ts = Tesselator.getInstance();
        buffer = ts.getBuilder();

        cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().reverse();
    }

    public void begin(PoseStack stack) {
        beginInternal(stack);
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
    }

    public void block(Vec3 min, Vec3 max, Color col) {
        var bb = new AABB(min, max).move(cameraPos);
        LevelRenderer.addChainedFilledBoxVertices(buffer, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, col.r, col.g, col.b, col.a);
    }

    public void line(Vec3 a, Vec3 b, Color col) {
        var bb = new AABB(a, b).move(cameraPos);

        var dx = bb.maxX - bb.minX;
        var dy = bb.maxY - bb.minY;
        var dz = bb.maxZ - bb.minZ;

        var inv = (1.f / Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.125;
        dx *= inv;
        dz *= inv;

        buffer.vertex(bb.minX + dz, bb.minY, bb.minZ - dx).color(col.r, col.g, col.b, col.a).endVertex();
        buffer.vertex(bb.maxX + dz, bb.maxY, bb.maxZ - dx).color(col.r, col.g, col.b, col.a).endVertex();
        buffer.vertex(bb.maxX - dz, bb.maxY, bb.maxZ + dx).color(col.r, col.g, col.b, col.a).endVertex();

        buffer.vertex(bb.minX + dz, bb.minY, bb.minZ - dx).color(col.r, col.g, col.b, col.a).endVertex();
        buffer.vertex(bb.maxX - dz, bb.maxY, bb.maxZ + dx).color(col.r, col.g, col.b, col.a).endVertex();
        buffer.vertex(bb.minX - dz, bb.minY, bb.minZ + dx).color(col.r, col.g, col.b, col.a).endVertex();
    }

    public void end() {
        ts.end();
        endInternal();
    }

    private void beginInternal(PoseStack stack) {
        if (poseStack != null) {
            throw new RuntimeException("Cannot start context which is not ended");
        }

        poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.mulPoseMatrix(stack.last().pose());

        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
    }

    private void endInternal() {
        if (poseStack == null) {
            throw new RuntimeException("Cannot end context which is not started");
        }

        poseStack.popPose();

        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        poseStack = null;
    }
}
