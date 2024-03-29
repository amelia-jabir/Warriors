package me.rey.core.events;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.core.Warriors;
import me.rey.core.players.User;

public class ClassHandler extends BukkitRunnable {
	
	public ClassHandler() {
		this.start();
	}
	
	private void start() {
		this.runTaskTimer(Warriors.getInstance(), 0, 1);
	}

	@Override
	public void run() {
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			
			User user = new User(p);
				
			user.updateClassEffects();
					
			if(Warriors.userCache.containsKey(p)) {
				
				if(!hasArmor(p)) {
					
					Warriors.userCache.remove(p);
					user.resetEffects();
					user.sendMessageWithPrefix("Class", "You took off your armor set.");
					continue;
				}
				
				if(Warriors.userCache != null && Warriors.userCache.get(p) != null && hasArmor(p)
						&& !Warriors.userCache.get(p).equals(user.getWearingClass())) {
					
					Warriors.userCache.replace(p, user.getWearingClass());
					user.resetEffects();
					user.sendMessageWithPrefix("Class", "You took off your armor set.");
					user.sendMessageWithPrefix("Class", "You equipped &e" + user.getWearingClass().getName() + "&7.");
					p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
					user.sendBuildEquippedMessage(user.getWearingClass());
					continue;
				}
				
				
			} else {
				
				if(!hasArmor(p)) continue;
				
				Warriors.userCache.put(p, user.getWearingClass());
				user.resetEffects();
				user.sendMessageWithPrefix("Selector", "You equipped &e" + user.getWearingClass().getName() + "&7.");
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
				user.sendBuildEquippedMessage(user.getWearingClass());
				
			}
		}
	}
	
	private boolean hasArmor(Player p) {
		return p.getInventory().getHelmet() != null && p.getInventory().getChestplate() != null
				&& p.getInventory().getLeggings() != null && p.getInventory().getBoots() != null;
	}

}
