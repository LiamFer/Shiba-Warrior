package com.liamfer.shibawarrior.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import com.liamfer.shibawarrior.entity.BarneyEntity;

public class BarneyModel<T extends BarneyEntity> extends EntityModel<T> implements ArmedModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("shibawarrior", "barney"), "main");
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart tail;

	public BarneyModel(ModelPart root) {
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.left_arm = root.getChild("left_arm");
		this.right_arm = root.getChild("right_arm");
		this.right_leg = root.getChild("right_leg");
		this.left_leg = root.getChild("left_leg");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 6).addBox(1.0F, -8.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 28).addBox(-3.0F, -8.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-3.0F, -6.0F, -2.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, -1.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, -10.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(20, 20).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 15.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(20, 12).addBox(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 15.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(10, 22).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(7, 2).addBox(-1.0F, 3.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 20.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(7, 2).addBox(-1.0F, 3.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 20.0F, 0.0F));

		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 3.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// Cabeça
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
		this.head.xRot = headPitch * ((float)Math.PI / 180F);

		if (entity.isInSittingPose()) {
		    this.right_leg.xRot = -1.5708F; // 90 graus
		    this.left_leg.xRot = -1.5708F;
		    this.right_arm.xRot = 0;
		    this.left_arm.xRot = 0;
		    this.tail.yRot = 0;
            // Descer um pouco pra parecer sentado
            this.body.y = 26.0F;
            this.head.y = 16.0F;
            this.left_arm.y = 17.0F;
            this.right_arm.y = 17.0F;
            this.tail.y = 22.0F;
		} else {
            // Restaurar posições normais
            this.body.y = 24.0F;
            this.head.y = 14.0F;
            this.left_arm.y = 15.0F;
            this.right_arm.y = 15.0F;
            this.tail.y = 20.0F;

		    // Andar (Pernas)
		    this.right_leg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		    this.left_leg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		    // Andar (Braços)
		    this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		    this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		    // Cauda abanando
		    this.tail.yRot = Mth.cos(limbSwing * 0.6662F) * 0.5F * limbSwingAmount;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        if (arm == HumanoidArm.RIGHT) {
            this.right_arm.translateAndRotate(poseStack);
            // Empurrar a espada para o final da pata
            poseStack.translate(0.0D, 0.35D, -0.05D);
            // Rotacionar para ela apontar para frente como no braço do player
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
        } else {
            this.left_arm.translateAndRotate(poseStack);
            // Empurrar o escudo para o final da pata esquerda
            poseStack.translate(0.0D, 0.35D, -0.05D);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90.0F));
        }
    }
}