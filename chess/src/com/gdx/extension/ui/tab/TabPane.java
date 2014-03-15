/** Copyright 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gdx.extension.ui.tab;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.gdx.extension.ui.tab.TabPane;
import com.badlogic.gdx.utils.ArrayMap;
import com.esotericsoftware.tablelayout.Cell;
import com.gdx.extension.exception.OptimizationException;

/**
 * 
 * @author Kyu
 *
 */
public class TabPane extends Table
{

    private HorizontalGroup tabs;
    private Stack body;
    
    private ButtonGroup buttonGroup;

    private ArrayMap<Tab, TabContainer> tabBind = new ArrayMap<Tab, TabContainer>();

    private Tab currentTab;
    
    private TabPaneStyle style;

    /**
     * Create a {@link TabPane} with default style.
     * 
     * @param skin the skin to use for style
     */
    public TabPane(Skin skin)
    {
	this(skin, "default");
    }

    /**
     * Create a {@link TabPane} with the specified style.
     * 
     * @param skin the skin to use for style
     * @param styleName the name of the style to use
     */
    public TabPane(Skin skin, String styleName)
    {
	super(skin);

	setStyle(skin.get(styleName, TabPaneStyle.class));
	
	buttonGroup = new ButtonGroup();
	tabs = new HorizontalGroup();
	
	body = new Stack();
	this.left().top();

	super.add(tabs).left().expandX().fillX();
	this.row();
	super.add(body).expand().fill();
    }
    
    /**
     * Apply a {@link TabPaneStyle style}.
     * 
     * @param style
     */
    public void setStyle(TabPaneStyle style)
    {
	this.style = style;
	
	setBackground(style.background);
    }
    
    /**
     * @return the current {@link TabPaneStyle style}
     */
    public TabPaneStyle getStyle()
    {
	return style;
    }

    @Deprecated
    @Override
    public Cell<?> add() { throw new OptimizationException("Use addTab(Tab) instead."); };
    
    @Deprecated
    @Override
    public Cell<?> add(Actor actor)
    {
	if(actor instanceof HorizontalGroup || actor instanceof Stack)
	    return super.add(actor);
	
	throw new OptimizationException("Use addTab(Tab) instead.");
    }
    
    @Deprecated
    @Override
    public Cell<?> add(String text) { throw new OptimizationException("Use addTab(Tab) instead."); }
    
    @Deprecated
    @Override
    public Cell<?> add(String text, String fontName, Color color) { throw new OptimizationException("Use addTab(Tab) instead."); }
    
    @Deprecated
    @Override
    public Cell<?> add(String text, String fontName, String colorName) { throw new OptimizationException("Use addTab(Tab) instead."); }
    
    @Deprecated
    @Override
    public Cell<?> add(String text, String labelStyleName) { throw new OptimizationException("Use addTab(Tab) instead."); }
    
    @Deprecated
    @Override
    public void addActor(Actor actor)
    {
	if(actor instanceof HorizontalGroup || actor instanceof Stack)
	    super.addActor(actor);
	else
	    throw new OptimizationException("Use addTab(Tab) instead.");
    }
    
    @Deprecated
    @Override
    public void addActorAfter(Actor actorAfter, Actor actor) { throw new OptimizationException("Use addTab(Tab) instead."); }
    
    @Deprecated
    @Override
    public void addActorAt(int index, Actor actor) { throw new OptimizationException("Use addTab(Tab) instead."); }
    
    @Deprecated
    @Override
    public void addActorBefore(Actor actorBefore, Actor actor) { throw new OptimizationException("Use addTab(Tab) instead."); }
    
    /**
     * Add the specified {@link Tab}.
     * 
     * @param tab the {@link Tab} to add
     */
    public void addTab(Tab tab)
    {
	tabBind.put(tab, tab.getContainer());
	buttonGroup.add(tab);
	tabs.addActor(tab);

	body.add(tab.getContainer());
	tab.setFrom(this);
	
	setCurrentTab(tab);
    }

    /**
     * Remove the specified {@link Tab}.
     * 
     * @param tab the {@ink Tab} to remove
     */
    public void removeTab(Tab tab)
    {
	Tab _tab = null;
	int _index = tabBind.indexOfKey(tab);
	if(_index + 1 < tabBind.size)
	    _tab = tabBind.getKeyAt(_index + 1);
	else if(_index - 1 >= 0)
	    _tab = tabBind.getKeyAt(_index - 1);
	
	buttonGroup.remove(tab);
	tabBind.getValueAt(_index).remove();
	tabBind.removeIndex(_index);
	tab.remove();

	setCurrentTab(_tab);
    }
    
    @Deprecated
    @Override
    public boolean removeActor(Actor actor) { throw new OptimizationException("Use removeTab(Tab) instead."); }

    private void hideAllContainer()
    {
	for (TabContainer _cont : tabBind.values())
	    _cont.setVisible(false);
    }

    /**
     * Get all {@link Tab}s of this {@link TabPane}.
     * 
     * @return all {@link Tab}
     */
    public ArrayMap<Tab, TabContainer> getTabs()
    {
	return tabBind;
    }

    /**
     * Get the selected {@link TabContainer}.
     * 
     * @return the selected {@link TabContainer}
     */
    public TabContainer getCurrentTab()
    {
	return currentTab.getContainer();
    }

    /**
     * Set the selected {@link Tab}.
     * 
     * @param currentTab the {@link Tab} to set selected
     */
    public void setCurrentTab(Tab currentTab)
    {
	this.currentTab = currentTab;
	
	if(currentTab == null)
	    return;
	
	hideAllContainer();
	tabBind.get(currentTab).setVisible(true);
	currentTab.setChecked(true);
    }
    
    /**
     * Set the selected {@ink Tab}.
     * 
     * @param index the index of the {@link Tab}
     */
    public void setCurrentTab(int index)
    {
	setCurrentTab(tabBind.getKeyAt(index));
    }
    
    /**
     * Get the {@link TabContainer} at the specified index.
     * Start from 0.
     * 
     * @param index position of the {@link Tab} of the {@link TabContainer}
     * @return the {@link TabContainer} at the specified index
     */
    public TabContainer getTab(int index)
    {
	return tabBind.getValueAt(index);
    }
    
    /**
     * Get the first {@link TabContainer} of the specified type.
     * 
     * @param clazz the type of the searched {@link TabCOntainer}
     * @return the first found {@link TabContainer} of the searched type or null if nothing is found
     */
    public <T extends TabContainer> T getTab(Class<T> clazz)
    {
	for(TabContainer _container : tabBind.values())
	{
	    try {
    	    T _cast = clazz.cast(_container);
    	    
    	    return _cast;
	    } catch(ClassCastException e)
	    {
		
	    }
	}
	
	return null;
    }
    
    /**
     * Define the style of a {@link TabPane tab pane}.
     * 
     * @author Kyu
     *
     */
    public static class TabPaneStyle
    {

	/** Optional */
	public Drawable background;

	public TabPaneStyle()
	{

	}

    }

}
