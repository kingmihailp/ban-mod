package com.banmod.screen;

import com.banmod.data.BanScreenData;
import com.banmod.network.NetworkHandler;
import com.banmod.network.packet.UpdateConfigPacket;
import com.banmod.util.ColorUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * In-game configuration GUI opened via {@code /banscreen gui}.
 *
 * <p>The GUI is split into tabbed pages so every field fits comfortably.
 * Tabs: Background | Title | Reason | Expiry | Divider | Footer | Appeal | Button
 *
 * <p>Changes are sent to the server when the admin clicks "Save".
 */
@OnlyIn(Dist.CLIENT)
public class BanScreenConfigGui extends Screen {

    // ── Working copy edited by the GUI ───────────────────────────────────────
    private final BanScreenData data;
    private int currentTab = 0;

    // Keep references to active widgets so we can read their values on save
    private final List<EditBox> activeFields = new ArrayList<>();
    private final List<FieldDef> fieldDefs   = new ArrayList<>();

    // ── Tab labels ───────────────────────────────────────────────────────────
    private static final String[] TABS = {
            "Background", "Title", "Reason", "Expiry",
            "Divider", "Footer", "Appeal", "Button"
    };

    public BanScreenConfigGui(BanScreenData data) {
        super(Component.literal("BanMod Configuration"));
        this.data = data;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Init
    // ────────────────────────────────────────────────────────────────────────

    @Override
    protected void init() {
        activeFields.clear();
        fieldDefs.clear();

        // Tab buttons (top row)
        int tabW = Math.max(60, (width - 20) / TABS.length);
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            addRenderableWidget(
                    Button.builder(Component.literal(TABS[i]), btn -> switchTab(idx))
                            .bounds(10 + i * tabW, 22, tabW - 2, 16)
                            .build()
            );
        }

        // Content area – build fields for the current tab
        buildTabContent();

        // Bottom buttons
        int bw = 100;
        addRenderableWidget(
                Button.builder(Component.literal("Save & Close"), btn -> saveAndClose())
                        .bounds(width / 2 - bw - 4, height - 28, bw, 20)
                        .build()
        );
        addRenderableWidget(
                Button.builder(Component.literal("Cancel"), btn -> onClose())
                        .bounds(width / 2 + 4, height - 28, bw, 20)
                        .build()
        );
    }

