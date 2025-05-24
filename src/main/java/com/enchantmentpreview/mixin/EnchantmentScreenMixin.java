package com.enchantmentpreview.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

// Target the enchantment screen specifically using the string target approach
@Mixin(targets = "net.minecraft.client.gui.screen.ingame.EnchantingScreen")
public class EnchantmentScreenMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Get the screen instance
        Object screenObj = this;
        
        // We'll need to use reflection or cast to access the screen handler
        try {
            // Cast to HandledScreen to access the screen handler
            if (screenObj instanceof HandledScreen<?> screen && 
                screen.getScreenHandler() instanceof EnchantmentScreenHandler handler) {
                
                // Check if hovering over any enchantment buttons
                for (int i = 0; i < 3; i++) {
                    if (isHoveringOverEnchantmentButton(screen, mouseX, mouseY, i)) {
                        ItemStack itemToEnchant = handler.getSlot(0).getStack();
                        ItemStack lapisStack = handler.getSlot(1).getStack();
                        
                        // Check if we have a valid enchantment setup
                        if (!itemToEnchant.isEmpty() && 
                            handler.enchantmentPower[i] > 0 && 
                            lapisStack.getItem() == Items.LAPIS_LAZULI && 
                            lapisStack.getCount() >= handler.enchantmentId[i]) {
                            
                            List<Text> tooltip = generateEnchantmentTooltip(
                                itemToEnchant, 
                                handler.enchantmentId[i], 
                                handler.enchantmentPower[i], 
                                handler.seed,
                                i
                            );
                            
                            if (!tooltip.isEmpty()) {
                                context.drawTooltip(screen.getTextRenderer(), tooltip, mouseX, mouseY);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail if we can't access the screen handler
            com.enchantmentpreview.EnchantmentPreviewMod.LOGGER.debug("Could not access enchantment screen handler", e);
        }
    }
    
    private boolean isHoveringOverEnchantmentButton(HandledScreen<?> screen, int mouseX, int mouseY, int buttonIndex) {
        // Calculate button positions based on vanilla enchantment screen layout
        int screenX = (screen.width - 176) / 2;
        int screenY = (screen.height - 166) / 2;
        
        // Enchantment button positions in the GUI
        int buttonX = screenX + 60;
        int buttonY = screenY + 14 + buttonIndex * 19;
        int buttonWidth = 108;
        int buttonHeight = 19;
        
        return mouseX >= buttonX && mouseX < buttonX + buttonWidth && 
               mouseY >= buttonY && mouseY < buttonY + buttonHeight;
    }
    
    private List<Text> generateEnchantmentTooltip(ItemStack itemStack, int cost, int level, int seed, int option) {
        List<Text> tooltip = new ArrayList<>();
        
        try {
            // Create a copy of the item to test enchantments on
            ItemStack testStack = itemStack.copy();
            
            // Use the enchantment table's seed combined with the option index
            Random random = Random.create(seed + option);
            
            // Generate enchantments using vanilla logic
            List<net.minecraft.enchantment.EnchantmentLevelEntry> enchantments = 
                EnchantmentHelper.generateEnchantments(random, testStack, level, false);
            
            if (!enchantments.isEmpty()) {
                // Header
                tooltip.add(Text.literal("Enchantment Preview").formatted(Formatting.GOLD, Formatting.UNDERLINE));
                tooltip.add(Text.literal(""));
                
                // List each enchantment
                for (net.minecraft.enchantment.EnchantmentLevelEntry entry : enchantments) {
                    Enchantment enchantment = entry.enchantment;
                    int enchLevel = entry.level;
                    
                    // Get the proper enchantment name with level
                    Text enchantmentName = enchantment.getName(enchLevel);
                    tooltip.add(Text.literal("• ").formatted(Formatting.GRAY)
                        .append(enchantmentName.copy().formatted(Formatting.AQUA)));
                }
                
                // Cost information
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("Cost: " + cost + " Experience Levels").formatted(Formatting.GREEN));
                tooltip.add(Text.literal("Requires: " + cost + " Lapis Lazuli").formatted(Formatting.BLUE));
                
                // Add helpful hint
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("Click to enchant!").formatted(Formatting.YELLOW, Formatting.ITALIC));
                
            } else {
                // No enchantments case
                tooltip.add(Text.literal("No Enchantments Available").formatted(Formatting.DARK_GRAY, Formatting.UNDERLINE));
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("This item cannot be enchanted").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("at this experience level.").formatted(Formatting.GRAY));
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("Try using more bookshelves").formatted(Formatting.YELLOW));
                tooltip.add(Text.literal("or a higher experience level.").formatted(Formatting.YELLOW));
            }
            
        } catch (Exception e) {
            // Error handling
            tooltip.add(Text.literal("Enchantment Preview Error").formatted(Formatting.RED, Formatting.UNDERLINE));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("Cannot preview enchantments").formatted(Formatting.DARK_RED));
            tooltip.add(Text.literal("for this item.").formatted(Formatting.DARK_RED));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("This may be due to:").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("• Mod compatibility issues").formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.literal("• Invalid item type").formatted(Formatting.DARK_GRAY));
            
            // Log the error for debugging
            com.enchantmentpreview.EnchantmentPreviewMod.LOGGER.error("Error generating enchantment tooltip for item: " + itemStack.getItem().toString(), e);
        }
        
        return tooltip;
    }
}