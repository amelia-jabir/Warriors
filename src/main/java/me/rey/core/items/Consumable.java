package me.rey.core.items;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.rey.core.Warriors;
import me.rey.core.utils.Utils;

public abstract class Consumable implements Listener {
	
	protected HashMap<Player, Long> cooldown;
	
	private String consumableName;
	private Material mat;
	private boolean leftClick;
	private long cooldownInMillis;
	
	public Consumable(long cooldownInMillis, String name, Material item, boolean withLeftClick) {
		this.cooldownInMillis = cooldownInMillis;
		consumableName = name;
		mat = item;
		leftClick = withLeftClick;
		
		this.cooldown = new HashMap<>();;
	}
	
	protected abstract boolean ConsumeItem(Player p);
	
	public Material getMaterial() {
		return mat;
	}
	
	public String getConsumableName() {
		return consumableName;
	}
	
	public boolean hasLeftClick() {
		return leftClick;
	}
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		System.out.println(1);
		if(this.cooldown.containsKey(e.getPlayer()) && System.currentTimeMillis() - this.cooldown.get(e.getPlayer()) <= cooldownInMillis) return;
		
		Action cur = e.getAction();
		boolean allow = (cur.equals(Action.RIGHT_CLICK_AIR) || cur.equals(Action.RIGHT_CLICK_BLOCK) || (leftClick && (cur.equals(Action.LEFT_CLICK_AIR)
				|| cur.equals(Action.LEFT_CLICK_BLOCK)))) && (e.getClickedBlock() == null || e.getClickedBlock().getType().equals(Material.AIR) ||
				!Utils.usableBlocks().contains(e.getClickedBlock().getType())) && (e.getItem() != null && e.getItem().getType().equals(this.getMaterial()));

		if(allow) { 
			System.out.println(2);
			boolean success = this.ConsumeItem(e.getPlayer());
			
			if(success) {
				System.out.println(3);
				if(this.cooldown.containsKey(e.getPlayer()))
					this.cooldown.replace(e.getPlayer(), System.currentTimeMillis());
				else
					this.cooldown.put(e.getPlayer(), System.currentTimeMillis());
				
				e.setCancelled(true);
				e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
			}
		}
				
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		if(this.cooldown.containsKey(e.getPlayer()) && System.currentTimeMillis() - this.cooldown.get(e.getPlayer()) <= cooldownInMillis) return;
		
		ItemStack item = e.getPlayer().getItemInHand();
		boolean allow = item != null && item.getType().equals(this.getMaterial());

		if(allow) { 
			boolean success = this.ConsumeItem(e.getPlayer());
			
			if(success) {
				if(this.cooldown.containsKey(e.getPlayer()))
					this.cooldown.replace(e.getPlayer(), System.currentTimeMillis());
				else
					this.cooldown.put(e.getPlayer(), System.currentTimeMillis());
				
				e.setCancelled(true);
				
				item.setAmount(item.getAmount() - 1);
			}
		}
		
	}

}
