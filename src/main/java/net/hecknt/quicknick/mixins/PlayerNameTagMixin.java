package net.hecknt.quicknick.mixins;

import com.mojang.authlib.GameProfile;
import net.hecknt.quicknick.QuickNick;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerNameTagMixin extends LivingEntity {


    protected PlayerNameTagMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getName", at = @At(value = "HEAD"), cancellable = true)
    public void getName(CallbackInfoReturnable<Component> cir) {
        Player p = (Player) (Object) this;
        Component c = this.getCustomName();
        if (c != null) {
            this.setCustomNameVisible(true);
            cir.setReturnValue(c);
        } else {
            this.setCustomNameVisible(false);
            cir.setReturnValue(Component.literal(p.getGameProfile().getName()));
        }
    }
}
