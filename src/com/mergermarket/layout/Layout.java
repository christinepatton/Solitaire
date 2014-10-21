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
        topDrawStackIndex = 0;
    }

    /**
     * Accept a move, do some sanity checking on it, and change the game's
     * internal state accordingly.  Return a flag indicating whether the move
     * was successfully processed.
     *   @param move The move to make.
     *   @return true if the move is allowable and was executed, false otherwise.
     */
    public boolean processMove(final String move) throws InvalidGameStateException {
        if (!isAllowedMove(move)) {
            return false;
        }

        if (move.equals(NEW_GAME)) {
            initialise();
        }

        if (move.equals(TURN)) {
            // TODO: move the state of the draw stack.

        }

        // If we're here, it's a move card to column move.
        // TODO: find the relevant card in the relevant column.  Then move that slice of the
        // list to the other list.  Be careful, the discard piles are just single cards, not
        // lists!

        return true;
    }

    /**
     * Check to see if the given move can be parsed by the game.  (This is
     * distinct from checking whether the move is valid given the state of
     * the board.)  Allowed moves are "Q", "N", "T", and a valid card followed
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
                firstRow += discardPiles.get(String.valueOf(s));
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
