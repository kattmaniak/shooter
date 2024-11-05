package com.example.shooter;

public class Constants {
	public static final double WINDOW_WIDTH = 750;
	public static final double WINDOW_HEIGHT = 750;
	
	public static final double PLAYER_RADIUS = 20;
	public static final double PLAYER_STEP   = 4;
	public static final double BULLET_SPEED  = 15;
	
	public static final double FIELD_RADIUS = Math.min ( WINDOW_WIDTH, WINDOW_WIDTH ) * 0.3;
	
	public static final int    NUMBER_OF_ENEMIES      = 4;
	public static final double ENEMY_RADIUS = PLAYER_RADIUS * 1.4;
	public static final double ENEMY_PLACEMENT_RADIUS = Math.min ( WINDOW_WIDTH, WINDOW_HEIGHT ) * 0.4;
}
