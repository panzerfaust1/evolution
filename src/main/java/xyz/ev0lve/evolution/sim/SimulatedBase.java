package xyz.ev0lve.evolution.sim;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public abstract class SimulatedBase {
    protected Vec3 pos;
    protected Vec3 deltaMovement;
    protected boolean didHit = false;
    protected boolean didHitEntity = false;

    public void setDeltaMovement(Vec3 vec) {
        deltaMovement = vec;
    }

    public Vec3 getDeltaMovement() {
        return deltaMovement;
    }

    public Vec3 getPos() {
        return pos;
    }

    public boolean didHit() {
        return didHit;
    }

    public boolean didHitEntity() {
        return didHitEntity;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(pos);
    }

    public abstract void advance();

    protected float interpolateRotation(float a, float b) {
        // clamp
        while (b - a < -180.f) {
            a -= 360.0F;
        }

        while (b - a >= 180.f) {
            a += 360.0F;
        }

        return Mth.lerp(0.2f, a, b);
    }
}
