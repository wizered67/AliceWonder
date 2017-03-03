package com.wizered67.game.gui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.wizered67.game.Constants;
import com.wizered67.game.Evidence;
import com.wizered67.game.GameManager;
import com.wizered67.game.conversations.CompleteEvent;
import com.wizered67.game.conversations.Conversation;
import com.wizered67.game.conversations.ConversationController;
import com.wizered67.game.conversations.Transcript;
import com.wizered67.game.saving.SaveManager;

/** Contains GUI elements and the ConversationController which the GUI elements are passed into.
 * Fixes GUI elements if screen is resized.
 * @author Adam Victor
 */
public class GUIManager {
    /** Main Table that all GUI elements are added to. */
	private static Table dialogueElementsTable;
    /** Skin used by all GUI elements. */
	private static Skin skin = new Skin();
    /** Label for the main textbox. Displays text when spoken by characters. */
	private static TypingLabel textboxLabel;
    /** Label to display the name of the current speaker. */
    private static Label speakerLabel;
    /** Array containing TextButtons to be displayed when the player is offered a choice. */
	private static TextButton[] choiceButtons;
    /** The stage to which the GUI elements are added. Part of Scene2D. */
	private static Stage stage;
    /** Constant Vector2 containing textbox dimensions. */
	private final static Vector2 TEXTBOX_SIZE = new Vector2(360, 50);
    /** Constant Vector2 containing choice button dimensions. */
    private final static Vector2 CHOICES_SIZE = new Vector2(300, 22);
    /** Constant denoting space between left side of the textbox and text. */
    private final static int LEFT_PADDING = 10;
    /** Message Window that updates the GUI elements as a Conversation proceeds. */
    private static ConversationController conversationController;
    /** Default font used for text. */
    private static BitmapFont defaultFont;

    //debug related variables
    /** Scrollpane used to contain debug choices -- ie loading conversations, etc. */
    private static ScrollPane debugPane;
    /** Contains choices for debug options. */
    private static HorizontalGroup debugChoices;
    /** Whether the save input is showing. */
    private static boolean saveInputShowing = false;
    /** GUI List that shows all options for debug menu. */
    private static List<String> debugSelector;
    /** Table used to contain elements for the transcript. */
    private static Table transcriptTable;
    /** Scrollpane used to contain the transcript label. */
    private static ScrollPane transcriptPane;
    /** Label used for displaying transcript text. */
    private static Label transcriptLabel;
    /** Whether the transcript is scrolling, and in which direction. */
    private static float transcriptScrolling = 0;

    static Table evidenceTable;

    static Table column;
    static Table main;
    static int listTypeIndex = 0;
    static List<String> list;
    static Array<Evidence>[] allEvidence = new Array[2];
    static Button presentButton;
    static Evidence currentEvidence;
    static TextButton backButton;
    static TextButton evidenceButton;
    static Label descriptionLabel;
    static LinkLabel infoLabel;
    static Sound clickSound;

