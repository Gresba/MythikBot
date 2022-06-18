package CustomObjects;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

public class DropDowns {
    public static SelectMenu RESTOCK_ROLES =
            SelectMenu.create("select-roles")
                    .addOptions(SelectOption.of("Unbanned MFA", "unbanned-mfa")
                            .withDescription("MFAs unbanned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+2764")))

                    .addOptions(SelectOption.of("Banned MFA", "banned-mfa")
                            .withDescription("MFAs banned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F9E1")))

                    .addOptions(SelectOption.of("Unbanned NFA", "unbanned-nfa")
                            .withDescription("NFAs unbanned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F49B")))

                    .addOptions(SelectOption.of("Banned NFA", "banned-nfa")
                            .withDescription("NFAs banned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F49A")))

                    .addOptions(SelectOption.of("Microsoft Unbanned NFA", "microsoft-unbanned-nfa")
                            .withDescription("Microsoft NFAs unbanned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F499")))

                    .addOptions(SelectOption.of("Microsoft Banned NFA", "microsoft-banned-nfa")
                            .withDescription("Microsoft NFAs banned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F49C")))

                    .addOptions(SelectOption.of("Minecon NFA", "minecon-nfa")
                            .withDescription("NFAs with Minecon capes on them")
                            .withEmoji(Emoji.fromUnicode("U+1F90E")))

                    .addOptions(SelectOption.of("Hypixel Ranked/LvL", "hypixel-ranked-lvl")
                            .withDescription("NFAs with ranks, levels or both on them")
                            .withEmoji(Emoji.fromUnicode("U+1F5A4")))

                    .addOptions(SelectOption.of("Ranked MFAs", "ranked-mfa")
                            .withDescription("MFAs with a rank on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F90D")))

                    .addOptions(SelectOption.of("LvL 21+ MFAs", "lvl21-mfa")
                            .withDescription("MFAs level 21+ on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F4AF")))

                    .addOptions(SelectOption.of("Skyblock", "skyblock")
                            .withDescription("Coins or account with skyblock items")
                            .withEmoji(Emoji.fromUnicode("U+1F4A2")))

                    .addOptions(SelectOption.of("VPNs", "vpn")
                            .withDescription("VPN")
                            .withEmoji(Emoji.fromUnicode("U+1F4AB")))

                    .addOptions(SelectOption.of("Gaming Email Access", "gaming-email-access")
                            .withDescription("Emails with emails from gaming companies such as Steam, Discord, Riot Games and Epic Games")
                            .withEmoji(Emoji.fromUnicode("U+1F4A5")))
                    .setPlaceholder("Select Roles")
                    .build();
}
