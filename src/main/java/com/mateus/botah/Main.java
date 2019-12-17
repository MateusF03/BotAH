package com.mateus.botah;

import com.mateus.botah.event.GameEvent;
import com.mateus.botah.game.CardManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;
    public static void main(String[] args) {
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(Dotenv.load().get("DISCORD_TOKEN")).build();
            CardManager.get().loadCards();
            jda.addEventListener(new GameEvent());
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
