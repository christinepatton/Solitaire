package com.mergermarket.layout;

import com.mergermarket.card.Card;
import com.mergermarket.card.cardproperty.Suit;
import com.mergermarket.deck.Deck;
import com.mergermarket.exception.InvalidFaceValueException;
import com.mergermarket.exception.InvalidGameStateException;
import com.mergermarket.exception.InvalidSuitException;

import java.util.*;

/**
 * Class for holding the state of the solitaire game.
 */
public class Layout {

    /**
     * The deck of cards we're given to play the game with.
     */
    private Deck deck;

    /**
     * The draw stack, plus the index of the card currently at the top of said draw stack.
     */
    private List<Card> drawStack;
    private int topDrawStackIndex;

    /**
     * The columns of cards in play.
     */
    private List<List<Card>> columns;

    /**
     * The discard piles.  We actually only care about the most recent addition per suit.
     */
    private Map<String, Card> discardPiles;

    /**
     * How many columns can be in play?
     */
    private static final int NUM_COLUMNS = 7;

    /**
     * How many cards should we flip over if we're going through the draw stack?
     */
    private static final int NUM_CARDS_TO_TURN = 3;

    /**
     * The string to display for a card that's face down.
     */
    private static final String FACE_DOWN = "**";

    /** Other constants used in pretty-printing the state of the board.
     */
    private static final String FIRST_ROW_BUFFER = "                   ";
    private static final String FIRST_ROW_DRAW_STACK_BUFFER = "        ";
    private static final String LATER_ROW_BUFFER = "                             ";
    private static final String SPACE_BETWEEN_COLUMNS = "  ";
    private static final String BLANK_CARD = "  ";

    /** Valid non-card-value-containing moves.  The requirements seem to imply these are
     * case-sensitive, so I'm treating them as such.
     */
    private static final String NEW_GAME = "N";
    private static final String TURN = "T";

    /**
     * Constructor with argument.
     *   @param deck The deck of cards to use for the game.
     */
    public Layout(Deck deck) throws InvalidGameStateException {
        this.deck = deck;
        initialise();
    }

    /**
     * Set up the game's initial state before any moves have been played.
     */
    public void initialise() throws InvalidGameStateException {
        // Put everything in the draw stack to begin with.
        deck.shuffle();
        drawStack = new ArrayList<>();
        for (Card c : deck.getCards()) {
            drawStack.add(c);
        }

        // Make sure that the deck has the right number of cards for this game.
        int numCards = drawStack.size();
        if (numCards != 52) {
            throw new InvalidGameStateException("Can't start game with " + numCards + " cards, need 52");
        }

        // Then move cards from the draw stack into the initial configuration
        // of the columns.
        columns = new ArrayList<>();
        for (int i = 0; i < NUM_COLUMNS; i++) {
            List<Card> column = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                Card c = drawStack.remove(drawStack.size() - 1);
                if (i==j) {
                    c.setFaceUp(true);
                }
                else {
                    c.setFaceUp(false);
                }
                column.add(c);
            }
            columns.add(column);
        }

