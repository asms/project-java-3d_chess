package com.gdx.extension.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


/**
 * The master screen is singleton and 
 * 
 * @author Kyu
 *
 */
public abstract class MasterScreen implements Screen
{
    
    /**
     * Style
     */
    protected static Skin skin;
    
    /**
     * Propagate inputs
     */
    protected static InputMultiplexer inputProcessor;
    
    /** 
     * {@link SpriteBatch} used for all screens (improve performances)
     */
    protected static SpriteBatch mainBatch;
    
    /** 
     * Catch events and draw actors
     */
    protected static Stage stage;
    
    /**
     * Overlap all screen layouts
     */
    protected static Stack stageStack;
    
    /**
     * Initialize MasterScreen
     */
    static {
	inputProcessor = new InputMultiplexer();
	Gdx.input.setInputProcessor(inputProcessor);
	
	mainBatch = new SpriteBatch();
	stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, mainBatch);
	inputProcessor.addProcessor(stage);
	stageStack = new Stack();
	stage.addActor(stageStack);
	stageStack.setFillParent(true);
    }
    
    /**
     * Render and act the stage.
     * 
     * @param delta time from last frame
     */
    public final static void masterRender(float delta)
    {
	stage.act(delta);
	stage.draw();
	
	Table.drawDebug(stage);
    }

    @Override
    public abstract void render(float delta);

    @Override
    public abstract void resize(int width, int height);
    
    @Override
    public abstract void show();

    @Override
    public abstract void hide();

    @Override
    public abstract void pause();

    @Override
    public abstract void resume();
    
    @Override
    public abstract void dispose();

    /**
     * Free ressources of MasterScreen.
     */
    public final static void masterDispose()
    {
	skin.dispose();
	stage.dispose();
	mainBatch.dispose();
    }
    
    /**
     * @return the skin that is used by all screens
     */
    public static Skin getSkin()
    {
	return skin;
    }
    
    /**
     * Set the skin to use for all screen
     * 
     * @param skin the skin to use
     */
    public static void setSkin(Skin skin)
    {
	MasterScreen.skin = skin;
    }
    
    /**
     * @return the {@link InputMultiplexer}
     */
    public static InputMultiplexer getInputProcessor()
    {
	return inputProcessor;
    }
    
    /**
     * @return the {@link Stage stage} that is used by all screens
     */
    public static Stage getStage()
    {
	return stage;
    }
    
}
