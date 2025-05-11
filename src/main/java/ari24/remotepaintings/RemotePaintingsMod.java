package ari24.remotepaintings;

import ari24.remotepaintings.commands.Commands;
import ari24.remotepaintings.util.CustomPaintingManager;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemotePaintingsMod implements ModInitializer {
	public static final String MOD_ID = "remote-paintings";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static CustomPaintingManager customPaintingManager;

	@Override
	public void onInitialize() {
		customPaintingManager = new CustomPaintingManager();
		LOGGER.info("Hello Fabric world!");

		Commands.registerClient();
	}
}