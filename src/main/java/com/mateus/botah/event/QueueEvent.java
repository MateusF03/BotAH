package com.mateus.botah.event;

import com.mateus.botah.game.Card;
import com.mateus.botah.game.CardManager;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class QueueEvent extends ListenerAdapter {
    private final long channelId, userId;
    private List<Card> whiteCards;
    private Card card1, card2, card3;
    public QueueEvent(MessageChannel channel, User user, List<Card> whiteCards) {
        this.channelId = channel.getIdLong();
        this.userId = user.getIdLong();
        this.whiteCards = whiteCards;
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != channelId) return;
        if (event.getAuthor().getIdLong() != userId) return;
        int pick = CardManager.get().getPlayerGame(event.getAuthor()).blackCard.getPick();
        if (pick == 1) {
            try {
                int i = Integer.parseInt(event.getMessage().getContentRaw());
                if (i<1 || i>5) {
                    event.getChannel().sendMessage("Digite um número entre 1 a 5").queue();
                } else {
                    card1 = whiteCards.get(i - 1);
                    CardManager.get().getPlayerGame(event.getAuthor()).addChosenCards(event.getAuthor(), card1,null,null);
                    CardManager.get().getPlayerGame(event.getAuthor()).chosen.add(event.getAuthor());
                    if (CardManager.get().getPlayerGame(event.getAuthor()).chosen.size() == CardManager.get().getPlayerGame(event.getAuthor()).getPlayers().size() -1) {
                        CardManager.get().getPlayerGame(event.getAuthor()).vote();
                    }
                    event.getJDA().removeEventListener(this);
                }
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Não é um número!").queue();
            }
        } else if (pick == 2) {
            String message = event.getMessage().getContentRaw();
            String[] m = message.split(" ");
            try{
                if (m.length != 2) {
                    event.getChannel().sendMessage("Mande dois números").queue();
                    return;
                }
                int i1 = Integer.valueOf(m[0]);
                int i2 = Integer.valueOf(m[1]);
                if ((i1<1 || i1>5) && (i2<1||i2>5)) {
                    event.getChannel().sendMessage("Digite dois um número entre 1 a 5").queue();
                } else {
                    if (i1 == i2) {
                        event.getChannel().sendMessage("Essas são as mesmas cartas").queue();
                    }
                    card1 = whiteCards.get(i1 - 1);
                    card2 = whiteCards.get(i2 - 1);
                    CardManager.get().getPlayerGame(event.getAuthor()).addChosenCards(event.getAuthor(), card1,card2, null);
                    if (CardManager.get().getPlayerGame(event.getAuthor()).chosen.size() == CardManager.get().getPlayerGame(event.getAuthor()).getPlayers().size() -1) {
                        CardManager.get().getPlayerGame(event.getAuthor()).vote();
                    }
                    event.getJDA().removeEventListener(this);
                }
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Não é um número!").queue();
            }
        } else if (card3 == null) {
            String message = event.getMessage().getContentRaw();
            String[] m = message.split(" ");
            try{
                if (m.length != 3) {
                    event.getChannel().sendMessage("Mande dois números").queue();
                    return;
                }
                int i1 = Integer.valueOf(m[0]);
                int i2 = Integer.valueOf(m[1]);
                int i3 = Integer.valueOf(m[2]);
                if ((i1<1 || i1>5) && (i2<1||i2>5) && (i3<1||i3>5)) {
                    event.getChannel().sendMessage("Digite três um número entre 1 a 5").queue();
                } else {
                    if (i1==i2 || i2==i3) {
                        event.getChannel().sendMessage("Essas são as mesmas cartas").queue();
                    }
                    card1 = whiteCards.get(i1 - 1);
                    card2 = whiteCards.get(i2 - 1);
                    card3 = whiteCards.get(i3 - 1);
                    CardManager.get().getPlayerGame(event.getAuthor()).addChosenCards(event.getAuthor(), card1,card2, card3);
                    if (CardManager.get().getPlayerGame(event.getAuthor()).chosen.size() == CardManager.get().getPlayerGame(event.getAuthor()).getPlayers().size() -1) {
                        CardManager.get().getPlayerGame(event.getAuthor()).vote();
                    }
                    event.getJDA().removeEventListener(this);
                }
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Não é um número!").queue();
            }
        }
    }
}
