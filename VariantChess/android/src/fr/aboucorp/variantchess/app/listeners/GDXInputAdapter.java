package fr.aboucorp.variantchess.app.listeners;

import com.badlogic.gdx.InputAdapter;

import fr.aboucorp.variantchess.libgdx.Board3dManager;

/**
 * Cette classe couplé au GDXGestureListener se charge de recevoir les évènements de clic
 * Created by Meriemi on 10/04/2020.
 */
public class GDXInputAdapter extends InputAdapter {

    private Board3dManager board3dManager;

    public GDXInputAdapter(Board3dManager board3dManager) {
        this.board3dManager = board3dManager;
    }

    @Override
    public boolean keyDown(int keycode) {
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return super.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(int amount) {
        return super.scrolled(amount);
    }
}


