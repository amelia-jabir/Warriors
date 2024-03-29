package me.rey.core.classes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.rey.core.events.customevents.update.UpdateEvent;
import me.rey.core.players.User;

public abstract class ClassCondition implements Listener {
	
	private ClassType classType;
	
	public ClassCondition(ClassType classType) {
		this.classType = classType;
	}
	
	public ClassType getClassType() {
		return classType;
	}
	
	@EventHandler
	public void onUpdate(UpdateEvent e) {
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(new User(p).getWearingClass() != this.classType) continue;
			
			this.execute(new User(p), p);
		}
	}
	
	protected abstract void execute(User user, Player player);

}