    /** Initializes all of the GUI elements and adds them to the Stage ST. Also
     * initializes ConversationController with the elements it will update.
     */
    public GUIManager(Stage st) {
		stage = st;
 		// Generate a 1x1 white texture and store it in the skin named "white".
 		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
 		pixmap.setColor(Color.WHITE);
 		pixmap.fill();

 		// Store the default libgdx font under the name "default".
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        float densityIndependentSize = Constants.REGULAR_FONT_SIZE * Gdx.graphics.getDensity();
        parameter.size = Math.round(densityIndependentSize);
		defaultFont = generator.generateFont(parameter); // font size 12 pixels
        defaultFont.getData().markupEnabled = true;

        skin.add("white", new Texture(pixmap));
        skin.add("default", defaultFont);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        Drawable newDrawable = skin.newDrawable("white", Color.DARK_GRAY);
        newDrawable.setLeftWidth(LEFT_PADDING);
        newDrawable.setLeftWidth(LEFT_PADDING);
        //newDrawable.setRightWidth(20);
        labelStyle.background = newDrawable;
        skin.add("default", labelStyle);
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
    	 dialogueElementsTable = new Table();
    	 dialogueElementsTable.setFillParent(true);
    	 stage.addActor(dialogueElementsTable);
	     //dialogueElementsTable.setDebug(Constants.DEBUG); // This is optional, but enables debug lines for tables.
    	    // Add widgets to the table here.
	     
	     TextButtonStyle textButtonStyle = new TextButtonStyle();
			textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
			textButtonStyle.checked = skin.newDrawable("white", Color.LIGHT_GRAY);
			textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
			textButtonStyle.font = skin.getFont("default");
			skin.add("default", textButtonStyle);
        Table choiceTable = new Table();
        //choiceTable.setDebug(Constants.DEBUG);
        dialogueElementsTable.add(choiceTable).top().grow();//.expand().fill();
        dialogueElementsTable.row();
        choiceButtons = new TextButton[4];
        for (int i = 0; i < choiceButtons.length; i += 1) {
            TextButton tb = new TextButton("", skin);
            tb.setUserObject(i);
            tb.addListener(new ChangeListener() {
                public void changed (ChangeEvent event, Actor actor) {
                    System.out.println("Clicked button " + actor.getUserObject());
                    conversationController.processChoice((Integer) actor.getUserObject());
                    event.cancel();
                    ((Button) actor).setProgrammaticChangeEvents(false);
                    ((Button) actor).setChecked(false);
                    ((Button) actor).setProgrammaticChangeEvents(true);
                }
            });
            tb.setVisible(false);
            choiceButtons[i] = tb;
            Cell cell = choiceTable.top().add(tb).expandX().fillX().padLeft(100).padRight(100).padBottom(30).minHeight(35);
            if (i == 0) {
                cell.padTop(30);
            }
            if (i < choiceButtons.length - 1) {
                choiceTable.row();
            }
        }

        Label.LabelStyle speakerLabelStyle = new Label.LabelStyle();
        speakerLabelStyle.font = skin.getFont("default");
        Drawable speakerDrawable = skin.newDrawable("white", Color.GRAY);
        speakerDrawable.setLeftWidth(5);
        //speakerDrawable.setRightWidth(5);
        //newDrawable.setRightWidth(20);
        speakerLabelStyle.background = speakerDrawable;
        skin.add("speaker", speakerLabelStyle);
        speakerLabel = new Label("Really long speaker name", skin, "speaker");
        speakerLabel.toBack();
        //speakerLabel.setStyle(speakerLabelStyle);
        speakerLabel.setAlignment(Align.center);

        //stage.addActor(speakerLabel);
        Table aboveTextboxTable = new Table();
        aboveTextboxTable.setDebug(Constants.DEBUG);
        dialogueElementsTable.add(aboveTextboxTable).expandX().fillX().colspan(3).padLeft(40).padRight(40).minHeight(40);

        aboveTextboxTable.left().add(speakerLabel).minWidth(80).maxWidth(150).minHeight(40);
        Table menuButtonsTable = new Table();
        menuButtonsTable.setDebug(Constants.DEBUG);
        aboveTextboxTable.add(menuButtonsTable).expandX().fillX();

        TextButton transcriptButton = new TextButton("T", skin);
        transcriptButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.cancel();
                toggleTranscript();
            }
        });
        menuButtonsTable.right().add(transcriptButton).minWidth(40).minHeight(40).padRight(20);
        TextButton saveButton = new TextButton("S", skin);
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.cancel();
                SaveManager.save(Gdx.files.local("Saves/game.sav"));
            }
        });
        menuButtonsTable.add(saveButton).minWidth(40).minHeight(40).padRight(20);
        TextButton loadButton = new TextButton("L", skin);
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.cancel();
                SaveManager.load(Gdx.files.local("Saves/game.sav"));
            }
        });
        menuButtonsTable.add(loadButton).minWidth(40).minHeight(40);
        dialogueElementsTable.row();
		textboxLabel = new TypingLabel("", skin);
		textboxLabel.setAlignment(Align.topLeft);
		textboxLabel.setStyle(labelStyle);
        textboxLabel.setWrap(true);
        textboxLabel.toFront();
        dialogueElementsTable.bottom().add(textboxLabel).expandX().fillX().minHeight(150).maxHeight(150).padLeft(40).padRight(40).padBottom(40).colspan(3);
        //stage.addActor(textboxLabel);
        conversationController = new ConversationController(textboxLabel, speakerLabel, choiceButtons);
        setTextboxShowing(false);
        //System.out.println(remainingTextNoTags);
        //remainingText = "this is a new message just so you know.";
		//textboxLabel.setText("TESTING A MESSAGE BRO");
		//textboxLabel = new Label("this is a really long test message and I want to see if word wrap is doing anything? Test MessageCommand!", labelStyle);

		//textboxLabel.setPosition(200, 400);
		//textboxLabel.setSize(350, 60);
		//textboxLabel.setFullVisible(false);
		/*
        TextTooltip.TextTooltipStyle textTooltipStyle = new TextTooltip.TextTooltipStyle();
		textTooltipStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
		textTooltipStyle.label = labelStyle;
		textTooltipStyle.wrapWidth = 300;
		skin.add("default", textTooltipStyle);
		TextTooltip textTooltip = new TextTooltip("This [GREEN]is [WHITE]a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip!", skin);
		//textTooltip.setAlways(true);
		textTooltip.setInstant(true);
		button.addListener(textTooltip);
		*/
		//Table tooltipTable = new Table(skin);
		//tooltipTable.pad(10).background("default-round");
		//tooltipTable.add(new TextButton("Fancy tooltip!", skin));
        addDebug();
        addTranscript();
	}
    public GUIManager() {
        conversationController = new ConversationController();
    }
    /** Returns the Stage used to display GUI elements. */
	public static Stage getStage(){
		return stage;
	}
	/** Returns the batch being used by the stage. */
	public static Batch getBatch() {
	    return stage.getBatch();
    }
    /** Called every frame. Updates the ConversationController. DELTA TIME is the time
     * elapsed since the last frame.
     */
	public static void update(float deltaTime){
        conversationController.update(deltaTime);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
            toggleTranscript();
        }
        if (Constants.DEBUG && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            toggleDebugDisplay();
            conversationController.setPaused(debugChoices.isVisible());
        }
        if (transcriptPane.isVisible()) {
            if (transcriptScrolling != 0) {
                float velocity = transcriptPane.getVelocityY();
                transcriptPane.fling(1, 0, velocity);
                transcriptPane.setVelocityY(velocity + 4 * Math.signum(velocity));
            }
            //transcriptPane.setScrollY(transcriptPane.getScrollY() + transcriptScrolling);
        }
        if (main.isVisible()) {
            for (TextButton button : choiceButtons) {
                button.setVisible(false);
            }
        } else {
            if (conversationController.isChoiceShowing()) {
                for (TextButton button : choiceButtons) {
                    button.setVisible(button.getText() != null && !button.getText().toString().isEmpty());
                }
            }
        }
        updateTranscript(); //todo remove, only do so when transcript is visible
	}
	/** Toggles whether the transcript is being shown. */
	public static void toggleTranscript() {
        transcriptTable.setVisible(!transcriptTable.isVisible());
        conversationController.setPaused(transcriptTable.isVisible());
        updateTranscript(); //todo fix. part of hacky solution to make update first time
        //transcriptPane.invalidate();
        transcriptPane.validate();
        transcriptPane.setScrollPercentY(1f);
        transcriptPane.updateVisualScroll();
        transcriptPane.setVelocityY(0);
        stage.setScrollFocus(transcriptPane);
    }

	/** Called every frame. Updates and draws the stage, needed for UI elements. DELTA TIME is
     * the time elapsed since the last frame. */
	public static void updateAndRenderStage(float deltaTime) {
        GameManager.guiViewport().apply(true);
        stage.act(Math.min(1 / 30f, deltaTime));
        stage.draw();
    }
    /** Resize all GUI elements when the screen is resized to dimensions
     * WIDTH by HEIGHT. Keeps GUI elements proportional to virtual size.
     */
	public static void resize(int width, int height) {

	}

    /** Returns the y position of the top of the textbox label. */
    public static float getTextboxTop() {
        return textboxLabel.getTop();
    }
    /** Sets the visibility of the textbox and speaker label to SHOW. */
    public static void setTextboxShowing(boolean show) {
        conversationController.setTextBoxShowing(show);
    }
    /** Returns the ConversationController. */
    public static ConversationController conversationController() {
        return conversationController;
    }

    private static void addTranscript() {
        transcriptTable = new Table();
        transcriptTable.setDebug(Constants.DEBUG);
        transcriptTable.setFillParent(true);
        transcriptTable.setVisible(false);
        transcriptTable.toFront();

        transcriptLabel = new Label("", skin);
        transcriptLabel.setWrap(true);
        transcriptLabel.setAlignment(Align.topLeft);
        transcriptLabel.setWidth(Gdx.graphics.getWidth() - 64);
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        transcriptPane = new ScrollPane(transcriptLabel, scrollPaneStyle);
        transcriptPane.setOverscroll(false, false);
        transcriptPane.setWidth(transcriptLabel.getWidth());
        transcriptPane.setHeight(Gdx.graphics.getHeight() - 64);
        transcriptPane.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
        transcriptPane.toFront();
        //transcriptPane.setVisible(false);
        stage.addActor(transcriptTable);
        transcriptTable.add(transcriptPane).expand().fill().pad(40);
    }

    private static void updateTranscript() {
        if (!transcriptTable.isVisible()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Transcript.TranscriptMessage message : conversationController.getTranscript().getTranscriptMessages()) {
            stringBuilder.append("[CYAN]").append(message.getSpeaker()).append(": [WHITE]\n");
            stringBuilder.append(message.getMessage()).append("\n\n");
        }
        transcriptLabel.setText(stringBuilder);
        transcriptLabel.invalidate();
        transcriptLabel.getPrefHeight(); //fixme part of hacky solution to get size correct for first time
    }

    public static boolean isTranscriptVisible() {
        return transcriptTable.isVisible();
    }

    public static void scrollTranscript(int direction) {
        transcriptPane.fling(1, 0, -direction * 50);
        transcriptScrolling = direction * 0.005f * transcriptPane.getHeight();
        System.out.println("Velocity set to " + transcriptPane.getVelocityY());
    }

    public static void stopTranscriptScrolling() {
        transcriptScrolling = 0;
        transcriptPane.setVelocityY(0);
    }

    private static void initEvidence() {
        allEvidence[0] = new Array<>(true, 10);
        allEvidence[1] = new Array<>(true, 6);
        allEvidence[0].add(new Evidence("First Amendment",
                "The Federal government, as well as the states, may not infringe on the people’s rights to press, free speech, assembly, petition, and to practice any religion.", 2,
                "https://en.wikipedia.org/wiki/First_Amendment_to_the_United_States_Constitution" ));

        allEvidence[0].add(new Evidence("Second Amendment",
                "People have the right to keep a weapon and use it to protect themselves.", 0,
                "https://en.wikipedia.org/wiki/Second_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Third Amendment", "Soldiers can not stay in people’s houses' without their consent.", 9, "https://en.wikipedia.org/wiki/Third_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Fourth Amendment",
                "The government cannot arrest a person, or search their property, unless there is \"probable cause\" that a crime has been committed.", 10, "https://en.wikipedia.org/wiki/Fourth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Fifth Amendment",
                "The Federal government must follow the due process of the law before punishing a person and that all citizens had the right to a trial by jury. It also states that a person cannot be put on trial twice for the same crime (the Double Jeopardy Clause) or that person on trial for a crime does not have to testify against themselves in court, known as \"Pleading the 5th\".",
                9, "https://en.wikipedia.org/wiki/Fifth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Sixth Amendment", "A person has the right to be told what crimes they are charged with, have a speedy and fair trial by a jury, to have a lawyer during the trial and the right to question witnesses against them and have the right to get their own witnesses to testify for them.", 9, "https://en.wikipedia.org/wiki/Sixth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Seventh Amendment", "People who are sued have to a jury trial for civil cases. ", 9, "https://en.wikipedia.org/wiki/Seventh_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Eighth Amendment", "The government cannot demand excessive bail, excessive fines, or inflict any cruel and unusual punishment.", 1, "https://en.wikipedia.org/wiki/Eighth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Ninth Amendment", "The Constitution does not include all of the rights of the people and the states. It provides reassurance that rights not listed could not be taken away and that the adoption of Constitution itself, and its construction, would limit the powers of government.", 9, "https://en.wikipedia.org/wiki/Ninth_Amendment_to_the_United_States_Constitution"));
        allEvidence[0].add(new Evidence("Tenth Amendment", "Any powers that the Constitution does not give to the government, belong to the states and  the people, excluding the powers that the Constitution says the states cannot have.", 9, "https://en.wikipedia.org/wiki/Tenth_Amendment_to_the_United_States_Constitution"));

        allEvidence[1].add(new Evidence("New York Times v. Sullivan", "The Court held that the First Amendment protects the publication of all statements, even false ones, about the conduct of public officials except when statements are made with actual malice (with knowledge that they are false or in reckless disregard of their truth or falsity).", 9, "https://www.oyez.org/cases/1963/39"));
        allEvidence[1].add(new Evidence("UC Regents v. Bakke", "Race can be used as part of the criteria for admissions, but the use of quotas is unconstitutional.", 4, "https://www.oyez.org/cases/1979/76-811"));
        allEvidence[1].add(new Evidence("Roe v. Wade", "The Court held that a woman's right to an abortion fell within the right to privacy protected by the Fourteenth Amendment. The decision gave a woman total autonomy over the pregnancy during the first trimester and defined different levels of state interest for the second and third trimesters. ", 9, "https://www.oyez.org/cases/1971/70-18"));
        allEvidence[1].add(new Evidence("Texas v. Johnson", "Johnson's burning of a flag was protected expression under the First Amendment. The Court found that Johnson's actions fell into the category of expressive conduct and had a distinctively political nature. The fact that an audience takes offense to certain ideas or expression, the Court found, does not justify prohibitions of speech. ", 3, "https://www.oyez.org/cases/1988/88-155"));
        allEvidence[1].add(new Evidence("United States v. Santana", "The Court upheld the search. Relying on the the Court's decision in United States v. Watson (1976), Justice Rehnquist argued that by standing on her porch when the officers arrived, Santana was \"not in an area where she had any expectation of privacy.\" Since the police had probable cause to arrest and search her at that point, their behavior was consistent with the Court's Watson precedent.", 11, "https://www.oyez.org/cases/1975/75-19"));
        allEvidence[1].add(new Evidence("Mapp v. Ohio", "The Court declared that \"all evidence obtained by searches and seizures in violation of the Constitution is, by [the Fourth Amendment], inadmissible in a state court.\" Mapp had been convicted on the basis of illegally obtained evidence. This was an historic -- and controversial -- decision. It placed the requirement of excluding illegally obtained evidence from court at all levels of the government. ", 9, "https://www.oyez.org/cases/1960/236"));
        //allEvidence[1].add(new Evidence("New York Times Co. v Sullivan", "Hello world", 3, "https://en.wikipedia.org/wiki/New_York_Times_Co._v._Sullivan"));
        //allEvidence[1].add(new Evidence("Roe v Wade", "desc", 1, "https://en.wikipedia.org/wiki/Roe_v._Wade"));
    }

    private static void hideEvidence() {
        main.setVisible(false);
        column.setVisible(false);
        evidenceButton.setVisible(true);
        descriptionLabel.setVisible(false);
        infoLabel.setVisible(false);
    }

    public static void showEvidence(boolean canChoose) {
        main.setVisible(true);
        column.setVisible(true);
        evidenceButton.setVisible(false);
        presentButton.setVisible(canChoose);
        backButton.setVisible(true);
        //backButton.setVisible(!canChoose);
        descriptionLabel.setVisible(true);
        infoLabel.setVisible(true);
    }

    private static void updateList() {
        list.setItems(allEvidence[listTypeIndex]);
    }

    private static void present(int id) {
        hideEvidence();
        conversationController.complete(CompleteEvent.present(id));
    }

    public static void makeEvidenceGUI() {

        clickSound = GameManager.assetManager().get("click.wav");
        evidenceButton = new TextButton("Evidence", skin);
        evidenceButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.cancel();
                showEvidence(false);
                clickSound.play(2);
            }
        });
        evidenceButton.setX(50);
        evidenceButton.setY(Gdx.graphics.getHeight() - 50);
        evidenceButton.setWidth(100);
        evidenceButton.setHeight(40);
        stage.addActor(evidenceButton);


        evidenceTable = new Table();
        evidenceTable.setFillParent(true);
        evidenceTable.setDebug(Constants.DEBUG);
        stage.addActor(evidenceTable);

        initEvidence();
        column = new Table(skin);
        column.setDebug(Constants.DEBUG);
        main = new Table(skin);
        main.setDebug(Constants.DEBUG);
        evidenceTable.top().left().add(column).minWidth(400);
        evidenceTable.add(main).top().minWidth(400);
        List.ListStyle style = new List.ListStyle(defaultFont, Color.WHITE, Color.GRAY, skin.newDrawable("white", Color.GRAY));
        style.background = skin.newDrawable("white", Color.DARK_GRAY);
        list = new List<>(style);
        list.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Object o = list.getItems().get(list.getSelectedIndex());
                currentEvidence = (Evidence) o;
                if (descriptionLabel != null) {
                    descriptionLabel.setText("\n" + currentEvidence.description);
                    descriptionLabel.setHeight(descriptionLabel.getPrefHeight());
                    descriptionLabel.setY(545 - descriptionLabel.getHeight());
                    descriptionLabel.invalidate();
                }
                if (infoLabel != null) {
                    infoLabel.setUrl(currentEvidence.info);
                }
            }
        });
        updateList();
        //String[] type = new String[] {"BoR", "Rulings"};
        ImageButton.ImageButtonStyle[] imageButtonStyle = new ImageButton.ImageButtonStyle[3];
        Drawable d = new TextureRegionDrawable(new TextureRegion(GameManager.assetManager().get("amendmenticon", Texture.class)));
        imageButtonStyle[0] = new ImageButton.ImageButtonStyle(d, d, d, d, d, d);
        Drawable d2 = new TextureRegionDrawable(new TextureRegion(GameManager.assetManager().get("courtcaseicon", Texture.class)));
        imageButtonStyle[2] = new ImageButton.ImageButtonStyle(d2, d2, d2, d2, d2, d2);
        for (int i = 0; i < 3; i += 1) {
            if (i == 1) {
                backButton = new TextButton("Back", skin);
                backButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        hideEvidence();
                        event.cancel();
                        clickSound.play(2);
                        if (presentButton.isVisible()) {
                            present(-1);
                        }
                    }
                });
                column.left().add(backButton).minWidth(100).minHeight(40);
                continue;
            }
            Button button = new ImageButton(imageButtonStyle[i]);
            button.setUserObject(i == 0 ? 0 : 1);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    listTypeIndex = (int) actor.getUserObject();
                    updateList();
                    event.cancel();
                    clickSound.play(2);
                }
            });
            column.left().add(button).width(64).height(64).pad(30, 10, 10, 10);
        }
        column.row();
        column.add(list).minWidth(120).pad(0, 20, 0, 20).colspan(3);
        column.row();

        presentButton = new TextButton("Present", skin);
        presentButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.cancel();
                present(currentEvidence.id);
            }
        });
        main.add(presentButton).minWidth(150).minHeight(40).pad(10, 0, 0, 0).colspan(3);
        main.row();
        VisUI.load();
        infoLabel = new LinkLabel("More Info", currentEvidence.info);
        infoLabel.toFront();
        main.add(infoLabel).colspan(3).pad(0, 0, 10, 0);
        Label.LabelStyle descriptionStyle = new Label.LabelStyle(defaultFont, Color.WHITE);
        descriptionStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        descriptionLabel = new Label("", descriptionStyle);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.topLeft);
        stage.addActor(descriptionLabel);//.left().colspan(1);
        descriptionLabel.setX(Gdx.graphics.getWidth() - 400);
        descriptionLabel.setWidth(390);
        descriptionLabel.setText("\n" + currentEvidence.description);
        descriptionLabel.invalidate();
        descriptionLabel.setHeight(descriptionLabel.getPrefHeight());
        descriptionLabel.setY(545 - descriptionLabel.getHeight());
        descriptionLabel.invalidate();
        descriptionLabel.toBack();
        descriptionLabel.setVisible(false);
        main.setVisible(false);
        column.setVisible(false);

    }

    public static boolean isEvidenceShowing() {
        return main.isVisible();
    }


    private static void addDebug() {
        List.ListStyle listStyle = new List.ListStyle(skin.getFont("default"), Color.GRAY, Color.WHITE, skin.newDrawable("white", Color.LIGHT_GRAY));
        listStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        debugSelector = new List<>(listStyle);
        //debugSelector.setItems("demonstration", "test conversation");
        //debugSelector.setSelectedIndex(-1);
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        debugPane = new ScrollPane(debugSelector, scrollPaneStyle);
        debugPane.setWidth(Gdx.graphics.getWidth() / 3);
        debugPane.setHeight(Gdx.graphics.getHeight() / 2);
        debugPane.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 64, Align.center);
        debugPane.toFront();
        debugPane.setUserObject(DebugMode.CONV);
        stage.addActor(debugPane);
        debugPane.setVisible(false);
        debugChoices = new HorizontalGroup();
        debugChoices.space(15);
        TextButton convChange = new TextButton("Conversations", skin);
        convChange.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                debugConversations();
                event.setCapture(true);
                event.cancel();
            }
        });
        TextButton branchChange = new TextButton("Branches", skin);
        branchChange.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                debugBranches();
                event.setCapture(true);
                event.cancel();
            }
        });
        TextButton save = new TextButton("Save", skin);
        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.setCapture(true);
                event.cancel();
                Input.TextInputListener inputListener = new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        SaveManager.save(Gdx.files.local("Saves/" + text));
                        if (debugPane.getUserObject() == DebugMode.LOAD) {
                            debugLoad();
                        }
                        saveInputShowing = false;
                    }

                    @Override
                    public void canceled() {
                        saveInputShowing = false;
                    }
                };
                if (!saveInputShowing) {
                    Gdx.input.getTextInput(inputListener, "Save File", "new save", "");
                    saveInputShowing = true;
                }
            }
        });
        TextButton load = new TextButton("Load", skin);
        load.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                debugLoad();
                event.setCapture(true);
                event.cancel();
            }
        });
        TextButton confirm = new TextButton("Confirm", skin);
        confirm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                debugConfirmChoice();
                event.setCapture(true);
                event.cancel();
            }
        });
        debugChoices.addActor(convChange);
        debugChoices.addActor(branchChange);
        debugChoices.addActor(confirm);
        debugChoices.addActor(save);
        debugChoices.addActor(load);
        debugChoices.setPosition(Gdx.graphics.getWidth() / 2 - debugChoices.getMinWidth() / 2, Gdx.graphics.getHeight() - 64, Align.center);
        debugChoices.toFront();
        debugChoices.setVisible(false);
        stage.addActor(debugChoices);
    }

    public static void toggleDebugDisplay() {
        debugChoices.setVisible(!debugChoices.isVisible());
        if (!debugChoices.isVisible()) {
            debugPane.setVisible(false);
        }
    }

    private static void debugChangePaneType(Array<String> content, DebugMode type) {
        debugSelector.setItems(content);
        debugSelector.setSelectedIndex(-1);
        debugPane.setUserObject(type);
        debugPane.setVisible(true);
    }

    private static void debugConversations() {
        Array<String> fileNames = new Array<>();
        FileHandle[] files = Gdx.files.local("Conversations/").list();
        for(FileHandle file: files) {
            fileNames.add(file.name());
        }
        debugChangePaneType(fileNames, DebugMode.CONV);
    }

    private static void debugBranches() {
        Conversation currentConv = conversationController.conversation();
        Array<String> branches = new Array<>();
        for (String branch : currentConv.getAllBranches()) {
            branches.add(branch);
        }
        debugChangePaneType(branches, DebugMode.BRANCH);
    }

    private static void debugLoad() {
        Array<String> fileNames = new Array<>();
        FileHandle[] files = Gdx.files.local("Saves/").list();
        for(FileHandle file: files) {
            fileNames.add(file.name());
        }
        debugChangePaneType(fileNames, DebugMode.LOAD);
    }

    private static void debugConfirmChoice() {
        int index = debugSelector.getSelectedIndex();
        if (index < 0) {
            return;
        }
        String selection = debugSelector.getItems().get(debugSelector.getSelectedIndex());
        switch ((DebugMode) debugPane.getUserObject()) {
            case CONV:
                conversationController.loadConversation(selection);
                conversationController.setBranch("default");
                break;
            case BRANCH:
                conversationController.setBranch(selection);
                break;
            case LOAD:
                SaveManager.load(Gdx.files.internal("Saves/" + selection));
                break;
        }
    }

    private enum DebugMode {
        CONV, BRANCH, LOAD
    }
}
