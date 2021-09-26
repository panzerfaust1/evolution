package xyz.ev0lve.evolution.executor.hacks;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import xyz.ev0lve.evolution.Config;
import xyz.ev0lve.evolution.Render;
import xyz.ev0lve.evolution.Util;
import xyz.ev0lve.evolution.executor.IExecutor;
import xyz.ev0lve.evolution.sim.BowSimulation;
import xyz.ev0lve.evolution.sim.SimulationResult;

import java.util.ArrayList;

public class Trajectory implements IExecutor {
    private ArrayList<Vec3> travel = new ArrayList<>();
    private Render.Color color = new Render.Color(255, 255, 255, 50);

    @Override
    public void tick(TickEvent.Type type, TickEvent.Phase phase) {
        if (!Config.get("trajectory.enable")) {
            return;
        }

        var me = Util.getMe();
        var stack = me.getMainHandItem();
        var item = stack.getItem();

        travel.clear();
        color = new Render.Color(255, 255, 255, 50);

        if (item instanceof BowItem && Config.get("trajectory.bow")) {
            var result = BowSimulation.simulate(me, (BowItem) item, stack);
            if (result.getResult() != SimulationResult.Result.MISS) {
                travel = result.getTravelPath();
                if (result.getResult() == SimulationResult.Result.HIT_ENTITY) {
                    color = new Render.Color(255, 25, 25, 50);
                }
            }
        }
    }

    @Override
    public void render(RenderWorldLastEvent event) {
        if (!Config.get("trajectory.enable") || travel.isEmpty()) {
            return;
        }

        var render = new Render();
        render.begin(event.getMatrixStack());

        var delta = new Vec3(0.125, 0.125, 0.125);
        for (var travelPoint : travel) {
            render.block(travelPoint.subtract(delta), travelPoint.add(delta), color);
        }

        render.end();
    }
}
