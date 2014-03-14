package com.gdx.extension.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;


public class ScreenManager implements Disposable
{
    
    private Map<Class<? extends Screen>, BaseScreen> screens = new ConcurrentHashMap<Class<? extends Screen>, BaseScreen>();
    private List<BaseScreen> depthList = new ArrayList<BaseScreen>();
    
    public ScreenManager(Skin skin)
    {
	if(skin == null)
	    throw new IllegalArgumentException("Skin may not be null.");
	
	MasterScreen.setSkin(skin);
    }
    
    public synchronized void render(float delta)
    {
	for (Screen _screen : depthList)
	    _screen.render(delta);
	
	MasterScreen.masterRender(delta);
    }
    
    public void resize(int width, int height)
    {
	for (Screen _screen : screens.values())
	    _screen.resize(width, height);
    }
    
    public void pause()
    {
	for (Class<? extends Screen> _clazz : screens.keySet())
	{
	    Screen _screen = screens.get(_clazz);
	    if (_screen == null)
		throw new NullPointerException("[Pause] Active screen is not in the global list of screens");
	    else
		_screen.pause();
	}
    }

    public void resume()
    {
	for (Class<? extends Screen> _clazz : screens.keySet())
	{
	    Screen _screen = screens.get(_clazz);
	    if (_screen == null)
		throw new NullPointerException("[Resume] Active screen is not in the global list of screens");
	    else
		_screen.resume();
	}
    }

    public void dispose()
    {
	for (BaseScreen _screen : screens.values())
	    _screen.dispose();
	
	MasterScreen.masterDispose();
    }
    
    public synchronized <T extends BaseScreen> T addScreen(Class<T> screen)
    {
	if (screen == null)
	    throw new NullPointerException("Can't add the screen");

	BaseScreen _screen;
	if (screens.containsKey(screen)) // Already added
	    _screen = screens.get(screen);
	else
	{
	    // Instantiate screen
	    try
	    {
		_screen = screen.getConstructor(ScreenManager.class).newInstance(this);
	    } catch (Exception e)
	    {
		Gdx.app.error("ScreenManager", "Unable to instanciate screen " + screen.getSimpleName(), e);

		return null;
	    }

	    screens.put(screen, _screen);
	}

	((BaseScreen) _screen).show();;
	
	updateScreens();

	return (T) _screen;
    }
    
    public void removeScreen(Class<? extends BaseScreen> screen)
    {
	BaseScreen _screen = screens.get(screen);
	
	if(_screen != null)
	    _screen.hide();
	
	updateScreens();
    }
    
    public <T extends BaseScreen> T getScreen(Class<T> screen)
    {
	return ((T) screens.get(screen));
    }

    /**
     * Called internally when add, remove or modify screens.
     * Call it only if you know what you do.
     */
    public synchronized void updateScreens()
    {
	depthList.clear();
	for(BaseScreen _screen : screens.values())
	{
	    if(_screen.isActive)
		depthList.add(_screen);
	}
	
	Collections.sort(depthList);
    }
    
    public List<BaseScreen> getActiveScreen()
    {
	return depthList;
    }
    
}
