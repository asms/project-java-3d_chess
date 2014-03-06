// This class will be the main editor GUI window. The alternate view from the game play window.
// TODO: Make this interface similar to the game but
// with a side bar with things either for:
// 						- Editing game pieces (Black/White, or NPC)
// 						- Editing game boards. Set size and give palette of pieces.
// with save/open buttons for files and resources.
// Way to organise things into game packages containing files for a collection of pieces and a board.

package com.a7m5.chess.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ChessGameEditor implements ApplicationListener {
	private SpriteBatch batch;
	private BitmapFont font;

	@Override
	public void create() {
		 batch = new SpriteBatch();    
	        font = new BitmapFont();
	        font.setColor(Color.RED);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		 Gdx.gl.glClearColor(1, 1, 1, 1);
	        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	        
	        batch.begin();
	        font.draw(batch, "Hello World", 200, 200);
	        batch.end();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
        batch.dispose();
        font.dispose();
	}

}
