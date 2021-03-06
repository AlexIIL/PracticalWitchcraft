package io.github.cottonmc.witchcraft.karma;

import com.raphydaphy.crochet.data.PlayerData;
import io.github.cottonmc.witchcraft.Witchcraft;
import io.github.cottonmc.witchcraft.util.WitchcraftNetworking;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.ServerPlayerEntity;

public class KarmaManager {
	public static void shiftKarma(PlayerEntity player, int amount) {
		shiftKarma(player, amount, false);
	}

	public static void shiftKarma(PlayerEntity player, int amount, boolean passive) {
		if (amount == 0) return;
		CompoundTag tag = PlayerData.get(player, Witchcraft.MODID);
		int karma;
		if (!tag.containsKey("Karma", NbtType.INT)) {
			karma = amount;
			tag.putInt("Karma", amount);
		} else {
			karma = tag.getInt("Karma");
			karma += amount;
		}
		tag.putInt("Karma", karma);
		PlayerData.markDirty(player);
		player.addChatMessage(new TranslatableComponent("msg.witchcraft.karma." + (amount > 0? "gain" : "lose")), true);
		if (passive) return;
		if (amount > 0) {
			player.removePotionEffect(StatusEffects.UNLUCK);
			WitchcraftNetworking.removeEffect((ServerPlayerEntity)player, StatusEffects.UNLUCK);
			int multiplier = amount / 5;
			int duration = 1200 * (amount % 5);
			player.addPotionEffect(new StatusEffectInstance(StatusEffects.LUCK, duration, multiplier, false, false, true));
		} else {
			player.removePotionEffect(StatusEffects.LUCK);
			WitchcraftNetworking.removeEffect((ServerPlayerEntity)player, StatusEffects.LUCK);
			int multiplier = ((amount * -1) / 5);
			int duration = 1200 * ((amount * -1) % 5);
			player.addPotionEffect(new StatusEffectInstance(StatusEffects.UNLUCK, duration, multiplier, false, false, true));
		}
		if (karma > 10) {
			player.removePotionEffect(StatusEffects.BAD_OMEN);
			WitchcraftNetworking.removeEffect((ServerPlayerEntity)player, StatusEffects.BAD_OMEN);
		} else if (karma < 10) {
			player.removePotionEffect(StatusEffects.HERO_OF_THE_VILLAGE);
			WitchcraftNetworking.removeEffect((ServerPlayerEntity)player, StatusEffects.HERO_OF_THE_VILLAGE);
		}
		if (karma >= 20) {
			player.addPotionEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 18000, 0, false, false, true));
		} else if (karma <= -20) {
			int multiplier = ((karma * -1) - 20) / 5;
			player.addPotionEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 18000, multiplier, false, false, true));
		}
	}

	public static void resetKarma(PlayerEntity player) {
		CompoundTag tag = PlayerData.get(player, Witchcraft.MODID);
		tag.putInt("Karma", 0);
		PlayerData.markDirty(player);
	}

	public static void setKarma(PlayerEntity player, int amount) {
		setKarma(player, amount, false);
	}

	public static void setKarma(PlayerEntity player, int amount, boolean passive) {
		CompoundTag tag = PlayerData.get(player, Witchcraft.MODID);
		tag.putInt("Karma", amount);
		PlayerData.markDirty(player);
		if (passive) return;
		if (amount > 10) {
			player.removePotionEffect(StatusEffects.BAD_OMEN);
			WitchcraftNetworking.removeEffect((ServerPlayerEntity)player, StatusEffects.BAD_OMEN);
		} else if (amount < 10) {
			player.removePotionEffect(StatusEffects.HERO_OF_THE_VILLAGE);
			WitchcraftNetworking.removeEffect((ServerPlayerEntity)player, StatusEffects.HERO_OF_THE_VILLAGE);
		}
		if (amount >= 20) {
			player.addPotionEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 18000, 0, true, false));
		} else if (amount <= -20) {
			int multiplier = ((amount * -1) - 20) / 5;
			player.addPotionEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 18000, multiplier, true, false));
		}
	}
}
