package org.lintx.mincraft.plugins.expcake.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEatExpCakeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Player player;
    private int useExp;
    private int totalExp;

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public PlayerEatExpCakeEvent(Player player,int totalExp,int useExp){
        this.player = player;
        this.totalExp = totalExp;
        this.useExp = useExp;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public int getUseExp(){
        return useExp;
    }

    public int getTotalExp(){
        return totalExp;
    }

    public Player getPlayer(){
        return player;
    }
}
