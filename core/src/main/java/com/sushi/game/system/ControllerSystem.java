package com.sushi.game.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.sushi.game.component.Controller;
import com.sushi.game.component.Move;
import com.sushi.game.component.Transform;
import com.sushi.game.input.Command;

import static com.sushi.game.SushiGame.WORLD_HEIGHT;
import static com.sushi.game.SushiGame.WORLD_WIDTH;

public class ControllerSystem extends IteratingSystem {

    public ControllerSystem() {
        super(Family.all(Controller.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Controller controller = Controller.MAPPER.get(entity);
        Move move = Move.MAPPER.get(entity); // Defined here so both loops can see it

        if (move == null) return;

        for (Command command : controller.getPressedCommands()) {
            switch (command) {
                case UP    -> moveEntity(move, 0f, 1f);
                case DOWN  -> moveEntity(move, 0f, -1f);
                case LEFT  -> moveEntity(move, -1f, 0f);
                case RIGHT -> moveEntity(move, 1f, 0f);
            }
        }
        controller.getPressedCommands().clear();


        for (Command command : controller.getReleasedCommands()) {
            switch (command) {
                case UP    -> moveEntity(move, 0f, -1f);
                case DOWN  -> moveEntity(move, 0f, 1f);
                case LEFT  -> moveEntity(move, 1f, 0f);
                case RIGHT -> moveEntity(move, -1f, 0f);
            }
        }
        controller.getReleasedCommands().clear();

        // ... (After your Pressed and Released loops) ...

        Transform transform = Transform.MAPPER.get(entity);
        if (transform != null) {
            Vector2 pos = transform.getPosition();
            Vector2 size = transform.getSize();

            // Clamp X within world units
            // 0 is the left wall, WORLD_WIDTH - size.x is the right wall
            if (pos.x < 0) {
                pos.x = 0;
            } else if (pos.x > WORLD_WIDTH - size.x) {
                pos.x = WORLD_WIDTH - size.x;
            }

            // Clamp Y within world units
            // 0 is the bottom wall, WORLD_HEIGHT - size.y is the top wall
            if (pos.y < 0) {
                pos.y = 0;
            } else if (pos.y > WORLD_HEIGHT - size.y) {
                pos.y = WORLD_HEIGHT - size.y;
            }
        }


        if (Math.abs(move.getDirection().x) < 0.1f) move.getDirection().x = 0;
        if (Math.abs(move.getDirection().y) < 0.1f) move.getDirection().y = 0;
    }

    private void moveEntity(Move move, float directionX, float directionY) {
        move.getDirection().x += directionX;
        move.getDirection().y += directionY;
    }
}
