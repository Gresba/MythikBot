package BotCommands;

import Bot.BotProperty;
import Bot.MessageObj;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.getChannelType().isGuild() || event.getMember() == null) {
            return;
        }

        Member member = event.getMember();

        User memberUser;

        Message message = event.getMessage();
        String messageString = message.getContentRaw();
        String messageId = message.getId();


        memberUser = member.getUser();

        if(!memberUser.isBot())
        {
            MessageObj messageObj = new MessageObj(messageString, member.getId(), message.getTimeCreated().toZonedDateTime());

            if(MessageObj.getMessageCount() > 100)
            {
                // Decrement the count and remove the last message from the hashMap and queue
                MessageObj.setMessageCount(MessageObj.getMessageCount() - 1);
                String poppedId = BotProperty.getMessageHistoryQueue().poll();
                BotProperty.getMessageHistory().remove(poppedId);
            }

            // Add the messages to the queue and hashMap
            BotProperty.getMessageHistory().put(messageId, messageObj);
            BotProperty.getMessageHistoryQueue().add(messageId);
        }
    }
}
