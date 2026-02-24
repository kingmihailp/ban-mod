package com.banmod.command;

import com.banmod.config.BanScreenConfig;
import com.banmod.data.BanScreenData;
import com.banmod.network.NetworkHandler;
import com.banmod.network.packet.PreviewPacket;
import com.banmod.util.ColorUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

/**
 * Admin command tree: {@code /banscreen <subcommand>}
 *
 * <pre>
 * /banscreen preview                              – show the current ban screen to yourself
 * /banscreen reload                               – reload config from disk
 *
 * /banscreen set background color <#hex>          – set top background colour
 * /banscreen set background colorBottom <#hex>    – set bottom gradient colour
 * /banscreen set background gradient <bool>
 * /banscreen set background opacity <0.0–1.0>
 *
 * /banscreen set title text <text>
 * /banscreen set title color <#hex>
 * /banscreen set title x <0.0–1.0>
 * /banscreen set title y <0.0–1.0>
 * /banscreen set title scale <1–4>
 * /banscreen set title bold <bool>
 * /banscreen set title italic <bool>
 * /banscreen set title shadow <bool>
 * /banscreen set title visible <bool>
 *
 * /banscreen set reason prefix <text>
 * /banscreen set reason color <#hex>
 * /banscreen set reason x <0.0–1.0>
 * /banscreen set reason y <0.0–1.0>
 * /banscreen set reason bold <bool>
 * /banscreen set reason italic <bool>
 * /banscreen set reason shadow <bool>
 * /banscreen set reason visible <bool>
 *
 * /banscreen set expiry prefix <text>
 * /banscreen set expiry color <#hex>
 * /banscreen set expiry x <float>
 * /banscreen set expiry y <float>
 * /banscreen set expiry neverText <text>
 * /banscreen set expiry visible <bool>
 *
 * /banscreen set footer text <text>
 * /banscreen set footer color <#hex>
 * /banscreen set footer x <float>
 * /banscreen set footer y <float>
 * /banscreen set footer visible <bool>
 *
 * /banscreen set appeal text <text>
 * /banscreen set appeal color <#hex>
 * /banscreen set appeal x <float>
 * /banscreen set appeal y <float>
 * /banscreen set appeal visible <bool>
 *
 * /banscreen set divider color <#hex>
 * /banscreen set divider y <float>
 * /banscreen set divider thickness <1–5>
 * /banscreen set divider width <1–100>
 * /banscreen set divider visible <bool>
 *
 * /banscreen set button text <text>
 * /banscreen set button color <#hex>
 * /banscreen set button textColor <#hex>
 * /banscreen set button x <float>
 * /banscreen set button y <float>
 * </pre>
 */
