package com.mateus.botah.game;

import com.mateus.botah.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class CardManager {
    private List<Card> blackCardList = new ArrayList<>();
    private List<Card> whiteCardList = new ArrayList<>();
    public Map<Game, TextChannel> games = new HashMap<>();
    private static CardManager instance;
    public static CardManager get() {
        if (instance == null) {
            instance = new CardManager();
        }
        return instance;
    }
    public void loadCards() {
        File databaseFolder = new File(System.getProperty("user.dir") + "/cards");
        if (!databaseFolder.exists()) databaseFolder.mkdirs();
        File databaseFile = new File(databaseFolder, "cardsfile.json");
        if (!databaseFile.exists()) {
            System.out.println("Database file does not exist");
            Main.jda.shutdown();
            System.exit(0);
        } else {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(databaseFile)) {
                Object obj = jsonParser.parse(reader);
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(obj);
                registerCard(jsonArray);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    public Card getRandomCard(CardType cardType) {
        Random r = new Random();
        switch (cardType) {
            case BLACK:
                Card b = blackCardList.get(r.nextInt(blackCardList.size()));
                return b;
            case WHITE:
                Card w = whiteCardList.get(r.nextInt(whiteCardList.size()));
                return w;
        }
        return null;
    }
    private void registerCard(JSONArray jsonArray) {
        long start = System.currentTimeMillis();
        int loaded = 0;
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        JSONArray bla = (JSONArray) jsonObject.get("blackCards");
        JSONArray whi = (JSONArray) jsonObject.get("whiteCards");
        for (int i=0; i<bla.size(); i++) {
            JSONObject card = (JSONObject) bla.get(i);
            Card c = new Card(CardType.BLACK);
            c.setContent((String) card.get("text"));
            c.setPick(Math.toIntExact((Long) card.get("pick")));
            blackCardList.add(c);
            loaded++;
        }
        for (int i=0; i<whi.size(); i++) {
            String card = (String) whi.get(i);
            Card c = new Card(CardType.WHITE);
            c.setContent(card);
            whiteCardList.add(c);
            loaded++;
        }
        long timeLoading = System.currentTimeMillis() - start;
        System.out.println("Loaded " + loaded + " cards in " + timeLoading + "ms" );
    }
    public void startGame(List<User> players, TextChannel textChannel) {
        Game game = new Game(players, textChannel);
        games.put(game, textChannel);
        game.start();
    }
    public Game getPlayerGame(User player) {
        for (Map.Entry<Game, TextChannel> entry :games.entrySet()) {
            if (entry.getKey().containsPlayer(player)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public List<Card> generateWhiteCards(Game game) {
        List<Card> whiteCards = new ArrayList<>();
        for (int i = 0; i<game.getPlayers().size()*5; i++) {
            Card card = getRandomCard(CardType.WHITE);
            if (whiteCards.contains(card)) continue;
            whiteCards.add(card);
        }
        return whiteCards;
    }
    public void removeGame(Game game) {
        games.remove(game);
    }
}
