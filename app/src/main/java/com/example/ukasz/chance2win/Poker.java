package com.example.ukasz.chance2win;

/**
 * Created by Łukasz on 04.04.2017.
 */

public class Poker {

    private Deck.Cards[] cards;
    private int handType;
    private Deck.Cards.Rank card = null, card2 = null;


    Poker(int[] cards) {
        this.cards = new Deck.Cards[cards.length];
        for(int i=0; i < cards.length; i++) {
            this.cards[i] = App.deck.getCardByDrawable(cards[i]);
        }

        handType = findHandType();

    }

    private Deck.Cards.Rank findStraight() {
        int n = cards.length;
        do {
            for(int i=0; i < n-1; i++) {
                if(cards[i].getRank().getValue() < cards[i+1].getRank().getValue()) {
                    Deck.Cards card = cards[i];
                    cards[i] = cards[i+1];
                    cards[i+1] = card;
                }
            }
            n--;
        } while( n > 1 );

        int maxSeries = 1;
        int currentSeries = 1;
        Deck.Cards.Rank firstElement = null;

        for(int i=0; i < cards.length-1; i++) {
            if(currentSeries < 5) {
                if ((cards[i].getRank().getValue() - 1) == cards[i+1].getRank().getValue()) {
                    if(currentSeries == 1) {
                        firstElement = cards[i].getRank();
                    }
                    currentSeries++;

                } else {
                    if (maxSeries < currentSeries) {
                        maxSeries = currentSeries;
                    }
                    currentSeries = 1;
                }
            } else {
                break;
            }
        }

        if(maxSeries < currentSeries) {
            maxSeries = currentSeries;
        }

        if(maxSeries == 5) {
            return firstElement;
        } else {
            return null;
        }
    }

