package com.sushi.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.sushi.game.component.Controller;
import com.sushi.game.component.Move;
import com.sushi.game.component.Transform;

import static com.sushi.game.SushiGame.WORLD_HEIGHT;
import static com.sushi.game.SushiGame.WORLD_WIDTH;

public class ControllerSystem extends IteratingSystem {

    // Friction/Acceleration factor (0 to 1).
    // Higher = snappy/instant, Lower = slippery/weighty.
    private static final float LERP_FACTOR = 1f;

    public ControllerSystem() {
        super(Family.all(Controller.class, Move.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        Transform transform = Transform.MAPPER.get(entity);

        // 1. DETERMINE TARGET DIRECTION
        // Instead of adding/subtracting, we define where we WANT to go this frame.
        float targetX = 0;
        float targetY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    targetY += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))  targetY -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))  targetX -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) targetX += 1;

        // 2. APPLY FRICTION / SMOOTHING (LERP)
        // We move the current direction slightly toward the target direction.
        // If target is 0, this acts as friction.
        move.getDirection().x = lerp(move.getDirection().x, targetX, LERP_FACTOR);
        move.getDirection().y = lerp(move.getDirection().y, targetY, LERP_FACTOR);

        // 3. DEADZONE CLEANUP
        // Stop the "infinite micro-sliding" when the values get very small.
        if (Math.abs(move.getDirection().x) < 0.01f) move.getDirection().x = 0;
        if (Math.abs(move.getDirection().y) < 0.01f) move.getDirection().y = 0;

        // 4. BOUNDARY CLAMPING
        if (transform != null) {
            clampToWorld(transform);
        }
    }

    /**
     * Standard Linear Interpolation formula: start + (end - start) * alpha
     */
    private float lerp(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    private void clampToWorld(Transform transform) {
        Vector2 pos = transform.getPosition();
        Vector2 size = transform.getSize();

        if (pos.x < 0) pos.x = 0;
        else if (pos.x > WORLD_WIDTH - size.x) pos.x = WORLD_WIDTH - size.x;

        if (pos.y < 0) pos.y = 0;
        else if (pos.y > WORLD_HEIGHT - size.y) pos.y = WORLD_HEIGHT - size.y;
    }
}
