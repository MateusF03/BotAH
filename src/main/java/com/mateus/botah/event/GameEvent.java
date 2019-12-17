package com.mateus.botah.event;

import com.mateus.botah.game.CardManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GameEvent implements EventListener {
    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            CardManager cardManager = CardManager.get();
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            String content = e.getMessage().getContentRaw();
            if (content.toLowerCase().startsWith("c!start")) {
                if (e.getMessage().getMentionedMembers().isEmpty()) {
                    e.getTextChannel().sendMessage("Mencione os usuários que vão jogar").queue();
                } else if (cardManager.games.containsValue(e.getTextChannel())) {
                    e.getTextChannel().sendMessage("Este canal já esta com um jogo em progresso!").queue();
                } else if (cardManager.getPlayerGame(e.getAuthor()) != null){
                    e.getTextChannel().sendMessage("Você já esta em um jogo!").queue();
                } else if (e.getMessage().getMentionedMembers().size() < 2) {
                    e.getTextChannel().sendMessage("Usuários insuficientes").queue();
                } else {
                    List<User> players = new ArrayList<>();
                    players.add(e.getAuthor());
                    players.addAll(e.getMessage().getMentionedUsers());
                    CardManager.get().startGame(players, e.getTextChannel());
                }
            }
            if (e.getMessage().getContentRaw().equals("c!shutdown") && e.getAuthor().getId().equals("175653597982359552")) {
                e.getJDA().shutdown();
                System.exit(0);
            }
        }
    }
}
