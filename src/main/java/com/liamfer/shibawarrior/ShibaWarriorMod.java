package com.liamfer.shibawarrior;

import com.liamfer.shibawarrior.entity.BarneyEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShibaWarriorMod implements ModInitializer {
	public static final String MOD_ID = "shibawarrior";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<BarneyEntity> BARNEY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "barney"),
			FabricEntityTypeBuilder.create(MobCategory.CREATURE, BarneyEntity::new)
					.dimensions(EntityDimensions.fixed(0.75f, 1.5f))
					.build()
	);

	public static final Item BARNEY_SPAWN_EGG = Registry.register(
			BuiltInRegistries.ITEM,
			new ResourceLocation(MOD_ID, "barney_spawn_egg"),
			new SpawnEggItem(BARNEY, 0x990099, 0x00FF00, new Item.Properties())
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello from Shiba Warrior Mod!");
		FabricDefaultAttributeRegistry.register(BARNEY, BarneyEntity.createAttributes());
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
			entries.accept(BARNEY_SPAWN_EGG);
		});
	}
}
