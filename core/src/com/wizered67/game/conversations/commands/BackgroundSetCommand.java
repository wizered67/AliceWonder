package com.wizered67.game.conversations.commands;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.wizered67.game.conversations.CompleteEvent;
import com.wizered67.game.conversations.ConversationController;
import com.wizered67.game.conversations.scene.SceneImage;
import com.wizered67.game.conversations.scene.SceneManager;

/**
 * Command to set the scene's background image.
 * @author Adam Victor
 */
public class BackgroundSetCommand implements ConversationCommand {
    private static final String BACKGROUND_IDENTIFIER = "bg";
    /** Identifier of the Texture to set as the background. */
    private String imageIdentifier;

    /** Creates a BackgroundSetCommand that sets background to the texture with identifier ID. */
    public BackgroundSetCommand(String id) {
        imageIdentifier = id;
    }
    /**
     * Executes the command on the CONVERSATION CONTROLLER.
     */
    @Override
    public void execute(ConversationController conversationController) {
        SceneManager manager = conversationController.sceneManager();
        SceneImage bgImage = manager.getImage(BACKGROUND_IDENTIFIER);
        if (bgImage == null) {
            bgImage = new SceneImage(BACKGROUND_IDENTIFIER);
            manager.addImage(bgImage);
            bgImage.addToScene(manager);
        }
        bgImage.setPosition(0, 0);
        bgImage.setDepth(manager, -99999);
        bgImage.finishVisibility(true);
        bgImage.setTexture(imageIdentifier);
    }
    /**
     * Whether to wait before proceeding to the next command in the branch. */
    @Override
    public boolean waitToProceed() {
        return false;
    }
    /**
     * Checks whether the CompleteEvent C completes this command,
     * and if so acts accordingly. */
    @Override
    public void complete(CompleteEvent c) {

    }
    /** Static method to create a new command from XML Element ELEMENT. */
    public static BackgroundSetCommand makeCommand(XmlReader.Element element) {
        String filename = element.getAttribute("id");
        return new BackgroundSetCommand(filename);
    }
    /**
     * Outputs XML to the XML WRITER for this command.
     */
    @Override
    public void writeXml(XmlWriter xmlWriter) {

    }
}
