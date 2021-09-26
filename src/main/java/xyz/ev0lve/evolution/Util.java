package xyz.ev0lve.evolution;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class Util {
    public static List<LivingEntity> getNearbyEntities() {
        var me = Minecraft.getInstance().player;
        assert me != null;

        var area = me.getBoundingBox().inflate(64.0);
        return me.level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, me, area);
    }

    public static AABB getNearby(double inflate) {
        var me = getMe();
        assert me != null;

        return me.getBoundingBox().inflate(inflate);
    }

    public static ChunkPos getChunk() {
        var me = getMe();
        assert me != null;

        return me.chunkPosition();
    }

    public static LocalPlayer getMe() {
        return Minecraft.getInstance().player;
    }

    public static LevelAccessor getLevel() {
        return Minecraft.getInstance().level;
    }
}
