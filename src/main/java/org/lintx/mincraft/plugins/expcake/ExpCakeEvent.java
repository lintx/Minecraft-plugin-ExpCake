package org.lintx.mincraft.plugins.expcake;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.lintx.mincraft.plugins.expcake.config.Config;

public class ExpCakeEvent implements Listener {
    /**
     * if the anvil have cake and lapis, set the result to expcake
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event){
        if (event.getResult().getType() == Material.AIR){
            AnvilInventory anvilInventory = event.getInventory();
            ItemStack itemStack = anvilInventory.getItem(0);
            if (Util.isCake(itemStack)){
                ItemStack itemStack1 = anvilInventory.getItem(1);
                if (itemStack1!=null){
                    Material type = itemStack1.getType();
                    if (type!=null && type.equals(Material.LAPIS_LAZULI)){
                        event.setResult(Util.getExpCake());
                        anvilInventory.setRepairCost(0);
                    }
                }
            }
        }

    }

    /**
     * get the expcake from invil
     * @param event
     */
    @EventHandler(priority= EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event){
        if (event.isCancelled()) return;
        if (event.getWhoClicked() instanceof Player){
            Player player = (Player)event.getWhoClicked();
            if (event.getClickedInventory() instanceof AnvilInventory && event.getSlot()==2){
                AnvilInventory anvilInventory = (AnvilInventory) event.getClickedInventory();
                ItemStack itemStack = event.getCurrentItem();
                if (Util.isExpCake(itemStack)){
                    ItemStack itemStack1 = anvilInventory.getItem(1);
                    if (itemStack1!=null && itemStack1.getType().equals(Material.LAPIS_LAZULI)){
                        event.setCancelled(true);
                        boolean done = Util.minusExp(player);
                        if (done){
                            anvilInventory.setItem(0,null);
                            if (itemStack1.getAmount()==1){
                                anvilInventory.setItem(1,null);
                            }
                            else {
                                itemStack1.setAmount(itemStack1.getAmount()-1);
                                anvilInventory.setItem(1,itemStack1);
                            }
                            event.getView().setCursor(Util.getCloneExpCake());
                        }
                    }
                }
            }
        }
    }

    /**
     * place the expcake
     * @param event
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if (!event.canBuild()) return;
        ItemStack itemStack = event.getItemInHand();
        if (itemStack!=null && Util.isExpCake(itemStack)){
            Block block = event.getBlockPlaced();
            if (block!=null){

                if (block.getBlockData() instanceof Cake){
                    FixedMetadataValue value = new FixedMetadataValue(ExpCakePlugin.getPlugin(), Util.experience);
                    block.setMetadata("exp",value);
                }
            }
        }
    }

    /**
     * eat expcake or checking a cake is not an expcake
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.getAction()!= Action.RIGHT_CLICK_BLOCK && event.getAction()!=Action.LEFT_CLICK_BLOCK) return;
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (player.isSneaking()) return;
        Block block = event.getClickedBlock();
        if (Util.isExpCakeBlock(block)){
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            if (event.getAction()==Action.LEFT_CLICK_BLOCK){
                Util.sendTitleMessage(player, Config.getInstance().lang().isexpcake,30);
            }
            else{
                Integer exp = 0;
                for (MetadataValue value:block.getMetadata("exp")){
                    if (value instanceof FixedMetadataValue){
                        FixedMetadataValue v = (FixedMetadataValue)value;
                        if (v.value() instanceof Integer){
                            exp = (Integer)v.value();
                        }
                    }
                }
                if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)){
                    return;
                }
                Cake cakeData = (Cake) block.getBlockData();
                int i = cakeData.getBites();
                if (i < 6){
                    cakeData.setBites(i+1);
                    block.setBlockData(cakeData);
                }
                else {
                    block.setBlockData(Bukkit.createBlockData(Material.AIR));
                    block.setType(Material.AIR);
                }
                int getExp = 0;
                if (i<5){
                    getExp = 199;
                }
                else {
                    getExp = 200;
                }
                if (getExp>exp){
                    getExp = exp;
                }
                if (exp-getExp>0){
                    FixedMetadataValue value = new FixedMetadataValue(ExpCakePlugin.getPlugin(), exp-getExp);
                    block.setMetadata("exp",value);
                }
                else {
                    block.setBlockData(Bukkit.createBlockData(Material.AIR));
                    block.setType(Material.AIR);
                }
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0f,1.0f);
                if (event.hasItem()){
                    ItemStack item = event.getItem();
                    if (item.getEnchantmentLevel(Enchantment.MENDING)>0){
                        ItemMeta meta = item.getItemMeta();
                        if (meta instanceof Damageable){
                            int damage = ((Damageable)meta).getDamage();
                            if (damage>=getExp){
                                damage -= getExp;
                                getExp = 0;
                            }
                            else {
                                getExp -= damage;
                                damage = 0;
                            }
                            ((Damageable)meta).setDamage(damage);
                            item.setItemMeta(meta);
                        }
                    }
                }
                if (getExp>0){
                    Util.addExp(player,getExp);
                }
            }
        }
    }
}
