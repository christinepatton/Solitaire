package com.mergermarket.layout;

import com.mergermarket.card.Card;
import com.mergermarket.deck.Deck;
import com.mergermarket.exception.InvalidFaceValueException;
import com.mergermarket.exception.InvalidGameStateException;
import com.mergermarket.exception.InvalidSuitException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

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
    public void testProcessMoveWithDisallowedInput() throws InvalidGameStateException, InvalidFaceValueException, InvalidSuitException {
        assertFalse(layout.processMove(""));
        assertFalse(layout.processMove("F"));
        assertFalse(layout.processMove("SomeTooLongString"));
        assertFalse(layout.processMove("c2 X"));
        assertFalse(layout.processMove("X2 c"));
        assertFalse(layout.processMove("c2 0"));
        assertFalse(layout.processMove("c2 8"));
        assertFalse(layout.processMove("c2 10"));
    }

    @Test
    /** Test that starting a new game causes the cards to be re-shuffled.
     */
    public void testStartingNewGame() throws InvalidGameStateException, InvalidFaceValueException, InvalidSuitException {
        List<String> firstState = layout.print();

        assertTrue(layout.processMove("N"));

        List<String> secondState = layout.print();

        for (int i = 0; i < firstState.size(); i++) {
            if (!(firstState.get(i).equals(secondState.get(i)))) {
                return;
            }
        }

        fail("Game state didn't change when new game started");
    }

    @Test
    /** Test turning over cards with the draw stack in various states.
     */
    public void testTurningDrawStack() throws InvalidFaceValueException, InvalidSuitException, InvalidGameStateException {
        Layout layout = createUnshuffledLayout();

        // Test basic card turning, hitting the end of the draw stack, plus
        // going back to the beginning when we hit the end.
        String[] values = {"D6", "D9", "DQ", "H2", "H5", "H8", "HJ", "D3"};
        for (String s : values) {
            assertEquals(true, layout.processMove("T"));
            List<String> state = layout.print();
            assertEquals(s, getTopDrawStackCard(state));
        }
    }

    @Test
    /** Test moving cards in various ways:
     *   - from draw stack to a column
     *   - from draw stack to a discard pile
     *   - from column to another column
     *   - from column to discard pile
     */
    public void testMoveCard() throws InvalidFaceValueException, InvalidSuitException, InvalidGameStateException {
        Layout layout = createUnshuffledLayout();

        // NB: This test generates invalid moves, which is fine for now, but
        // the test will need refactoring when and if we add move validity checking.

        // Move 3 cards (sequentially) from the top of the draw stack to column 1.  Check that they
        // stack correctly and that the draw stack continues to display correctly (including
        // turning over 3 more cards automatically when the stack becomes empty).
        assertTrue(layout.processMove("D3 1"));
        List<String> state = layout.print();
        assertTrue(state.get(2).contains("  D2  "));
        assertTrue(state.get(3).contains("D3  sJ  **  **  **  **  **"));

        assertTrue(layout.processMove("D2 1"));
        state = layout.print();
        assertTrue(state.get(2).contains("  DA  "));
        assertTrue(state.get(3).contains("D3  sJ  **  **  **  **  **"));
        assertTrue(state.get(4).contains("D2      s8  **  **  **  **"));

        assertTrue(layout.processMove("DA 1"));
        state = layout.print();
        assertTrue(state.get(2).contains("  D6  "));
        assertTrue(state.get(3).contains("D3  sJ  **  **  **  **  **"));
        assertTrue(state.get(4).contains("D2      s8  **  **  **  **"));
        assertTrue(state.get(5).contains("DA          s4  **  **  **"));

        // Move a card from the top of the draw stack to a discard pile.
        assertTrue(layout.processMove("D6 H"));
        state = layout.print();
        assertTrue(state.get(2).contains("sK  **  **  **  **  **  **      D6"));

        // Move 2 cards (sequentially) from a column to a discard pile.
        assertTrue(layout.processMove("DA D"));
        state = layout.print();
        assertTrue(state.get(2).contains("sK  **  **  **  **  **  **   DA D6"));
        assertFalse(state.get(5).contains("DA"));

        assertTrue(layout.processMove("D2 D"));
        state = layout.print();
        assertTrue(state.get(2).contains("sK  **  **  **  **  **  **   D2 D6"));

        // Move all the cards from one column to another, resulting in an empty column.
        assertTrue(layout.processMove("sK 2"));
        state = layout.print();
        assertTrue(state.get(2).contains("   **  **  **  **  **  **   D2 D6"));
        assertTrue(state.get(4).contains("   sK  s8  **  **  **  **"));
        assertTrue(state.get(5).contains("   D3      s4  **  **  **"));

        // Move all the face-up cards from one column to another.  Make sure the
        // last card in the source column gets turned face up.
        assertTrue(layout.processMove("sJ 3"));
        state = layout.print();
        assertTrue(state.get(2).contains("   sQ  **  **  **  **  **"));

        // Move some of the face-up cards from one column to another.
        assertTrue(layout.processMove("sJ 7"));
        state = layout.print();
        assertTrue(state.get(4).contains("s8  **  **  **  **"));
        assertTrue(state.get(8).contains("HQ"));
        assertTrue(state.get(9).contains("sJ"));
        assertTrue(state.get(10).contains("sK"));
        assertTrue(state.get(11).contains("D3"));

        // Try to move a column of cards to a discard pile.  The move should
        // come back as unprocessed.
        assertFalse(layout.processMove("HQ c"));

        // Try to move a card that's not face up.  The move should come back
        // as unprocessed.
        assertFalse(layout.processMove("c9 1"));
    }


    /** Given the result of calling print, return the card on the top of the draw stack.
     */
    private String getTopDrawStackCard(final List<String> state) {
        return state.get(2).substring(19,21);
    }

    /** Start a new game with our mock, unshuffled deck.
     */
    private Layout createUnshuffledLayout() throws InvalidFaceValueException, InvalidSuitException, InvalidGameStateException {
        Deck deck = createUnshufflableDeck();
        return new Layout(deck);
    }

    /** Create a mock deck that always has the cards in strictly ascending order and for which
     * shuffling is a no-op.
     */
    private Deck createUnshufflableDeck() throws InvalidFaceValueException, InvalidSuitException {
        List<Card> cards = new ArrayList<>();

        char[] suits = { 'D', 'H', 'c', 's' };
        char[] faceValues = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

        for (char suit : suits) {
            for (char faceValue : faceValues) {
                cards.add(new Card(suit, faceValue));
            }
        }

        Deck deck = mock(Deck.class);
        when(deck.getCards()).thenReturn(cards);
        Mockito.doNothing().when(deck).shuffle();

        return deck;
    }

}