public class BanScreenCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("banscreen")
                .requires(src -> src.hasPermission(3))

                // ── /banscreen preview ───────────────────────────────────────────
                .then(Commands.literal("preview")
                    .executes(ctx -> {
                        CommandSourceStack src = ctx.getSource();
                        ServerPlayer player = src.getPlayerOrException();
                        BanScreenData data = BanScreenConfig.toBanScreenData();
                        NetworkHandler.CHANNEL.sendTo(
                                new PreviewPacket(data),
                                player.connection.connection,
                                NetworkDirection.PLAY_TO_CLIENT
                        );
                        src.sendSuccess(() -> Component.literal("§aShowing ban screen preview."), false);
                        return 1;
                    })
                )

                // ── /banscreen reload ────────────────────────────────────────────
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        // Force Forge to re-read the TOML from disk
                        BanScreenConfig.SPEC.afterReload();
                        ctx.getSource().sendSuccess(
                                () -> Component.literal("§aBanMod config reloaded."), false);
                        return 1;
                    })
                )

                // ── /banscreen set ───────────────────────────────────────────────
                .then(Commands.literal("set")

                    // ── background ───────────────────────────────────────────────
                    .then(Commands.literal("background")
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.BG_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "background.color"))))
                        .then(Commands.literal("colorBottom")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.BG_COLOR_BOTTOM,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "background.colorBottom"))))
                        .then(Commands.literal("gradient")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.BG_GRADIENT,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "background.gradient"))))
                        .then(Commands.literal("opacity")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.BG_OPACITY,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "background.opacity"))))
                    )

                    // ── title ────────────────────────────────────────────────────
                    .then(Commands.literal("title")
                        .then(Commands.literal("text")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> setString(ctx.getSource(),
                                        BanScreenConfig.TITLE_TEXT,
                                        StringArgumentType.getString(ctx, "text"),
                                        "title.text"))))
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.TITLE_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "title.color"))))
                        .then(Commands.literal("x")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.TITLE_X,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "title.x"))))
                        .then(Commands.literal("y")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.TITLE_Y,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "title.y"))))
                        .then(Commands.literal("scale")
                            .then(Commands.argument("value", IntegerArgumentType.integer(1, 4))
                                .executes(ctx -> setInt(ctx.getSource(),
                                        BanScreenConfig.TITLE_SCALE,
                                        IntegerArgumentType.getInteger(ctx, "value"),
                                        "title.scale"))))
                        .then(Commands.literal("bold")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.TITLE_BOLD,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "title.bold"))))
                        .then(Commands.literal("italic")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.TITLE_ITALIC,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "title.italic"))))
                        .then(Commands.literal("shadow")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.TITLE_SHADOW,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "title.shadow"))))
                        .then(Commands.literal("visible")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.TITLE_VISIBLE,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "title.visible"))))
                    )

                    // ── reason ───────────────────────────────────────────────────
                    .then(Commands.literal("reason")
                        .then(Commands.literal("prefix")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> setString(ctx.getSource(),
                                        BanScreenConfig.REASON_PREFIX,
                                        StringArgumentType.getString(ctx, "text"),
                                        "reason.prefix"))))
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.REASON_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "reason.color"))))
                        .then(Commands.literal("x")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.REASON_X,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "reason.x"))))
                        .then(Commands.literal("y")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.REASON_Y,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "reason.y"))))
                        .then(Commands.literal("bold")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.REASON_BOLD,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "reason.bold"))))
                        .then(Commands.literal("italic")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.REASON_ITALIC,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "reason.italic"))))
                        .then(Commands.literal("shadow")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.REASON_SHADOW,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "reason.shadow"))))
                        .then(Commands.literal("visible")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.REASON_VISIBLE,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "reason.visible"))))
                    )

                    // ── expiry ───────────────────────────────────────────────────
                    .then(Commands.literal("expiry")
                        .then(Commands.literal("prefix")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> setString(ctx.getSource(),
                                        BanScreenConfig.EXPIRY_PREFIX,
                                        StringArgumentType.getString(ctx, "text"),
                                        "expiry.prefix"))))
                        .then(Commands.literal("neverText")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> setString(ctx.getSource(),
                                        BanScreenConfig.NEVER_BAN_TEXT,
                                        StringArgumentType.getString(ctx, "text"),
                                        "expiry.neverText"))))
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.EXPIRY_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "expiry.color"))))
                        .then(Commands.literal("x")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.EXPIRY_X,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "expiry.x"))))
                        .then(Commands.literal("y")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.EXPIRY_Y,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "expiry.y"))))
                        .then(Commands.literal("visible")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.EXPIRY_VISIBLE,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "expiry.visible"))))
                    )

                    // ── footer ───────────────────────────────────────────────────
                    .then(Commands.literal("footer")
                        .then(Commands.literal("text")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> setString(ctx.getSource(),
                                        BanScreenConfig.FOOTER_TEXT,
                                        StringArgumentType.getString(ctx, "text"),
                                        "footer.text"))))
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.FOOTER_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "footer.color"))))
                        .then(Commands.literal("x")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.FOOTER_X,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "footer.x"))))
                        .then(Commands.literal("y")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.FOOTER_Y,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "footer.y"))))
                        .then(Commands.literal("shadow")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.FOOTER_SHADOW,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "footer.shadow"))))
                        .then(Commands.literal("visible")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.FOOTER_VISIBLE,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "footer.visible"))))
                    )

                    // ── appeal ───────────────────────────────────────────────────
                    .then(Commands.literal("appeal")
                        .then(Commands.literal("text")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> setString(ctx.getSource(),
                                        BanScreenConfig.APPEAL_TEXT,
                                        StringArgumentType.getString(ctx, "text"),
                                        "appeal.text"))))
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.APPEAL_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "appeal.color"))))
                        .then(Commands.literal("x")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.APPEAL_X,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "appeal.x"))))
                        .then(Commands.literal("y")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.APPEAL_Y,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "appeal.y"))))
                        .then(Commands.literal("visible")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.APPEAL_VISIBLE,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "appeal.visible"))))
                    )

                    // ── divider ──────────────────────────────────────────────────
                    .then(Commands.literal("divider")
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.DIVIDER_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "divider.color"))))
                        .then(Commands.literal("y")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.DIVIDER_Y,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "divider.y"))))
                        .then(Commands.literal("thickness")
                            .then(Commands.argument("value", IntegerArgumentType.integer(1, 5))
                                .executes(ctx -> setInt(ctx.getSource(),
                                        BanScreenConfig.DIVIDER_THICKNESS,
                                        IntegerArgumentType.getInteger(ctx, "value"),
                                        "divider.thickness"))))
                        .then(Commands.literal("width")
                            .then(Commands.argument("value", IntegerArgumentType.integer(1, 100))
                                .executes(ctx -> setInt(ctx.getSource(),
                                        BanScreenConfig.DIVIDER_WIDTH_PCT,
                                        IntegerArgumentType.getInteger(ctx, "value"),
                                        "divider.widthPercent"))))
                        .then(Commands.literal("visible")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> setBool(ctx.getSource(),
                                        BanScreenConfig.DIVIDER_VISIBLE,
                                        BoolArgumentType.getBool(ctx, "value"),
                                        "divider.visible"))))
                    )

                    // ── button ───────────────────────────────────────────────────
                    .then(Commands.literal("button")
                        .then(Commands.literal("text")
                            .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> setString(ctx.getSource(),
                                        BanScreenConfig.BUTTON_TEXT,
                                        StringArgumentType.getString(ctx, "text"),
                                        "button.text"))))
                        .then(Commands.literal("color")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.BUTTON_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "button.color"))))
                        .then(Commands.literal("textColor")
                            .then(Commands.argument("hex", StringArgumentType.word())
                                .executes(ctx -> setColor(ctx.getSource(),
                                        BanScreenConfig.BUTTON_TEXT_COLOR,
                                        StringArgumentType.getString(ctx, "hex"),
                                        "button.textColor"))))
                        .then(Commands.literal("x")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.BUTTON_X,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "button.x"))))
                        .then(Commands.literal("y")
                            .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .executes(ctx -> setDouble(ctx.getSource(),
                                        BanScreenConfig.BUTTON_Y,
                                        DoubleArgumentType.getDouble(ctx, "value"),
                                        "button.y"))))
                    )
                )
        );
    }

    // ── Setters ──────────────────────────────────────────────────────────────

    private static int setColor(CommandSourceStack src,
                                net.minecraftforge.common.ForgeConfigSpec.ConfigValue<String> cfg,
                                String hex, String key) {
        if (!ColorUtils.isValid(hex)) {
            src.sendFailure(Component.literal("§cInvalid hex colour: " + hex + " (use #RRGGBB)"));
            return 0;
        }
        String normalised = hex.startsWith("#") ? hex : "#" + hex;
        cfg.set(normalised);
        BanScreenConfig.SPEC.save();
        src.sendSuccess(() -> Component.literal("§aSet §e" + key + " §ato §r" + normalised), true);
        return 1;
    }

    private static int setString(CommandSourceStack src,
                                 net.minecraftforge.common.ForgeConfigSpec.ConfigValue<String> cfg,
                                 String value, String key) {
        cfg.set(value);
        BanScreenConfig.SPEC.save();
        src.sendSuccess(() -> Component.literal("§aSet §e" + key + " §ato: §r" + value), true);
        return 1;
    }

    private static int setBool(CommandSourceStack src,
                               net.minecraftforge.common.ForgeConfigSpec.BooleanValue cfg,
                               boolean value, String key) {
        cfg.set(value);
        BanScreenConfig.SPEC.save();
        src.sendSuccess(() -> Component.literal("§aSet §e" + key + " §ato §r" + value), true);
        return 1;
    }

    private static int setDouble(CommandSourceStack src,
                                 net.minecraftforge.common.ForgeConfigSpec.DoubleValue cfg,
                                 double value, String key) {
        cfg.set(value);
        BanScreenConfig.SPEC.save();
        src.sendSuccess(() -> Component.literal("§aSet §e" + key + " §ato §r" + value), true);
        return 1;
    }

    private static int setInt(CommandSourceStack src,
                              net.minecraftforge.common.ForgeConfigSpec.IntValue cfg,
                              int value, String key) {
        cfg.set(value);
        BanScreenConfig.SPEC.save();
        src.sendSuccess(() -> Component.literal("§aSet §e" + key + " §ato §r" + value), true);
        return 1;
    }
}