    private int findHandType() {
        int clubs = 0, spades = 0, diamonds = 0, hearts = 0;

        for(int i=0; i < cards.length; i++) {
            switch(cards[i].getColor()) {
                case CLUBS:
                    clubs++;
                    break;
                case SPADES:
                    spades++;
                    break;
                case DIAMONDS:
                    diamonds++;
                    break;
                case HEARTS:
                    hearts++;
                    break;
            }
        }

        boolean flush = false;

        if(clubs >= 5 || spades >= 5 || diamonds >= 5 || hearts >= 5) {

            Deck.Cards.Rank status = findStraight();
            card = status;
            if(status != null) {
                Deck.Cards.Color color = null;
                Boolean thereIs = true;
                for(int i=0; i < 5; i++) {
                    for(int l=0; l < cards.length; l++) {
                        if(cards[l].getRank().getValue() == (status.getValue() - i)) {
                            if(i == 0) {
                                color = cards[l].getColor();
                            } else {
                                if(cards[l].getColor() != color) {
                                    thereIs = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(thereIs) {
                    return 8;
                }
            } else {
                flush = true;
            }

        }

        // 4 of hands
        int max = 0;
        Deck.Cards.Rank currentRank = null;
        for(int i=0; i < cards.length; i++) {

            for(int l=0; l < cards.length; l++) {
                if(i != l) {
                    if (cards[i].getRank() == cards[l].getRank()) {
                        int newMax = howMatch(cards[i].getRank());
                        if(max < newMax) {
                            max = newMax;
                            currentRank = cards[i].getRank();
                        } else {
                            if(max == newMax && currentRank.getValue() < cards[i].getRank().getValue()) {
                                currentRank = cards[i].getRank();
                            }
                        }
                    }
                }
            }

        }

        if(max == 4) {
            card = currentRank;
            return 7;
        } else {
            if(max == 3) {

                // full house
                for(int i=0; i < cards.length; i++) {
                    for(int l=0; l < cards.length; l++) {
                        if(cards[i].getRank() == cards[l].getRank() && currentRank != cards[i].getRank() && i != l) {
                            card = currentRank;
                            card2 = cards[i].getRank();
                            return 6;
                        }
                    }
                }

            }

            // flush
            if(flush) {
                return 5;
            }

            // straight
            Deck.Cards.Rank status = findStraight();
            card = status;
            if(status != null) {
                return 4;

            } else {

                // 3 of kind
                if(max == 3) {
                    card = currentRank;
                    return 3;
                }

                // 2 pairs or 1 pair
                if(max == 2) {
                    // 2 pair
                    card = currentRank;
                    for(int i=0; i < cards.length; i++) {
                        for(int l=0; l < cards.length; l++) {
                            if(cards[i].getRank() == cards[l].getRank() && currentRank != cards[i].getRank() && i != l) {
                                card2 = cards[i].getRank();
                                return 2;
                            }
                        }
                    }
                    // 1 pair
                    return 1;
                }


            }

        }

        // high card
        Deck.Cards.Rank highCard = cards[0].getRank();
        for(int i = 1; i < cards.length; i++) {
            if(cards[i].getRank().getValue() > highCard.getValue()) {
                highCard = cards[i].getRank();
            }
        }
        card = highCard;
        return 0;
    }

    private int howMatch(Deck.Cards.Rank rank) {
        int howMatch = 0;
        for(int i=0; i < cards.length; i++) {
            if(rank == cards[i].getRank()) {
                howMatch++;
            }
        }
        return howMatch;
    }

    private String getCard(Deck.Cards.Rank rank) {
        switch (rank) {
            case ACE:
                return App.getContext().getResources().getString(R.string.ace);

            case KING:
                return App.getContext().getResources().getString(R.string.king);

            case QUEEN:
                return App.getContext().getResources().getString(R.string.queen);

            case JACK:
                return App.getContext().getResources().getString(R.string.jack);

            default:
                 int card = rank.getValue()+1;
                 return Integer.toString(card);
        }
    }

    // compare current poker with other
    // returns 1 when current poker is higher than given in funct
    // return 2 - poker hands are equal
    public Integer compare(Poker poker) {
        if(handType > poker.handType) {
            return 1;
        } else {
            if(handType == poker.handType) {
                if(card != null && poker.card != null) {
                    if (card.getValue() == poker.card.getValue()) {
                        if(card2 != null && poker.card2 != null) {
                            if (card2.getValue() == poker.card2.getValue()) {
                                return 2;
                            } else {
                                if (card2.getValue() > poker.card2.getValue()) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            return 2;
                        }
                    } else {
                        if (card.getValue() > poker.card.getValue()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                } else {
                    return 2;
                }
            } else {
                if(handType < poker.handType) {
                    return 0;
                }
            }
        }

        return null;
    }

    public String getHandType() {
        String text = "";
        switch(handType) {

            case 8:
                if(card.getValue() == 13) {
                    text = App.getContext().getResources().getString(R.string.royalStraightFlush);
                } else {
                    text = App.getContext().getResources().getString(R.string.StraightFlush);
                }
                break;

            case 7:
                text = App.getContext().getResources().getString(R.string.FourOfKind);
                break;

            case 6:
                text = App.getContext().getResources().getString(R.string.FullHouse);
                break;

            case 5:
                text = App.getContext().getResources().getString(R.string.Flush);
                break;

            case 4:
                text = App.getContext().getResources().getString(R.string.Straight);
                break;

            case 3:
                text = App.getContext().getResources().getString(R.string.ThreeOfKind);
                break;

            case 2:
                text = App.getContext().getResources().getString(R.string.TwoPair);
                break;

            case 1:
                text = App.getContext().getResources().getString(R.string.Pair);
                break;

            case 0:
                text = App.getContext().getResources().getString(R.string.HighCard);

        }

        if(handType != 2 && handType != 6) {
            if(handType != 5) {
                text = text.concat(" (" + getCard(card) + ")");
            }
        } else {
            text = text.concat(" (" + getCard(card) + ", " + getCard(card2) +")");
        }

        return text;

    }

}
