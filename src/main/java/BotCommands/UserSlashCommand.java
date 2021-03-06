package BotCommands;

import CustomObjects.Embeds;
import CustomObjects.Modals;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class UserSlashCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getName())
        {
            case "payment" -> {
                switch (event.getSubcommandName()) {
                    case "btc" -> event.reply("**Bitcoin Address:** 3EnYnATc8SuHDBhXVvtF8m3Zoir1KcQEiU").queue();
                    case "ltc" -> event.reply("**Lite coin Address:** MK2sc3NFNo4rwTdgRjQ5fJHC32W7B2QYsp").queue();
                    case "eth" -> event.reply("**Ethereum Address:** 0xc8b33e106CB10DDB0980Fb3c2eb4f3e28F81F44d").queue();
                    case "bch" -> event.reply("**Bitcoin Cash Address:** qqmcveumtz5adcxj85yu37808nzyx7w7ag28sljpc2").queue();
                    case "sol" -> event.reply("**Solana Address:** CwTGK46ng2ygQpQXrCVdWynaEwPDXa3Lpfckx4zDMjpY").queue();
                    case "ada" -> event.reply("**Cardano Address:** addr1v939psedcu5swmwltlmdveq0gxn69jd7w9k26mmmsq9lwsg55hl7g").queue();
                    case "cashapp" -> event.reply("**Cash App:** $UFACapital").queue();
                    case "venmo" -> event.reply("**Venmo:** pkvenmo11").queue();
                    case "paypal" ->
                        event.reply("Send $" + event.getOption("price").getAsInt() + " USD to betteralts01@gmail.com\n" +
                                "Make sure the payment is **PayPal Friends and Family**.\n\n" +
                                "If not, you will **NOT** receive the product. A refund will require a refund fee to be paid.\n" +
                                "**NOTE:** If you did not specifically click *friends and family* option that means you did not send using friends and family\n" +
                                "Do **NOT** send if you don't see the option!").queue();
                }
            }

            // HELP COMMAND
            case "help" ->
                event.replyEmbeds(Embeds.HELP.build())
                        .addActionRow(
                                Button.primary("staff-commands-help", "Staff Help"),
                                Button.secondary("user-command-help", "User help"),
                                Button.primary("customer-commands-help", "Customer Commands"),
                                Button.secondary("faq-help", "FAQ")
                        ).queue();
        }
    }
}
