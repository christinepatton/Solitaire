package com.mergermarket.layout;

import com.mergermarket.card.Card;
import com.mergermarket.deck.Deck;
import com.mergermarket.exception.InvalidFaceValueException;
import com.mergermarket.exception.InvalidGameStateException;
import com.mergermarket.exception.InvalidSuitException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LayoutTest {

    private static Deck deck;
    private static Set<String> cardValuesInDeck;

    private Layout layout;

    @BeforeClass
    /** Set up the deck of cards and, for convenience, put all the cards into a Set that
     *  we can then use as a lookup table for checking if a card is a 'real' card.
     */
    public static void setUp() throws InvalidSuitException, InvalidFaceValueException {
        deck  = new Deck();
        cardValuesInDeck = new HashSet<>();

        for (Card c : deck.getCards()) {
            cardValuesInDeck.add(c.getValue());
        }
    }

    @Before
    public void before() throws InvalidGameStateException {
        layout = new Layout(deck);
    }

    @Test
     /** Test that the constructor sets up the initial state of the game correctly.
      *  This is a bit tricky because of:
      *   - the randomness element of shuffling the cards
      *   - the fact that the class's contract with the outside world is just via
      *     text output (we could use reflection to examine the class's internal state,
      *     but I'm not a big fan of that as we're then testing what the code is rather
      *     than what the code does -- which in my experience makes the tests hard to
      *     maintain)
     */
    public void testInitialLayout() throws InvalidGameStateException {
        List<String> initialState = layout.print();

        assertEquals(9, initialState.size());
        assertTrue(initialState.get(0).
                contains("ColumnNames   S[T]ack        [1] [2] [3] [4] [5] [6] [7] [D] [H] [c] [s]"));

        // I'm not going to make assertions about the row of dashes as I don't think it's important
        // and I don't want to make this test brittler than it already is.

        // The first row of cards should contain six (and not seven) face down cards.
        assertTrue(initialState.get(2).contains("**  **  **  **  **  **"));
        assertFalse(initialState.get(2).contains("** **  **  **  **  **  **"));

        // The first card in the first row should be a valid card.
        String firstCardFirstRow = initialState.get(2).substring(29,31);
        assertTrue(cardValuesInDeck.contains(firstCardFirstRow));

        // The second row of cards should contain five (and not six) face down cards.
        assertTrue(initialState.get(3).contains("**  **  **  **  **"));
        assertFalse(initialState.get(3).contains("**  **  **  **  **  **"));

        // The first card in the second row should be a valid card.
        String firstCardSecondRow = initialState.get(3).substring(33,35);
        assertTrue(cardValuesInDeck.contains(firstCardSecondRow));


        // The last row of cards should contain no face down cards.
        assertFalse(initialState.get(8).contains("**"));

        // The first card in the last row should be a valid card.
        String firstCardLastRow = initialState.get(8).substring(53,55);
        assertTrue(cardValuesInDeck.contains(firstCardLastRow));
    }

    @Test(expected = com.mergermarket.exception.InvalidGameStateException.class)
    /** Test that the game won't start if the deck doesn't meet the requirements for this game.
     */
    public void testWrongNumberOfCardsInDeck() throws InvalidFaceValueException, InvalidSuitException, InvalidGameStateException {
        // Set up a mock deck with only 1 card in it.
        Card card = new Card('c', 'A');
        List<Card> badCards = new ArrayList<>();
        badCards.add(card);

        Deck badDeck = mock(Deck.class);
        when(badDeck.getCards()).thenReturn(badCards);

        Layout layout = new Layout(badDeck);
    }

    @Test
    /** Test that processMove returns false if the given move is not allowed (not to be confused
     * with invalid due to the state of the game).
     */
    public void testProcessMoveWithDisallowedInput() throws InvalidGameStateException {
        assertEquals(false, layout.processMove(""));
        assertEquals(false, layout.processMove("F"));
        assertEquals(false, layout.processMove("SomeTooLongString"));
        assertEquals(false, layout.processMove("c2 X"));
        assertEquals(false, layout.processMove("X2 c"));
        assertEquals(false, layout.processMove("c2 0"));
        assertEquals(false, layout.processMove("c2 8"));
        assertEquals(false, layout.processMove("c2 10"));

        assertEquals(true, layout.processMove("c2 c"));
        assertEquals(true, layout.processMove("c2 1"));
        assertEquals(true, layout.processMove("c2 7"));
    }

    @Test
    /** Test that starting a new game causes the cards to be re-shuffled.
     */
    public void testStartingNewGame() throws InvalidGameStateException {
        List<String> firstState = layout.print();

        assertEquals(true, layout.processMove("N"));

        List<String> secondState = layout.print();

        for (int i = 0; i < firstState.size(); i++) {
            if (!(firstState.get(i).equals(secondState.get(i)))) {
                return;
            }
        }

        fail("Game state didn't change when new game started");
    }

}