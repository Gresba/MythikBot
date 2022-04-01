package Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class memberLeaveGuildEvent extends ListenerAdapter {
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        EmbedBuilder joinEmbed = new EmbedBuilder();

        // Embed Properties
        joinEmbed.setTitle("Member Left");
        joinEmbed.setAuthor(member.getUser().getAsTag(), member.getEffectiveAvatarUrl(), member.getEffectiveAvatarUrl());
        joinEmbed.setDescription(member.getAsMention() + " left the server");
        joinEmbed.setColor(Color.RED);

        joinEmbed.setFooter("Member ID: " + member.getId());
        guild.getTextChannelById("937170925655310386").sendMessageEmbeds(joinEmbed.build()).queue();
    }
}
