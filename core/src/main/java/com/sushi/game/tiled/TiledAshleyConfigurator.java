package com.sushi.game.tiled;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.sushi.game.SushiGame;
import com.sushi.game.asset.AssetService;
import com.sushi.game.component.Controller;
import com.sushi.game.component.Graphic;
import com.sushi.game.component.Move;
import com.sushi.game.component.Transform;

public class TiledAshleyConfigurator {
    private static final Vector2 DEFAULT_PHYSIC_SCALING = new Vector2(1f, 1f);

    private final Engine engine;
    private final AssetService assetService;
    private final World physicWorld;

    public TiledAshleyConfigurator(Engine engine, AssetService assetService, World physicWorld) {
        this.engine = engine;
        this.assetService = assetService;
        this.physicWorld = physicWorld;
    }

    public void onLoadTile(TiledMapTile tiledMapTile, float x, float y) {
        createBody(
            tiledMapTile.getObjects(),
            new Vector2(x, y),
            DEFAULT_PHYSIC_SCALING,
            BodyDef.BodyType.StaticBody,
            Vector2.Zero,
            "environment"
        );
    }

    private Body createBody(MapObjects mapObjects,
                            Vector2 position,
                            Vector2 scaling,
                            BodyDef.BodyType bodyType,
                            Vector2 relativeTo,
                            String UserData) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(position);
        bodyDef.fixedRotation = true;

        Body body = physicWorld.createBody(bodyDef);
        body.setUserData(UserData);
        for (MapObject object : mapObjects) {
            FixtureDef fixtureDef = TiledPhysics.fixtureDef(object, scaling, relativeTo);
            Fixture fixture = body.createFixture(fixtureDef);
            fixture.setUserData(object.getName());
            fixtureDef.shape.dispose();
        }
        return body;
    }

    public void onLoadObject(TiledMapTileMapObject tileMapObject) {
        Entity entity = this.engine.createEntity();
        TiledMapTile tile = tileMapObject.getTile();
        TextureRegion textureRegion = getTextureRegion(tile);
        int z = tile.getProperties().get("z",1, Integer.class);

        entity.add(new Graphic(Color.WHITE.cpy(), textureRegion));
        addEntityTransform(
            tileMapObject.getX(), tileMapObject.getY(), z,
            textureRegion.getRegionWidth(), textureRegion.getRegionHeight(),
            tileMapObject.getScaleX(), tileMapObject.getScaleY(),entity
        );
        addEntityController(tileMapObject, entity);
        addEntityMove(tile, entity);


        this.engine.addEntity(entity);
    }

    private void addEntityMove(TiledMapTile tile, Entity entity) {
        float speed = tile.getProperties().get("speed", 0f, Float.class);
        if(speed == 0f) return;

        entity.add(new Move(speed));
    }

    private void addEntityController(TiledMapTileMapObject tileMapObject, Entity entity) {
        Boolean controller = tileMapObject.getProperties().get("controller", false, boolean.class);
        if(!controller) return;

        entity.add(new Controller());
    }

    private void addEntityTransform(
        float x, float y, int z,
        float w, float h,
        float scaleX, float scaleY,
        Entity entity
    ) {
        Vector2 position = new Vector2(x, y);
        Vector2 size = new Vector2(w , h);
        Vector2 scaling = new Vector2(scaleX, scaleY);

        position.scl(SushiGame.UNIT_SCALE);
        size.scl(SushiGame.UNIT_SCALE);

        entity.add(new Transform(position, z, size, scaling,0f));
    }

    private TextureRegion getTextureRegion(TiledMapTile tile) {
        return tile.getTextureRegion();
    }


}
