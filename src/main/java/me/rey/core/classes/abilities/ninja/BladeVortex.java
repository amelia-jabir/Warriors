package me.rey.core.classes.abilities.ninja;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import me.rey.core.Warriors;
import me.rey.core.classes.abilities.IConstant;
import me.rey.core.events.customevents.UpdateEvent;
import me.rey.core.utils.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import me.rey.core.classes.ClassType;
import me.rey.core.classes.abilities.Ability;
import me.rey.core.classes.abilities.AbilityType;
import me.rey.core.players.User;

public class BladeVortex extends Ability {

    private final double radius = 5;

    HashMap<UUID, Double> vortexing = new HashMap<UUID, Double>();
    HashMap<UUID, Integer> vortexingS = new HashMap<UUID, Integer>();

    public BladeVortex() {
        super(4, "Blade Vortex", ClassType.LEATHER, AbilityType.SWORD, 1, 3, 6.5, Arrays.asList(
                "Create a blade vortex, pulling players into you",
                "and casting players near you afar.",
                "",
                "Players hit with the blade vortex take <variable>5+l</variable> damage",
                "as well as receive slowness for <variable>1.0 + 0.5*l</variable> seconds.",
                "",
                "Recharge: <variable>7.5 - l</variable>"
        ));
    }

    @Override
    protected boolean execute(User u, Player p, int level, Object... conditions) {

        this.setCooldown(10.5 - level);

        if(vortexing.containsKey(p.getUniqueId()) == false) {
            vortexing.put(p.getUniqueId(), 0D);
        }

        int S = Bukkit.getScheduler().scheduleSyncRepeatingTask(Warriors.getPlugin(Warriors.class), new Runnable() {
            @Override
            public void run() {
                if(vortexing.containsKey(p.getUniqueId())) {

                    vortexing.replace(p.getUniqueId(), vortexing.get(p.getUniqueId()) + 0.3D);

                    double ticks = vortexing.get(p.getUniqueId());

                    playParticles(p, radius - ticks, false);
                    playParticles(p, radius - ticks, true);

                    if (ticks >= 4D) {

                        vortexing.remove(p.getUniqueId());
                        Bukkit.getScheduler().cancelTask(vortexingS.get(p.getUniqueId()));
                    }
                }
            }
        }, 0L, 1L);

        if(vortexingS.containsKey(p.getUniqueId()) == false) {
            vortexingS.put(p.getUniqueId(), S);
        }

        for(Entity e : p.getNearbyEntities(radius, 4, radius)) {
            double distance = p.getLocation().distance(e.getLocation());
            if(inCircle(p, e)) {
                if (distance < 2.6) {
                    pushAway(p, e);
                } else {
                    pushIn(p, e);
                }
            }

        }

        sendUsedMessageToPlayer(p, this.getName());
        return false;
    }

    public boolean inCircle(Player p, Entity e) {

        HashMap<Double, double[]> maxmincords = new HashMap<Double, double[]>();

        for(double degree=0; degree<=360; degree++) {
            maxmincords.put(degree, BlockLocation.getXZCordsFromDegree(p, radius, degree));
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

    public void playParticles(Player user, double radius, boolean rotated) {

        HashMap<Double, double[]> maxmincords = new HashMap<Double, double[]>();

        for(double degree=0; degree<=360; degree++) {
            maxmincords.put(degree, BlockLocation.getXZCordsFromDegree(user, true, radius, radius, degree));
        }

        for(double degree=0; degree<=360; degree+=4) {
            double[] cords = maxmincords.get(degree);

            double xCords = cords[0];
            double zCords = cords[1];

            Location loc = new Location(user.getWorld(), xCords, user.getLocation().getY(), zCords);

            float red = 160;
            float green = 9;
            float blue = 140;

            user.getWorld().spigot().playEffect(loc, Effect.COLOURED_DUST, 0, 0, red/255, green/255, blue/255, 1F, 0, 50);

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
