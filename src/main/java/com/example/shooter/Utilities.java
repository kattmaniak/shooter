package com.example.shooter;

public class Utilities {
	public static double clamp ( double value, double low, double high ) {
		if ( value <= low ) {
			return low;
		} else if ( value >= high ) {
			return high;
		} else {
			return value;
		}
	}
}
