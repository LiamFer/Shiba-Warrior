package com.liamfer.shibawarrior.client.renderer;

import com.liamfer.shibawarrior.client.model.BarneyModel;
import com.liamfer.shibawarrior.entity.BarneyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BarneyRenderer extends MobRenderer<BarneyEntity, BarneyModel<BarneyEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("shibawarrior", "textures/entity/barney.png");

    public BarneyRenderer(EntityRendererProvider.Context context) {
        super(context, new BarneyModel<>(context.bakeLayer(BarneyModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BarneyEntity entity) {
        return TEXTURE;
    }
}
