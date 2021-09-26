package xyz.ev0lve.evolution.sim;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class SimulationResult {
    public enum Result {
        MISS,
        HIT_BLOCK,
        HIT_ENTITY
    }

    private Result hitResult;
    private final ArrayList<Vec3> travelPath;

    public SimulationResult() {
        hitResult = Result.MISS;
        travelPath = new ArrayList<>();
    }

    public void setResult(Result r) {
        hitResult = r;
    }

    public Result getResult() {
        return hitResult;
    }

    public void addTravelPoint(Vec3 point) {
        travelPath.add(point);
    }

    public ArrayList<Vec3> getTravelPath() {
        return travelPath;
    }
}
