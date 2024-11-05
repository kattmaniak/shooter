package com.example.shooter;

import javafx.scene.Group;

public interface Pickup {
    public void effect(Player player);
    public boolean isPickedUp(Player player);
}
