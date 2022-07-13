package CustomObjects;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

public class DropDowns {
    public static SelectMenu PRODUCTS =
            SelectMenu.create("select-roles")
                    .addOptions(SelectOption.of("Unbanned MFA", "unbanned-mfa")
                            .withDescription("MFAs unbanned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+2764")))

                    .addOptions(SelectOption.of("Banned MFA", "banned-mfa")
                            .withDescription("MFAs banned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F9E1")))

                    .addOptions(SelectOption.of("Microsoft Unbanned NFA", "microsoft-unbanned-nfa")
                            .withDescription("Microsoft NFAs unbanned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F499")))

                    .addOptions(SelectOption.of("Microsoft Banned NFA", "microsoft-banned-nfa")
                            .withDescription("Microsoft NFAs banned on Hypixel")
                            .withEmoji(Emoji.fromUnicode("U+1F49C")))

                    .addOptions(SelectOption.of("Minecon NFA", "minecon-nfa")
                            .withDescription("NFAs with Minecon capes on them")
                            .withEmoji(Emoji.fromUnicode("U+1F90E")))

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

                    .setPlaceholder("Select Roles")
                    .build();
}
