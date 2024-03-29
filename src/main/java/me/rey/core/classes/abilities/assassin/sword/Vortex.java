package me.rey.core.classes.abilities.assassin.sword;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.rey.core.Warriors;
import me.rey.core.classes.ClassType;
import me.rey.core.classes.abilities.Ability;
import me.rey.core.classes.abilities.AbilityType;
import me.rey.core.players.User;
import me.rey.core.utils.UtilBlock;
import me.rey.core.utils.UtilEnt;

public class Vortex extends Ability {

    private final double radius = 5.5;

    HashMap<UUID, Double> vortexing = new HashMap<UUID, Double>();

    public Vortex() {
        super(1, "Vortex", ClassType.LEATHER, AbilityType.SWORD, 1, 3, 12.0, Arrays.asList(
                "Create a vortex, pulling players into you",
                "and casting players near you afar.",
                "",
                "Players hit with the blade vortex take <variable>3+l</variable> damage.",
                "",
                "Recharge: <variable>12-(0.5*l)</variable> Seconds."
        ));
    }

    @Override
    protected boolean execute(User u, Player p, int level, Object... conditions) {

        this.setCooldown(12 - (level * 0.5));

        if(vortexing.containsKey(p.getUniqueId()) == false) {
            vortexing.put(p.getUniqueId(), 0D);
        }

        new BukkitRunnable() {
            @Override
            public void run() {

                if(vortexing.containsKey(p.getUniqueId())) {

                    if (vortexing.get(p.getUniqueId()) >= 4D) {

                        this.cancel();
                        vortexing.remove(p.getUniqueId());

                    } else {

                        vortexing.replace(p.getUniqueId(), vortexing.get(p.getUniqueId()) + 1D);

                        double ticks = vortexing.get(p.getUniqueId());

                        p.getWorld().playSound(p.getLocation(), Sound.PIG_DEATH, 1F, 0.65F);
                        p.getWorld().playSound(p.getLocation(), Sound.LAVA_POP, 1F, 1.35F);

                        playParticles(p.getLocation(), radius - ticks, false);
                        playParticles(p.getLocation(), radius - ticks, true);
                    }
                }
            }
        }.runTaskTimer(Warriors.getInstance(), 1L, 1L);


        for(Entity e : p.getNearbyEntities(radius, 4, radius)) {
            if(e instanceof LivingEntity) {
                double distance = p.getLocation().distance(e.getLocation());
                if (inCircle(p, e)) {
                    LivingEntity le = (LivingEntity) e;
                    
                    
                    UtilEnt.damage(3+level, this.getName(), le, p);
                    
                    if (distance < 2.6) {
                        pushAway(p, e);
                    } else {
                        pushIn(p, e);
                    }
                }
            }
        }

        sendUsedMessageToPlayer(p, this.getName());
        return false;
    }

    public boolean inCircle(Player p, Entity e) {

        HashMap<Double, double[]> maxmincords = new HashMap<Double, double[]>();

        for(double degree=0; degree<=360; degree++) {
            maxmincords.put(degree, UtilBlock.getXZCordsFromDegree(p, radius, degree));
        }

        for(double degree=0;degree<=90;degree++) {

            double[] maxcords = maxmincords.get(degree);
            double[] mincords = maxmincords.get(180 + degree);

            double maxX = maxcords[0];
            double maxZ = maxcords[1];

            double minX = mincords[0];
            double minZ = mincords[1];

            if(e.getLocation().getX() <= maxX && e.getLocation().getZ() <= maxZ && e.getLocation().getX() >= minX && e.getLocation().getZ() >= minZ) {
                return true;
            } else {
                continue;
            }

        }

        return false;
    }

    public void playParticles(Location location, double radius, boolean rotated) {

        HashMap<Double, double[]> maxmincords = new HashMap<Double, double[]>();

        for(double degree=0; degree<=360; degree++) {
            maxmincords.put(degree, UtilBlock.getXZCordsFromDegree(location, rotated, radius, radius, degree));
        }

        for(double degree=0; degree<=360; degree+=4) {
            double[] cords = maxmincords.get(degree);

            double xCords = cords[0];
            double zCords = cords[1];

            Location loc = location;
            loc.setX(xCords);
            loc.setZ(zCords);

            float red = 230;
            float green = 0;
            float blue = 200;

            location.getWorld().spigot().playEffect(location, Effect.COLOURED_DUST, 0, 0, red/255, green/255, blue/255, 1F, 0, 50);

        }
    }

    public void pushAway(Player user, Entity pToPush) {
        double pX = user.getLocation().getX();
        double pY = user.getLocation().getY();
        double pZ = user.getLocation().getZ();

        double tX = pToPush.getLocation().getX();
        double tY = pToPush.getLocation().getY();
        double tZ = pToPush.getLocation().getZ();

        double deltaX = tX - pX;
        double deltaY = tY - pY;
        double deltaZ = tZ - pZ;

        pToPush.setVelocity(new Vector(deltaX, deltaY, deltaZ).normalize().multiply(1.25D).setY(0.3D));
    }

    public void pushIn(Player user, Entity pToPush) {
        double pX = user.getLocation().getX();
        double pY = user.getLocation().getY();
        double pZ = user.getLocation().getZ();

        double tX = pToPush.getLocation().getX();
        double tY = pToPush.getLocation().getY();
        double tZ = pToPush.getLocation().getZ();

        double deltaX = tX - pX;
        double deltaY = tY - pY;
        double deltaZ = tZ - pZ;

        pToPush.setVelocity(new Vector(deltaX, deltaY, deltaZ).normalize().multiply(-1.25D).setY(0.3D));
    }
}