    private void switchTab(int idx) {
        // Flush current fields to data before switching
        flushFields();
        currentTab = idx;
        clearWidgets();
        init();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Tab content builders
    // ────────────────────────────────────────────────────────────────────────

    private void buildTabContent() {
        switch (currentTab) {
            case 0 -> buildBackgroundTab();
            case 1 -> buildTitleTab();
            case 2 -> buildReasonTab();
            case 3 -> buildExpiryTab();
            case 4 -> buildDividerTab();
            case 5 -> buildFooterTab();
            case 6 -> buildAppealTab();
            case 7 -> buildButtonTab();
        }
    }

    private void buildBackgroundTab() {
        int y = 46;
        addColorField("Top color (#RRGGBB):",       data.bgColor,       "bgColor",       y); y += 24;
        addColorField("Bottom color (#RRGGBB):",    data.bgColorBottom, "bgColorBottom", y); y += 24;
        addFloatField("Opacity (0.0–1.0):",         String.valueOf(data.bgOpacity),   "bgOpacity", y); y += 24;
        addCheckbox("Gradient background",          data.bgGradient, "bgGradient", y);
    }

    private void buildTitleTab() {
        int y = 46;
        addTextField("Title text:",    data.titleText,   "titleText",   y); y += 24;
        addColorField("Color (#RRGGBB):", data.titleColor, "titleColor", y); y += 24;
        addFloatField("X position (0–1):", String.valueOf(data.titleX), "titleX", y); y += 24;
        addFloatField("Y position (0–1):", String.valueOf(data.titleY), "titleY", y); y += 24;
        addIntField("Scale (1–4):",        String.valueOf(data.titleScale), "titleScale", y); y += 24;
        addCheckbox("Bold",   data.titleBold,   "titleBold",    y); y += 20;
        addCheckbox("Italic", data.titleItalic, "titleItalic",  y); y += 20;
        addCheckbox("Shadow", data.titleShadow, "titleShadow",  y); y += 20;
        addCheckbox("Visible",data.titleVisible,"titleVisible", y);
    }

    private void buildReasonTab() {
        int y = 46;
        addTextField("Prefix:",        data.reasonPrefix,  "reasonPrefix", y); y += 24;
        addColorField("Color (#RRGGBB):", data.reasonColor,"reasonColor", y); y += 24;
        addFloatField("X position (0–1):", String.valueOf(data.reasonX), "reasonX", y); y += 24;
        addFloatField("Y position (0–1):", String.valueOf(data.reasonY), "reasonY", y); y += 24;
        addCheckbox("Bold",    data.reasonBold,    "reasonBold",    y); y += 20;
        addCheckbox("Italic",  data.reasonItalic,  "reasonItalic",  y); y += 20;
        addCheckbox("Shadow",  data.reasonShadow,  "reasonShadow",  y); y += 20;
        addCheckbox("Visible", data.reasonVisible, "reasonVisible", y);
    }

    private void buildExpiryTab() {
        int y = 46;
        addTextField("Expiry prefix:",    data.expiryPrefix,  "expiryPrefix", y); y += 24;
        addTextField("Permanent text:",   data.neverBanText,  "neverBanText", y); y += 24;
        addColorField("Color (#RRGGBB):", data.expiryColor,   "expiryColor",  y); y += 24;
        addFloatField("X position (0–1):", String.valueOf(data.expiryX), "expiryX", y); y += 24;
        addFloatField("Y position (0–1):", String.valueOf(data.expiryY), "expiryY", y); y += 24;
        addCheckbox("Shadow",  data.expiryShadow,  "expiryShadow",  y); y += 20;
        addCheckbox("Visible", data.expiryVisible, "expiryVisible", y);
    }

    private void buildDividerTab() {
        int y = 46;
        addColorField("Color (#RRGGBB):",    data.dividerColor,    "dividerColor",    y); y += 24;
        addFloatField("Y position (0–1):",   String.valueOf(data.dividerY), "dividerY", y); y += 24;
        addIntField("Thickness (px):",       String.valueOf(data.dividerThickness), "dividerThickness", y); y += 24;
        addIntField("Width % of screen:",    String.valueOf(data.dividerWidthPct),  "dividerWidthPct",  y); y += 24;
        addCheckbox("Visible", data.dividerVisible, "dividerVisible", y);
    }

    private void buildFooterTab() {
        int y = 46;
        addTextField("Footer text:",       data.footerText,   "footerText",   y); y += 24;
        addColorField("Color (#RRGGBB):",  data.footerColor,  "footerColor",  y); y += 24;
        addFloatField("X position (0–1):", String.valueOf(data.footerX), "footerX", y); y += 24;
        addFloatField("Y position (0–1):", String.valueOf(data.footerY), "footerY", y); y += 24;
        addCheckbox("Shadow",  data.footerShadow,  "footerShadow",  y); y += 20;
        addCheckbox("Visible", data.footerVisible, "footerVisible", y);
    }

    private void buildAppealTab() {
        int y = 46;
        addTextField("Appeal text:",       data.appealText,   "appealText",   y); y += 24;
        addColorField("Color (#RRGGBB):",  data.appealColor,  "appealColor",  y); y += 24;
        addFloatField("X position (0–1):", String.valueOf(data.appealX), "appealX", y); y += 24;
        addFloatField("Y position (0–1):", String.valueOf(data.appealY), "appealY", y); y += 24;
        addCheckbox("Shadow",  data.appealShadow,  "appealShadow",  y); y += 20;
        addCheckbox("Visible", data.appealVisible, "appealVisible", y);
    }

    private void buildButtonTab() {
        int y = 46;
        addTextField("Button text:",          data.buttonText,      "buttonText",      y); y += 24;
        addColorField("BG color (#RRGGBB):",  data.buttonColor,     "buttonColor",     y); y += 24;
        addColorField("Text color (#RRGGBB):",data.buttonTextColor, "buttonTextColor", y); y += 24;
        addFloatField("X position (0–1):", String.valueOf(data.buttonX), "buttonX", y); y += 24;
        addFloatField("Y position (0–1):", String.valueOf(data.buttonY), "buttonY", y);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Widget factory helpers
    // ────────────────────────────────────────────────────────────────────────

    private static final int LBL_W = 160;
    private static final int FLD_W = 120;
    private static final int FLD_H = 16;

    private void addTextField(String label, String value, String fieldKey, int y) {
        int x = (width - LBL_W - FLD_W - 8) / 2;
        EditBox box = new EditBox(font, x + LBL_W + 8, y, FLD_W, FLD_H, Component.literal(label));
        box.setValue(value);
        box.setMaxLength(256);
        addRenderableWidget(box);
        activeFields.add(box);
        fieldDefs.add(new FieldDef(label, fieldKey, FieldType.TEXT, box));
    }

    private void addColorField(String label, String value, String fieldKey, int y) {
        int x = (width - LBL_W - FLD_W - 8) / 2;
        EditBox box = new EditBox(font, x + LBL_W + 8, y, FLD_W, FLD_H, Component.literal(label));
        box.setValue(value);
        box.setMaxLength(7);
        // Validate on each keystroke
        box.setResponder(text -> box.setTextColor(
                ColorUtils.isValid(text) ? 0xFFFFFFFF : 0xFFFF4444
        ));
        addRenderableWidget(box);
        activeFields.add(box);
        fieldDefs.add(new FieldDef(label, fieldKey, FieldType.COLOR, box));
    }

    private void addFloatField(String label, String value, String fieldKey, int y) {
        int x = (width - LBL_W - FLD_W - 8) / 2;
        EditBox box = new EditBox(font, x + LBL_W + 8, y, FLD_W, FLD_H, Component.literal(label));
        box.setValue(value);
        box.setMaxLength(10);
        addRenderableWidget(box);
        activeFields.add(box);
        fieldDefs.add(new FieldDef(label, fieldKey, FieldType.FLOAT, box));
    }

    private void addIntField(String label, String value, String fieldKey, int y) {
        int x = (width - LBL_W - FLD_W - 8) / 2;
        EditBox box = new EditBox(font, x + LBL_W + 8, y, FLD_W, FLD_H, Component.literal(label));
        box.setValue(value);
        box.setMaxLength(5);
        addRenderableWidget(box);
        activeFields.add(box);
        fieldDefs.add(new FieldDef(label, fieldKey, FieldType.INT, box));
    }

    private void addCheckbox(String label, boolean checked, String fieldKey, int y) {
        int x = (width - LBL_W - FLD_W - 8) / 2;
        Checkbox cb = new Checkbox(x + LBL_W + 8, y, FLD_H, FLD_H,
                Component.literal(label), checked);
        addRenderableWidget(cb);
        fieldDefs.add(new FieldDef(label, fieldKey, FieldType.BOOL, null, cb));
    }

    // ────────────────────────────────────────────────────────────────────────
    // Rendering
    // ────────────────────────────────────────────────────────────────────────

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        renderBackground(gfx);

        // Panel background
        gfx.fill(5, 18, width - 5, height - 32, 0xAA000000);

        // Tab highlight
        int tabW = Math.max(60, (width - 20) / TABS.length);
        int tx = 10 + currentTab * tabW;
        gfx.fill(tx, 22, tx + tabW - 2, 38, 0x88FFFFFF);

        // Title
        gfx.drawCenteredString(font, "BanMod Config — " + TABS[currentTab], width / 2, 8, 0xFFFFFFFF);

        // Labels for fields
        int labelX = (width - LBL_W - FLD_W - 8) / 2;
        for (FieldDef fd : fieldDefs) {
            if (fd.box != null) {
                gfx.drawString(font, fd.label, labelX, fd.box.getY() + 3, 0xFFCCCCCC, false);
            }
        }

        super.render(gfx, mouseX, mouseY, delta);

        // Colour preview swatches next to colour fields
        for (FieldDef fd : fieldDefs) {
            if (fd.type == FieldType.COLOR && fd.box != null) {
                String hex = fd.box.getValue();
                if (ColorUtils.isValid(hex)) {
                    int col = ColorUtils.parseHex(hex, 0xFF888888);
                    int sx = fd.box.getX() + FLD_W + 4;
                    int sy = fd.box.getY();
                    gfx.fill(sx, sy, sx + FLD_H, sy + FLD_H, col);
                    // thin border
                    gfx.fill(sx - 1, sy - 1, sx + FLD_H + 1, sy + FLD_H + 1, 0xFFAAAAAA);
                    gfx.fill(sx, sy, sx + FLD_H, sy + FLD_H, col);
                }
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Save logic
    // ────────────────────────────────────────────────────────────────────────

    private void flushFields() {
        for (FieldDef fd : fieldDefs) {
            try {
                applyField(fd);
            } catch (Exception ignored) {}
        }
    }

    private void applyField(FieldDef fd) {
        switch (fd.fieldKey) {
            // Background
            case "bgColor"         -> data.bgColor         = colorOf(fd);
            case "bgColorBottom"   -> data.bgColorBottom   = colorOf(fd);
            case "bgOpacity"       -> data.bgOpacity       = floatOf(fd, 0f, 1f, data.bgOpacity);
            case "bgGradient"      -> data.bgGradient      = boolOf(fd);
            // Title
            case "titleText"       -> data.titleText       = textOf(fd);
            case "titleColor"      -> data.titleColor      = colorOf(fd);
            case "titleX"          -> data.titleX          = floatOf(fd, 0f, 1f, data.titleX);
            case "titleY"          -> data.titleY          = floatOf(fd, 0f, 1f, data.titleY);
            case "titleScale"      -> data.titleScale      = intOf(fd, 1, 4, data.titleScale);
            case "titleBold"       -> data.titleBold       = boolOf(fd);
            case "titleItalic"     -> data.titleItalic     = boolOf(fd);
            case "titleShadow"     -> data.titleShadow     = boolOf(fd);
            case "titleVisible"    -> data.titleVisible    = boolOf(fd);
            // Reason
            case "reasonPrefix"    -> data.reasonPrefix    = textOf(fd);
            case "reasonColor"     -> data.reasonColor     = colorOf(fd);
            case "reasonX"         -> data.reasonX         = floatOf(fd, 0f, 1f, data.reasonX);
            case "reasonY"         -> data.reasonY         = floatOf(fd, 0f, 1f, data.reasonY);
            case "reasonBold"      -> data.reasonBold      = boolOf(fd);
            case "reasonItalic"    -> data.reasonItalic    = boolOf(fd);
            case "reasonShadow"    -> data.reasonShadow    = boolOf(fd);
            case "reasonVisible"   -> data.reasonVisible   = boolOf(fd);
            // Expiry
            case "expiryPrefix"    -> data.expiryPrefix    = textOf(fd);
            case "neverBanText"    -> data.neverBanText    = textOf(fd);
            case "expiryColor"     -> data.expiryColor     = colorOf(fd);
            case "expiryX"         -> data.expiryX         = floatOf(fd, 0f, 1f, data.expiryX);
            case "expiryY"         -> data.expiryY         = floatOf(fd, 0f, 1f, data.expiryY);
            case "expiryShadow"    -> data.expiryShadow    = boolOf(fd);
            case "expiryVisible"   -> data.expiryVisible   = boolOf(fd);
            // Divider
            case "dividerColor"    -> data.dividerColor    = colorOf(fd);
            case "dividerY"        -> data.dividerY        = floatOf(fd, 0f, 1f, data.dividerY);
            case "dividerThickness"-> data.dividerThickness= intOf(fd, 1, 5, data.dividerThickness);
            case "dividerWidthPct" -> data.dividerWidthPct = intOf(fd, 1, 100, data.dividerWidthPct);
            case "dividerVisible"  -> data.dividerVisible  = boolOf(fd);
            // Footer
            case "footerText"      -> data.footerText      = textOf(fd);
            case "footerColor"     -> data.footerColor     = colorOf(fd);
            case "footerX"         -> data.footerX         = floatOf(fd, 0f, 1f, data.footerX);
            case "footerY"         -> data.footerY         = floatOf(fd, 0f, 1f, data.footerY);
            case "footerShadow"    -> data.footerShadow    = boolOf(fd);
            case "footerVisible"   -> data.footerVisible   = boolOf(fd);
            // Appeal
            case "appealText"      -> data.appealText      = textOf(fd);
            case "appealColor"     -> data.appealColor     = colorOf(fd);
            case "appealX"         -> data.appealX         = floatOf(fd, 0f, 1f, data.appealX);
            case "appealY"         -> data.appealY         = floatOf(fd, 0f, 1f, data.appealY);
            case "appealShadow"    -> data.appealShadow    = boolOf(fd);
            case "appealVisible"   -> data.appealVisible   = boolOf(fd);
            // Button
            case "buttonText"      -> data.buttonText      = textOf(fd);
            case "buttonColor"     -> data.buttonColor     = colorOf(fd);
            case "buttonTextColor" -> data.buttonTextColor = colorOf(fd);
            case "buttonX"         -> data.buttonX         = floatOf(fd, 0f, 1f, data.buttonX);
            case "buttonY"         -> data.buttonY         = floatOf(fd, 0f, 1f, data.buttonY);
        }
    }

    private void saveAndClose() {
        flushFields();
        NetworkHandler.CHANNEL.sendToServer(new UpdateConfigPacket(data));
        onClose();
    }

    // ── Field read helpers ───────────────────────────────────────────────────

    private String textOf(FieldDef fd)  { return fd.box != null ? fd.box.getValue() : ""; }
    private String colorOf(FieldDef fd) {
        String v = fd.box != null ? fd.box.getValue() : "#FFFFFF";
        return ColorUtils.isValid(v) ? (v.startsWith("#") ? v : "#" + v) : "#FFFFFF";
    }
    private boolean boolOf(FieldDef fd) { return fd.cb != null && fd.cb.selected(); }
    private float floatOf(FieldDef fd, float min, float max, float def) {
        try {
            float v = Float.parseFloat(fd.box.getValue());
            return Math.max(min, Math.min(max, v));
        } catch (NumberFormatException e) { return def; }
    }
    private int intOf(FieldDef fd, int min, int max, int def) {
        try {
            int v = Integer.parseInt(fd.box.getValue());
            return Math.max(min, Math.min(max, v));
        } catch (NumberFormatException e) { return def; }
    }

    // ── Inner types ──────────────────────────────────────────────────────────

    private enum FieldType { TEXT, COLOR, FLOAT, INT, BOOL }

    private static class FieldDef {
        final String    label;
        final String    fieldKey;
        final FieldType type;
        final EditBox   box;
        final Checkbox  cb;

        FieldDef(String label, String key, FieldType type, EditBox box) {
            this(label, key, type, box, null);
        }
        FieldDef(String label, String key, FieldType type, EditBox box, Checkbox cb) {
            this.label    = label;
            this.fieldKey = key;
            this.type     = type;
            this.box      = box;
            this.cb       = cb;
        }
    }
}
