package com.example.shooter;

import javafx.animation.Animation;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import static com.example.shooter.Constants.WINDOW_HEIGHT;
import static com.example.shooter.Constants.WINDOW_WIDTH;

public class Enemy extends Group {
    private Polygon gun;
    private Rectangle counterweight;
    private Circle body;
    private int maxHp;
    private int hp;

    public Enemy(double x, double y, int hp) {
        double radius = 25;
        this.hp = hp;
        this.maxHp = hp;
        body = new Circle(radius);
        body.setFill(Color.RED);

        gun = new Polygon(
                0, -3,
                0, 3,
                35, 3,
                41, 6,
                41, -6,
                35, -3
        );
        gun.setFill(Color.PURPLE);
        gun.setStroke(Color.BLACK);

        counterweight = new Rectangle(6, 41);
        counterweight.setFill(Color.TRANSPARENT);
        counterweight.getTransforms().addAll(new Translate(0, -3), new Rotate(90));


        super.getChildren().add(body);
        super.getChildren().add(gun);
        super.getChildren().add(counterweight);
        this.setTranslateX(x + WINDOW_WIDTH / 2);
        this.setTranslateY(y + WINDOW_HEIGHT / 2);
    }

    public void updateGunAimAt(double x, double y) {
        double dx = x - this.getTranslateX();
        double dy = y - this.getTranslateY();

        double angle = Math.toDegrees(Math.atan2(dy, dx));
        this.setRotate(angle);
    }

    public Bullet shoot() {
        double angle = this.getRotate();
        double x = Math.cos(Math.toRadians(angle))/10;
        double y = Math.sin(Math.toRadians(angle))/10;
        double barrelEndX = this.getTranslateX() + 41 * Math.cos(Math.toRadians(angle));
        double barrelEndY = this.getTranslateY() + 41 * Math.sin(Math.toRadians(angle));
        this.gun.setTranslateX(-10);
        this.counterweight.setTranslateX(10);
        Animation resetGun = new javafx.animation.Transition() {
            {
                setCycleDuration(javafx.util.Duration.millis(250));
            }

            protected void interpolate(double frac) {
                gun.setTranslateX(-10 + 10 * frac);
                counterweight.setTranslateX(10 - 10 * frac);
            }
        };
        resetGun.playFromStart();

        return new Bullet(5, new Translate(barrelEndX, barrelEndY), new Point2D(x, y), false);
    }

    public boolean handleCollision(Bullet bullet) {

        Bounds bounds = super.getBoundsInParent();

        if(bounds.intersects(bullet.getBoundsInParent()) && bullet.isPlayerBullet()){
            hp -= bullet.getDamage();
            body.setOpacity((double)hp/(double)maxHp);
            return true;
        }
        return false;
    }

    public boolean isDead() {
        return hp <= 0;
    }
}
