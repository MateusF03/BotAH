package com.mateus.botah.event;

import com.mateus.botah.game.Card;
import com.mateus.botah.game.Game;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.javatuples.Triplet;

import java.util.Map;

public class CsarEvent extends ListenerAdapter {
    private final long csarId;
    private final Map<User, Triplet<Card, Card, Card>> cards;
    private final long channelId;
    private Game game;
    public CsarEvent(Game game) {
        this.csarId = game.getCsar().getIdLong();
        this.cards = game.chosenCard;
        this.channelId = game.getTextChannel().getIdLong();
        this.game = game;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().getIdLong() != csarId) return;
        if (event.getChannel().getIdLong() != channelId) return;
        try {
            String choice = event.getMessage().getContentDisplay();
            User winner = event.getJDA().getUsersByName(choice, false).get(0);
            if (cards.containsKey(winner)) {
                game.addPoint(winner);
                game.nextTurn();
                game.chosen.clear();
                game.chosenCard.clear();
                event.getJDA().removeEventListener(this);
            }
            event.getJDA().removeEventListener(this);
        } catch (NumberFormatException e) {
            event.getTextChannel().sendMessage("Não é um número!").queue();
        }
    }
}
