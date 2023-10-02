package dev.acrispycookie.crispybukkitapi.utils;

import org.bukkit.enchantments.Enchantment;

public enum FriendlyEnchants {

    ARROW_DAMAGE("Power"),
    ARROW_FIRE("Flame"),
    ARROW_INFINITE("Infinite"),
    ARROW_KNOCKBACK("Punch"),
    DAMAGE_ALL("Sharpness"),
    DAMAGE_ARTHROPODS("Bane of Arthropods"),
    DAMAGE_UNDEAD("Smite"),
    DEPTH_STRIDER("Depth Strider"),
    DIG_SPEED("Efficiency"),
    DURABILITY("Unbreaking"),
    FIRE_ASPECT("Fire Aspect"),
    KNOCKBACK("Knockback"),
    LOOT_BONUS_BLOCKS("Fortune"),
    LOOT_BONUS_MOBS("Looting"),
    LUCK("Luck of the Sea"),
    LURE("Lure"),
    OXYGEN("Respiration"),
    PROTECTION_ENVIRONMENTAL("Protection"),
    PROTECTION_EXPLOSIONS("Blast Protection"),
    PROTECTION_FALL("Feather Falling"),
    PROTECTION_FIRE("Fire Protection"),
    PROTECTION_PROJECTILE("Projectile Protection"),
    SILK_TOUCH("Silk Touch"),
    THORNS("Thorns"),
    WATER_WORKER("Aqua Affinity");


    private final String friendlyName;

    FriendlyEnchants(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public static FriendlyEnchants getFriendlyEnchantment(Enchantment ench) {
        return FriendlyEnchants.valueOf(ench.getName());
    }

    public String getFriendlyName() {
        return friendlyName;
    }

}