package me.marc3308.koSystem_Alkusia;

import me.marc3308.koSystem_Alkusia.events.Ko;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class KoSystem_Alkusia extends JavaPlugin {

    public static KoSystem_Alkusia plugin;
    public static int koZeit = 10; // Default KO time in seconds
    public static int pickuptime = 2; // Zeit die man braucht um den spieler aufzuheben

    @Override
    public void onEnable() {
        plugin = this;

        //loop for server performance
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Check for players in KO state and apply effects
                Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER))
                        .forEach(p -> {
                            // do the ko
                            if(p.getPersistentDataContainer().get(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER)-1<=0){
                                p.getLocation().getNearbyEntities(1,1,1).stream().filter(et -> et instanceof ArmorStand && ((ArmorStand) et).isSmall()).forEach(entity -> ((ArmorStand) entity).remove());
                                p.getPersistentDataContainer().remove(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 1, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 1, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA,20*5,1,false,false));
                                p.setHealth(p.getMaxHealth()/20.0);
                            } else{
                                // Reduce KO time
                                p.getPersistentDataContainer().set(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER,
                                        p.getPersistentDataContainer().get(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER)-1);
                                p.sendTitle(ChatColor.DARK_RED+""+p.getPersistentDataContainer().get(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER),"");
                            }
                        });
            }
        },0,20);

        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new Ko(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static KoSystem_Alkusia getPlugin() {
        return plugin;
    }
}
