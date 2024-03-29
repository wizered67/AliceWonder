package com.wizered67.game.conversations.commands;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.wizered67.game.conversations.CompleteEvent;
import com.wizered67.game.conversations.ConversationController;
import com.wizered67.game.scripting.GameScript;
import com.wizered67.game.scripting.ScriptManager;

/**
 * A ConversationCommand that waits until some condition is met.
 * The condition is a script that, when executed, returns a value that can be converted
 * by the ScriptManager to true or false.
 * @author Adam Victor
 */
public class WaitUntilVariableCommand implements ConversationCommand {
    /** A script that returns whether a condition has been met when run. */
    private GameScript condition;
    /** The ScriptManager used to convert script result to a boolean. */
    private ScriptManager scriptManager;

    /** No arguments constructor. Needed for serialization. */
    public WaitUntilVariableCommand() {
        scriptManager = null;
        condition = null;
    }
    /** Sets the scriptManager to the one used for LANG and then loads the CONDITION SCRIPT.
     * Iff FILE, it load the file named CONDITION SCRIPT. Also sets the commands to be
     * executed if true to RESULT, and sets the commands to be executed otherwise to ELSE RESULT.
     */
    public WaitUntilVariableCommand(String conditionScript, boolean file, String lang) {
        scriptManager = ConversationController.scriptManager(lang);
        if (!file && scriptManager.requiresReturn() && !conditionScript.matches(".*return .*")) {
            conditionScript = "return " + conditionScript;
        }
        condition = scriptManager.load(conditionScript, file);
    }
    /** Whether the condition in the script passed in has been met. */
    public boolean conditionMet() {
        return scriptManager.objectToBoolean(condition.execute());
    }
    /**
     * Executes the command on the CONVERSATION CONTROLLER.
     */
    @Override
    public void execute(ConversationController conversationController) {
        //do nothing, just wait until the condition is met.
    }
    /**
     * Whether to wait before proceeding to the next command in the branch.
     */
    @Override
    public boolean waitToProceed() {
        return !conditionMet();
    }
    /**
     * Checks whether the CompleteEvent C completes this command,
     * and if so acts accordingly.
     */
    @Override
    public void complete(CompleteEvent c) {

    }
    /**
     * Outputs XML to the XML WRITER for this command.
     */
    @Override
    public void writeXml(XmlWriter xmlWriter) {

    }

    /** Static method to create a new command from XML Element ELEMENT. */
    public static WaitUntilVariableCommand makeCommand(XmlReader.Element element) {
        String language = element.getAttribute("language", ConversationController.defaultScriptingLanguage());
        boolean isFile = element.getBoolean("isfile", false);
        String text = element.getChild(0).getText().trim();
        return new WaitUntilVariableCommand(text, isFile, language);
    }
}
