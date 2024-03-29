package me.rey.core.classes.abilities.druid.passive_b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.core.Warriors;
import me.rey.core.classes.ClassType;
import me.rey.core.classes.abilities.Ability;
import me.rey.core.classes.abilities.AbilityType;
import me.rey.core.classes.abilities.IDamageTrigger.IPlayerDamagedEntity;
import me.rey.core.events.customevents.combat.DamageEvent;
import me.rey.core.players.User;

public class MagmaBlade extends Ability implements IPlayerDamagedEntity {
	
	private ArrayList<UUID> cooldowns;
	private double igniteSeconds = 2;
	private double damageToFire = 1;

	public MagmaBlade() {
		super(241, "Magma Blade", ClassType.GOLD, AbilityType.PASSIVE_B, 1, 3, 0.00, Arrays.asList(
				"Your sword deals an additional",
				"<variable>1</variable> damage to burning opponents,",
				"but also extinguishes them.", "",
				"When the opponent is not in fire,",
				"you ignite them for <variable>1</variable> second with",
				"a <variable>5.5-(0.5*l)</variable> (-0.5) second cooldown."
				));
		
		this.cooldowns = new ArrayList<>();
		this.setIgnoresCooldown(true);
		this.setInLiquid(true);
	}

	@Override
	protected boolean execute(User u, Player p, int level, Object... conditions) {
		DamageEvent e = (DamageEvent) conditions[0];
		if(cooldowns.contains(p.getUniqueId())) return false;
		
		if(e.getDamagee().getFireTicks() <= 0) {
			e.getDamagee().setFireTicks((int) (igniteSeconds * 20)); 
			return true;
		}
		
		e.addMod(damageToFire);
		cooldowns.add(p.getUniqueId());
		double cooldownToRemove = 5.5 - (0.5 * level);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				cooldowns.remove(p.getUniqueId());
			}
		}.runTaskLater(Warriors.getInstance(), (int) (cooldownToRemove * 20));
		return true;
	}


}
