package Commands;

import Bot.Embeds;
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
        Guild guild = event.getGuild();
        Member sender = event.getMember();

        TextChannel channel = event.getTextChannel();

        switch (event.getName())
        {
            case "btc":
                event.reply("**Bitcoin Address:** 3EnYnATc8SuHDBhXVvtF8m3Zoir1KcQEiU").queue();
                break;
            case "ltc":
                event.reply("**Litecoin Address:** MK2sc3NFNo4rwTdgRjQ5fJHC32W7B2QYsp").queue();
                break;
            case "eth":
                event.reply("**Ethereum Address:** 0xc8b33e106CB10DDB0980Fb3c2eb4f3e28F81F44d").queue();
                break;
            case "bch":
                event.reply("**Bitcoin Cash Address:** qqmcveumtz5adcxj85yu37808nzyx7w7ag28sljpc2").queue();
                break;
            case "sol":
                event.reply("**Solana Address:** CwTGK46ng2ygQpQXrCVdWynaEwPDXa3Lpfckx4zDMjpY").queue();
                break;
            case "ada":
                event.reply("**Cardano Address:** addr1v939psedcu5swmwltlmdveq0gxn69jd7w9k26mmmsq9lwsg55hl7g").queue();
                break;
            case "cashapp":
                event.reply("**Cash App:** $UFACapital").queue();
                break;
            case "venmo":
                event.reply("**Venmo:** pkvenmo11").queue();
                break;

            // HELP COMMAND
            case "help":
                event.replyEmbeds(Embeds.HELP.getEmbed().build())
                        .addActionRow(
                                Button.primary("staff-commands-help", "Staff Help"),
                                Button.secondary("user-command-help", "User help"),
                                Button.primary("customer-commands-help", "Customer Commands"),
                                Button.secondary("faq-help", "FAQ")
                        ).queue();
                break;
        }
    }
}
