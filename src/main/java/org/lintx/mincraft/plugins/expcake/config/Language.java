package org.lintx.mincraft.plugins.expcake.config;

import org.lintx.plugins.modules.configure.YamlConfig;

import java.util.ArrayList;
import java.util.List;

@YamlConfig(path = "en_us.yml")
public class Language {

    @YamlConfig(path = "expcake_item.title")
    public String title = "";

    @YamlConfig(path = "expcake_item.lore")
    public List<String> lore = new ArrayList<String>();

    @YamlConfig(path = "tips.insufficient_experience_in_making_expcake")
    public String noexp = "";

    @YamlConfig(path = "tips.this_block_is_expcake")
    public String isexpcake = "";
}
