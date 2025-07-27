package me.marc3308.koSystem_Alkusia.events;

import me.marc3308.koSystem_Alkusia.KoSystem_Alkusia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Ko implements Listener {


    //checkt für spielertot
    @EventHandler
    public void ondeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        //guckt ob der spieler schon ko ist
        if(p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER)){

            //lass den spieler sterben und lösche den armosrstand
            p.getLocation().getNearbyEntities(1,1,1).stream().filter(et -> et instanceof ArmorStand && ((ArmorStand) et).isSmall()).forEach(entity -> ((ArmorStand) entity).remove());
            p.getPersistentDataContainer().remove(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"));
        } else {
            e.setCancelled(true);
            p.setHealth(1);
            Location loc = p.getLocation();
            loc.setPitch(90);
            p.teleport(loc);
            p.getPersistentDataContainer().set(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER, KoSystem_Alkusia.koZeit);

            // Spawn ArmorStand slightly lower for sitting look
            ArmorStand chair = (ArmorStand) p.getWorld().spawn(p.getLocation().clone().add(0, 0, 0), ArmorStand.class);
            chair.setVisible(false);
            chair.setGravity(true);
            chair.setMarker(true); // hitbox = 0
            chair.setInvulnerable(true);
            chair.setSmall(true);
            chair.setCustomNameVisible(false);

            // Set player as passenger
            chair.addPassenger(p);
        }
    }

    //verhindert das der ko spieler jemanden schlägt
    @EventHandler
    public void onschlagen(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player))return;
        Player p= (Player) e.getDamager();
        if(p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER))e.setCancelled(true);
    }

    //verhindert das der ko spieler sich bewegt / auser nach unten
    @EventHandler
    public void onmove(PlayerMoveEvent e){
        Player p= e.getPlayer();
        if(p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER) && e.getFrom().getY()<=e.getTo().getY())e.setCancelled(true);
    }

    //verhindert das der ko spieler sein inventar benutzt
    @EventHandler
    public void inclick(InventoryClickEvent e){
        Player p= (Player) e.getWhoClicked();
        if(p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER))e.setCancelled(true);
    }

    //verhindert das der ko spieler items aufnimmt
    @EventHandler
    public void armor(PlayerAttemptPickupItemEvent e){
        Player p = e.getPlayer();
        if(p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER))e.setCancelled(true);
    }

    //löscht den armor stand wenn der spieler das spiel verlässt
    @EventHandler
    public void onqit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER))
            p.getLocation().getNearbyEntities(1,1,1).stream().filter(et -> et instanceof ArmorStand && ((ArmorStand) et).isSmall()).forEach(entity -> ((ArmorStand) entity).remove());
    }

    //verhindert das der ko spieler sich von einem stuhl runtersetzt
    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player p && p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER) && e.getDismounted() instanceof ArmorStand)e.setCancelled(true);
    }

    //verhindert das der ko spieler blöcke abbaut
    @EventHandler
    public void onblockbreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        if(p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER))e.setCancelled(true);
    }

    //öffnet das inventar des ko spielers
    @EventHandler
    public void oninv(PlayerInteractEntityEvent e){
        //check if clickt is ko
        if(e.getRightClicked() instanceof Player p && p.getPersistentDataContainer().has(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"), PersistentDataType.INTEGER)){
            //check if player is sneaking
            if(e.getPlayer().isSneaking()){
                BossBar bar =Bukkit.createBossBar(""+KoSystem_Alkusia.pickuptime, BarColor.RED, BarStyle.SEGMENTED_20);
                bar.setProgress(1);
                bar.addPlayer(e.getPlayer());
                //check delay if player still sneacking
                new BukkitRunnable(){
                    int i=0;
                    @Override
                    public void run() {
                        if(!e.getPlayer().isSneaking()){
                            cancel();
                            return;
                        }

                        bar.setTitle(""+(KoSystem_Alkusia.pickuptime-i));
                        //bar check
                        bar.setColor((i/KoSystem_Alkusia.pickuptime)>=0.6 ? BarColor.GREEN
                                : (i/KoSystem_Alkusia.pickuptime)>=0.3 ? BarColor.YELLOW
                                : BarColor.RED);
                        bar.setProgress(Math.min(Math.max(1-(i/KoSystem_Alkusia.pickuptime), 0.0), 1.0));

                        i++;
                        //aufheben check
                        if(i>=KoSystem_Alkusia.pickuptime){
                            bar.removeAll();
                            e.getPlayer().sendMessage("§cDu hast " + p.getName() + " aufgehoben!");
                            p.getLocation().getNearbyEntities(1,1,1).stream().filter(et -> et instanceof ArmorStand && ((ArmorStand) et).isSmall()).forEach(entity -> ((ArmorStand) entity).remove());
                            p.getPersistentDataContainer().remove(new NamespacedKey(KoSystem_Alkusia.getPlugin(), "istko"));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1, false, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 1, false, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 1, false, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA,20*5,1,false,false));
                            p.setHealth(p.getMaxHealth()/20.0);
                            cancel();
                        }
                    }
                }.runTaskTimer(KoSystem_Alkusia.getPlugin(), 0, 20);

            } else e.getPlayer().openInventory(p.getInventory());
        }
    }
}
