package xyz.ev0lve.evolution;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.ev0lve.evolution.executor.ExecutorList;
import xyz.ev0lve.evolution.gui.Menu;

@Mod("evolution")
public class Evolution
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final KeyMapping KEY_GUI = new KeyMapping("Evolution", 999, "key.categories.misc");

    private boolean isGuiKeyDown = false;

    public Evolution() {
        MinecraftForge.EVENT_BUS.register(this);

        // add gui key
        var mc = Minecraft.getInstance();
        mc.options.keyMappings = ArrayUtils.add(mc.options.keyMappings, KEY_GUI);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ExecutorList.getInstance().render(event);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        var mc = Minecraft.getInstance();

        // don't run if player doesn't exist
        if (mc.player == null) {
            return;
        }

        if (KEY_GUI.isDown()) {
            if (!isGuiKeyDown) {
                var menu = new Menu(mc.screen);
                mc.setScreen(menu);

                isGuiKeyDown = true;
            }
        } else {
            isGuiKeyDown = false;
        }

        ExecutorList.getInstance().tick(event.type, event.phase);
    }

    @SubscribeEvent
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        event.setResult(Event.Result.ALLOW);
    }
}
