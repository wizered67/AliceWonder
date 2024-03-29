package com.wizered67.game.conversations;

import com.wizered67.game.conversations.scene.SceneManager;

/** Contains information to be passed to the current ConversationCommand
 * about an event that has occurred that could lead to its completion.
 * Contains a TYPE enum and some DATA, if extra information is necessary,
 * such as what choice a player selected.
 * @author Adam Victor
 */
public class CompleteEvent {
    /** The data specific to this CompleteEvent, providing additional information. */
    public Object data;
    /** The type of event that has occurred. */
    public Type type;

    /** A CompleteEvent for when input is given. */
    private static final CompleteEvent INPUT_COMPLETE_EVENT = new CompleteEvent(Type.INPUT);
    /** A CompleteEvent for when a choice is made. */
    private static final CompleteEvent CHOICE_COMPLETE_EVENT = new CompleteEvent(Type.CHOICE);
    /** A CompleteEvent for when an animation ends. */
    private static final CompleteEvent ANIMATION_END_EVENT = new CompleteEvent(Type.ANIMATION_END);
    /** A CompleteEvent for when fading ends. */
    private static final CompleteEvent FADE_END_EVENT = new CompleteEvent(Type.FADE_END);
    /** A CompleteEvent for when all current text has been displayed. */
    private static final CompleteEvent TEXT_END_EVENT = new CompleteEvent(Type.TEXT);
    /** A CompleteEvent for when a position interpolation ends. */
    private static final CompleteEvent POSITION_INTERPOLATION_END_EVENT = new CompleteEvent(Type.POSITION_INTERPOLATION);

    private static final CompleteEvent PRESENT_EVENT = new CompleteEvent(Type.PRESENT);

    public CompleteEvent(Type t) {
        this(t, null);
    }

    public CompleteEvent(Type t, Object d) {
        type = t;
        data = d;
    }

    public static CompleteEvent input() {
        return INPUT_COMPLETE_EVENT;
    }

    public static CompleteEvent choice() {
        return CHOICE_COMPLETE_EVENT;
    }

    public static CompleteEvent animationEnd(String name) {
        ANIMATION_END_EVENT.data = name;
        return ANIMATION_END_EVENT;
    }

    public static CompleteEvent fade(SceneManager manager, Object entity) {
        FADE_END_EVENT.data = new Object[] { manager, entity };
        return FADE_END_EVENT;
    }

    public static CompleteEvent text() {
        return TEXT_END_EVENT;
    }

    public static CompleteEvent positionInterpolation(SceneManager manager, Object entity) {
        POSITION_INTERPOLATION_END_EVENT.data = new Object[] { manager, entity };
        return POSITION_INTERPOLATION_END_EVENT;
    }

    public static CompleteEvent present(int id) {
        PRESENT_EVENT.data = id;
        return PRESENT_EVENT;
    }

    public enum Type {
        INPUT, CHOICE, ANIMATION_END, FADE_END, TEXT, POSITION_INTERPOLATION, PRESENT
    }
}
