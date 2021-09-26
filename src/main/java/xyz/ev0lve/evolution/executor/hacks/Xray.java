package xyz.ev0lve.evolution.executor.hacks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import xyz.ev0lve.evolution.Config;
import xyz.ev0lve.evolution.Render;
import xyz.ev0lve.evolution.Util;
import xyz.ev0lve.evolution.executor.IExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Xray implements IExecutor {
    private static final HashMap<String, Render.Color> colors = new HashMap<>() {{
        put("end_portal_frame", new Render.Color(255, 25, 255, 10));
        put("ancient_debris", new Render.Color(255, 255, 255, 50));
        put("diamond_ore", new Render.Color(100, 100, 255, 50));
        put("emerald_ore", new Render.Color(100, 255, 100, 50));
        put("_ore", new Render.Color(255, 255, 255, 10));
        put("trapped_chest", new Render.Color(255, 25, 25, 50));
        put("chest", new Render.Color(255, 100, 255, 10));
        put("spawner", new Render.Color(255, 25, 25, 50));
    }};

    private static final ArrayList<String> rare = new ArrayList<>() {{
        add("end_portal_frame");
        add("ancient_debris");
        add("diamond_ore");
        add("emerald_ore");
        add("spawner");
        add("chest");
    }};

    private final HashMap<BlockPos, Render.Color> renderQueue = new HashMap<>();
    private long lastUpdate = 0;

    @Override
    public void tick(TickEvent.Type type, TickEvent.Phase phase) {
        var isEnabled = Config.get("xray.enable");
        if (type != TickEvent.Type.CLIENT || !isEnabled) {
            return;
        }

        var time = System.currentTimeMillis() / 1000;
        if (time - lastUpdate > 5) {
            renderQueue.clear();

            var limitDist = 16;
            if (Config.get("xray.limit_distance")) {
                limitDist = -8;
            }

            var world = Util.getLevel();
            var chunk = Util.getChunk();
            for (var y = 0; y <= 64; y++) {
                for (var x = chunk.getMinBlockX() - limitDist; x <= chunk.getMaxBlockX() + limitDist; x++) {
                    for (var z = chunk.getMinBlockZ() - limitDist; z <= chunk.getMaxBlockZ() + limitDist; z++) {
                        var block = world.getBlockState(new BlockPos(x, y, z)).getBlock();

                        var registryName = block.getRegistryName();
                        if (registryName == null) {
                            continue;
                        }

                        var registryNameStr = registryName.toString();
                        if (Config.get("xray.rare_only")) {
                            var found = new AtomicBoolean(false);
                            rare.forEach((name) -> {
                                if (!found.get()) {
                                    found.set(registryNameStr.endsWith(name));
                                }
                            });

                            if (!found.get()) {
                                continue;
                            }
                        }

                        var renderColor = new AtomicReference<Render.Color>(null);
                        colors.forEach((name, color) -> {
                            if (registryNameStr.endsWith(name) && renderColor.get() == null) {
                                renderColor.set(color);
                            }
                        });

                        var color = renderColor.get();
                        if (color != null) {
                            renderQueue.put(new BlockPos(x, y, z), color);
                        }
                    }
                }
            }

            lastUpdate = time;
        }
    }

    @Override
    public void render(RenderWorldLastEvent event) {
        if (!Config.get("xray.enable")) {
            return;
        }

        var render = new Render();
        render.begin(event.getMatrixStack());

        renderQueue.forEach((pos, color) -> {
            var basePos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
            render.block(basePos, basePos.add(new Vec3(1, 1, 1)), color);
        });

        render.end();
    }
}
