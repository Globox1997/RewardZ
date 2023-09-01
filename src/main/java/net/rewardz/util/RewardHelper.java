package net.rewardz.util;

import java.time.LocalDate;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.rewardz.RewardzMain;

public class RewardHelper {

    public static int getDaysOfMonth() {
        return LocalDate.now().lengthOfMonth();
    }

    public static int getMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static int getDay() {
        return LocalDate.now().getDayOfMonth();
    }

    public static void runRewardCommands(ServerPlayerEntity player, int day) {
        if (RewardzMain.REWARD_MAP.containsKey(getMonth()) && RewardzMain.REWARD_MAP.get(getMonth()).containsKey(day)) {
            int tooltipSize = (int) RewardzMain.REWARD_MAP.get(getMonth()).get(day).get(2);
            int commandSize = (int) RewardzMain.REWARD_MAP.get(getMonth()).get(day).get(3 + tooltipSize);
            for (int i = 0; i < commandSize; i++) {
                runCommand(player.getServer(), player, (String) RewardzMain.REWARD_MAP.get(getMonth()).get(day).get(4 + tooltipSize + i));
            }
        }

    }

    public static void runCommand(MinecraftServer server, ServerPlayerEntity player, String command) {
        try {
            server.getCommandManager().executeWithPrefix(new ServerCommandSource(CommandOutput.DUMMY, player.getPos(), player.getRotationClient(), player.getServerWorld(), 2,
                    player.getName().getString(), player.getName(), server, player), command);
        } catch (Throwable throwable) {
        }
    }

}
