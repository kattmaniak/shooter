package com.example.shooter;

import com.example.shooter.timer.MyTimer;
import com.example.shooter.timer.Updatable;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.shooter.Constants.*;

public class JavaFXApplication extends Application {

    private boolean gameOver = false;

    @Override
    public void start(Stage stage) throws IOException {
        Group menuRoot = new Group();
        Scene menuScene = new Scene(menuRoot, WINDOW_WIDTH, WINDOW_HEIGHT);

        ToggleGroup fieldSelection = new ToggleGroup();
        RadioButton fieldX = new RadioButton("X");
        RadioButton fieldO = new RadioButton("O");
        RadioButton fieldI = new RadioButton("I");
        fieldX.setToggleGroup(fieldSelection);
        fieldO.setToggleGroup(fieldSelection);
        fieldO.setSelected(true);
        fieldI.setToggleGroup(fieldSelection);
        Tooltip fieldXTooltip = new Tooltip("X shaped field");
        fieldX.setTooltip(fieldXTooltip);
        Tooltip fieldOTooltip = new Tooltip("O shaped field");
        fieldO.setTooltip(fieldOTooltip);
        Tooltip fieldITooltip = new Tooltip("I shaped field");
        fieldI.setTooltip(fieldITooltip);
        Label fieldSelectionLabel = new Label("Select field:");

        ToggleGroup difficultySelection = new ToggleGroup();
        RadioButton easy = new RadioButton("Easy");
        RadioButton medium = new RadioButton("Medium");
        RadioButton hard = new RadioButton("Hard");
        easy.setToggleGroup(difficultySelection);
        medium.setToggleGroup(difficultySelection);
        medium.setSelected(true);
        hard.setToggleGroup(difficultySelection);
        Tooltip easyTooltip = new Tooltip("Enemies have half health");
        easy.setTooltip(easyTooltip);
        Tooltip mediumTooltip = new Tooltip("Enemies have normal health");
        medium.setTooltip(mediumTooltip);
        Tooltip hardTooltip = new Tooltip("Enemies have double health");
        hard.setTooltip(hardTooltip);
        Label difficultySelectionLabel = new Label("Select difficulty:");

        ToggleGroup tankSelection = new ToggleGroup();
        RadioButton heavyTank = new RadioButton("Heavy");
        RadioButton mediumTank = new RadioButton("Medium");
        RadioButton lightTank = new RadioButton("Light");
        heavyTank.setToggleGroup(tankSelection);
        mediumTank.setToggleGroup(tankSelection);
        mediumTank.setSelected(true);
        lightTank.setToggleGroup(tankSelection);
        Tooltip heavyTankTooltip = new Tooltip("Double health but half speed");
        heavyTank.setTooltip(heavyTankTooltip);
        Tooltip mediumTankTooltip = new Tooltip("Normal health and speed");
        mediumTank.setTooltip(mediumTankTooltip);
        Tooltip lightTankTooltip = new Tooltip("Half health but double speed");
        lightTank.setTooltip(lightTankTooltip);
        Label tankSelectionLabel = new Label("Select tank:");

        GridPane selectionGroup = new GridPane();
        selectionGroup.addColumn(0, fieldSelectionLabel, fieldX, fieldO, fieldI);
        selectionGroup.addColumn(1, difficultySelectionLabel, easy, medium, hard);
        selectionGroup.addColumn(2, tankSelectionLabel, heavyTank, mediumTank, lightTank);
        selectionGroup.setVgap(10);
        selectionGroup.setHgap(10);
        selectionGroup.setMinWidth(WINDOW_WIDTH - 100);
        selectionGroup.setMaxWidth(WINDOW_WIDTH - 100);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(25);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(25);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setPercentWidth(25);
        selectionGroup.getColumnConstraints().addAll(column1, column2, column3, column4);

        Button startButton = new Button("Start");
        selectionGroup.add(startButton, 3, 0);
        startButton.setOnAction(e -> stage.setScene(startGame(((RadioButton) fieldSelection.getSelectedToggle()).getText(), ((RadioButton) difficultySelection.getSelectedToggle()).getText(), ((RadioButton) tankSelection.getSelectedToggle()).getText())));

        Label title = new Label("Shooter");
        title.setFont(javafx.scene.text.Font.font("Impact", 50));
        GridPane menuGrid = new GridPane();
        menuGrid.addColumn(0, title, selectionGroup);
        menuGrid.setAlignment(javafx.geometry.Pos.CENTER);
        menuGrid.setPrefWidth(WINDOW_WIDTH);
        menuGrid.setPrefHeight(WINDOW_HEIGHT);
        menuRoot.getChildren().addAll(menuGrid);
        stage.setResizable(false);
        stage.setTitle("Shooter");
        menuScene.setFill(Color.LIGHTCYAN);
        stage.setScene(menuScene);
        stage.show();
    }

