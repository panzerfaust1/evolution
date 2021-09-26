package xyz.ev0lve.evolution.sim;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import xyz.ev0lve.evolution.Evolution;
import xyz.ev0lve.evolution.Util;

public class SimulatedProjectile extends SimulatedBase {
    private final float force;
    private final Vec3 direction;

    private Vec3 rotation;
    private Vec3 oldRotation;

    public SimulatedProjectile(float force, Vec3 initialPos, Vec3 dir) {
        this.force = force;
        pos = initialPos;
        direction = dir;
    }

    public void initiate() {
        deltaMovement = direction.normalize().scale(force);

        var degree = 180.f / Math.PI;
        var dirDelta = direction.horizontalDistance();
        rotation = new Vec3(Mth.atan2(direction.x, direction.z) * degree, Mth.atan2(direction.y, dirDelta) * degree, 0.f);
        oldRotation = rotation;
    }

    private static boolean canHitEntity(Entity ent) {
        var me = Util.getMe();
        if (!ent.isAlive() || !ent.isAttackable() || me.isPassengerOfSameVehicle(ent)) {
            return false;
        }

        if (ent.isSpectator() || ent == me) {
            return false;
        }

        return !(ent instanceof EnderMan);
    }

    public void advance() {
        var level = Util.getLevel();
        var degree = (float)(180.f / Math.PI);

        oldRotation = rotation;

        // advance position
        var deltaPos = deltaMovement;
        var deltaLen = deltaPos.horizontalDistance();

        // check if hit block
        var nextPos = pos.add(deltaPos);
        var hitResult = level.clip(new ClipContext(pos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        if (hitResult.getType() != HitResult.Type.MISS) {
            nextPos = hitResult.getLocation();
            pos = nextPos;
            didHit = true;
        }

        if (didHit) {
            return;
        }

        // check if hit entity
        var boxSize = new Vec3(0.5f, 0.5f, 0.5f);
        var bb = new AABB(boxSize.multiply(-1.f, -1.f, -1.f), boxSize).move(nextPos);

        var entities = level.getEntities((Entity)null, bb, SimulatedProjectile::canHitEntity);
        if (!entities.isEmpty()) {
            didHit = true;
            didHitEntity = true;
            pos = nextPos;
        }

        if (didHit) {
            return;
        }

        // advance rotation and move position
        var newYRotation = interpolateRotation((float)oldRotation.y, (float)Mth.atan2(deltaPos.x, deltaPos.z) * degree);
        var newXRotation = interpolateRotation((float)oldRotation.x, (float)Mth.atan2(deltaPos.y, deltaLen) * degree);

        rotation = new Vec3(newXRotation, newYRotation, 0.f);

        // TODO: check if in water
        var movementMod = 0.99f;

        var scaledDeltaPos = deltaPos.scale(movementMod);
        deltaMovement = new Vec3(scaledDeltaPos.x, scaledDeltaPos.y - 0.05f, scaledDeltaPos.z);
        pos = nextPos;
    }
}
