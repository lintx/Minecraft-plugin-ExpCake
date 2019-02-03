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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.lintx.mincraft.plugins.expcake.Event.PlayerEatExpCakeEvent;
import org.lintx.mincraft.plugins.expcake.Event.PlayerPlaceExpCakeEvent;
import org.lintx.mincraft.plugins.expcake.Helper.CoreProtectHelper;
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
    @EventHandler(priority= EventPriority.HIGH,ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event){
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
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE,SoundCategory.BLOCKS,1.0f,1.0f);
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
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event){
        ItemStack itemStack = event.getItemInHand();
        int exp = Util.getExpCakeExp(itemStack);
        if (itemStack!=null && exp>0){
            Block block = event.getBlockPlaced();
            if (block!=null){
                if (block.getBlockData() instanceof Cake){
                    PlayerPlaceExpCakeEvent placeCakeEvent = new PlayerPlaceExpCakeEvent(event.getPlayer(),block,exp);
                    ExpCakePlugin.getPlugin().getServer().getPluginManager().callEvent(placeCakeEvent);
                    if (placeCakeEvent.isCancelled()){
                        event.setCancelled(true);
                        return;
                    }
                    FixedMetadataValue value = new FixedMetadataValue(ExpCakePlugin.getPlugin(), Config.getInstance().experience);
                    block.setMetadata("exp",value);
                }
            }
        }
    }

    /**
     * break the expcake
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        if (Util.isExpCakeBlock(block)){
            block.removeMetadata("exp",ExpCakePlugin.getPlugin());
        }
    }

    /**
     * eat expcake or checking a cake is not an expcake
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.getAction()!= Action.RIGHT_CLICK_BLOCK && event.getAction()!=Action.LEFT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (player.isSneaking()) return;
        if (event.getHand()!=EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (Util.isExpCakeBlock(block)){
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            Integer totalExp = 0;
            for (MetadataValue value:block.getMetadata("exp")){
                if (value instanceof FixedMetadataValue){
                    FixedMetadataValue v = (FixedMetadataValue)value;
                    if (v.value() instanceof Integer){
                        totalExp = (Integer)v.value();
                    }
                }
            }
            if (event.getAction()==Action.LEFT_CLICK_BLOCK){
                Util.sendTitleMessage(player, Util.formatMessage(Config.getInstance().lang().isexpcake,totalExp),30);
                player.playSound(block.getLocation(), Sound.UI_TOAST_IN,SoundCategory.BLOCKS,0.3f, 1.0F);
            }
            else{
                if (player.getSaturation()<20.0f) player.setSaturation(Math.min(player.getSaturation() + 0.4f,20.0f));
                if (player.getFoodLevel()<20) player.setFoodLevel(Math.min(player.getFoodLevel() + 2,20));

                Cake cakeData = (Cake) block.getBlockData();
                int i = cakeData.getBites();
                int getExp;
                if (i < 6){
                    cakeData.setBites(i+1);
                    block.setBlockData(cakeData);
                    getExp = (int) Math.floor(totalExp/(7.0f-i));

                    FixedMetadataValue value = new FixedMetadataValue(ExpCakePlugin.getPlugin(), totalExp-getExp);
                    block.setMetadata("exp",value);
                }
                else {
                    getExp = totalExp;

                    block.removeMetadata("exp",ExpCakePlugin.getPlugin());
                    block.setBlockData(Bukkit.createBlockData(Material.AIR));
                    block.setType(Material.AIR);
                }
                if (getExp==0){
                    return;
                }

                PlayerEatExpCakeEvent eatCakeEvent = new PlayerEatExpCakeEvent(player,totalExp,getExp);
                ExpCakePlugin.getPlugin().getServer().getPluginManager().callEvent(eatCakeEvent);
                if (eatCakeEvent.isCancelled()) return;

                try {
                    CoreProtectHelper.logUseCake(player,block.getLocation());
                }
                catch (Exception igone){

                }
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,SoundCategory.PLAYERS,1.0f, (float) Math.random());
                if (event.hasItem()){
                    ItemStack item = event.getItem();
                    if (item.getEnchantmentLevel(Enchantment.MENDING)>0){
                        ItemMeta meta = item.getItemMeta();
                        if (meta instanceof Damageable){
                            int damage = ((Damageable)meta).getDamage();
                            if (damage>0){
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
                }
                if (getExp>0){
                    Util.addExp(player,getExp);
                }
            }
        }
    }
}
