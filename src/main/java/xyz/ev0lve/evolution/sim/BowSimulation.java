package xyz.ev0lve.evolution.sim;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BowSimulation {
    public static SimulationResult simulate(Player player, BowItem item, ItemStack stack) {
        var travel = new SimulationResult();
        if (!player.isUsingItem()) {
            return travel;
        }

        var travelTime = item.getUseDuration(stack) - player.getUseItemRemainingTicks();
        var power = BowItem.getPowerForTime(travelTime);

        if (power <= 0.1f) {
            return travel;
        }

        var playerMovement = player.getDeltaMovement();
        var shootPos = new Vec3(player.getX(), player.getEyeY() - 0.1, player.getZ());
        var playerDirection = new Vec3(playerMovement.x, player.isOnGround() ? 0.f : playerMovement.y, playerMovement.z);

        var arrow = new SimulatedProjectile(power * 3.f, shootPos, getShootDirection(new Vec3(player.getXRot(), player.getYRot(), 0.f)));
        arrow.initiate();
        arrow.setDeltaMovement(arrow.getDeltaMovement().add(playerDirection));

        var maxTicks = 128;
        while (!arrow.didHit() && (maxTicks-- > 0)) {
            arrow.advance();
            travel.addTravelPoint(arrow.getPos());
        }

        if (arrow.didHit()) {
            travel.setResult(arrow.didHit() && arrow.didHitEntity() ? SimulationResult.Result.HIT_ENTITY : SimulationResult.Result.HIT_BLOCK);
        }

        return travel;
    }

    private static Vec3 getShootDirection(Vec3 rotation) {
        var radian = (float)(Math.PI / 180.f);

        var x = -Mth.sin((float)rotation.y * radian) * Mth.cos((float)rotation.x * radian);
        var y = -Mth.sin((float)(rotation.x + rotation.z) * radian);
        var z = Mth.cos((float)rotation.y * radian) * Mth.cos((float)rotation.x * radian);

        return new Vec3(x, y, z);
    }
}
