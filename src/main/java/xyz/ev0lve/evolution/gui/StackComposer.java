package xyz.ev0lve.evolution.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import xyz.ev0lve.evolution.Config;

public class StackComposer {
    private int yOffset = 0;
    private int xOffset = 0;

    public Button createToggle(String cfg, String title) {
        var button = new Button(15 + 102 * xOffset, 15 + 22 * yOffset, 100, 20,
                new TextComponent("%s [%s]".formatted(title, Config.get(cfg) ? "ON" : "OFF")), (btn) -> {
            Config.toggle(cfg);
            btn.setMessage(new TextComponent("%s [%s]".formatted(title, Config.get(cfg) ? "ON" : "OFF")));
        });

        yOffset++;
        return button;
    }

    public void nextColumn() {
        xOffset++;
        yOffset = 0;
    }

    public void nextRow() {
        yOffset++;
    }
}
