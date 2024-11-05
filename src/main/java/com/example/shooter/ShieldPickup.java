package com.example.shooter;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class ShieldPickup extends Group implements Pickup {

    public ShieldPickup(double x, double y) {
        double radius = 15;
        Polygon body = new Polygon(
                0, -radius,
                Math.sin(Math.PI / 3) * radius, -radius / 2,
                Math.sin(Math.PI / 3) * radius, radius / 2,
                0, radius,
                -Math.sin(Math.PI / 3) * radius, radius / 2,
                -Math.sin(Math.PI / 3) * radius, -radius / 2
        );
        body.setFill(Color.CYAN);
        this.getChildren().add(body);
        this.setTranslateX(x);
        this.setTranslateY(y);
    }
    @Override
    public void effect(Player player) {
        player.activateShield();
        Thread shieldTimer = new Thread(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            player.deactivateShield();
        });
        shieldTimer.start();
    }

    @Override
    public boolean isPickedUp(Player player) {
        return this.getBoundsInParent().intersects(player.getBoundsInParent());
    }
}
