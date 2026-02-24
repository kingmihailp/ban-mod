package com.banmod.config;

import com.banmod.data.BanScreenData;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Server-side TOML configuration (config/banmod-server.toml).
 * All values mirror the fields of {@link BanScreenData}.
 * Call {@link #toBanScreenData()} to get a ready-to-send data object.
 */
public class BanScreenConfig {

    public static final ForgeConfigSpec SPEC;

    // Background
    public static final ForgeConfigSpec.ConfigValue<String>  BG_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String>  BG_COLOR_BOTTOM;
    public static final ForgeConfigSpec.BooleanValue         BG_GRADIENT;
    public static final ForgeConfigSpec.DoubleValue          BG_OPACITY;

    // Title
    public static final ForgeConfigSpec.ConfigValue<String>  TITLE_TEXT;
    public static final ForgeConfigSpec.ConfigValue<String>  TITLE_COLOR;
    public static final ForgeConfigSpec.DoubleValue          TITLE_X;
    public static final ForgeConfigSpec.DoubleValue          TITLE_Y;
    public static final ForgeConfigSpec.BooleanValue         TITLE_BOLD;
    public static final ForgeConfigSpec.BooleanValue         TITLE_ITALIC;
    public static final ForgeConfigSpec.BooleanValue         TITLE_SHADOW;
    public static final ForgeConfigSpec.IntValue             TITLE_SCALE;
    public static final ForgeConfigSpec.BooleanValue         TITLE_VISIBLE;

    // Reason
    public static final ForgeConfigSpec.ConfigValue<String>  REASON_PREFIX;
    public static final ForgeConfigSpec.ConfigValue<String>  REASON_COLOR;
    public static final ForgeConfigSpec.DoubleValue          REASON_X;
    public static final ForgeConfigSpec.DoubleValue          REASON_Y;
    public static final ForgeConfigSpec.BooleanValue         REASON_BOLD;
    public static final ForgeConfigSpec.BooleanValue         REASON_ITALIC;
    public static final ForgeConfigSpec.BooleanValue         REASON_SHADOW;
    public static final ForgeConfigSpec.BooleanValue         REASON_VISIBLE;

    // Expiry
    public static final ForgeConfigSpec.BooleanValue         EXPIRY_VISIBLE;
    public static final ForgeConfigSpec.ConfigValue<String>  EXPIRY_PREFIX;
    public static final ForgeConfigSpec.ConfigValue<String>  NEVER_BAN_TEXT;
    public static final ForgeConfigSpec.ConfigValue<String>  EXPIRY_COLOR;
    public static final ForgeConfigSpec.DoubleValue          EXPIRY_X;
    public static final ForgeConfigSpec.DoubleValue          EXPIRY_Y;
    public static final ForgeConfigSpec.BooleanValue         EXPIRY_SHADOW;

    // Player name
    public static final ForgeConfigSpec.BooleanValue         PLAYER_VISIBLE;
    public static final ForgeConfigSpec.ConfigValue<String>  PLAYER_PREFIX;
    public static final ForgeConfigSpec.ConfigValue<String>  PLAYER_COLOR;
    public static final ForgeConfigSpec.DoubleValue          PLAYER_X;
    public static final ForgeConfigSpec.DoubleValue          PLAYER_Y;

    // Divider
    public static final ForgeConfigSpec.BooleanValue         DIVIDER_VISIBLE;
    public static final ForgeConfigSpec.ConfigValue<String>  DIVIDER_COLOR;
    public static final ForgeConfigSpec.DoubleValue          DIVIDER_Y;
    public static final ForgeConfigSpec.IntValue             DIVIDER_THICKNESS;
    public static final ForgeConfigSpec.IntValue             DIVIDER_WIDTH_PCT;

    // Footer
    public static final ForgeConfigSpec.BooleanValue         FOOTER_VISIBLE;
    public static final ForgeConfigSpec.ConfigValue<String>  FOOTER_TEXT;
    public static final ForgeConfigSpec.ConfigValue<String>  FOOTER_COLOR;
    public static final ForgeConfigSpec.DoubleValue          FOOTER_X;
    public static final ForgeConfigSpec.DoubleValue          FOOTER_Y;
    public static final ForgeConfigSpec.BooleanValue         FOOTER_SHADOW;

    // Appeal
    public static final ForgeConfigSpec.BooleanValue         APPEAL_VISIBLE;
    public static final ForgeConfigSpec.ConfigValue<String>  APPEAL_TEXT;
    public static final ForgeConfigSpec.ConfigValue<String>  APPEAL_COLOR;
    public static final ForgeConfigSpec.DoubleValue          APPEAL_X;
    public static final ForgeConfigSpec.DoubleValue          APPEAL_Y;
    public static final ForgeConfigSpec.BooleanValue         APPEAL_SHADOW;

    // Button
    public static final ForgeConfigSpec.ConfigValue<String>  BUTTON_TEXT;
    public static final ForgeConfigSpec.ConfigValue<String>  BUTTON_COLOR;
    public static final ForgeConfigSpec.ConfigValue<String>  BUTTON_TEXT_COLOR;
    public static final ForgeConfigSpec.DoubleValue          BUTTON_X;
    public static final ForgeConfigSpec.DoubleValue          BUTTON_Y;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.comment("Background settings").push("background");
        BG_COLOR        = b.comment("Top background color (#RRGGBB)").define("color", "#0D0D1A");
        BG_COLOR_BOTTOM = b.comment("Bottom gradient color (#RRGGBB) — used when gradient=true").define("colorBottom", "#1A0D0D");
        BG_GRADIENT     = b.comment("Enable top→bottom gradient").define("gradient", true);
        BG_OPACITY      = b.comment("Background opacity (0.0–1.0)").defineInRange("opacity", 0.97, 0.0, 1.0);
        b.pop();

        b.comment("Title settings").push("title");
        TITLE_TEXT    = b.comment("Title text").define("text", "You have been banned");
        TITLE_COLOR   = b.comment("Title color (#RRGGBB)").define("color", "#FF4444");
        TITLE_X       = b.comment("X position (0.0=left, 0.5=center, 1.0=right)").defineInRange("x", 0.5, 0.0, 1.0);
        TITLE_Y       = b.comment("Y position (0.0=top, 1.0=bottom)").defineInRange("y", 0.18, 0.0, 1.0);
        TITLE_BOLD    = b.define("bold", true);
        TITLE_ITALIC  = b.define("italic", false);
        TITLE_SHADOW  = b.define("shadow", true);
        TITLE_SCALE   = b.comment("Font scale multiplier (1–4)").defineInRange("scale", 2, 1, 4);
        TITLE_VISIBLE = b.define("visible", true);
        b.pop();

        b.comment("Reason settings").push("reason");
        REASON_PREFIX  = b.comment("Prefix shown before the ban reason").define("prefix", "Reason: ");
        REASON_COLOR   = b.comment("Reason text color (#RRGGBB)").define("color", "#FFFFFF");
        REASON_X       = b.defineInRange("x", 0.5, 0.0, 1.0);
        REASON_Y       = b.defineInRange("y", 0.42, 0.0, 1.0);
        REASON_BOLD    = b.define("bold", false);
        REASON_ITALIC  = b.define("italic", false);
        REASON_SHADOW  = b.define("shadow", false);
        REASON_VISIBLE = b.define("visible", true);
        b.pop();

        b.comment("Ban expiry display").push("expiry");
        EXPIRY_VISIBLE = b.define("visible", true);
        EXPIRY_PREFIX  = b.comment("Prefix before expiry date").define("prefix", "Expires: ");
        NEVER_BAN_TEXT = b.comment("Text shown for permanent bans").define("neverText", "Permanent");
        EXPIRY_COLOR   = b.comment("Expiry text color (#RRGGBB)").define("color", "#FFB347");
        EXPIRY_X       = b.defineInRange("x", 0.5, 0.0, 1.0);
        EXPIRY_Y       = b.defineInRange("y", 0.52, 0.0, 1.0);
        EXPIRY_SHADOW  = b.define("shadow", false);
        b.pop();

        b.comment("Player name display").push("player");
        PLAYER_VISIBLE = b.define("visible", false);
        PLAYER_PREFIX  = b.define("prefix", "Player: ");
        PLAYER_COLOR   = b.comment("Player name color (#RRGGBB)").define("color", "#AAAAAA");
        PLAYER_X       = b.defineInRange("x", 0.5, 0.0, 1.0);
        PLAYER_Y       = b.defineInRange("y", 0.62, 0.0, 1.0);
        b.pop();

        b.comment("Decorative horizontal divider").push("divider");
        DIVIDER_VISIBLE   = b.define("visible", true);
        DIVIDER_COLOR     = b.comment("Divider color (#RRGGBB)").define("color", "#553333");
        DIVIDER_Y         = b.comment("Y position of the divider").defineInRange("y", 0.35, 0.0, 1.0);
        DIVIDER_THICKNESS = b.comment("Line thickness in pixels").defineInRange("thickness", 1, 1, 5);
        DIVIDER_WIDTH_PCT = b.comment("Width as percent of screen width (1–100)").defineInRange("widthPercent", 60, 1, 100);
        b.pop();

        b.comment("Footer text").push("footer");
        FOOTER_VISIBLE = b.define("visible", true);
        FOOTER_TEXT    = b.define("text", "Contact the server staff to appeal");
        FOOTER_COLOR   = b.comment("Footer color (#RRGGBB)").define("color", "#888888");
        FOOTER_X       = b.defineInRange("x", 0.5, 0.0, 1.0);
        FOOTER_Y       = b.defineInRange("y", 0.76, 0.0, 1.0);
        FOOTER_SHADOW  = b.define("shadow", false);
        b.pop();

        b.comment("Appeal / website link").push("appeal");
        APPEAL_VISIBLE = b.define("visible", false);
        APPEAL_TEXT    = b.define("text", "Appeal at: your-server.com/appeal");
        APPEAL_COLOR   = b.comment("Appeal text color (#RRGGBB)").define("color", "#55AAFF");
        APPEAL_X       = b.defineInRange("x", 0.5, 0.0, 1.0);
        APPEAL_Y       = b.defineInRange("y", 0.85, 0.0, 1.0);
        APPEAL_SHADOW  = b.define("shadow", false);
        b.pop();

        b.comment("Return-to-title button").push("button");
        BUTTON_TEXT       = b.define("text", "Return to Title Screen");
        BUTTON_COLOR      = b.comment("Button background color (#RRGGBB)").define("color", "#AA2222");
        BUTTON_TEXT_COLOR = b.comment("Button label color (#RRGGBB)").define("textColor", "#FFFFFF");
        BUTTON_X          = b.defineInRange("x", 0.5, 0.0, 1.0);
        BUTTON_Y          = b.defineInRange("y", 0.92, 0.0, 1.0);
        b.pop();

        SPEC = b.build();
    }

    /** Converts the current config values into a {@link BanScreenData} instance. */
    public static BanScreenData toBanScreenData() {
        BanScreenData d = new BanScreenData();

        d.bgColor          = BG_COLOR.get();
        d.bgColorBottom    = BG_COLOR_BOTTOM.get();
        d.bgGradient       = BG_GRADIENT.get();
        d.bgOpacity        = (float) (double) BG_OPACITY.get();

        d.titleText        = TITLE_TEXT.get();
        d.titleColor       = TITLE_COLOR.get();
        d.titleX           = (float) (double) TITLE_X.get();
        d.titleY           = (float) (double) TITLE_Y.get();
        d.titleBold        = TITLE_BOLD.get();
        d.titleItalic      = TITLE_ITALIC.get();
        d.titleShadow      = TITLE_SHADOW.get();
        d.titleScale       = TITLE_SCALE.get();
        d.titleVisible     = TITLE_VISIBLE.get();

        d.reasonPrefix     = REASON_PREFIX.get();
        d.reasonColor      = REASON_COLOR.get();
        d.reasonX          = (float) (double) REASON_X.get();
        d.reasonY          = (float) (double) REASON_Y.get();
        d.reasonBold       = REASON_BOLD.get();
        d.reasonItalic     = REASON_ITALIC.get();
        d.reasonShadow     = REASON_SHADOW.get();
        d.reasonVisible    = REASON_VISIBLE.get();

        d.expiryVisible    = EXPIRY_VISIBLE.get();
        d.expiryPrefix     = EXPIRY_PREFIX.get();
        d.neverBanText     = NEVER_BAN_TEXT.get();
        d.expiryColor      = EXPIRY_COLOR.get();
        d.expiryX          = (float) (double) EXPIRY_X.get();
        d.expiryY          = (float) (double) EXPIRY_Y.get();
        d.expiryShadow     = EXPIRY_SHADOW.get();

        d.playerVisible    = PLAYER_VISIBLE.get();
        d.playerPrefix     = PLAYER_PREFIX.get();
        d.playerColor      = PLAYER_COLOR.get();
        d.playerX          = (float) (double) PLAYER_X.get();
        d.playerY          = (float) (double) PLAYER_Y.get();

        d.dividerVisible   = DIVIDER_VISIBLE.get();
        d.dividerColor     = DIVIDER_COLOR.get();
        d.dividerY         = (float) (double) DIVIDER_Y.get();
        d.dividerThickness = DIVIDER_THICKNESS.get();
        d.dividerWidthPct  = DIVIDER_WIDTH_PCT.get();

        d.footerVisible    = FOOTER_VISIBLE.get();
        d.footerText       = FOOTER_TEXT.get();
        d.footerColor      = FOOTER_COLOR.get();
        d.footerX          = (float) (double) FOOTER_X.get();
        d.footerY          = (float) (double) FOOTER_Y.get();
        d.footerShadow     = FOOTER_SHADOW.get();

        d.appealVisible    = APPEAL_VISIBLE.get();
        d.appealText       = APPEAL_TEXT.get();
        d.appealColor      = APPEAL_COLOR.get();
        d.appealX          = (float) (double) APPEAL_X.get();
        d.appealY          = (float) (double) APPEAL_Y.get();
        d.appealShadow     = APPEAL_SHADOW.get();

        d.buttonText       = BUTTON_TEXT.get();
        d.buttonColor      = BUTTON_COLOR.get();
        d.buttonTextColor  = BUTTON_TEXT_COLOR.get();
        d.buttonX          = (float) (double) BUTTON_X.get();
        d.buttonY          = (float) (double) BUTTON_Y.get();

        return d;
    }
}
