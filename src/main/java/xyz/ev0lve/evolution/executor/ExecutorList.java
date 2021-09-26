package xyz.ev0lve.evolution.executor;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import xyz.ev0lve.evolution.executor.hacks.Trajectory;
import xyz.ev0lve.evolution.executor.hacks.Xray;

import java.util.HashMap;
import java.util.Map;

public class ExecutorList {
    private static ExecutorList instance;
    private final Map<String, IExecutor> executors;

    public ExecutorList() {
        executors = new HashMap<>();
        executors.put("xray", new Xray());
        executors.put("trajectory", new Trajectory());
    }

    public static ExecutorList getInstance() {
        if (instance == null) {
            instance = new ExecutorList();
        }

        return instance;
    }

    public void tick(TickEvent.Type type, TickEvent.Phase phase) {
        executors.forEach((name, exec) -> {
            exec.tick(type, phase);
        });
    }

    public void render(RenderWorldLastEvent event) {
        executors.forEach((name, exec) -> {
            exec.render(event);
        });
    }

    public IExecutor get(String name) {
        return executors.get(name);
    }
}
