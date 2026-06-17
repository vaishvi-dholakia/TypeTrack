package com.typetrack.engine;

import com.typetrack.model.Result;
//After the test ends, we must return a Result object.
import com.typetrack.model.User;
//Who is taking the test?

/**
 * Manages the lifecycle (Start ->Running -> Finish-> Generate Result)of a
 * single typing test session for a user.
 */
public class TypingTest {
    private final User user;
    private final String targetText;
    private final TypingEngine engine;

    public TypingTest(User user, String targetText) {
        this.user = user;
        this.targetText = targetText;
        this.engine = new TypingEngine(targetText);
        // HAS-A relationship : TypingTest HAS A TypingEngine.
    }

    // Starts the test session.
    public void start() {
        engine.start();
    }

    /**
     * Completes the test session and returns a computed Result.
     * parameter - typedText : It is the text typed by the user.
     * It returns the typing metrics encapsulated in a Result object.
     */
    public Result finish(String typedText) {
        engine.stop();
        engine.calculateResults(typedText);

        return new Result(
                engine.getTimeTakenSeconds(),
                engine.getMistakes(),
                engine.getAccuracy(),
                engine.getWpm(typedText),
                engine.getTypoFrequency());
    }

    public User getUser() {
        return user;
    }

    public String getTargetText() {
        return targetText;
    }
}
