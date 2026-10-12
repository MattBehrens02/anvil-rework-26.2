package ca.bonzi.anvil_rework.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Shadow @Final private DataSlot cost;

    @Inject(method = "calculateIncreasedRepairCost", at = @At("HEAD"), cancellable = true)
    private static void enchantmentRework$noPenalty(int baseCost, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    // Lambda name comes from the decompiled onTake; 0.12F is the per-use damage chance.
    @ModifyConstant(method = "lambda$onTake$0", constant = @Constant(floatValue = 0.12f))
    private static float enchantmentRework$noUseDamage(float original) {
        return 0.0f;
    }

    // Ordinal 2 is the "cost >= 40" Too Expensive check; 0 and 1 are the stack-price and rename-cap uses.
    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2))
    private int enchantmentRework$noTooExpensive(int original) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void enchantmentRework$overrideCost(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        ItemStack input = self.getSlot(0).getItem();
        ItemStack addition = self.getSlot(1).getItem();
        ItemStack result = self.getSlot(2).getItem();
        if (result.isEmpty()) {
            return;
        }

        if (input.is(Items.ENCHANTED_BOOK) && addition.is(Items.ENCHANTED_BOOK)) {
            this.cost.set(1);
            return;
        }

        ItemEnchantments before = EnchantmentHelper.getEnchantmentsForCrafting(input);
        ItemEnchantments after = EnchantmentHelper.getEnchantmentsForCrafting(result);
        int applied = 0;
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : after.entrySet()) {
            if (before.getLevel(entry.getKey()) != entry.getIntValue()) {
                applied++;
            }
        }
        this.cost.set(applied);
    }

    // Vanilla refuses to release a result whose cost is 0.
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void enchantmentRework$allowFree(Player player, boolean hasItem, CallbackInfoReturnable<Boolean> cir) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        if (this.cost.get() == 0 && !self.getSlot(2).getItem().isEmpty()) {
            cir.setReturnValue(true);
        }
    }
}
