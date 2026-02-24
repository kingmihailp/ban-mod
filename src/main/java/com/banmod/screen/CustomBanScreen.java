package com.banmod.screen;

import com.banmod.data.BanScreenData;
import com.banmod.util.ColorUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * Custom ban screen rendered client-side when the server sends a
 * {@link com.banmod.network.packet.BanScreenConfigPacket} before disconnecting.
 *
 * <p>All visual parameters come from {@link BanScreenData}, which is populated
 * from the server's {@code banmod-server.toml}.
 */
@OnlyIn(Dist.CLIENT)
public class CustomBanScreen extends Screen {

    private final Component disconnectReason;
    private final Component banReason;
    private final Component expiryText;
    private final Component playerName;
    private final BanScreenData cfg;
    private final boolean isPreview;

    public CustomBanScreen(Component disconnectReason,
                           Component banReason,
                           Component expiryText,
                           Component playerName,
                           BanScreenData cfg,
                           boolean isPreview) {
        super(Component.literal("Ban Screen"));
        this.disconnectReason = disconnectReason;
        this.banReason        = banReason;
        this.expiryText       = expiryText;
        this.playerName       = playerName;
        this.cfg              = cfg;
        this.isPreview        = isPreview;
    }

    // ── Screen lifecycle ─────────────────────────────────────────────────────

    @Override
    protected void init() {
        // "Return to Title" / "Close Preview" button
        int bw = 200;
        int bh = 20;
        int bx = Math.round(cfg.buttonX * width) - bw / 2;
        int by = Math.round(cfg.buttonY * height) - bh / 2;

        String btnLabel = isPreview ? "Close Preview" : cfg.buttonText;

        addRenderableWidget(
                Button.builder(Component.literal(btnLabel), btn -> {
                    if (isPreview) {
                        // Return to previous screen
                        minecraft.setScreen(null);
                    } else {
                        minecraft.setScreen(null);
                        minecraft.clearLevel();
                    }
                })
                .bounds(bx, by, bw, bh)
                .build()
        );
    }

    // ── Rendering ────────────────────────────────────────────────────────────

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, delta);   // draws the button
    }

    @Override
    public void renderBackground(GuiGraphics gfx) {
        renderBanBackground(gfx);
        renderBanContent(gfx);
    }

    // ── Background ───────────────────────────────────────────────────────────

    private void renderBanBackground(GuiGraphics gfx) {
        int topColor    = ColorUtils.parseHexWithOpacity(cfg.bgColor,       cfg.bgOpacity, 0xCC0D0D1A);
        int bottomColor = ColorUtils.parseHexWithOpacity(cfg.bgColorBottom, cfg.bgOpacity, 0xCC1A0D0D);

        if (cfg.bgGradient) {
            gfx.fillGradient(0, 0, width, height, topColor, bottomColor);
        } else {
            gfx.fill(0, 0, width, height, topColor);
        }
    }

    // ── Content ──────────────────────────────────────────────────────────────

    private void renderBanContent(GuiGraphics gfx) {
        // Title
        if (cfg.titleVisible) {
            renderScaledText(gfx, cfg.titleText,
                    cfg.titleColor, cfg.titleX, cfg.titleY,
                    cfg.titleScale, cfg.titleBold, cfg.titleItalic, cfg.titleShadow);
        }

        // Divider
        if (cfg.dividerVisible) {
            renderDivider(gfx);
        }

        // Reason
        if (cfg.reasonVisible) {
            String reasonStr = cfg.reasonPrefix + banReason.getString();
            renderText(gfx, reasonStr, cfg.reasonColor,
                    cfg.reasonX, cfg.reasonY, cfg.reasonBold, cfg.reasonItalic, cfg.reasonShadow);
        }

        // Expiry
        if (cfg.expiryVisible) {
            String expStr = cfg.expiryPrefix + expiryText.getString();
            renderText(gfx, expStr, cfg.expiryColor,
                    cfg.expiryX, cfg.expiryY, false, false, cfg.expiryShadow);
        }

        // Player name
        if (cfg.playerVisible) {
            String pStr = cfg.playerPrefix + playerName.getString();
            renderText(gfx, pStr, cfg.playerColor,
                    cfg.playerX, cfg.playerY, false, false, false);
        }

        // Footer
        if (cfg.footerVisible && !cfg.footerText.isEmpty()) {
            renderText(gfx, cfg.footerText, cfg.footerColor,
                    cfg.footerX, cfg.footerY, false, false, cfg.footerShadow);
        }

        // Appeal
        if (cfg.appealVisible && !cfg.appealText.isEmpty()) {
            renderText(gfx, cfg.appealText, cfg.appealColor,
                    cfg.appealX, cfg.appealY, false, false, cfg.appealShadow);
        }

        // Preview badge
        if (isPreview) {
            gfx.drawString(font, "§e[PREVIEW]", 4, 4, 0xFFFFFF00, false);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Renders text at a relative position (0.0–1.0) centred on the X coordinate.
     */
    private void renderText(GuiGraphics gfx, String text,
                             String hexColor, float relX, float relY,
                             boolean bold, boolean italic, boolean shadow) {
        int color = ColorUtils.parseHex(hexColor, 0xFFFFFFFF);
        int x = Math.round(relX * width);
        int y = Math.round(relY * height);

        Component comp = buildComponent(text, bold, italic);
        int textW = font.width(comp);
        int drawX = x - textW / 2;

        if (shadow) {
            gfx.drawString(font, comp, drawX, y, color, true);
        } else {
            gfx.drawString(font, comp, drawX, y, color, false);
        }
    }

    /**
     * Renders text with a pixel-level scale multiplier (uses PoseStack scaling).
     */
    private void renderScaledText(GuiGraphics gfx, String text,
                                   String hexColor, float relX, float relY,
                                   int scale, boolean bold, boolean italic, boolean shadow) {
        if (scale <= 1) {
            renderText(gfx, text, hexColor, relX, relY, bold, italic, shadow);
            return;
        }

        int color = ColorUtils.parseHex(hexColor, 0xFFFFFFFF);
        Component comp = buildComponent(text, bold, italic);

        float sx = relX * width;
        float sy = relY * height;

        int textW = font.width(comp) * scale;
        float drawX = sx - textW / 2f;
        float drawY = sy - (font.lineHeight * scale) / 2f;

        gfx.pose().pushPose();
        gfx.pose().translate(drawX, drawY, 0);
        gfx.pose().scale(scale, scale, 1f);

        if (shadow) {
            gfx.drawString(font, comp, 0, 0, color, true);
        } else {
            gfx.drawString(font, comp, 0, 0, color, false);
        }

        gfx.pose().popPose();
    }

    private void renderDivider(GuiGraphics gfx) {
        int color = ColorUtils.parseHex(cfg.dividerColor, 0xFF553333);
        int y = Math.round(cfg.dividerY * height);
        int lineW = Math.round(width * cfg.dividerWidthPct / 100f);
        int x0 = (width - lineW) / 2;
        int x1 = x0 + lineW;
        int half = Math.max(1, cfg.dividerThickness / 2);
        gfx.fill(x0, y - half, x1, y + half + (cfg.dividerThickness % 2), color);
    }

    /** Wraps plain text in a Component, applying bold/italic styles. */
    private Component buildComponent(String text, boolean bold, boolean italic) {
        net.minecraft.network.chat.MutableComponent comp = Component.literal(text);
        comp.withStyle(style -> style.withBold(bold).withItalic(italic));
        return comp;
    }

    // ── Screen meta ──────────────────────────────────────────────────────────

    @Override
    public boolean shouldCloseOnEsc() {
        return isPreview;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
