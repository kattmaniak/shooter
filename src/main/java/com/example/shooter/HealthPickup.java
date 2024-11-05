package com.example.shooter;

import javafx.animation.Animation;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class HealthPickup extends Group implements Pickup{

    public HealthPickup(double x, double y) {
        Circle heartTopLeft = new Circle(5);
        heartTopLeft.setFill(Color.RED);
        heartTopLeft.setTranslateX(-5);
        heartTopLeft.setTranslateY(-5);
        Circle heartTopRight = new Circle(5);
        heartTopRight.setFill(Color.RED);
        heartTopRight.setTranslateX(5);
        heartTopRight.setTranslateY(-5);
        Polygon heartBottom = new Polygon();
        heartBottom.setFill(Color.RED);
        heartBottom.getPoints().addAll(-10.0, -5.0,
                0.0, 13.0,
                10.0, -5.0);
        this.getChildren().addAll(heartTopLeft, heartTopRight, heartBottom);
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    @Override
    public void effect(Player player) {
        if(player.getHp() == player.getMaxHp()){
            return;
        }
        player.heal(1);
        Rectangle playerHealthBar = player.getHealthBar();
        Animation animation = new javafx.animation.Transition() {
            {
                setCycleDuration(javafx.util.Duration.millis(1000));
            }

            protected void interpolate(double frac) {
                playerHealthBar.setWidth(280 * (((double)player.getHp()-1+frac)/(double)player.getMaxHp()));
            }
        };
        animation.playFromStart();
    }

    @Override
    public boolean isPickedUp(Player player) {
        return this.getBoundsInParent().intersects(player.getBoundsInParent());
    }
}
