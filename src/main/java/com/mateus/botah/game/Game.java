package com.mateus.botah.game;

import com.mateus.botah.Main;
import com.mateus.botah.event.CsarEvent;
import com.mateus.botah.event.QueueEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.javatuples.Triplet;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Game {
    private List<User> players;
    private User csar;
    private int turn;
    public Card blackCard;
    public Map<User, Triplet<Card, Card, Card>> chosenCard = new HashMap<>();
    private TextChannel textChannel;
    private Map<User, Integer> score = new HashMap<>();
    public Map<User, List<Card>> wc = new HashMap<>();
    public List<User> chosen = new ArrayList<>();
    public Game(List<User> players, TextChannel textChannel) {
        this.players = players;
        this.textChannel = textChannel;
    }
    public void setCsar(User csar) {
        if (this.csar != null) {
            this.csar = null;
        }
        this.csar = csar;
    }
    public User getCsar() {
        return csar;
    }
    public void addPlayer(User player) {
        players.add(player);
    }
    public void removePlayer(User player) {
        players.remove(player);
    }
    public List<User> getPlayers() {
        return players;
    }
    public boolean containsPlayer(User player) {
        return players.contains(player);
    }
    public boolean isCsar(User player) {
        return csar.getId().equals(player.getId());
    }
    public void start() {
        if (players.size()<3) {
            textChannel.sendMessage("**Não tem jogadores suficientes, o mínimo é 3**").queue();
        } else {
            turn = 0;
            Collections.shuffle(players);
            setCsar(players.get(0));
            blackCard = CardManager.get().getRandomCard(CardType.BLACK);
            nextTurn();
        }
    }
    public void nextTurn() {
        if (turn>0) {
            Collections.shuffle(players);
            setCsar(players.get(0));
            blackCard = CardManager.get().getRandomCard(CardType.BLACK);
        }
        turn++;
        for(Map.Entry<User, Integer> entry: score.entrySet()) {
            if (entry.getValue() == 3) {
                win(entry.getKey());
                players.clear();
                CardManager.get().removeGame(this);
                break;
            }
        }
        List<Card> whiteCards = CardManager.get().generateWhiteCards(this);
        for (User p: players) {
            if (isCsar(p)) {
                p.openPrivateChannel().queue(c-> c.sendMessage("Você é o csar sua carta preta é: " + blackCard.getContent()).queue());
            } else {
                List<Card> pc = new ArrayList<>();
                for (int i = 0; i<5;i++) {
                    pc.add(whiteCards.get(i));
                    whiteCards.remove(i);
                    wc.put(p, pc);
                }
                p.openPrivateChannel().queue(c-> {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.RED);
                    eb.setTitle("Você é um jogador, aqui estão suas cartas, você deve jogar " + blackCard.getPick() + " delas, A Frase é ***" + blackCard.getContent().replaceAll("<br/>", "\n") + "***" );

                    /*c.sendMessage("Você é um jogador, aqui estão suas cartas, você deve jogar " + blackCard.getPick() + " delas").queue();
                    c.sendMessage("A Frase é: " + blackCard.getContent()).queue();*/
                    for (int i = 0; i<5; i++) {
                        eb.addField(i+1 + ".", pc.get(i).getContent(), false);
                    }
                    c.sendMessage(eb.build()).queue();
                });
                Main.jda.addEventListener(new QueueEvent(p.openPrivateChannel().complete(), p, pc));
            }
        }
    }
    public void addChosenCards(User player, Card card1, Card card2, Card card3) {
        chosenCard.put(player, new Triplet<>(card1, card2, card3));
    }
    private void win(User player) {
        textChannel.sendMessage("***O JOGADOR " + player.getName().toUpperCase() + " VENCEU!!!!!! YEE YAHHH").queue();
    }
    public TextChannel getTextChannel() {
        return textChannel;
    }
    public void addPoint(User user) {
        if (score.containsKey(user)) {
            score.put(user, score.get(user) + 1);
        } else {
            score.put(user, 1);
        }
        textChannel.sendMessage("***" + user.getName() + " ganhou um ponto!").queue();
    }
    public void vote() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Frase: " + blackCard.getContent());
        for (Map.Entry<User, Triplet<Card, Card, Card>> cards: chosenCard.entrySet()) {
            if (cards.getValue().getValue1()== null) {
                eb.addField(cards.getKey().getName(), cards.getValue().getValue0().getContent(), true);
            } else if (cards.getValue().getValue2()== null) {
                eb.addField(cards.getKey().getName(), cards.getValue().getValue0().getContent(), true);
                eb.addField(cards.getKey().getName(), cards.getValue().getValue1().getContent(), true);
            } else {
                eb.addField(cards.getKey().getName(), cards.getValue().getValue0().getContent() + ", " + cards.getValue().getValue1() + ", " + cards.getValue().getValue2(), true);
            }
        }
        eb.setFooter("Escolha um usúario csar");
        eb.setColor(Color.RED);
        textChannel.sendMessage(eb.build()).queue();
        Main.jda.addEventListener(new CsarEvent(this));
    }
}
