package dev.acrispycookie.crispybukkitapi.utils;

import com.mysql.jdbc.StringUtils;
import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Hologram {

	CrispyBukkitAPI api;
	final ArrayList<Player> players = new ArrayList<>();
	final ArrayList<String> lines = new ArrayList<>();
	final ArrayList<EntityArmorStand> stands = new ArrayList<>();
	Location loc;
	boolean isHidden = false;
	int ticks = -1;
	BukkitTask expire;
	
	public Hologram(CrispyBukkitAPI api, Collection<? extends Player> players, String text, Location loc, int ticks) {
		this.api = api;
		this.players.addAll(players);
		String[] texts = text.split("\n");
		lines.addAll(Arrays.asList(texts));
		this.loc = loc;
		this.ticks = ticks;
		setupStands();
	}
	
	public Hologram(Player player, String text, Location loc, int ticks) {
		this.players.add(player);
		String[] texts = text.split("\n");
		lines.addAll(Arrays.asList(texts));
		this.loc = loc;
		this.ticks = ticks;
		setupStands();
	}
	
	private void setupStands() {
		for(String s : lines) {
			if(!StringUtils.isEmptyOrWhitespaceOnly(s)) {
				EntityArmorStand as = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY() - (stands.size() * 0.25), loc.getZ());
				as.setInvisible(true);
				as.setGravity(false);
				as.setCustomNameVisible(true);
				as.setCustomName(ChatColor.translateAlternateColorCodes('&', s));
				as.setSmall(true);
				stands.add(as);
			}
			else {
				stands.add(null);
			}
		}
	}
	
	public void show() {
		isHidden = false;
		if(ticks > -1) {
			expire = new BukkitRunnable() {
				@Override
				public void run() {
					hide();
				}
			}.runTaskLater(api.getPlugin(), ticks);
		}
		for(Player p : players) {
			for(EntityArmorStand eas : stands) {
				if(eas != null) {
					PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(eas, 78);
					PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(eas.getId(), eas.getDataWatcher(), true);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawn);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(metadata);
				}
			}
		}
	}
	
	public void hide() {
		isHidden = true;
		for(Player p : players) {
			for(EntityArmorStand eas : stands) {
				if(eas != null) {
					PacketPlayOutEntityDestroy spawn = new PacketPlayOutEntityDestroy(eas.getId());
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawn);
				}
			}
		}
	}
	
	public void addPlayer(Player p) {
		if(!isHidden) {
			if(players.contains(p)) {
				players.remove(p);
			}
			players.add(p);
			for(EntityArmorStand eas : stands) {
				if(eas != null) {
					PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(eas, 78);
					PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(eas.getId(), eas.getDataWatcher(), true);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawn);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(metadata);
				}
			}
		}
	}
	
	public void setLine(int line, String text) {
		if(!isHidden) {
			if(line >= 0 && line < lines.size()) {
				if(StringUtils.isEmptyOrWhitespaceOnly(text)) {
					lines.set(line, " ");
					for(Player p : Bukkit.getOnlinePlayers()) {
						PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(stands.get(line).getId());
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
					}
					stands.set(line, null);
				}
				else {
					lines.set(line, text);
					if(stands.get(line) == null) {
						EntityArmorStand as = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
						as.setInvisible(true);
						as.setGravity(false);
						as.setCustomNameVisible(true);
						as.setSmall(true);
						as.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
						stands.set(line, as);
						for(Player p : Bukkit.getOnlinePlayers()) {
							PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(stands.get(line), 78);
							PacketPlayOutEntityMetadata packet1 = new PacketPlayOutEntityMetadata(stands.get(line).getId(), stands.get(line).getDataWatcher(), true);
							((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
							((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
						}
					}
					else {
						for(Player p : Bukkit.getOnlinePlayers()) {
							PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(stands.get(line).getId(), stands.get(line).getDataWatcher(), true);
							((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
						}
					}
				}
			}
		}
	}
	
	public void removeLine(int line) {
		if(!isHidden) {
			if(line >= 0 && line < lines.size()) {
				lines.remove(line);
				recalculateArmorStands();
			}
		}
	}
	
	public void addLine(String text) {
		if(!isHidden) {
			lines.add(text);
			recalculateArmorStands();
		}
	}
	
	public void addLine(int index, String text) {
		if(!isHidden) {
			if(index >= 0 && index < lines.size()) {
				lines.add(index, text);
				recalculateArmorStands();
			}
		}
	}
	
	public void setLocation(Location location) {
		if(!isHidden) {
			loc = location;
			recalculateArmorStands();
		}
	}
	
	public ArrayList<String> getLines() {
		return lines;
	}
	
	public int getLineCount() {
		return lines.size();
	}
	
	private void recalculateArmorStands() {
		if(!isHidden) {
			hide();
			stands.clear();
			setupStands();
			show();
		}
	}

}
