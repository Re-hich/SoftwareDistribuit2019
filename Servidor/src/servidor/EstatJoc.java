package servidor;

import java.util.ArrayList;
import java.util.Random;

public class EstatJoc {
    private int id;
    private int initBet;
    private int maxBet;
    private int actualBet;

    private ArrayList clientHand;
    private ArrayList serverHand;
    private int clientNum;
    private int serverNum;

    private char winner;

    private boolean showClient;
    private boolean showServer;

    


    private ArrayList cards;
    private Card card;

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
        this.initBet = 0;
        this.maxBet = 0;
        this.actualBet = 0;
        this.winner = 'N';
        this.clientHand = new ArrayList();
        this.serverHand = new ArrayList();

        initCards();




    }


    private void initCards() {
        this.cards = new ArrayList() {
            {
                add(new Card('A', (byte) 0x03));
                add(new Card('A', (byte) 0x04));
                add(new Card('A', (byte) 0x05));
                add(new Card('A', (byte) 0x06));

                add(new Card('K', (byte) 0x03));
                add(new Card('K', (byte) 0x04));
                add(new Card('K', (byte) 0x05));
                add(new Card('K', (byte) 0x06));

                add(new Card('Q', (byte) 0x03));
                add(new Card('Q', (byte) 0x04));
                add(new Card('Q', (byte) 0x05));
                add(new Card('Q', (byte) 0x06));

                add(new Card('J', (byte) 0x03));
                add(new Card('J', (byte) 0x04));
                add(new Card('J', (byte) 0x05));
                add(new Card('J', (byte) 0x06));

                add(new Card('X', (byte) 0x03));
                add(new Card('X', (byte) 0x04));
                add(new Card('X', (byte) 0x05));
                add(new Card('X', (byte) 0x06));

                add(new Card('9', (byte) 0x03));
                add(new Card('9', (byte) 0x04));
                add(new Card('9', (byte) 0x05));
                add(new Card('9', (byte) 0x06));

                add(new Card('8', (byte) 0x03));
                add(new Card('8', (byte) 0x04));
                add(new Card('8', (byte) 0x05));
                add(new Card('8', (byte) 0x06));

                add(new Card('7', (byte) 0x03));
                add(new Card('7', (byte) 0x04));
                add(new Card('7', (byte) 0x05));
                add(new Card('7', (byte) 0x06));

                add(new Card('6', (byte) 0x03));
                add(new Card('6', (byte) 0x04));
                add(new Card('6', (byte) 0x05));
                add(new Card('6', (byte) 0x06));

                add(new Card('5', (byte) 0x03));
                add(new Card('5', (byte) 0x04));
                add(new Card('5', (byte) 0x05));
                add(new Card('5', (byte) 0x06));

                add(new Card('4', (byte) 0x03));
                add(new Card('4', (byte) 0x04));
                add(new Card('4', (byte) 0x05));
                add(new Card('4', (byte) 0x06));

                add(new Card('3', (byte) 0x03));
                add(new Card('3', (byte) 0x04));
                add(new Card('3', (byte) 0x05));
                add(new Card('3', (byte) 0x06));

                add(new Card('2', (byte) 0x03));
                add(new Card('2', (byte) 0x04));
                add(new Card('2', (byte) 0x05));
                add(new Card('2', (byte) 0x06));
            }
        };
    }


    public Card getRandCard() {
        Random rn = new Random();
        int x = rn.nextInt(this.cards.size());
        card = (Card) this.cards.remove(x);
        return card;
    }

    public void setInitBet(int initBet){
        this.initBet = initBet;
    }

    public void setMaxBet(int maxBet){
        this.maxBet = maxBet;
    }

    public void addClientCard(Card card) {
        this.clientHand.add(card);
        this.clientNum = getSum(clientHand);
        if (this.clientNum > 21) {
            this.winner = '1';
        }
    }

    public void addServerCard(Card card) {
        this.serverHand.add(card);
        this.serverNum = getSum(serverHand);
        if (this.serverNum > 21) {
            this.winner = '0';
        }
    }

    public int getClientNum() {
        return this.clientNum;
    }

    public int getServerNum() {
        return this.serverNum;
    }

    public ArrayList getClientHand() {
        return this.clientHand;
    }

    public ArrayList getServerHand() {
        return this.serverHand;
    }

    public void doubleBet() {
        this.initBet = this.initBet * 2;
    }

    public void setWinner(char winner){
        this.winner = winner;
    }

    public char getWinner() {
        return this.winner;
    }

    public int getBet() {
        return this.actualBet;
    }




    private int getSum(ArrayList hand) {
        Card card;
        int sum = 0;
        int a = 0;

        for (int i = 0; i < hand.size(); i++) {
            card = (Card) hand.get(i);
            switch(card.rank) {
                case 'A':
                    a++;
                    sum = sum + 11;
                    break;

                case 'K':
                    sum = sum + 10;
                    break;

                case 'Q':
                    sum = sum + 10;
                    break;

                case 'J':
                    sum = sum + 10;
                    break;

                case 'X':
                    sum = sum + 10;
                    break;

                case '9':
                    sum = sum + 9;
                    break;

                case '8':
                    sum = sum + 8;
                    break;

                case '7':
                    sum = sum + 7;
                    break;

                case '6':
                    sum = sum + 6;
                    break;

                case '5':
                    sum = sum + 5;
                    break;

                case '4':
                    sum = sum + 4;
                    break;

                case '3':
                    sum = sum + 3;
                    break;

                case '2':
                    sum = sum + 2;
                    break;

            }
        }

        if (a == 1 && sum > 21) {
            sum = sum - 10;
            if (a == 2 && sum > 21) {
                sum = sum - 10;
                if (a == 3 && sum > 21) {
                    sum = sum - 10;
                    if (a == 4 && sum > 21) {
                        sum = sum - 10;
                    }
                }
            }
        }

        return sum;
    }

}