        // Set up empty discard piles and fresh draw stack.
        discardPiles = new HashMap<>();
        topDrawStackIndex = NUM_CARDS_TO_TURN - 1;
    }

    /**
     * Accept a move, do some sanity checking on it, and change the game's
     * internal state accordingly.  Return a flag indicating whether the move
     * was successfully processed.
     *   @param move The move to make.
     *   @return true if the move is allowable and was executed, false otherwise.
     */
    public boolean processMove(final String move) throws InvalidGameStateException, InvalidFaceValueException, InvalidSuitException {
        if (!isAllowedMove(move)) {
            return false;
        }

        if (move.equals(NEW_GAME)) {
            initialise();
            return true;
        }

        if (move.equals(TURN)) {
            turnDrawStack();
            return true;
        }

        // If we're here, it's a move card to column move.
        Card card = new Card(move.substring(0, 2));
        String column = move.substring(3);
        return moveColumn(card, column);
    }


    /**
     * Move the given card into the given column or discard stack.
     *   @return true if the state of the board has changed, false otherwise
     */
    private boolean moveColumn(final Card card, final String where) {

        // Figure out if the destination is a numbered column or a discard pile.
        boolean goingToDiscardPile = false;
        int column = 0;
        String suit = "";
        try {
            column = Integer.parseInt(where);
        }
        catch (NumberFormatException e) {
            suit = where;
            goingToDiscardPile = true;
        }

        // If the card is the top card in the draw stack, remove it from the draw
        // stack and put it in the destination.
        Card topDrawStack = drawStack.get(topDrawStackIndex);
        if (topDrawStack.equals(card)) {
            card.setFaceUp(true);
            drawStack.remove(card);

            // Reveal the previous card in the draw stack.  If there is no
            // previous card to reveal, do a turn-cards move automatically.
            topDrawStackIndex -= 1;
            if (topDrawStackIndex < 0) {
                turnDrawStack();
            }

            if (goingToDiscardPile) {
                discardPiles.put(suit, card);
            }
            else {
                columns.get(column - 1).add(card);
            }

            return true;
        }

        // The card isn't the top card in the draw stack, so it must be in
        // one of the numbered columns.
        for (List<Card> columnToSearch : columns) {
            for (Card cardToCompare : columnToSearch) {
                if (cardToCompare.isFaceUp() && cardToCompare.equals(card)) {

                    // Found it, now move it and everything below it.
                    int lastCardIndex = columnToSearch.size() - 1;
                    int indexFoundAt = columnToSearch.indexOf(cardToCompare);

                    if (goingToDiscardPile) {
                        // It doesn't make sense to move multiple cards from a column into the
                        // same discard pile, so don't do it.
                        if (lastCardIndex > indexFoundAt) {
                            return false;
                        }
                        else {
                            columnToSearch.remove(cardToCompare);
                            discardPiles.put(suit, cardToCompare);
                            return true;
                        }
                    }

                    for (int i = indexFoundAt; i <= lastCardIndex; i++) {
                        columns.get(column - 1).add(columnToSearch.get(indexFoundAt));
                        columnToSearch.remove(indexFoundAt);
                    }

                    // If there are any cards left in the source column, flip the last one face up.
                    int newColumnSize = columnToSearch.size();
                    if (newColumnSize > 0) {
                        columnToSearch.get(newColumnSize - 1).setFaceUp(true);
                    }
                    return true;
                }
            }
        }

        // If we're here, we haven't found the card we're supposed to move.
        return false;
    }

    /**
     * Advance the pointer to the top card of the draw stack.  Start from the beginning
     * if we're already at the end of the stack.
     */
    private void turnDrawStack() {

        int size = drawStack.size();
        int lastIndex = size - 1;

        // Check that there's something in the draw stack to turn.
        if (size == 0) {
            return;
        }

        // If we're at the very end of the stack, go back to the beginning
        // and proceed to turn over a batch of cards.  (The requirement around
        // this is a bit vague.  I'm interpreting "refresh the Stack from the
        // Waste pile" to mean go back to the beginning of the stack and then
        // turn over cards.)
        if (topDrawStackIndex == lastIndex) {
            topDrawStackIndex = -1;
        }

        // Can we turn over a batch of cards without running off the end?
        if (size > topDrawStackIndex + NUM_CARDS_TO_TURN) {
            topDrawStackIndex += NUM_CARDS_TO_TURN;
        }
        else {
            topDrawStackIndex = lastIndex;
        }
    }

    /**
     * Check to see if the given move can be parsed by the game.  (This is
     * distinct from checking whether the move is valid given the state of
     * the board.)  Allowed moves are "N", "T", and a valid card followed
     * by a valid column.
     *   @param move The move to check.
     *   @return true if the move is valid, false otherwise.
     */
    private boolean isAllowedMove(final String move) {
        return (move.equals(NEW_GAME) ||
                move.equals(TURN) ||
                isAllowedColumnMove(move));
    }

    /**
     * Check to see if the given move consists of a valid card followed
     * by a valid column or discard pile.
     *   @param move The move to check.
     *   @return true if the move is valid, false otherwise.
     */
    private boolean isAllowedColumnMove(final String move) {
        // We expect a two-character card value, a space, then a column number.
        String cardValue, column;
        try {
            cardValue = move.substring(0, 2);
            column = move.substring(3);
        }
        catch (StringIndexOutOfBoundsException e) {
            return false;
        }

        // Let the Card constructor tell us whether we've got a valid card.
        try {
            Card card = new Card(cardValue);
        }
        catch (InvalidSuitException | InvalidFaceValueException | StringIndexOutOfBoundsException e) {
            return false;
        }

        // Ask the Suit constructor whether the column we have is a discard pile.
        if (column.length() == 1) {
            try {
                Suit columnAsSuit = new Suit(column.charAt(0));
                return true;
            }
            catch (InvalidSuitException e) {
            }
        }

        // Ask the Integer constructor whether the column we have is a number,
        // then ask this class's internal state whether that column is valid.
        try {
            Integer columnAsInt = new Integer(column);
            return (columnAsInt >= 1 && columnAsInt <= columns.size());
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Output the game's current state.
     *  @return a list of Strings that can be printed by the caller to show the board.
     */
    public List<String> print() {
        String header = "ColumnNames   S[T]ack        ";
        for (int i = 1; i <= NUM_COLUMNS; i++) {
            header += "[" + i + "] ";
        }
        for (char s : Suit.getValidInputs()) {
            header += "[" + s + "] ";
        }

        String separator = "";
        for (int i = 0; i < header.length(); i++) {
            separator += "-";
        }

        // If there are any cards in the draw stack, get the top one.  Otherwise display blanks.
        // (The draw stack will be empty if all the cards are in the discard piles, i.e. if the game
        // is won.)
        String topDrawStackValue = drawStack.size() > 0 ?
                drawStack.get(topDrawStackIndex).getValue() :
                BLANK_CARD;

        // The first line of actual card data contains the draw stack and the discard piles.
        String firstRow = FIRST_ROW_BUFFER + topDrawStackValue + FIRST_ROW_DRAW_STACK_BUFFER;
        firstRow += getColumnsSlice(0);
        for (char s : Suit.getValidInputs()) {
            firstRow += " ";
            if (discardPiles.containsKey(String.valueOf(s))) {
                firstRow += discardPiles.get(String.valueOf(s)).getValue();
            }
            else {
                firstRow += BLANK_CARD;
            }
        }

        // Subsequent lines of card data don't need to worry about the draw stack or discard piles.
        List<String> moreRows = new ArrayList<>();
        int longestColumn = longestColumnLength();
        for (int i = 1; i < longestColumn; i++) {
            String thisRow = LATER_ROW_BUFFER;
            thisRow += getColumnsSlice(i);
            moreRows.add(thisRow);
        }

        List<String> output = new ArrayList<>();
        output.add(header);
        output.add(separator);
        output.add(firstRow);
        for (String row : moreRows) {
            output.add(row);
        }

        return output;
    }


    /**
     * Get and format the nth card in all the columns.
     *   @param index Which card in all the columns to get.
     *   @return A string with the card values (or blank if appropriate) formatted.
     */
    private String getColumnsSlice(final int index) {
        String result = "";

        for (int i = 0; i < NUM_COLUMNS; i++) {
            List<Card> column = columns.get(i);
            if (column.size() > index) {
                Card c = column.get(index);
                result += c.isFaceUp() ? c.getValue() : FACE_DOWN;
            }
            else {
                result += BLANK_CARD;
            }
            result += SPACE_BETWEEN_COLUMNS;
        }
        return result;
    }

    /**
     * Get the length of the longest column in the game.
     *   @return the length as an int.
     */
    private int longestColumnLength() {
        int max = 0;

        for (List<Card> column : columns) {
            int thisSize = column.size();
            if (thisSize > max) {
                max = thisSize;
            }
        }

        return max;
    }
}
