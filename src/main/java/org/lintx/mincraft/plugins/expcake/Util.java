package org.lintx.mincraft.plugins.expcake;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.lintx.mincraft.plugins.expcake.config.Config;

import java.util.List;

public class Util {
    private final static ItemStack itemCake = new ItemStack(Material.CAKE);
    private static ItemStack expCake;
    private final static ItemFlag cakeFlag = ItemFlag.HIDE_ENCHANTS;
    private final static Enchantment cakeEnchant = Enchantment.DURABILITY;

    static boolean isCake(ItemStack item){
        return item !=null && item.isSimilar(itemCake);
    }

    private static ItemStack newExpCake(){
        ItemStack cake = new ItemStack(Material.CAKE);
        ItemMeta meta = cake.getItemMeta();
        meta.setDisplayName(formatMessage(Config.getInstance().lang().title));
        meta.setLore(Config.getInstance().lang().lore);

        meta.addItemFlags(cakeFlag);
        meta.addEnchant(cakeEnchant,Config.getInstance().experience,true);

        cake.setItemMeta(meta);

        return cake;
    }

    static ItemStack getExpCake(){
        if (expCake==null){
            expCake = newExpCake();
        }
        return expCake;
    }

    static ItemStack getCloneExpCake(){
        return getExpCake().clone();
    }

    static boolean isExpCake(ItemStack item){
        return item !=null && item.isSimilar(getExpCake());
    }

    static int getExpCakeExp(ItemStack item){
        if (item==null || !item.hasItemMeta()) return -1;
        if (!item.getType().equals(Material.CAKE)) return -2;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasItemFlag(cakeFlag)
                || meta.hasItemFlag(ItemFlag.HIDE_DESTROYS)
                || meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON)
                || meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                || meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                || meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
        ) return -3;
        if (!meta.hasEnchant(cakeEnchant)) return -4;
        return meta.getEnchantLevel(cakeEnchant);
    }

    static int getPlayerExp(Player player){
        int level = player.getLevel();
        float exp = player.getExp();
        int total = getLevelTotalExp(level);
        int levelExp = Math.round(getLevelUpExp(level)*exp);
        return total + levelExp;
    }

    private static int getLevelTotalExp(int level){
        int total = 0;
        if (level<=15){
            total = level*level + 6*level;
        }
        else if (level<=30){
            total = (int) (2.5*level*level - 40.5*level + 360);
        }
        else {
            total = (int) (4.5*level*level - 162.5*level + 2220);
        }
        return total;
    }

    private static int getLevelUpExp(int level){
        int total = 0;
        if (level<=15){
            total = 2*level+7;
        }
        else if (level<=30){
            total = 5*level-38;
        }
        else {
            total = 9*level-158;
        }
        return total;
    }

    private static void setPlayerExp(Player player, int exp){
        player.setTotalExperience(0);
        player.setExp(0);
        player.setLevel(0);
        player.giveExp(exp);
    }

    static boolean minusExp(Player player){
        int exp = getPlayerExp(player);
        if (exp<Config.getInstance().experience){
            sendTitleMessage(player,formatMessage(Config.getInstance().lang().noexp),70);
            return false;
        }
        setPlayerExp(player,exp-Config.getInstance().experience);
        return true;
    }

    static void addExp(Player player,int add){
        int exp = getPlayerExp(player);
        setPlayerExp(player,exp+add);
    }

    static void sendTitleMessage(Player player,String message,int showTime){
        sendTitleMessage(player,"",message,showTime);
    }

    static void sendTitleMessage(Player player,String title,String subtitle,int showTime){
        player.sendTitle(title,subtitle,10,showTime,20);
    }

    static boolean isExpCakeBlock(Block block){
        if (block.getType().equals(Material.CAKE)){
            if (block.hasMetadata("exp")){
                List<MetadataValue> list = block.getMetadata("exp");
                return list != null && list.size() > 0;
            }
        }
        return false;
    }

    static void unload(){
        expCake = null;
    }

    public static String formatMessage(String message){
        return formatMessage(message,Config.getInstance().experience);
    }

    static String formatMessage(String message, int exp){
        return message.replaceAll("%experience%", String.valueOf(exp));
    }
}
