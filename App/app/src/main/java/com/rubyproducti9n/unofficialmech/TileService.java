package com.rubyproducti9n.unofficialmech;

import android.content.Intent;
import android.os.Looper;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.widget.Toast;

public class TileService extends android.service.quicksettings.TileService {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onTileAdded() {
        // Called when the tile is added to Quick Settings
        Tile tile = getQsTile();
        tile.setState(Tile.STATE_INACTIVE);  // Default state
        tile.setLabel("My App");  // Set Tile Label
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        // Called when Quick Settings panel is opened
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setLabel("Tap to Open");
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        // Called when user taps the tile
        Tile tile = getQsTile();
        if (tile != null) {
            if (tile.getState() == Tile.STATE_INACTIVE) {
                activateTile(tile);
            } else {
                deactivateTile(tile);
            }
        }
    }

    private void activateTile(Tile tile) {
        handler.post(() -> {
            Toast.makeText(getApplicationContext(), "Feature Activated!", Toast.LENGTH_SHORT).show();
        });

        tile.setState(Tile.STATE_ACTIVE);
        tile.setLabel("Active");
        tile.updateTile();

        startForegroundService(new Intent(this, ForegroundService.class));
        // Perform action, e.g., open an activity
//        Intent intent = new Intent(this, MainActivity.class);  // Change to desired activity
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    private void deactivateTile(Tile tile) {
        handler.post(() -> {
            Toast.makeText(getApplicationContext(), "Feature Deactivated!", Toast.LENGTH_SHORT).show();
        });

        tile.setState(Tile.STATE_INACTIVE);
        tile.setLabel("Tap to Open");
        tile.updateTile();
    }

    @Override
    public void onTileRemoved() {
        // Called when the tile is removed from Quick Settings
        super.onTileRemoved();
    }
}
