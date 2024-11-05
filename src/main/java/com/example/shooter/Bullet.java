package com.example.shooter;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Translate;

import java.math.BigDecimal;

import static com.example.shooter.Constants.*;

public class Bullet extends Circle {
	private Translate position;
	private Point2D speed;
	private boolean isPlayerBullet;
	private int damage;

	public Bullet ( double radius, Translate position, Point2D speed, boolean isPlayerBullet ){
		this ( radius, position, speed, isPlayerBullet, 1 );
	}

	public Bullet ( double radius, Translate position, Point2D speed, boolean isPlayerBullet, int damage ) {
		super ( radius + (damage-1)*3, Color.ORANGE );

		this.position = position;
		this.speed    = speed;//.multiply(0.25);
		this.isPlayerBullet = isPlayerBullet;
		this.damage = damage;

		super.getTransforms ( ).add ( this.position );
	}

	public boolean update ( long dns, double left, double right, double up, double down ) {
		double newX = this.position.getX ( ) + this.speed.getX ( ) * BULLET_SPEED;
		double newY = this.position.getY ( ) + this.speed.getY ( ) * BULLET_SPEED;

		this.position.setX ( newX );
		this.position.setY ( newY );

		double radius = super.getRadius ( );

		boolean isXOutOfBounds = newX <= ( left - radius ) || newX >= ( right + radius );
		boolean isYOutOfBounds = newY <= ( up - radius ) || newY >= ( down + radius );

		return isXOutOfBounds && isYOutOfBounds;
	}

	public boolean isPlayerBullet() {
		return isPlayerBullet;
	}

	public int getDamage() {
		return damage;
	}

	public void bounce() {
		this.speed = this.speed.multiply(-1);
	}

	public void bounceOff(Player player) {
		System.out.println("Old speed: " + this.speed);
		Point2D playerCenter = new Point2D(player.getBoundsInParent().getCenterX(), player.getBoundsInParent().getCenterY());
		System.out.println("Player center: " + playerCenter);
		Point2D bulletCenter = new Point2D(this.getBoundsInParent().getCenterX(), this.getBoundsInParent().getCenterY());
		System.out.println("Bullet center: " + bulletCenter);
		Point2D direction = bulletCenter.subtract(playerCenter).normalize();
		System.out.println("Direction: " + direction);
		this.speed = direction.multiply(0.125);
		System.out.println("New speed: " + this.speed);
	}
}
