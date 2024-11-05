package com.example.shooter;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Collection;

import static com.example.shooter.Constants.WINDOW_HEIGHT;
import static com.example.shooter.Constants.WINDOW_WIDTH;

public class Player extends Group {
    private double radius;
    private double mouseX;
    private double mouseY;
    private Translate position;
    private Rotate rotate;
    private int hp;
    private int maxHp;
    private double speed;
    private Rectangle healthBar;
    private int pshots = 4;
    private ArrayList<Rectangle> powerShots;
    private Circle shield;
    private int shieldActive = 0;

    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    public Player(double radius, Translate position, String tankSelection) {
        this.radius = radius;
        this.position = position;
        this.shield = new Circle(radius + 5);
        this.shield.setFill(Color.TRANSPARENT);
        this.shield.setStroke(Color.CYAN);
        this.shield.setStrokeWidth(3);
        this.shield.setVisible(false);

        powerShots = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Rectangle powerShot = new Rectangle(10, 40);
            powerShot.setFill(Color.ORANGE);
            powerShot.setTranslateX(WINDOW_WIDTH - 40 - 20 * i);
            powerShot.setTranslateY(20);
            powerShots.add(powerShot);
        }

        Shape body;
        switch(tankSelection){
            case "Light":
                body = new Circle(radius, Color.YELLOW);
                speed = 2;
                hp = 2;
                maxHp = 2;
                break;
            case "Medium":
                //hexagon body
                body = new Polygon(
                        0, -radius,
                        Math.sin(Math.PI / 3) * radius, -radius / 2,
                        Math.sin(Math.PI / 3) * radius, radius / 2,
                        0, radius,
                        -Math.sin(Math.PI / 3) * radius, radius / 2,
                        -Math.sin(Math.PI / 3) * radius, -radius / 2

                );
                body.setFill(Color.YELLOW);
                speed = 1;
                hp = 4;
                maxHp = 4;
                break;
            case "Heavy":
                //plus shaped body
                body = new Polygon(
                        -radius / 2, -radius,
                        -radius / 2, -radius / 2,
                        -radius, -radius / 2,
                        -radius, radius / 2,
                        -radius / 2, radius / 2,
                        -radius / 2, radius,
                        radius / 2, radius,
                        radius / 2, radius / 2,
                        radius, radius / 2,
                        radius, -radius / 2,
                        radius / 2, -radius / 2,
                        radius / 2, -radius
                );
                body.setFill(Color.YELLOW);
                speed = 0.5;
                hp = 8;
                maxHp = 8;
                break;
            default:
                throw new IllegalArgumentException("Invalid tank selection");

        }
        body.setStroke(Color.INDIGO);

        this.rotate = new Rotate();

        final double gunWidth = 0.3 * radius;
        final double gunHeight = 2 * radius;
        Rectangle gun = new Rectangle(gunWidth, gunHeight);
        gun.setFill(Color.PURPLE);
        gun.getTransforms().addAll(
                this.rotate,
                new Translate(-gunWidth / 2, 0)
        );

        super.getChildren().addAll(body, gun, shield);

        super.getTransforms().add(position);
    }

    public boolean isShieldActive() {
        return this.shield.isVisible();
    }

    private void updateGun() {
        Point2D vector = new Point2D(
                this.mouseX - this.position.getX(),
                this.mouseY - this.position.getY()
        ).normalize();

        double angle = -Math.signum(vector.getX()) * vector.angle(0, 1);
        this.rotate.setAngle(angle);
    }



    public void move(Field field) {
        double stepX = (movingRight ? speed : 0) - (movingLeft ? speed : 0);
        double stepY = (movingDown ? speed : 0) - (movingUp ? speed : 0);
        double newX = this.position.getX() + stepX;
        double newY = this.position.getY() + stepY;

        if (field.getShape().contains(new Point2D(newX - WINDOW_WIDTH / 2, newY - WINDOW_HEIGHT / 2))) {
            this.position.setX(newX);
            this.position.setY(newY);

            this.updateGun();
        }
    }

    public Bullet handleMouseEvent(MouseEvent mouseEvent) {
        this.mouseX = mouseEvent.getX();
        this.mouseY = mouseEvent.getY();

        this.updateGun();

        Bullet bullet = null;

        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED) && mouseEvent.isPrimaryButtonDown() && !mouseEvent.isSecondaryButtonDown()){
            Point2D speed = new Point2D(
                    this.mouseX - this.position.getX(),
                    this.mouseY - this.position.getY()
            ).normalize();

            Point2D offset = speed.multiply(2.1 * radius);

            double x = this.position.getX() + offset.getX();
            double y = this.position.getY() + offset.getY();

            Translate position = new Translate(x, y);

            bullet = new Bullet(0.3 * radius, position, speed.multiply(0.25), true, 1);
        }
        if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_PRESSED) && mouseEvent.isSecondaryButtonDown() && !mouseEvent.isPrimaryButtonDown() && pshots > 0){
            pshots--;
            Point2D speed = new Point2D(
                    this.mouseX - this.position.getX(),
                    this.mouseY - this.position.getY()
            ).normalize();

            Point2D offset = speed.multiply(2.1 * radius);

            double x = this.position.getX() + offset.getX();
            double y = this.position.getY() + offset.getY();

            Translate position = new Translate(x, y);

            bullet = new Bullet(0.3 * radius, position, speed.multiply(0.25), true, 2);
        }

        return bullet;
    }

    public boolean handleCollision(Bullet bullet) {
        return super.getBoundsInParent().intersects(bullet.getBoundsInParent()) && !bullet.isPlayerBullet();
    }

    public void takeDamage() {
        this.hp--;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void heal(int i) {
        hp += i;
    }

    public Rectangle getHealthBar() {
        return healthBar;
    }

    public void setHealthBar(Rectangle playerHealthBar) {
        this.healthBar = playerHealthBar;
    }

    public void addPowerShot() {
        pshots++;
        Rectangle powerShot = new Rectangle(10, 40);
        powerShot.setFill(Color.ORANGE);
        powerShot.setTranslateX(WINDOW_WIDTH - 40 - 20 * powerShots.size());
        powerShot.setTranslateY(20);
        powerShots.add(powerShot);
        ((Group)this.getParent()).getChildren().add(powerShots.get(powerShots.size()-1));

    }

    public ArrayList<Rectangle> getPowerShots() {
        return powerShots;
    }

    public void activateShield() {
        shield.setVisible(true);
        shieldActive++;
    }

    public void deactivateShield() {
        shieldActive--;
        if(shieldActive == 0){
            shield.setVisible(false);
        }
    }
}
