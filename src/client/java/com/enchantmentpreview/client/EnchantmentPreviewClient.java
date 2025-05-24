package client.java.com.enchantmentpreview.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class EnchantmentPreviewClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("enchantment-preview-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Enchantment Preview Client initialized!");
        
        // Additional client-side initialization can go here
        // For example: key bindings, client-side config, etc.
    }
}