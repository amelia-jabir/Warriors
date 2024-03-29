package me.rey.core.classes.abilities.assassin.axe;

import java.util.Arrays;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.core.Warriors;
import me.rey.core.classes.ClassType;
import me.rey.core.classes.abilities.Ability;
import me.rey.core.classes.abilities.AbilityType;
import me.rey.core.players.User;

public class Dash extends Ability {

	public Dash() {
		super(11, "Dash", ClassType.LEATHER, AbilityType.AXE, 1, 4, 10.00, Arrays.asList(
				"Dash forward at extreme speed,",
				"moving up to 20 blocks.",
				"",
				"Recharge: <variable>0-1*l+11</variable> (-1) Seconds"
				));
		
		this.setWhileInAir(false);
		this.setWhileSlowed(false);
	}

	@Override
	protected boolean execute(User u, final Player p, int level, Object... conditions) {

		p.setWalkSpeed(1F);

		this.sendUsedMessageToPlayer(p, this.getName());
		this.setCooldown(-1*level+11);
		
		// lava pop PARTICLES
		int points = 100;
		double radius = 1.0d;
		Location pLoc = p.getLocation();
		for(int i = 0; i < points; i++) {
			double angle = 2 * Math.PI * i / points;
			Location point = pLoc.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
			point.getWorld().playEffect(point, Effect.VOID_FOG, Integer.MAX_VALUE);
		}
		
		// FLAME PARTICLES
		final int periodTicks = 2;
		new BukkitRunnable() {
			
			int ticks = 0;
			@Override
			public void run() {
				if(ticks >= 10) {
					p.setWalkSpeed(0.2F);
					this.cancel();
				}

				Location loc = p.getLocation();
				loc.setY(loc.getY()+0.4);
				p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
				
				ticks = ticks + periodTicks;
			}
			
		}.runTaskTimer(Warriors.getInstance(), 0, periodTicks);
		return true;
	}

}
