package net.rewardz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.rewardz.RewardzMain;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.init.ConfigInit;
import net.rewardz.init.RenderInit;
import net.rewardz.packet.RewardsClientPacket;
import net.rewardz.util.RewardHelper;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> info) {
        if (this.client != null && this.focusedSlot == null
                && this.isPointWithinBounds(ConfigInit.CONFIG.posX + (RewardzMain.isPatchouliButtonLoaded ? 23 : 0), ConfigInit.CONFIG.posY, 20, 18, (double) mouseX, (double) mouseY)) {
            RewardsClientPacket.writeC2SRewardsScreenPacket(client);
        }
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    protected void drawBackgroundMixin(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
        int rewardButtonX = +ConfigInit.CONFIG.posX + (RewardzMain.isPatchouliButtonLoaded ? 23 : 0);
        if (this.isPointWithinBounds(ConfigInit.CONFIG.posX, ConfigInit.CONFIG.posY, 20, 18, (double) mouseX, (double) mouseY)) {
            context.drawTexture(RenderInit.REWARD_ICONS, this.x + rewardButtonX, this.y + ConfigInit.CONFIG.posY, 196, 42, 20, 18);
            renderCheckMark(context);
            context.drawTooltip(this.textRenderer, Text.translatable("screen.rewardz"), mouseX, mouseY);
        } else {
            context.drawTexture(RenderInit.REWARD_ICONS, this.x + rewardButtonX, this.y + ConfigInit.CONFIG.posY, 176, 42, 20, 18);
            renderCheckMark(context);
        }
    }

    private void renderCheckMark(DrawContext context) {
        if (this.client.player != null && ((RewardPlayerAccess) this.client.player).getUsedRewardDays().size() < ((RewardPlayerAccess) this.client.player).getRewardDayCount()
                && !hasOnlyExactRewardsLeft()) {
            context.drawTexture(RenderInit.REWARD_ICONS, this.x + 2 + ConfigInit.CONFIG.posX + (RewardzMain.isPatchouliButtonLoaded ? 23 : 0), this.y + 2 + ConfigInit.CONFIG.posY, 192, 0, 16, 14);
        } else {
            context.drawTexture(RenderInit.REWARD_ICONS, this.x + 2 + ConfigInit.CONFIG.posX + (RewardzMain.isPatchouliButtonLoaded ? 23 : 0), this.y + 2 + ConfigInit.CONFIG.posY, 176, 0, 16, 14);
        }
    }

    private boolean hasOnlyExactRewardsLeft() {
        boolean exact = false;
        for (int i = 0; i < ((RewardPlayerAccess) this.client.player).getRewardDayCount(); i++) {
            if (((RewardPlayerAccess) this.client.player).getUsedRewardDays().contains(i)) {
                continue;
            }
            if (RewardzMain.REWARD_MAP.containsKey(RewardHelper.getMonth()) && RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).containsKey(i)
                    && (boolean) RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).get(i).get(0) && RewardHelper.getDay() != i) {
                exact = true;
            } else {
                exact = false;
            }
        }

        return exact;
    }
}