    private Scene startGame(String fieldSelection, String difficultySelection, String tankSelection) {
        Field field = new Field(fieldSelection);

        //field.setFill ( Color.GREEN );
        //field.getShape().setFill(new ImagePattern(new Image("grass.jpg")));

        Translate playerPosition = new Translate(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        Player player = new Player(PLAYER_RADIUS, playerPosition, tankSelection);

        Group root = new Group(field.getShape(), player);

        Rectangle playerHealthBar = new Rectangle(280, 20);
        playerHealthBar.setFill(Color.GREEN);
        playerHealthBar.setTranslateX(WINDOW_WIDTH/2 - 140);
        playerHealthBar.setTranslateY(WINDOW_HEIGHT - 40);
        root.getChildren().add(playerHealthBar);
        player.setHealthBar(playerHealthBar);

        root.getChildren().addAll(player.getPowerShots());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(new ImagePattern(new Image("water.jpg")));

        MyTimer timer = new MyTimer();
        timer.start();

        MyTimer pickupTimer = new MyTimer();
        pickupTimer.start();

        AtomicLong pickupTime = new AtomicLong(0L);
        Updatable pickupsLoop = dns -> {
            if (pickupTime.get() >= 2e9) {
                pickupTime.set(0L);
                double rnd = Math.random();
                if (rnd < 0.3) {
                    double x = Math.random() * WINDOW_WIDTH;
                    double y = Math.random() * WINDOW_HEIGHT;
                    while (!field.getShape().contains(x-WINDOW_HEIGHT/2, y-WINDOW_HEIGHT/2)) {
                        x = Math.random() * WINDOW_WIDTH;
                        y = Math.random() * WINDOW_HEIGHT;
                    }
                    HealthPickup healthPickup = new HealthPickup(x,y);
                    root.getChildren().add(healthPickup);
                    Updatable healthPickupLoop = dns1 -> {
                        if (healthPickup.isPickedUp(player)) {
                            healthPickup.effect(player);
                            root.getChildren().remove(healthPickup);
                            return true;
                        }
                        return false;
                    };
                    pickupTimer.add(healthPickupLoop);
                } else if (rnd < 0.6) {
                    double x = Math.random() * WINDOW_WIDTH;
                    double y = Math.random() * WINDOW_HEIGHT;
                    while (!field.getShape().contains(x-WINDOW_HEIGHT/2, y-WINDOW_HEIGHT/2)) {
                        x = Math.random() * WINDOW_WIDTH;
                        y = Math.random() * WINDOW_HEIGHT;
                    }
                    CoinPickup powerPickup = new CoinPickup(x,y);
                    root.getChildren().add(powerPickup);
                    Updatable powerPickupLoop = dns1 -> {
                        if (powerPickup.isPickedUp(player)) {
                            powerPickup.effect(player);
                            root.getChildren().remove(powerPickup);
                            return true;
                        }
                        return false;
                    };
                    pickupTimer.add(powerPickupLoop);
                } else if (rnd < 0.75) {
                    double x = Math.random() * WINDOW_WIDTH;
                    double y = Math.random() * WINDOW_HEIGHT;
                    while (!field.getShape().contains(x-WINDOW_HEIGHT/2, y-WINDOW_HEIGHT/2)) {
                        x = Math.random() * WINDOW_WIDTH;
                        y = Math.random() * WINDOW_HEIGHT;
                    }
                    ShieldPickup shieldPickup = new ShieldPickup(x,y);
                    root.getChildren().add(shieldPickup);
                    Updatable shieldPickupLoop = dns1 -> {
                        if (shieldPickup.isPickedUp(player)) {
                            shieldPickup.effect(player);
                            root.getChildren().remove(shieldPickup);
                            return true;
                        }
                        return false;
                    };
                    pickupTimer.add(shieldPickupLoop);
                }
            } else {
                pickupTime.addAndGet(dns);
            }
            return false;
        };

        timer.add(pickupsLoop);

        AtomicLong time = new AtomicLong();
        DateFormat df = new SimpleDateFormat("mm:ss");
        Label elapsedTime = new Label(df.format(time.get()));
        elapsedTime.setFont(javafx.scene.text.Font.font("Arial", 20));
        AtomicLong timeD = new AtomicLong();
        Updatable timeLoop = dns -> {
            if(timeD.get() >= 1e9){
                time.getAndAdd(1000);
                elapsedTime.setText(df.format(time.get()));
                timeD.set(0);
            }else {
                timeD.addAndGet(dns);
            }
            return false;
        };
        timer.add(timeLoop);
        elapsedTime.setTranslateX(20);
        elapsedTime.setTranslateY(20);
        root.getChildren().add(elapsedTime);

        MyTimer bulletTimer = new MyTimer();
        bulletTimer.start();

        List<EnemyShield> enemyShields = field.setUpEnemyShields();
        root.getChildren().addAll(enemyShields);

        AtomicReference<Long> lastShieldTime = new AtomicReference<>(0L);
        Updatable enemyShieldLoop = dns -> {
            if(lastShieldTime.get() >= 5e9) {
                lastShieldTime.set(0L);
                for (EnemyShield enemyShield : enemyShields) {
                    enemyShield.toggle();
                }
            } else {
                lastShieldTime.updateAndGet(v -> v + dns);
            }
            return false;
        };

        timer.add(enemyShieldLoop);

        List<Enemy> enemies = field.setUpEnemies(difficultySelection);
        root.getChildren().addAll(enemies);


        AtomicReference<Long> lastTime = new AtomicReference<>(0L);
        AtomicReference<Long> limit = new AtomicReference<>(new Random().nextLong(0, (long) 2e9));
        Updatable enemyShootLoop = dns -> {
            if(enemies.isEmpty()){
                return true;
            }
            Enemy chosenEnemy = enemies.get(new Random().nextInt(enemies.size()));
            if (lastTime.get() <= limit.get()) {
                lastTime.updateAndGet(v -> v + dns);
                return chosenEnemy.isDead();
            } else {
                lastTime.set(0L);
                limit.set(new Random().nextLong(0, (long) 2e9));

                Bullet bullet = chosenEnemy.shoot();
                root.getChildren().add(bullet);

                Updatable bulletLoop = dns1 -> {
                    boolean outOfBounds = bullet.update(dns1, 0, WINDOW_WIDTH, 0, WINDOW_HEIGHT);

                    boolean collided = player.handleCollision(bullet);
                    if (collided) {
                        if(player.isShieldActive()){
                            bullet.bounceOff(player);
                            return false;
                        }
                        player.takeDamage();
                        Animation animation = new javafx.animation.Transition() {
                            {
                                setCycleDuration(javafx.util.Duration.millis(1000));
                            }

                            protected void interpolate(double frac) {
                                playerHealthBar.setWidth(280 * (((double)player.getHp()+1-frac)/(double)player.getMaxHp()));
                            }
                        };
                        animation.playFromStart();

                        if (player.isDead()) {
                            timer.stop();
                            bulletTimer.stop();
                            root.getChildren().remove(player);
                            this.gameOver = true;
                            Label gameOverLabel = new Label("Game Over");
                            gameOverLabel.setFont(javafx.scene.text.Font.font("Impact", 50));
                            root.getChildren().add(gameOverLabel);
                            gameOverLabel.setTranslateX(WINDOW_WIDTH / 2 - 100);
                            gameOverLabel.setTranslateY(20);
                        }
                        bullet.relocate(-1000, -1000);
                        root.getChildren().remove(bullet);
                        root.requestLayout();

                    }
                    return outOfBounds || collided;
                };
                bulletTimer.add(bulletLoop);
                return chosenEnemy.isDead();
            }
        };
        timer.add(enemyShootLoop);


        for (Enemy enemy : enemies) {
            Updatable enemyAimLoop = dns -> {
                enemy.updateGunAimAt(playerPosition.getX(), playerPosition.getY());
                return enemy.isDead();
            };
            timer.add(enemyAimLoop);
        };

        Updatable playerLoop = dns -> {
            player.move(field);
            if (enemies.isEmpty()) {
                timer.stop();
                bulletTimer.stop();
                this.gameOver = true;
                Label winLabel = new Label("You Win!");
                winLabel.setFont(javafx.scene.text.Font.font("Impact", 50));
                root.getChildren().add(winLabel);
                winLabel.setTranslateX(WINDOW_WIDTH / 2 - 100);
                winLabel.setTranslateY(20);
            }
            return player.isDead() || this.gameOver;
        };

        timer.add(playerLoop);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (this.gameOver) {
                return;
            }
            switch (keyEvent.getCode()) {
                case UP, W -> player.setMovingUp(true);
                case DOWN, S -> player.setMovingDown(true);
                case LEFT, A -> player.setMovingLeft(true);
                case RIGHT, D -> player.setMovingRight(true);
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (this.gameOver) {
                return;
            }
            switch (keyEvent.getCode()) {
                case UP, W -> player.setMovingUp(false);
                case DOWN, S -> player.setMovingDown(false);
                case LEFT, A -> player.setMovingLeft(false);
                case RIGHT, D -> player.setMovingRight(false);
            }
        });

        scene.addEventHandler(MouseEvent.ANY, mouseEvent -> {
            if (this.gameOver) {
                return;
            }
            Bullet bullet = player.handleMouseEvent(mouseEvent);

            if (bullet != null) {
                if(bullet.getDamage() == 2){
                    if(player.getPowerShots().size() > 0){
                        root.getChildren().remove(player.getPowerShots().get(player.getPowerShots().size()-1));
                        player.getPowerShots().remove(player.getPowerShots().size()-1);
                    }
                }
                root.getChildren().add(bullet);

                Updatable updatable = dns -> {
                    boolean outOfBounds = bullet.update(dns, 0, WINDOW_WIDTH, 0, WINDOW_HEIGHT);

                    boolean collided = false;
                    if (outOfBounds) {
                        root.getChildren().remove(bullet);
                    } else {
                        for (int i = 0; i < enemyShields.size(); i++) {
                            collided = enemyShields.get(i).handleCollision(bullet) && enemyShields.get(i).isVisible();
                            if (collided) {
                                root.getChildren().remove(bullet);
                                return true;
                            }
                        }
                        for (int i = 0; i < enemies.size(); ++i) {
                            collided = enemies.get(i).handleCollision(bullet);
                            if (collided) {
                                root.getChildren().remove(bullet);
                                if (enemies.get(i).isDead()) {
                                    root.getChildren().remove(enemies.get(i));
                                    enemies.remove(i);
                                }
                                break;
                            }
                        }
                    }

                    return outOfBounds || collided;
                };
                System.out.println("Adding playerbullet loop");

                timer.add(updatable);
            }
        });

        return scene;
    }

    public static void main(String[] args) {
        launch();
    }
}