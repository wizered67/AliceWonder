package com.wizered67.game.conversations.commands;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.wizered67.game.conversations.scene.SceneCharacter;
import com.wizered67.game.conversations.CompleteEvent;
import com.wizered67.game.conversations.ConversationController;

import java.io.IOException;

/**
 * A ConversationCommand that changes an existing SceneCharacter's direction.
 * Mainly intended for debug use.
 * @author Adam Victor
 */
public class CharacterDirectionCommand implements ConversationCommand {
    /** Identifier of the SceneCharacter to change the direction of. */
    private String character;
    /** New direction for the SceneCharacter to face. */
    private int direction;

    /** No arguments constructor. */
    public CharacterDirectionCommand() {
        character = "";
        direction = 1;
    }
    /** Creates a new CharacterDirectionCommand that changes the SceneCharacter
     * ID's direction to DIR when executed.
     */
    public CharacterDirectionCommand(String id, int dir) {
        character = id;
        direction = dir;
    }
    /** Executes the command on the CONVERSATION CONTROLLER. */
    @Override
    public void execute(ConversationController conversationController) {
        SceneCharacter c = conversationController.sceneManager().getCharacterByIdentifier(character);
        if (c == null) {
            return;
        }
        c.setDirection(direction);
    }
    /** Whether to wait before proceeding to the next command in the branch. */
    @Override
    public boolean waitToProceed() {
        return false;
    }
    /** Checks whether the CompleteEvent C completes this command,
     * and if so acts accordingly. */
    @Override
    public void complete(CompleteEvent c) {

    }
    /** Outputs XML to the XML WRITER for this command. */
    @Override
    public void writeXml(XmlWriter xmlWriter) {
        try {
            xmlWriter.element("setdirection")
                    .attribute("name", character)
                    .attribute("direction", direction)
                    .pop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /** Static method to create a new command from XML Element ELEMENT. */
    public static CharacterDirectionCommand makeCommand(XmlReader.Element element) {
        String name = element.getAttribute("id");
        int direction = element.getInt("direction", 1);
        return new CharacterDirectionCommand(name, direction);
    }
}
