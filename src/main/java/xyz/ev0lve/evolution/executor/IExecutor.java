package xyz.ev0lve.evolution.executor;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;

public interface IExecutor {
    void tick(TickEvent.Type type, TickEvent.Phase phase);
    void render(RenderWorldLastEvent event);
}
