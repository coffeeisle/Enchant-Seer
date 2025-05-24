package com.enchantmentpreview;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantmentPreviewMod implements ClientModInitializer {
    public static final String MOD_ID = "enchantment-preview";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Enchantment Preview Mod initialized!");
        // The actual functionality is now handled entirely by the mixin
        // This keeps things clean and ensures compatibility
    }
}