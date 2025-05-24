package main.java.com.enchantmentpreview.mixin;

import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
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
import java.util.Map;

@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {
    
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        EnchantmentScreen screen = (EnchantmentScreen) (Object) this;
        EnchantmentScreenHandler handler = screen.getScreenHandler();
        
        // Check if hovering over enchantment buttons
        for (int i = 0; i < 3; i++) {
            if (isHoveringOverEnchantmentButton(screen, mouseX, mouseY, i)) {
                ItemStack itemToEnchant = handler.getSlot(0).getStack();
                ItemStack lapisStack = handler.getSlot(1).getStack();
                
                if (!itemToEnchant.isEmpty() && 
                    handler.enchantmentPower[i] > 0 && 
                    lapisStack.getItem() == Items.LAPIS_LAZULI && 
                    lapisStack.getCount() >= handler.enchantmentId[i]) {
                    
                    List<Text> tooltip = generateAccurateEnchantmentTooltip(
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
    
    private boolean isHoveringOverEnchantmentButton(EnchantmentScreen screen, int mouseX, int mouseY, int buttonIndex) {
        // Calculate button positions based on vanilla enchantment screen layout
        int screenX = (screen.width - 176) / 2;
        int screenY = (screen.height - 166) / 2;
        
        int buttonX = screenX + 60;
        int buttonY = screenY + 14 + buttonIndex * 19;
        int buttonWidth = 108;
        int buttonHeight = 19;
        
        return mouseX >= buttonX && mouseX < buttonX + buttonWidth && 
               mouseY >= buttonY && mouseY < buttonY + buttonHeight;
    }
    
    private List<Text> generateAccurateEnchantmentTooltip(ItemStack itemStack, int cost, int level, int seed, int option) {
        List<Text> tooltip = new ArrayList<>();
        
        try {
            // Create random with the same seed used by the enchantment table
            Random random = Random.create(seed + option);
            
            // Create a temporary copy of the item
            ItemStack tempStack = itemStack.copy();
            
            // Generate enchantments using the same method as the game
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.generateEnchantments(
                random, tempStack, level, false
            ).stream().collect(
                java.util.stream.Collectors.toMap(
                    entry -> entry.enchantment,
                    entry -> entry.level
                )
            );
            
            if (!enchantments.isEmpty()) {
                tooltip.add(Text.literal("Enchantments Preview:").formatted(Formatting.GOLD, Formatting.UNDERLINE));
                tooltip.add(Text.literal(""));
                
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    int enchLevel = entry.getValue();
                    
                    Text enchantmentText = enchantment.getName(enchLevel);
                    tooltip.add(Text.literal("• ").formatted(Formatting.GRAY)
                        .append(enchantmentText.copy().formatted(Formatting.AQUA)));
                }
                
                tooltip.add(Text.literal(""));
                tooltip.add(Text.literal("Cost: " + cost + " Experience Levels").formatted(Formatting.GREEN));
                tooltip.add(Text.literal("Requires: " + cost + " Lapis Lazuli").formatted(Formatting.BLUE));
                
            } else {
                tooltip.add(Text.literal("No enchantments available").formatted(Formatting.DARK_GRAY));
                tooltip.add(Text.literal("Item may not be enchantable at this level").formatted(Formatting.GRAY));
            }
            
        } catch (Exception e) {
            tooltip.add(Text.literal("Error: Cannot preview enchantments").formatted(Formatting.RED));
            tooltip.add(Text.literal("This may be due to mod compatibility issues").formatted(Formatting.DARK_RED));
        }
        
        return tooltip;
    }
}