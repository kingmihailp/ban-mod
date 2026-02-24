package com.banmod.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * All visual settings for the custom ban screen.
 * Sent from server -> client as JSON before the disconnect packet.
 * Also used client-side to render the screen.
 */
public class BanScreenData {

    private static final Gson GSON = new GsonBuilder().create();

    // ── Background ──────────────────────────────────────────────────────────
    public String  bgColor         = "#0D0D1A";
    public String  bgColorBottom   = "#1A0D0D";   // gradient end (used when bgGradient=true)
    public boolean bgGradient      = true;
    public float   bgOpacity       = 0.97f;

    // ── Title ───────────────────────────────────────────────────────────────
    public String  titleText       = "You have been banned";
    public String  titleColor      = "#FF4444";
    public float   titleX          = 0.5f;         // 0.0 left … 1.0 right
    public float   titleY          = 0.18f;        // 0.0 top  … 1.0 bottom
    public boolean titleBold       = true;
    public boolean titleItalic     = false;
    public boolean titleShadow     = true;
    public int     titleScale      = 2;            // pixel scale (1–4)
    public boolean titleVisible    = true;

    // ── Reason ──────────────────────────────────────────────────────────────
    public String  reasonPrefix    = "Reason: ";
    public String  reasonColor     = "#FFFFFF";
    public float   reasonX         = 0.5f;
    public float   reasonY         = 0.42f;
    public boolean reasonBold      = false;
    public boolean reasonItalic    = false;
    public boolean reasonShadow    = false;
    public boolean reasonVisible   = true;

    // ── Expiry ──────────────────────────────────────────────────────────────
    public boolean expiryVisible   = true;
    public String  expiryPrefix    = "Expires: ";
    public String  neverBanText    = "Permanent";
    public String  expiryColor     = "#FFB347";
    public float   expiryX         = 0.5f;
    public float   expiryY         = 0.52f;
    public boolean expiryShadow    = false;

    // ── Player name ─────────────────────────────────────────────────────────
    public boolean playerVisible   = false;
    public String  playerPrefix    = "Player: ";
    public String  playerColor     = "#AAAAAA";
    public float   playerX         = 0.5f;
    public float   playerY         = 0.62f;

    // ── Divider ─────────────────────────────────────────────────────────────
    public boolean dividerVisible  = true;
    public String  dividerColor    = "#553333";
    public float   dividerY        = 0.35f;        // relative Y of the horizontal line
    public int     dividerThickness = 1;
    public int     dividerWidthPct = 60;           // % of screen width

    // ── Footer ──────────────────────────────────────────────────────────────
    public boolean footerVisible   = true;
    public String  footerText      = "Contact the server staff to appeal";
    public String  footerColor     = "#888888";
    public float   footerX         = 0.5f;
    public float   footerY         = 0.76f;
    public boolean footerShadow    = false;

    // ── Appeal URL ──────────────────────────────────────────────────────────
    public boolean appealVisible   = false;
    public String  appealText      = "Appeal at: your-server.com/appeal";
    public String  appealColor     = "#55AAFF";
    public float   appealX         = 0.5f;
    public float   appealY         = 0.85f;
    public boolean appealShadow    = false;

    // ── OK Button ───────────────────────────────────────────────────────────
    public String  buttonText      = "Return to Title Screen";
    public String  buttonColor     = "#AA2222";
    public String  buttonTextColor = "#FFFFFF";
    public float   buttonX         = 0.5f;         // center X of button
    public float   buttonY         = 0.92f;        // center Y of button

    // ──────────────────────────────────────────────────────────────────────

    /** Serialises this object to a compact JSON string. */
    public String toJson() {
        return GSON.toJson(this);
    }

    /** Deserialises a JSON string back into a {@link BanScreenData} object. */
    public static BanScreenData fromJson(String json) {
        return GSON.fromJson(json, BanScreenData.class);
    }
}
