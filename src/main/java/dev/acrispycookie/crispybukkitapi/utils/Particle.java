package dev.acrispycookie.crispybukkitapi.utils;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Particle {

	private final CrispyBukkitAPI api;
	private Effect effect;
	private float r = 0;
	private float g = 0;
	private float b = 0;
	private int data;
	private Location loc;
	private BukkitRunnable runnable;
	
	public Particle(CrispyBukkitAPI api, Effect effect, int data, Location loc) {
		this.api = api;
		this.effect = effect;
		this.data = data;
		this.loc = loc;
	}
	
	public void play(Player player, int duration) {
		if(runnable != null) {
			runnable.cancel();
		}
		runnable = new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				if(duration > 0) {
					if(i != duration) {
						if(effect != Effect.COLOURED_DUST) {
							player.spigot().playEffect(loc, effect, data, data, 0, 0, 0, 1, 100, 160);
						}
						else {
							player.spigot().playEffect(loc, Effect.COLOURED_DUST, 0, 1, r, g, b, 1, 0, 160);
						}
					}
					else {
						cancel();
					}
					i++;
				}
				else {
					cancel();
				}
			}
		};
		runnable.runTaskTimerAsynchronously(api.getPlugin(), 0L, 1L);
	}
	
	public void stop() {
		runnable.cancel();
	}
	
	public void setEffect(Effect eff, int data) {
		effect = eff;
		this.data = data;
	}
	
	public void setRgb(float r, float g, float b) {
		this.r = r/255.0F - 1.0F;
		this.g = g/255.0F;
		this.b = b/255.0F;
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	public Location getLocation() {
		return loc.clone();
	}

}
