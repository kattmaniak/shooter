package com.example.shooter;

import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class CoinPickup extends Group implements Pickup{


    public CoinPickup(double x, double y) {
        Circle coin = new Circle(10);
        coin.setFill(javafx.scene.paint.Color.YELLOW);
        super.getChildren().add(coin);
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    @Override
    public void effect(Player player) {
        player.addPowerShot();
    }

    @Override
    public boolean isPickedUp(Player player) {
        return this.getBoundsInParent().intersects(player.getBoundsInParent());
    }
}
