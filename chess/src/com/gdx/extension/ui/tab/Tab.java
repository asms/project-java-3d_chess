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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


/**
 * Create a {@link Tab tab} for a {@link TabPane}.
 * 
 * @author Kyu
 *
 */
public class Tab extends TextButton
{
    
    private TabContainer container;
    private TabPane from;
    
    /**
     * Create a {@link Tab tab} with default style.
     * 
     * @param name the label of the tab
     * @param container the {@link TabContainer} to bind with this tab
     * @param skin the skin to use
     */
    public Tab(String name, TabContainer container, Skin skin)
    {
	this(name, container, skin, "default");
    }
    
    /**
     * Create a {@link Tab tab} with the specified style.
     * 
     * @param name the label of the tab
     * @param container the {@link TabContainer} to bind with this tab
     * @param skin the skin to use
     * @param styleName the style to use
     */
    public Tab(String name, TabContainer container, Skin skin, String styleName)
    {
	super(name, skin.get(styleName, TabStyle.class));
	
	this.container = container;
	
	this.addListener(new ClickListener() {
	    
	    @Override
	    public void clicked(InputEvent event, float x, float y)
	    {
		from.setCurrentTab((Tab) event.getListenerActor());
	    }
	    
	});
    }
    
    /**
     * @return the {@link TabContainer} bound to this tab
     */
    public TabContainer getContainer()
    {
	return container;
    }
    
    /**
     * Used internally.
     * 
     * @param tabPane the parent {@link TabPane tab pane}
     */
    public void setFrom(TabPane tabPane)
    {
	this.from = tabPane;
    }
    
    /**
     * Define the style of a {@link Tab tab}.
     * 
     * @author Kyu
     *
     */
    public static class TabStyle extends TextButtonStyle
    {

	public TabStyle()
	{
	    
	}

    }
    
}
