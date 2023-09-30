package dev.acrispycookie.crispybukkitapi.utils;

import com.mysql.jdbc.StringUtils;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class PublicHologram implements Listener {
	
	final ArrayList<String> lines = new ArrayList<>();
	final ArrayList<EntityArmorStand> stands = new ArrayList<>();
	String name;
	Location loc;
	boolean hidden = true;
	boolean destroyed = false;
	public static final ArrayList<PublicHologram> holos = new ArrayList<>();
	
	public PublicHologram(String text, Location loc) {
		String[] texts = text.split("\n");
		lines.addAll(Arrays.asList(texts));
		this.loc = loc;
		setupStands();
	}
	
	public PublicHologram(String text, Location loc, String name) {
		String[] texts = text.split("\n");
		lines.addAll(Arrays.asList(texts));
		this.loc = loc;
		this.name = name;
		setupStands();
	}
	
	public PublicHologram() {
	}
	
	private void setupStands() {
		holos.add(this);
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
		if(!destroyed) {
			hidden = false;
			for(Player p : Bukkit.getOnlinePlayers()) {
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
	}
	
	public void show(Player p) {
		for(EntityArmorStand eas : stands) {
			if(eas != null) {
				PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(eas, 78);
				PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(eas.getId(), eas.getDataWatcher(), true);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawn);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(metadata);
			}
		}
	}
	
	public void hide() {
		if(isHidden()) {
			hidden = true;
			for(Player p : Bukkit.getOnlinePlayers()) {
				for(EntityArmorStand eas : stands) {
					if(eas != null) {
						PacketPlayOutEntityDestroy spawn = new PacketPlayOutEntityDestroy(eas.getId());
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawn);
					}
				}
			}
		}
	}
	
	public void destroy() {
		if(!destroyed) {
			hide();
			hidden = true;
			destroyed = true;
			holos.remove(this);
		}
	}
	
	public void setLine(int line, String text) {
		if(!destroyed && !hidden) {
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
						EntityArmorStand as = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY() - (line * 0.25), loc.getZ());
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
						stands.get(line).setCustomName(ChatColor.translateAlternateColorCodes('&', text));
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
		if(!destroyed && !hidden) {
			if(line >= 0 && line < lines.size()) {
				lines.remove(line);
				recalculateArmorStands();
			}
		}
	}
	
	public void addLine(String text) {
		if(!destroyed && !hidden) {
			lines.add(text);
			recalculateArmorStands();
		}
	}
	
	public void addLine(int index, String text) {
		if(!destroyed && !hidden) {
			if(index >= 0 && index < lines.size()) {
				lines.add(index, text);
				recalculateArmorStands();
			}
		}
	}
	
	public void setLocation(Location location) {
		if(!destroyed && !hidden) {
			loc = location;
			recalculateArmorStands();
		}
	}
	
	public boolean isHidden() {
		return !hidden;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public ArrayList<String> getLines() {
		return lines;
	}
	
	public int getLineCount() {
		return lines.size();
	}
	
	public Location getLocation() {
		return loc.clone();
	}
	
	public String getName() {
		return name;
	}
	
	private void recalculateArmorStands() {
		if(!destroyed && !hidden) {
			hide();
			stands.clear();
			setupStands();
			show();
		}
	}
	
	public static PublicHologram getByName(String s) {
		for(PublicHologram h : holos) {
			if(h.name != null && h.name.equalsIgnoreCase(s)) {
				return h;
			}
		}
		return null;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		for(PublicHologram h : holos) {
			if(h.loc.getWorld().equals(p.getWorld()) && h.isHidden()) {
				h.show(p);
			}
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		for(PublicHologram h : holos) {
			if(h.loc.getWorld().equals(p.getWorld()) && h.isHidden()) {
				h.show(p);
			}
		}
	}

}
