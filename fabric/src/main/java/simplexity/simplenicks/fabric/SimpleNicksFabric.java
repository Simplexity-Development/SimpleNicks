package simplexity.simplenicks.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import simplexity.simplenicks.SimpleNicksCore;
import simplexity.simplenicks.config.ConfigHandler;
import simplexity.simplenicks.fabric.commands.FabricNicknameCommand;
import simplexity.simplenicks.fabric.events.FabricLeaveHandler;
import simplexity.simplenicks.fabric.events.FabricLoginHandler;
import simplexity.simplenicks.fabric.platform.FabricPlatformAdapter;
import simplexity.simplenicks.saving.SqlHandler;

import java.nio.file.Path;

/**
 * Fabric mod entry point for SimpleNicks.
 */
public class SimpleNicksFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Path dataDir = FabricLoader.getInstance().getConfigDir().resolve("simplenicks");

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            FabricPlatformAdapter adapter = new FabricPlatformAdapter(server, dataDir);
            SimpleNicksCore.initialize(adapter);
            ConfigHandler.getInstance().reloadConfig();
            SqlHandler.getInstance().init();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            SqlHandler.getInstance().closeDatabase();
            SimpleNicksCore.shutdown();
        });

        FabricLoginHandler.register();
        FabricLeaveHandler.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(FabricNicknameCommand.createCommand()));
    }
}
