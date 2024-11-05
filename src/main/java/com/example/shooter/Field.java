package com.example.shooter;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;

import static com.example.shooter.Constants.WINDOW_HEIGHT;
import static com.example.shooter.Constants.WINDOW_WIDTH;

public class Field {
    private Translate position;
    private String fieldType;
    private Shape shape;

    public Field(String fieldSelection) {

        this.position = new Translate(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        this.fieldType = fieldSelection;
        System.out.println(fieldSelection);

        switch (fieldSelection) {
            case "O":
                this.shape = new Circle(250);
                this.shape.setFill(new ImagePattern(new Image("grass.jpg")));
                break;
            case "X":
                this.shape = new Polygon(
                        0, -100,
                        -150, -250,
                        -250, -150,
                        -100, 0,
                        -250, 150,
                        -150, 250,
                        0, 100,
                        150, 250,
                        250, 150,
                        100, 0,
                        250, -150,
                        150, -250
                );
                this.shape.setFill(new ImagePattern(new Image("sand.jpg")));
                break;
            case "I":
                this.shape = new Polygon(
                        -250, -250,
                        -250, -150,
                        -100, -150,
                        -100, 150,
                        -250, 150,
                        -250, 250,
                        250, 250,
                        250, 150,
                        100, 150,
                        100, -150,
                        250, -150,
                        250, -250
                );
                this.shape.setFill(new ImagePattern(new Image("metal.jpg")));
                break;
            default:
                throw new IllegalArgumentException("Invalid field type " + fieldSelection);
        }

        shape.getTransforms().add(position);
    }

    public Translate getPosition() {
        return this.position;
    }

    public Shape getShape() {
        return shape;
    }

    public List<Enemy> setUpEnemies(String difficultySelection) {
        int enemyHP;
        switch (difficultySelection) {
            case "Easy":
                enemyHP = 2;
                break;
            case "Medium":
                enemyHP = 4;
                break;
            case "Hard":
                enemyHP = 8;
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty selection " + difficultySelection);
        }
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        switch (fieldType) {
            case "O":
                enemies.add(new Enemy(250, 250, enemyHP));
                enemies.add(new Enemy(-250, -250, enemyHP));
                enemies.add(new Enemy(250, -250, enemyHP));
                enemies.add(new Enemy(-250, 250, enemyHP));
                enemies.add(new Enemy(0, 300, enemyHP));
                enemies.add(new Enemy(0, -300, enemyHP));
                enemies.add(new Enemy(300, 0, enemyHP));
                enemies.add(new Enemy(-300, 0, enemyHP));
                break;
            case "X":
                enemies.add(new Enemy(250, 0, enemyHP));
                enemies.add(new Enemy(0, 250, enemyHP));
                enemies.add(new Enemy(-250, 0, enemyHP));
                enemies.add(new Enemy(0, -250, enemyHP));
                break;
            case "I":
                enemies.add(new Enemy(250, 0, enemyHP));
                enemies.add(new Enemy(-250, 0, enemyHP));
                enemies.add(new Enemy(200, 100, enemyHP));
                enemies.add(new Enemy(-200, 100, enemyHP));
                enemies.add(new Enemy(200, -100, enemyHP));
                enemies.add(new Enemy(-200, -100, enemyHP));

                break;

        }
        return enemies;
    }

    public ArrayList<EnemyShield> setUpEnemyShields() {
        ArrayList<EnemyShield> shields = new ArrayList<EnemyShield>();
        switch (fieldType) {
            case "O":
                shields.add(new EnemyShield(200, 200, -45));
                shields.add(new EnemyShield(-200, -200, -45));
                shields.add(new EnemyShield(200, -200, 45));
                shields.add(new EnemyShield(-200, 200, 45));
                shields.add(new EnemyShield(0, 260, 0));
                shields.add(new EnemyShield(0, -260, 0));
                shields.add(new EnemyShield(260, 0, 90));
                shields.add(new EnemyShield(-260, 0, 90));
                break;
            case "X":
                shields.add(new EnemyShield(200, 0, 90));
                shields.add(new EnemyShield(0, 200, 0));
                shields.add(new EnemyShield(-200, 0, 90));
                shields.add(new EnemyShield(0, -200, 0));
                break;
            case "I":
                shields.add(new EnemyShield(200, 0, 90));
                shields.add(new EnemyShield(-200, 0, 90));
                shields.add(new EnemyShield(150, 75, -60));
                shields.add(new EnemyShield(-150, 75, 60));
                shields.add(new EnemyShield(150, -75, 60));
                shields.add(new EnemyShield(-150, -75, -60));
                break;
        }
        return shields;
    }
}
