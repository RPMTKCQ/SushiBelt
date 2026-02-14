package com.sushi.game.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Physic implements Component {
    public static final ComponentMapper<Physic> MAPPER = ComponentMapper.getFor(Physic.class);

    private final Body body;
    private final Vector2 previousPosition;


    public Physic(Body body) {
        this.body = body;
        this.previousPosition = new Vector2(body.getPosition());
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

}

