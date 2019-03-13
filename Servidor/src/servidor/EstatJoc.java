package servidor;

import java.util.ArrayList;
import java.util.Random;

public class EstatJoc {
    private int id;
    private int initBet;


    private ArrayList cards;

    public class Card {
        public char rank;
        public byte suit;

        public Card(char rank, byte suit) {
            this.rank = rank;
            this.suit = suit;
        }
    }

    public EstatJoc(int id){
        this.id = id;

        initCards();

        
    }


    private void initCards() {
        this.cards = new ArrayList<Card>() {
            {
                add(new Card('A', (byte) 0x03));
                add(new Card('A', (byte) 0x04));
                add(new Card('A', (byte) 0x05));
                add(new Card('A', (byte) 0x06));
            }
        };
    }


    public Object getRandCard() {
        Random rn = new Random();
        int x = rn.nextInt(cards.size());

        return this.cards.remove(x);
    }

}



