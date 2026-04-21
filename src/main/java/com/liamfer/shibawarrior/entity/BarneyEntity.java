package com.liamfer.shibawarrior.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BarneyEntity extends TamableAnimal {

    // Inventário do Barney com 15 slots:
    //   0=Capacete, 1=Peitoral, 2=Calça, 3=Bota,
    //   4=Espada (MAINHAND), 5=Escudo (OFFHAND),
    //   6-14=Mochila geral
    public final SimpleContainer inventory = new SimpleContainer(15);

    public BarneyEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);

        // Sincronizar o inventário com os slots de equipamento da entidade
        this.inventory.addListener(container -> {
            this.setItemSlot(EquipmentSlot.HEAD,      container.getItem(0));
            this.setItemSlot(EquipmentSlot.CHEST,     container.getItem(1));
            this.setItemSlot(EquipmentSlot.LEGS,      container.getItem(2));
            this.setItemSlot(EquipmentSlot.FEET,      container.getItem(3));
            this.setItemSlot(EquipmentSlot.MAINHAND,  container.getItem(4));
            this.setItemSlot(EquipmentSlot.OFFHAND,   container.getItem(5));
        });
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    /**
     * Sobrescreve o dano para incluir o item equipado na mão principal.
     * O Minecraft aplica o bônus de atributo da espada automaticamente via setItemSlot,
     * mas aqui garantimos que os enchantamentos (ex: Sharpness) também sejam aplicados.
     */
    @Override
    public boolean doHurtTarget(Entity target) {
        float baseDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack weapon = this.getItemBySlot(EquipmentSlot.MAINHAND);

        float enchantBonus = 0F;
        if (!weapon.isEmpty() && target instanceof LivingEntity living) {
            enchantBonus = EnchantmentHelper.getDamageBonus(weapon, living.getMobType());
        }

        float totalDamage = baseDamage + enchantBonus;
        boolean didHurt = target.hurt(this.damageSources().mobAttack(this), totalDamage);

        if (didHurt && !weapon.isEmpty()) {
            // Aplica efeito de knockback de enchantamentos (Knockback)
            if (target instanceof LivingEntity living) {
                EnchantmentHelper.doPostHurtEffects(living, this);
                EnchantmentHelper.doPostDamageEffects(this, living);
            }
            weapon.hurtAndBreak(1, this, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return didHurt;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.level().isClientSide) {
            boolean isTameItem = itemStack.isEdible() && itemStack.getItem().getFoodProperties() != null && itemStack.getItem().getFoodProperties().isMeat();
            return isTameItem || this.isTame() || this.isOwnedBy(player) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                // Agachar + Qualquer Clique = Abrir Inventário
                if (player.isCrouching()) {
                    if (!this.level().isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        serverPlayer.openMenu(new net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory() {
                            @Override
                            public void writeScreenOpeningData(net.minecraft.server.level.ServerPlayer player, net.minecraft.network.FriendlyByteBuf buf) {
                                buf.writeInt(BarneyEntity.this.getId());
                            }

                            @Override
                            public Component getDisplayName() {
                                return Component.literal("Inventário do Barney");
                            }

                            @Nullable
                            @Override
                            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int syncId, net.minecraft.world.entity.player.Inventory inv, Player player) {
                                return new com.liamfer.shibawarrior.inventory.BarneyMenu(syncId, inv, BarneyEntity.this.inventory, BarneyEntity.this);
                            }
                        });
                    }
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
                
                // Mão vazia (sem shift) = Sentar/Levantar
                if (hand == InteractionHand.MAIN_HAND && itemStack.isEmpty() && !player.isCrouching()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (itemStack.isEdible() && itemStack.getItem().getFoodProperties() != null && itemStack.getItem().getFoodProperties().isMeat()) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        
        // Salvar estado de Sentar
        tag.putBoolean("BarneySitting", this.isOrderedToSit());
        
        // Salvar Inventário
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemStack = this.inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                itemStack.save(compoundTag);
                listTag.add(compoundTag);
            }
        }
        tag.put("BarneyInventory", listTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        
        // Restaurar estado de Sentar
        if (tag.contains("BarneySitting")) {
            this.setOrderedToSit(tag.getBoolean("BarneySitting"));
        }
        
        // Restaurar Inventário
        if (tag.contains("BarneyInventory", 9)) {
            ListTag listTag = tag.getList("BarneyInventory", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                int j = compoundTag.getByte("Slot") & 255;
                if (j >= 0 && j < this.inventory.getContainerSize()) {
                    this.inventory.setItem(j, ItemStack.of(compoundTag));
                }
            }
        }
    }

    @Override
    protected void dropAllDeathLoot(net.minecraft.world.damagesource.DamageSource source) {
        // Dropar todos os itens do inventário do Barney (armaduras, armas e mochila)
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
                this.spawnAtLocation(stack);
                this.inventory.setItem(i, ItemStack.EMPTY);
            }
        }
        // Chama o super para dropar xp e itens normais de entidade
        super.dropAllDeathLoot(source);
    }
}
