package com.sushi.game.tiled;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.sushi.game.SushiGame;

public class TiledPhysics {

    public static FixtureDef fixtureDef(MapObject mapObject, Vector2 scaling, Vector2 relativeTo) {
        if(mapObject instanceof RectangleMapObject rectMapObj){
            return rectangleFixtureDef(rectMapObj, scaling, relativeTo);

        } else {
            throw new GdxRuntimeException("Unsupported MapObject " + mapObject);
        }
    }

    private static FixtureDef rectangleFixtureDef(RectangleMapObject rectMapObj, Vector2 scaling, Vector2 relativeTo) {
        Rectangle rectangle = rectMapObj.getRectangle();
        float rectX = rectangle.x;
        float rectY = rectangle.y;
        float rectW = rectangle.width;
        float rectH = rectangle.height;

        float boxX = rectX * SushiGame.UNIT_SCALE * scaling.x - relativeTo.x;
        float boxY = rectY * SushiGame.UNIT_SCALE * scaling.y - relativeTo.y;
        float boxW = rectW * SushiGame.UNIT_SCALE * scaling.x * 0.5f;
        float boxH = rectH * SushiGame.UNIT_SCALE * scaling.y * 0.5f;


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boxW, boxH, new Vector2(boxX + boxW, boxY + boxH), 0f);
        return fixtureDefOfMapObjectAndShape(rectMapObj, shape);
    }

    private static FixtureDef fixtureDefOfMapObjectAndShape(MapObject mapObject, PolygonShape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = mapObject.getProperties().get("friction", 0.0f, Float.class);
        fixtureDef.restitution = mapObject.getProperties().get("restitution", 0.0f, Float.class);
        fixtureDef.density = mapObject.getProperties().get("density", 0.0f, Float.class);
        fixtureDef.isSensor = mapObject.getProperties().get("sensor", false, Boolean.class);
        return fixtureDef;
    }
}
