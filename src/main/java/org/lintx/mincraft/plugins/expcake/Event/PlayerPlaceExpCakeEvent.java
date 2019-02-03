package org.lintx.mincraft.plugins.expcake.Event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPlaceExpCakeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Player player;
    private Block block;
    private int exp;

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public PlayerPlaceExpCakeEvent(Player player, Block block, int exp){
        this.player = player;
        this.block = block;
        this.exp = exp;
    }

    public Player getPlayer(){
        return player;
    }

    public Block getBlock(){
        return block;
    }

    public int getExp(){
        return exp;
    }
}
