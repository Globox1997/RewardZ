package net.rewardz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.packet.RewardsServerPacket;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        RewardsServerPacket.writeS2CSyncRewardsPacket(player.networkHandler);
        ((RewardPlayerAccess) player).increaseRewardDay();
        RewardsServerPacket.writeS2CSyncRewardDayCountPacket(player);
    }

}
