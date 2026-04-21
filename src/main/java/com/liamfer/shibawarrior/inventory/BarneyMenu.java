package com.liamfer.shibawarrior.inventory;

import com.liamfer.shibawarrior.ShibaWarriorMod;
import com.liamfer.shibawarrior.entity.BarneyEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;

/**
 * Inventário do Barney — 176×166 (tamanho padrão do Minecraft).
 *
 * Layout de slots (coordenadas relativas ao painel):
 *
 *   ARMADURA (esquerda):
 *     slot 0 Capacete  (x=8,   y=8)
 *     slot 1 Peitoral  (x=8,   y=26)
 *     slot 2 Calça     (x=8,   y=44)
 *     slot 3 Bota      (x=8,   y=62)
 *
 *   ARMAS (direita do modelo do Barney):
 *     slot 4 Espada    (x=78,  y=26)
 *     slot 5 Escudo    (x=78,  y=44)
 *
 *   MOCHILA 3×3 (canto direito):
 *     slots 6-14       (x=116, y=8) → (x=152, y=44)
 *
 *   INVENTÁRIO DO PLAYER (padrão vanilla):
 *     slots 15-41      (x=8, y=84)
 *     slots 42-50      (x=8, y=142)  hotbar
 */
public class BarneyMenu extends AbstractContainerMenu {
    public final Container barneyInventory;
    public final BarneyEntity barney;

    // ── Coordenadas exactas de cada grupo de slots ──────────────────────────
    // Estas constantes são usadas tanto aqui como no BarneyScreen para desenhar
    // o background de cada slot na mesma posição.

    // Armaduras
    public static final int[] ARMOR_X = {8, 8, 8, 8};
    public static final int[] ARMOR_Y = {8, 26, 44, 62};

    // Armas
    public static final int SWORD_X = 78;
    public static final int SWORD_Y = 26;
    public static final int SHIELD_X = 78;
    public static final int SHIELD_Y = 44;

    // Mochila 3×3
    public static final int BAG_X0 = 116;
    public static final int BAG_Y0 = 8;

    // Inventário do player
    public static final int PINV_X = 8;
    public static final int PINV_Y = 84;
    public static final int PHOT_Y = 142;

    // ── Construtores ─────────────────────────────────────────────────────────

    /** Construtor do CLIENTE (Fabric envia o buf pelo writeScreenOpeningData) */
    public BarneyMenu(int syncId, Inventory playerInv, FriendlyByteBuf buf) {
        this(syncId, playerInv, new SimpleContainer(15),
                (BarneyEntity) playerInv.player.level().getEntity(buf.readInt()));
    }

    /** Construtor do SERVIDOR */
    public BarneyMenu(int syncId, Inventory playerInv, Container inv, BarneyEntity barney) {
        super(ShibaWarriorMod.BARNEY_MENU, syncId);
        this.barneyInventory = inv;
        this.barney = barney;
        inv.startOpen(playerInv.player);

        // Armaduras (slots 0-3) — só aceitam a peça correta
        EquipmentSlot[] order = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < 4; i++) {
            final EquipmentSlot es = order[i];
            addSlot(new Slot(inv, i, ARMOR_X[i], ARMOR_Y[i]) {
                @Override public boolean mayPlace(ItemStack s) {
                    return s.getItem() instanceof ArmorItem a && a.getEquipmentSlot() == es;
                }
                @Override public int getMaxStackSize() { return 1; }
            });
        }

        // Espada (slot 4)
        addSlot(new Slot(inv, 4, SWORD_X, SWORD_Y) {
            @Override public boolean mayPlace(ItemStack s) {
                Item it = s.getItem();
                return it instanceof SwordItem || it instanceof AxeItem
                        || it instanceof TridentItem || it instanceof BowItem
                        || it instanceof CrossbowItem;
            }
            @Override public int getMaxStackSize() { return 1; }
        });

        // Escudo (slot 5)
        addSlot(new Slot(inv, 5, SHIELD_X, SHIELD_Y) {
            @Override public boolean mayPlace(ItemStack s) { return s.is(Items.SHIELD); }
            @Override public int getMaxStackSize() { return 1; }
        });

        // Mochila 3×3 (slots 6-14)
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                addSlot(new Slot(inv, 6 + c + r * 3, BAG_X0 + c * 18, BAG_Y0 + r * 18));

        // Inventário do player (slots 15-41)
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlot(new Slot(playerInv, 9 + c + r * 9, PINV_X + c * 18, PINV_Y + r * 18));

        // Hotbar (slots 42-50)
        for (int c = 0; c < 9; c++)
            addSlot(new Slot(playerInv, c, PINV_X + c * 18, PHOT_Y));
    }

    @Override
    public boolean stillValid(Player p) {
        return barneyInventory.stillValid(p) && barney != null && barney.isAlive() && barney.distanceTo(p) < 8F;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int idx) {
        Slot slot = slots.get(idx);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (idx < 15) {
            if (!moveItemStackTo(stack, 15, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!moveItemStackTo(stack, 0, 15, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    @Override
    public void removed(Player p) { super.removed(p); barneyInventory.stopOpen(p); }
}
