/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.item.inventory.util.ContainerUtil;

import java.util.Random;

@Mixin(InventoryHelper.class)
public class MixinInventoryHelper {

    @Shadow @Final private static Random RANDOM;

    private static final String
            DROP_INVENTORY_ITEMS_BLOCK_POS =
            "dropInventoryItems(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/inventory/IInventory;)V";
    private static final String
            DROP_INVENTORY_ITEMS_X_Y_Z =
            "Lnet/minecraft/inventory/InventoryHelper;dropInventoryItems(Lnet/minecraft/world/World;DDDLnet/minecraft/inventory/IInventory;)V";

    @Redirect(method = DROP_INVENTORY_ITEMS_BLOCK_POS, at = @At(value = "INVOKE", target = DROP_INVENTORY_ITEMS_X_Y_Z))
    private static void spongeDropInventoryItems(World world, double x, double y, double z, IInventory inventory) {
        if (world instanceof WorldServer) {
            ContainerUtil.performBlockInventoryDrops((WorldServer) world, x, y, z, inventory);
        } else {
            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = inventory.getStackInSlot(i);

                if (itemstack != null) {
                    InventoryHelper.spawnItemStack(world, x, y, z, itemstack);
                }
            }
        }
    }

}
