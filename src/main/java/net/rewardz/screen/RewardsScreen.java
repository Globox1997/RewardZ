package net.rewardz.screen;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.rewardz.RewardzMain;
import net.rewardz.init.RenderInit;
import net.rewardz.util.RewardHelper;

@Environment(EnvType.CLIENT)
public class RewardsScreen extends HandledScreen<RewardsScreenHandler> {

    public RewardsScreen(RewardsScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.backgroundHeight = 252;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        for (int k = 0; k < 5; k++) {
            for (int u = 0; u < 7; u++) {
                int day = u + k * 7 + 1;
                if (k == 4 && day > RewardHelper.getDaysOfMonth()) {
                    break;
                }
                if (this.client.player != null && this.handler.getUsedRewardSet(this.client.player).contains(day)) {
                    continue;
                }
                if (isPointWithinBounds(7 + u * 24, 17 + k * 29, 18, 26, mouseX, mouseY)) {
                    if (RewardzMain.REWARD_MAP.containsKey(RewardHelper.getMonth()) && RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).containsKey(day)) {
                        List<Text> list = new ArrayList<Text>();
                        int tooltipCount = (int) RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).get(day).get(2);
                        for (int i = 0; i < tooltipCount; i++) {
                            list.add(Text.of((String) RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).get(day).get(3 + i)));
                        }
                        context.drawTooltip(textRenderer, list, mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderInit.REWARD_ICONS, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.client.player != null) {
            for (int k = 0; k < 5; k++) {
                for (int u = 0; u < 7; u++) {
                    int day = u + k * 7 + 1;
                    if (k == 4 && day > RewardHelper.getDaysOfMonth()) {
                        break;
                    }
                    if (day > 28) {
                        if (day <= this.handler.getRewardDayCount(this.client.player) && !this.handler.getUsedRewardSet(this.client.player).contains(day)) {
                        } else {
                            context.drawTexture(RenderInit.REWARD_ICONS, this.x + 7 + u * 24, this.y + 17 + k * 29, 176, 16, 18, 26);
                        }
                    }
                    if (day <= this.handler.getRewardDayCount(this.client.player) && !this.handler.getUsedRewardSet(this.client.player).contains(day)) {
                        if (RewardzMain.REWARD_MAP.containsKey(RewardHelper.getMonth()) && RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).containsKey(day)
                                && (boolean) RewardzMain.REWARD_MAP.get(RewardHelper.getMonth()).get(day).get(0) && RewardHelper.getDay() != day) {
                        } else {
                            context.drawTexture(RenderInit.REWARD_ICONS, this.x + 7 + u * 24, this.y + 17 + k * 29, 194, 16, 18, 26);
                        }
                    } else if (this.handler.getUsedRewardSet(this.client.player).contains(day)) {
                        context.drawTexture(RenderInit.REWARD_ICONS, this.x + 8 + u * 24, this.y + 18 + k * 29, 176, 0, 16, 16);
                    }
                    context.drawText(textRenderer, String.valueOf(day), this.x + 14 + u * 24 + (day >= 10 ? -3 : 0), this.y + 35 + k * 29, 0x373737, false);
                }
            }
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
    }

}
