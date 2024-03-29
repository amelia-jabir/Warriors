package me.rey.core.classes.abilities.bandit.axe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.rey.core.Warriors;
import me.rey.core.classes.ClassType;
import me.rey.core.classes.abilities.Ability;
import me.rey.core.classes.abilities.AbilityType;
import me.rey.core.enums.AbilityFail;
import me.rey.core.events.customevents.ability.AbilityFailEvent;
import me.rey.core.players.User;

public class Leap extends Ability {
	
	String wallkick = "Wall Kick";
	double wallkickCooldown = 0.50;
	private ArrayList<UUID> wallkickPlayers;
	
	public Leap() {
		super(112, "Leap", ClassType.BLACK, AbilityType.AXE, 1, 4, 3.00, Arrays.asList(
				"Take a great leap forwards",
				"",
				"Wall Kick by using Leap with your",
				"back against a wall. This doesn't",
				"trigger Leaps Recharge.",
				"",
				"Cannot be used while Slowed.",
				"Recharge: <variable>3.25 - 0.25 * l</variable> (-0.25) Seconds"));
		
		this.wallkickPlayers = new ArrayList<>();
		this.setWhileSlowed(false);
	}

	@Override
	protected boolean execute(User u, Player p, int level, Object... conditions) {
		return leap(p, level, false);
	}
	
	@EventHandler
	public void CooldownEvent(AbilityFailEvent e) {
		if(e.getAbility() == this && e.getFail() == AbilityFail.COOLDOWN)
			e.setCancelled(leap(e.getPlayer(), e.getLevel(), true));
	}

	@SuppressWarnings("deprecation")
	private boolean leap(final Player p, int level, boolean wallKickOnly) {
		Vector vectorBehind = p.getLocation().getDirection().setY(0).multiply(-1);
		Block blockBehind = p.getLocation().add(vectorBehind).getBlockY() == p.getLocation().getBlockY() ? p.getLocation().add(vectorBehind).getBlock() : null;
		if(!(blockBehind != null && blockBehind.getType().isSolid()) && wallKickOnly) return false;
		
		p.setFallDistance(-7);
		if(p.isOnGround())
			p.setVelocity(p.getLocation().getDirection().setY(0.8).multiply(1.2));
		else
			p.setVelocity(p.getLocation().getDirection().setY(1).multiply(0.8));
		
		int points = 20;
		double radius = 1.0d;
		Location pLoc = p.getLocation();
		for(int i = 0; i < points; i++) {
			double angle = 2 * Math.PI * i / points;
			Location point = pLoc.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
			point.getWorld().playEffect(point, Effect.SNOWBALL_BREAK, Integer.MAX_VALUE);
		}
		
		pLoc.getWorld().playSound(pLoc, Sound.ENDERDRAGON_WINGS, 1F, 2F);

		if(blockBehind != null && blockBehind.getType().isSolid()) {
			if(this.wallkickPlayers.contains(p.getUniqueId())) return false;
			sendUsedMessageToPlayer(p, this.wallkick);
			this.setMessage(null);
			this.setSound(null, 2F);
			this.setCooldownCanceled(true);
			
			this.wallkickPlayers.add(p.getUniqueId());
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					wallkickPlayers.remove(p.getUniqueId());
				}
				
			}.runTaskLater(Warriors.getInstance(), (int)(wallkickCooldown * 20));
			
			return true;
		}
		
		sendUsedMessageToPlayer(p, this.getName());
		this.setCooldown(3.25 - 0.25 * level);
		return true;
	}
	

}
