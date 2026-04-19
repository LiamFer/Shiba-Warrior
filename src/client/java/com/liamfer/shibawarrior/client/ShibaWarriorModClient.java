package com.liamfer.shibawarrior.client;

import com.liamfer.shibawarrior.ShibaWarriorMod;
import com.liamfer.shibawarrior.client.model.BarneyModel;
import com.liamfer.shibawarrior.client.renderer.BarneyRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ShibaWarriorModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ShibaWarriorMod.BARNEY, BarneyRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(BarneyModel.LAYER_LOCATION, BarneyModel::createBodyLayer);
	}
}
