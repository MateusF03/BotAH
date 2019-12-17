package com.mateus.botah.game;

public class Card {
    private CardType cardType;
    private String content;
    private Integer pick;
    public Card(CardType cardType) {
        this.cardType = cardType;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setPick(Integer pick) {
        this.pick = pick;
    }
    public int getPick() {
        return pick;
    }
}
