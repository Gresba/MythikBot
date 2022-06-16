package Events;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OptionSelectionEvent extends ListenerAdapter {

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {

        super.onSelectMenuInteraction(event);
    }
}
