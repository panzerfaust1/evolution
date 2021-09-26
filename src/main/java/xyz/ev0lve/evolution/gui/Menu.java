package xyz.ev0lve.evolution.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Menu extends Screen {
    private final Screen previous;

    public Menu(Screen parent) {
        super(new TextComponent("Evolution"));
        previous = parent;
    }

    @Override
    protected void init() {
        var composer = new StackComposer();

        addRenderableWidget(composer.createToggle("xray.enable", "X-Ray"));
        addRenderableWidget(composer.createToggle("xray.rare_only", "Rare only"));
        addRenderableWidget(composer.createToggle("xray.limit_distance", "Limit dist"));

        composer.nextColumn();

        addRenderableWidget(composer.createToggle("trajectory.enable", "Trajectories"));
        addRenderableWidget(composer.createToggle("trajectory.bow", "- Bow"));
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(previous);
    }
}
