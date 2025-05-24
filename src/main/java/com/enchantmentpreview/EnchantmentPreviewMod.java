package main.java.com.enchantmentpreview;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class EnchantmentPreviewMod implements ClientModInitializer {
    public static final String MOD_ID = "enchantment-preview";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Enchantment Preview Mod initialized!");
        
        // Register screen event to handle enchantment table tooltips
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof EnchantmentScreen enchantmentScreen) {
                // Add tooltip rendering for enchantment previews
                ScreenEvents.afterRender(enchantmentScreen).register((screenInstance, drawContext, mouseX, mouseY, delta) -> {
                    handleEnchantmentTooltips(enchantmentScreen, drawContext, mouseX, mouseY);
                });
            }
        });
    }

    private void handleEnchantmentTooltips(EnchantmentScreen screen, net.minecraft.client.gui.DrawContext drawContext, int mouseX, int mouseY) {
        // Access the enchantment table screen handler
        var handler = screen.getScreenHandler();
        
        // Check if we're hovering over an enchantment option
        for (int i = 0; i < 3; i++) {
            // Calculate button bounds (approximate positions based on vanilla GUI)
            int buttonX = screen.width / 2 + 60;
            int buttonY = screen.height / 2 - 27 + i * 19;
            int buttonWidth = 108;
            int buttonHeight = 19;
            
            if (mouseX >= buttonX && mouseX < buttonX + buttonWidth && 
                mouseY >= buttonY && mouseY < buttonY + buttonHeight) {
                
                // Get the item being enchanted
                ItemStack itemToEnchant = handler.getSlot(0).getStack();
                if (!itemToEnchant.isEmpty() && handler.enchantmentPower[i] > 0) {
                    // Get lapis lazuli count
                    ItemStack lapisStack = handler.getSlot(1).getStack();
                    int lapisCount = lapisStack.getItem() == Items.LAPIS_LAZULI ? lapisStack.getCount() : 0;
                    
                    if (lapisCount >= handler.enchantmentId[i]) {
                        // Generate the actual enchantments that would be applied
                        List<Text> tooltip = generateEnchantmentTooltip(itemToEnchant, handler.enchantmentId[i], handler.enchantmentPower[i], i);
                        
                        if (!tooltip.isEmpty()) {
                            // Render the tooltip
                            drawContext.drawTooltip(screen.getTextRenderer(), tooltip, mouseX, mouseY);
                        }
                    }
                }
                break;
            }
        }
    }

    private List<Text> generateEnchantmentTooltip(ItemStack itemStack, int enchantmentId, int level, int option) {
        List<Text> tooltip = new java.util.ArrayList<>();
        
        try {
            // Create a copy of the item to test enchantments on
            ItemStack testStack = itemStack.copy();
            
            // Use the same seed generation logic as the enchantment table
            Random random = Random.create();
            
            // Generate enchantments using the same logic as EnchantmentHelper
            // We need to simulate the enchantment selection process
            List<net.minecraft.enchantment.EnchantmentLevelEntry> possibleEnchantments = 
                EnchantmentHelper.generateEnchantments(random, testStack, level, false);
            
            if (!possibleEnchantments.isEmpty()) {
                tooltip.add(Text.literal("Enchantments:").formatted(Formatting.GOLD));
                
                for (net.minecraft.enchantment.EnchantmentLevelEntry entry : possibleEnchantments) {
                    Enchantment enchantment = entry.enchantment;
                    int enchLevel = entry.level;
                    
                    String enchantmentName = enchantment.getName(enchLevel).getString();
                    tooltip.add(Text.literal("• " + enchantmentName).formatted(Formatting.GRAY));
                }
                
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("Cost: " + enchantmentId + " Levels").formatted(Formatting.GREEN));
                tooltip.add(Text.literal("Required: " + enchantmentId + " Lapis Lazuli").formatted(Formatting.BLUE));
            } else {
                tooltip.add(Text.literal("No enchantments available").formatted(Formatting.DARK_GRAY));
            }
            
        } catch (Exception e) {
            LOGGER.error("Error generating enchantment tooltip", e);
            tooltip.add(Text.literal("Error loading enchantments").formatted(Formatting.RED));
        }
        
        return tooltip;
    }
}