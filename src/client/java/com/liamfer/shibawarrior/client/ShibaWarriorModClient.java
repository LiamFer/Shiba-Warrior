package com.liamfer.shibawarrior.client;

import com.liamfer.shibawarrior.ShibaWarriorMod;
import com.liamfer.shibawarrior.client.model.BarneyModel;
import com.liamfer.shibawarrior.client.renderer.BarneyRenderer;
import com.liamfer.shibawarrior.client.gui.screens.inventory.BarneyScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class ShibaWarriorModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ShibaWarriorMod.BARNEY, BarneyRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(BarneyModel.LAYER_LOCATION, BarneyModel::createBodyLayer);
		MenuScreens.register(ShibaWarriorMod.BARNEY_MENU, BarneyScreen::new);
	}
}
