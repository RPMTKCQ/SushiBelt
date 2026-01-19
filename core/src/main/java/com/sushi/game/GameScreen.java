package com.sushi.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sushi.game.asset.AssetService;
import com.sushi.game.asset.MapAsset;
import com.sushi.game.system.RenderSystem;


public class GameScreen extends ScreenAdapter {
    private final SushiGame game;
    private final Batch batch;
    private final AssetService assetService;
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final Engine engine;



    public GameScreen(SushiGame game) {
        this.game = game;
        this.assetService = game.getAssetService();
        this.viewport = game.getViewport();
        this.camera = game.getCamera();
        this.batch = game.getBatch();
        this.engine = new Engine();

        this.engine.addSystem( new RenderSystem(this.batch,this.viewport));

    }

    @Override
    public void show() {
        this.assetService.load(MapAsset.MAIN);
        this.engine.getSystem(RenderSystem.class).setMap(this.assetService.get(MapAsset.MAIN));
    }

    @Override
    public void hide() {
        this.engine.removeAllEntities();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/30f);
        this.engine.update(delta);

    }

    @Override
    public  void dispose() {
        for (EntitySystem system : this.engine.getSystems()) {
            if(system instanceof Disposable disposableSytstem){
                disposableSytstem.dispose();
            }
        }

    }
}
