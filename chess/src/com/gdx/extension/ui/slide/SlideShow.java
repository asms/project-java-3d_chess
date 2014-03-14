package com.gdx.extension.ui.slide;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;


public class SlideShow extends Table
{
    
    private SlideShowStyle style;
    
    private ScrollPane contentScroll;
    private WidgetGroup content;
    
    private ImageButton previousScrollButton;
    private ImageButton nextScrollButton;
    
    private Cell<ImageButton> previousScrollButtonCell;
    private Cell<ImageButton> nextScrollButtonCell;
    
    private boolean isVertical;
    private boolean showButtons = true;

    public SlideShow(Skin skin)
    {
	this(false, skin);
    }
    
    public SlideShow(boolean isVertical, Skin skin)
    {
	this(isVertical, skin, (isVertical) ? "default-vertical" : "default-horizontal");
    }

    public SlideShow(boolean isVertical, Skin skin, String styleName)
    {
	super(skin);
	
	setStyle(skin.get(styleName, SlideShowStyle.class));
	
	this.isVertical = isVertical;
	
	content = (isVertical) ? new VerticalGroup() : new HorizontalGroup();
	
	ScrollPaneStyle _scrollStyle = new ScrollPaneStyle();
	contentScroll = new ScrollPane(content, _scrollStyle);
	contentScroll.setScrollingDisabled(isVertical, !isVertical);
	contentScroll.setOverscroll(false, false);
	
	setBackground(style.background);
	
	previousScrollButton = new ImageButton(getStyle().previousScroll);
	previousScrollButtonCell = add(previousScrollButton);
	if(isVertical)
	    previousScrollButtonCell.row();
	Cell<WidgetGroup> _contentCell = add(contentScroll);
	if(isVertical)
	    _contentCell.row();
	nextScrollButton = new ImageButton(getStyle().nextScroll);
	nextScrollButtonCell = add(nextScrollButton);
	
	previousScrollButton.addListener(new ClickListener() {
	   
	    @Override
	    public void clicked(InputEvent event, float x, float y)
	    {
		if(SlideShow.this.isVertical)
		    contentScroll.setScrollPercentY(contentScroll.getScrollPercentY() - 0.1f);
		else
		    contentScroll.setScrollPercentX(contentScroll.getScrollPercentX() - 0.1f);
	    }
	    
	});
	nextScrollButton.addListener(new ClickListener() {
		   
	    @Override
	    public void clicked(InputEvent event, float x, float y)
	    {
		if(SlideShow.this.isVertical)
		    contentScroll.setScrollPercentY(contentScroll.getScrollPercentY() + 0.1f);
		else
		    contentScroll.setScrollPercentX(contentScroll.getScrollPercentX() + 0.1f);
	    }
	    
	});
    }
    
    public void addItem(Actor actor)
    {
	content.addActor(actor);
    }
    
    public void setStyle(SlideShowStyle style)
    {
	this.style = style;
    }
    
    @Override
    public void validate()
    {
        super.validate();
        
        if(isVertical)
            contentScroll.setWidth(content.getWidth());
        else
            contentScroll.setHeight(content.getHeight());
        
        if(showButtons)
        {
            float _scrollPercent;
            if(isVertical)
        	_scrollPercent = contentScroll.getScrollPercentY();
            else
        	_scrollPercent = contentScroll.getScrollPercentX();
            
            if(_scrollPercent > 0f)
        	previousScrollButtonCell.setWidget(previousScrollButton);
            else
        	previousScrollButtonCell.setWidget(null);
            if(_scrollPercent < 1f)
        	nextScrollButtonCell.setWidget(nextScrollButton);
            else
        	nextScrollButtonCell.setWidget(null);
        }
    }
    
    public void setShowButtons(boolean show)
    {
	this.showButtons = show;
	
	if(!show)
	{
	    previousScrollButtonCell.setWidget(null);
            nextScrollButtonCell.setWidget(null);
	}
    }
    
    public boolean getShowButtons()
    {
	return showButtons;
    }
    
    public boolean isVertical()
    {
	return isVertical;
    }
	
    public SlideShowStyle getStyle()
    {
	return style;
    }
    
    public static class SlideShowStyle
    {
	
	/** Optional */
	public Drawable background;
	
	public ImageButtonStyle previousScroll, nextScroll;	
    }
	    
}
