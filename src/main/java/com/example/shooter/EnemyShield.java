package com.example.shooter;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class EnemyShield extends Group {
    public EnemyShield(double x, double y, double angle) {
        super();
        Polygon shield = new Polygon(
                -45, -5,
                -45, 5,
                45, 5,
                45, -5
        );
        shield.setFill(javafx.scene.paint.Color.BROWN);
        super.getChildren().add(shield);
        this.setTranslateX(x + Constants.WINDOW_WIDTH / 2);
        this.setTranslateY(y + Constants.WINDOW_HEIGHT / 2);
        this.setRotate(angle);
    }

    public boolean handleCollision(Bullet bullet) {
        Bounds bounds = bullet.getBoundsInParent();
        return this.getBoundsInLocal().intersects(parentToLocal(bounds));
    }

    public void toggle() {
        this.setVisible(!this.isVisible());
    }
}
