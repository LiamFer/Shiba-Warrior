package com.liamfer.shibawarrior.client.gui.screens.inventory;

import com.liamfer.shibawarrior.inventory.BarneyMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Tela do inventário do Barney — 176×166 (vanilla standard).
 *
 * Todos os backgrounds de slot são desenhados com fill() usando as MESMAS
 * coordenadas exportadas pelo BarneyMenu, garantindo alinhamento perfeito.
 */
public class BarneyScreen extends AbstractContainerScreen<BarneyMenu> {
    private float xMouse, yMouse;

    public BarneyScreen(BarneyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // Esconder todos os textos padrão
        this.titleLabelX = 9999;
        this.inventoryLabelX = 9999;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);
        renderTooltip(g, mx, my);
        xMouse = mx;
        yMouse = my;
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        int x = leftPos;
        int y = topPos;

        // ── Fundo cinza do painel ────────────────────────────────────────────
        // Painel principal
        drawPanel(g, x, y, 176, 166);

        // ── Armaduras (slots 0-3) ────────────────────────────────────────────
        for (int i = 0; i < 4; i++)
            drawSlotBg(g, x + BarneyMenu.ARMOR_X[i], y + BarneyMenu.ARMOR_Y[i]);

        // ── Espada (slot 4) ──────────────────────────────────────────────────
        drawSlotBg(g, x + BarneyMenu.SWORD_X, y + BarneyMenu.SWORD_Y);

        // ── Escudo (slot 5) ──────────────────────────────────────────────────
        drawSlotBg(g, x + BarneyMenu.SHIELD_X, y + BarneyMenu.SHIELD_Y);

        // ── Mochila 3×3 (slots 6-14) ────────────────────────────────────────
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                drawSlotBg(g, x + BarneyMenu.BAG_X0 + c * 18, y + BarneyMenu.BAG_Y0 + r * 18);

        // ── Inventário do player (slots 15-41) ──────────────────────────────
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                drawSlotBg(g, x + BarneyMenu.PINV_X + c * 18, y + BarneyMenu.PINV_Y + r * 18);

        // ── Hotbar (slots 42-50) ─────────────────────────────────────────────
        for (int c = 0; c < 9; c++)
            drawSlotBg(g, x + BarneyMenu.PINV_X + c * 18, y + BarneyMenu.PHOT_Y);

        // ── Separadores ──────────────────────────────────────────────────────
        // Linha horizontal acima do inventário do player
        g.fill(x + 7, y + 80, x + 169, y + 81, 0xFF888888);
        // Linha vertical separando equipamento / modelo / mochila
        g.fill(x + 96, y + 6, x + 97, y + 72, 0xFF888888);

        // ── Ícones de rótulo nos slots de arma ───────────────────────────────
        // Só desenha se o slot estiver vazio
        if (!menu.barneyInventory.getItem(4).isEmpty()) {
            // Slot de espada ocupado — não desenhar ícone
        } else {
            g.drawString(font, "⚔", x + BarneyMenu.SWORD_X + 3, y + BarneyMenu.SWORD_Y + 3, 0xFF555555, false);
        }
        if (menu.barneyInventory.getItem(5).isEmpty()) {
            g.drawString(font, "🛡", x + BarneyMenu.SHIELD_X + 3, y + BarneyMenu.SHIELD_Y + 3, 0xFF555555, false);
        }

        // ── Barney 3D ────────────────────────────────────────────────────────
        if (menu.barney != null) {
            int ex = x + 51;
            int ey = y + 75;
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    g, ex, ey, 25,
                    (float) ex - xMouse,
                    (float) (ey - 50) - yMouse,
                    menu.barney
            );
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Desenha um fundo de slot 16×16 com bordas estilo vanilla. */
    private static void drawSlotBg(GuiGraphics g, int sx, int sy) {
        // Borda superior e esquerda (escura)
        g.fill(sx - 1, sy - 1, sx + 17, sy,     0xFF373737);
        g.fill(sx - 1, sy,     sx,      sy + 16, 0xFF373737);
        // Borda inferior e direita (clara)
        g.fill(sx,     sy + 16, sx + 17, sy + 17, 0xFFFFFFFF);
        g.fill(sx + 16, sy,     sx + 17, sy + 17, 0xFFFFFFFF);
        // Interior do slot
        g.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B);
    }

    /** Desenha o painel principal com borda 3D estilo Minecraft. */
    private static void drawPanel(GuiGraphics g, int px, int py, int pw, int ph) {
        // Preenchimento
        g.fill(px, py, px + pw, py + ph, 0xFFC6C6C6);
        // Borda clara (topo e esquerda — efeito 3D raised)
        g.fill(px,     py,     px + pw, py + 2,  0xFFFFFFFF);
        g.fill(px,     py,     px + 2,  py + ph, 0xFFFFFFFF);
        // Borda escura (base e direita)
        g.fill(px,      py + ph - 2, px + pw,     py + ph, 0xFF555555);
        g.fill(px + pw - 2, py,      px + pw,     py + ph, 0xFF555555);
    }
}
