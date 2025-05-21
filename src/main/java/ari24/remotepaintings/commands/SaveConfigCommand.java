package ari24.remotepaintings.commands;

import ari24.remotepaintings.RemotePaintingsMod;
import ari24.remotepaintings.registry.RemotePaintingRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SaveConfigCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("saveConfig")
            .executes(ctx -> {
                List<RemotePaintingRegistry.Entry> paintings = RemotePaintingRegistry.getAllRemotePaintings();
                JsonArray array = new JsonArray();

                for (RemotePaintingRegistry.Entry entry : paintings) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", entry.vanillaIdentifier().getPath());
                    obj.addProperty("url", entry.remoteUrl());
                    array.add(obj);
                }

                String hasteUrl = uploadToHaste(array);
                if (hasteUrl != null) {
                    ctx.getSource().sendFeedback(Text.of("Uploaded and saved: " + hasteUrl)
                            .getWithStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, hasteUrl))
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to open")))
                                    .withColor(Formatting.GREEN)).getFirst());
                    RemotePaintingsMod.CONFIG.currentConfigUrl(hasteUrl);
                } else {
                    ctx.getSource().sendError(Text.of("Failed to upload data to Haste.").getWithStyle(Style.EMPTY.withColor(Formatting.RED)).getFirst());
                }
                return 1;
            });
    }

    private static @Nullable String uploadToHaste(JsonArray array) {
        String jsonString = new Gson().toJson(array);
        String urlString = RemotePaintingsMod.CONFIG.hasteUrl();

        try {
            // Open a connection to the Haste server
            HttpURLConnection connection = writeJsonHTTP(urlString, jsonString);

            // Read the response from the server
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse the response to extract the key
                JsonObject responseObject = new Gson().fromJson(response.toString(), JsonObject.class);
                String key = responseObject.get("key").getAsString();

                // Return the full URL to the uploaded document
                return "https://haste.pinofett.de/raw/" + key;
            }
        } catch (URISyntaxException | IOException e) {
            RemotePaintingsMod.LOGGER.error("Failed to upload data to Haste server", e);
            return null;
        }
    }

    @NotNull
    private static HttpURLConnection writeJsonHTTP(String urlString, String jsonString) throws URISyntaxException, IOException {
        URL hasteUrl = new URI(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) hasteUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write the JSON string to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
