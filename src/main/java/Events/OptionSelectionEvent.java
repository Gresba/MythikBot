package Events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OptionSelectionEvent extends ListenerAdapter {

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {

        Member member = event.getMember();
        Guild guild = event.getGuild();

        switch (event.getComponentId())
        {
            case "select-roles":
                String roleId = "";
                switch (event.getValues().get(0))
                {
                    case "unbanned-mfa" ->
                        roleId = "930389137129898014";

                    case "banned-mfa" ->
                        roleId = "987770057490853899";

                    case "unbanned-nfa" ->
                        roleId = "930389471646593044";

                    case "banned-nfa" ->
                        roleId = "930389514164244542";

                    case "microsoft-unbanned-nfa" ->
                        roleId = "955743978337222666";

                    case "microsoft-banned-nfa" ->
                        roleId = "955744061430579230";

                    case "minecon-nfa" ->
                        roleId = "930389554026922005";

                    case "hypixel-ranked-lvl" ->
                        roleId = "930389340843016243";

                    case "ranked-mfa" ->
                        roleId = "987770454288764968";

                    case "lvl21-mfa" ->
                        roleId = "938329764408729631";

                    case "skyblock" ->
                        roleId = "930389282261180418";

                    case "vpn" ->
                        roleId = "934369917912903721";

                    case "gaming-email-access" ->
                        roleId= "934370021021466624";
                }

                Role chosenRole = guild.getRoleById(roleId);
                if(!member.getRoles().contains(chosenRole)) {
                    guild.addRoleToMember(member.getId(), chosenRole).queue();
                    event.reply("Successfully added " + chosenRole.getName() + "!").setEphemeral(true).queue();
                }else{
                    guild.removeRoleFromMember(member.getId(), chosenRole).queue();
                    event.reply("Successfully removed " + chosenRole.getName() + "!").setEphemeral(true).queue();
                }
                break;
        }

        super.onSelectMenuInteraction(event);
    }
}
