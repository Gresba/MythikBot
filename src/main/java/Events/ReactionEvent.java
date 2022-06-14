package Events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReactionEvent extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if(event.getMember() == null)
            return;

        if(event.getMessageId().equals("938330535145635861")) {
            Role role = null;
            // MFA
            if(event.getReactionEmote().getEmoji().equals("\uD83D\uDCE7"))
            {
                role = event.getGuild().getRoleById("930389137129898014");

            // Skyblock
            }else if(event.getReactionEmote().getEmoji().equals("☁️")){
                role = event.getGuild().getRoleById("930389282261180418");

            // Hypixel LvL 21+/Ranked
            }else if(event.getReactionEmote().getEmoji().equals("\uD83C\uDF32")){
                role = event.getGuild().getRoleById("930389340843016243");

            // Hypixel Unbanned NFA/SFA Mix
            }else if(event.getReactionEmote().getEmoji().equals("❤️")){
                role = event.getGuild().getRoleById("930389471646593044");

            // NFA
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDFE5")){
                role = event.getGuild().getRoleById("930389514164244542");

            // Discord Nitro
            }else if(event.getReactionEmote().getEmoji().equals("✨")){
                role = event.getGuild().getRoleById("930389514164244542");

            // Migrated Unbanned NFA
            }else if(event.getReactionEmote().getEmoji().equals("✅")){
                role = event.getGuild().getRoleById("955743978337222666");

            // Migrated Banned NFA
            }else if(event.getReactionEmote().getEmoji().equals("\uD83C\uDF51")){
                role = event.getGuild().getRoleById("955744061430579230");

            // Minecon
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDE00")){
                role = event.getGuild().getRoleById("930389554026922005");

            // Special MFA
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDE3B")){
                role = event.getGuild().getRoleById("938329764408729631");

            // YAHOO FA
            }else if(event.getReactionEmote().getEmoji().equals("☯️")){
                role = event.getGuild().getRoleById("934370021021466624");

            // VYPR VPN
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDC7D")){
                role = event.getGuild().getRoleById("934369917912903721");
            }
            event.getGuild().addRoleToMember(event.getMember(), role).queue();
        }else if(event.getMessageId().equals("934631585565794374")) {
            //Giveaway Role
            Role role = event.getGuild().getRoleById("934630629088321646");

            event.getGuild().addRoleToMember(event.getMember(), role).queue();
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if(event.getMember() == null)
            return;

        if(event.getMessageId().equals("938330535145635861"))
        {
            Role role = null;
            // MFA
            if(event.getReactionEmote().getEmoji().equals("\uD83D\uDCE7"))
            {
                role = event.getGuild().getRoleById("930389137129898014");

            // Skyblock
            }else if(event.getReactionEmote().getEmoji().equals("☁️")){
                role = event.getGuild().getRoleById("930389282261180418");

            // Hypixel LvL 21+/Ranked
            }else if(event.getReactionEmote().getEmoji().equals("\uD83C\uDF32")){
                role = event.getGuild().getRoleById("930389340843016243");

            // Hypixel Unbanned NFA/SFA Mix
            }else if(event.getReactionEmote().getEmoji().equals("❤️")){
                role = event.getGuild().getRoleById("930389471646593044");

            // NFA
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDFE5")){
                role = event.getGuild().getRoleById("930389514164244542");

            // Discord Nitro
            }else if(event.getReactionEmote().getEmoji().equals("✨")){
                role = event.getGuild().getRoleById("955744123816648746");

            // Migrated Unbanned NFA
            }else if(event.getReactionEmote().getEmoji().equals("✅")){
                role = event.getGuild().getRoleById("955743978337222666");

            // Migrated Banned NFA
            }else if(event.getReactionEmote().getEmoji().equals("\uD83C\uDF51")){
                role = event.getGuild().getRoleById("955744061430579230");

            // Minecon
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDE00")){
                role = event.getGuild().getRoleById("930389554026922005");

            // Special MFA
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDE3B")){
                role = event.getGuild().getRoleById("938329764408729631");

            // YAHOO FA
            }else if(event.getReactionEmote().getEmoji().equals("☯️")){
                role = event.getGuild().getRoleById("934370021021466624");

            // VYPR VPN
            }else if(event.getReactionEmote().getEmoji().equals("\uD83D\uDC7D")){
                role = event.getGuild().getRoleById("934369917912903721");
            }
            event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
        }else if(event.getMessageId().equals("934631585565794374")) {
            //Giveaway Role
            Role role = event.getGuild().getRoleById("934630629088321646");

            event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
        }
    }
}
