package org.lintx.mincraft.plugins.expcake.config;

import org.lintx.mincraft.plugins.expcake.ExpCakePlugin;
import org.lintx.mincraft.plugins.expcake.Util;
import org.lintx.plugins.modules.configure.Configure;
import org.lintx.plugins.modules.configure.YamlConfig;

import java.util.ListIterator;


@YamlConfig
public class Config {
    private static Config instance = new Config();
    private static Language language;

    private Config(){}

    public static Config getInstance(){
        return instance;
    }

    public void load(){
        Configure.bukkitLoad(ExpCakePlugin.getPlugin(),this);
        if (experience>32767) experience = 32767;
        if (lang.equals("")){
            lang = "en_us";
            Configure.bukkitSave(ExpCakePlugin.getPlugin(),this);
        }
        lang();
    }

    @YamlConfig(path = "language")
    private String lang = "";

    @YamlConfig
    public int experience = 1395;

    public void setLanguage(String language){
        lang = language;
        Config.language = null;
    }

    public Language lang(){
        if (language==null){
            Language language = new Language();
            Configure.bukkitLoad(ExpCakePlugin.getPlugin(),language,lang+".yml");
            if (language.title.equals("")){
                if (lang.equals("zh_cn")){
                    language.title = "§e经验蛋糕";

                    language.lore.add("§r§5将普通蛋糕和青金石放进铁砧");
                    language.lore.add("§r§5并花费§e%experience%§5点经验值制作而成.");
                    language.lore.add("");
                    language.lore.add("§r§5放置后右键使用可以将存储的经验吸收.");
                    language.lore.add("§r§5拿着§6[经验修补]§5附魔的物品使用可以修理装备或工具.");
                    language.lore.add("");
                    language.lore.add("§r§c破坏经验蛋糕方块将一无所获.");

                    language.noexp = "§c制作经验蛋糕需要%experience%点经验值, 你的经验值不够";
                    language.isexpcake = "§f这是一个经验蛋糕, 内有%experience%点经验值";
                }
                else if (lang.equals("zh_tw")){
                    language.title = "§e經驗蛋糕";

                    language.lore.add("§r§5將普通蛋糕和青金石放進鐵砧");
                    language.lore.add("§r§5並花費§e%experience%§5點經驗值製作而成.");
                    language.lore.add("");
                    language.lore.add("§r§5放置後右鍵使用可以將存儲的經驗吸收.");
                    language.lore.add("§r§5拿著§6[修補]§5附魔的物品使用可以修理裝備或工具.");
                    language.lore.add("");
                    language.lore.add("§r§c破壞經驗蛋糕方塊將一無所獲.");

                    language.noexp = "§c製作經驗蛋糕需要%experience%點經驗值, 你的經驗值不夠";
                    language.isexpcake = "§f這是一個經驗蛋糕, 內有%experience%點經驗值";
                }
                else {
                    language.title = "§eExpCake";

                    language.lore.add("§r§5Put a cake and");
                    language.lore.add("§r§5  Lapis Lazuli into the anvil");
                    language.lore.add("§r§5And cost §e%experience%§5 exp to make one.");
                    language.lore.add("");
                    language.lore.add("§r§5After placing it,");
                    language.lore.add("§r§5  right click to absorb");
                    language.lore.add("§r§5  the stored experience.");
                    language.lore.add("§r§5Can also repair §6[ Mending ] §5enchanted equipments.");
                    language.lore.add("");
                    language.lore.add("§r§cDestroy the ExpCake block will get nothing.");

                    language.noexp = "§cNot enough exp. You need %experience% exp to make one.";
                    language.isexpcake = "§fThis is a ExpCake, %experience% experience inside.";
                }
                Configure.bukkitSave(ExpCakePlugin.getPlugin(),language,lang+".yml");
            }
            ListIterator<String> lore = language.lore.listIterator();
            while (lore.hasNext()){
                String str = lore.next();
                lore.set(Util.formatMessage(str));
            }
            Config.language = language;
        }
        return Config.language;
    }

    public void unload(){
        lang = "";
        language = null;
    }
}
