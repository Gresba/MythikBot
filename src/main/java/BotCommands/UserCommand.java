package BotCommands;

import Bot.BotProperty;
import Bot.MessageObj;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.sql.Statement;

import static Bot.SQLConnection.getStatement;

public class UserCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.getChannelType().isGuild() || event.getMember() == null) {
            return;
        }

        Guild guild = event.getGuild();

        BotProperty botProperty = new BotProperty(guild.getId());

        Member member = event.getMember();

        User memberUser = null;

        Message message = event.getMessage();
        String messageString = message.getContentRaw();
        String messageId = message.getId();
        String[] messageArr = message.getContentRaw().split(" ");

        TextChannel channel = event.getTextChannel();

        Statement statement = getStatement();

            memberUser = member.getUser();

            if(!memberUser.isBot())
            {
                String messageLowerCase = messageString.toLowerCase();

                MessageObj messageObj = new MessageObj(messageId, messageString, member.getId(), message.getTimeCreated().toZonedDateTime());

                if(messageObj.getMessageCount() > 100)
                {
                    // Decrement the count and remove the last message from the hashMap and queue
                    messageObj.setMessageCount(messageObj.getMessageCount() - 1);
                    String poppedId = botProperty.getMessageHistoryQueue().poll();
                    botProperty.getMessageHistory().remove(poppedId);
                }

                // Add the messages to the queue and hashMap
                botProperty.getMessageHistory().put(messageId, messageObj);
                botProperty.getMessageHistoryQueue().add(messageId);


//                if(messageArr[0].equalsIgnoreCase(botPrefix + "orders"))
//                {
//                    String memberID = "";
//                    if(message.getMentionedMembers().size() > 0)
//                    {
//                        memberID = message.getMentionedMembers().get(0).getId();
//                    }else if(messageArr.length == 2){
//                        memberID = messageArr[1];
//                    }else{
//                        memberID = member.getId();
//                    }
//
//                    String query = "SELECT SUM(Amount) AS TotalAmount FROM Orders WHERE MemberID = '" + memberID + "'";
//                    try {
//                        ResultSet result = statement.executeQuery(query);
//                        result.next();
//                        Member target = guild.getMemberById(memberID);
//                        double totalSpent = result.getDouble("TotalAmount");
//
//                        String totalSpentString = String.format("$%.02f", totalSpent);
//
//                        EmbedBuilder embed = new EmbedBuilder()
//                                .setAuthor(target.getUser().getAsTag(), target.getEffectiveAvatarUrl(), target.getEffectiveAvatarUrl())
//                                .setDescription("Your order history")
//                                .setColor(Color.ORANGE)
//                                .addField("**Total Spent:**", totalSpentString, false);
//                        channel.sendMessageEmbeds(embed.build()).queue();
//                    }catch (SQLException e){
//                        e.printStackTrace();
//                    }
//                }
            }
    }
}
