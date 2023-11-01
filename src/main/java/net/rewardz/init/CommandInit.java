package net.rewardz.init;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.rewardz.access.RewardPlayerAccess;
import net.rewardz.packet.RewardsServerPacket;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("rewards").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(2);
            })).then(CommandManager.literal("reset").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                return executeRewardCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 0, 0);
            }))).then(CommandManager.literal("add")
                    .then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("days", IntegerArgumentType.integer()).executes((commandContext) -> {
                        return executeRewardCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "days"), 1);
                    })))).then(CommandManager.literal("remove")
                            .then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("days", IntegerArgumentType.integer()).executes((commandContext) -> {
                                return executeRewardCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"),
                                        IntegerArgumentType.getInteger(commandContext, "days"), 2);
                            }))))
                    .then(CommandManager.literal("set")
                            .then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("days", IntegerArgumentType.integer()).executes((commandContext) -> {
                                return executeRewardCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"),
                                        IntegerArgumentType.getInteger(commandContext, "days"), 3);
                            }))))
                    .then(CommandManager.literal("info").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                        return executeRewardCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 0, 4);
                    }))));
        });
    }

    private static int executeRewardCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int count, int mode) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();

        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var3.next();

            if (mode == 0) {
                ((RewardPlayerAccess) serverPlayerEntity).setRewardDayCount(0);
                ((RewardPlayerAccess) serverPlayerEntity).setUsedRewardDays(Set.of());
            } else if (mode == 1) {
                ((RewardPlayerAccess) serverPlayerEntity).setRewardDayCount(((RewardPlayerAccess) serverPlayerEntity).getRewardDayCount() + count);
            } else if (mode == 2) {
                int newRewardCount = ((RewardPlayerAccess) serverPlayerEntity).getRewardDayCount() - count;
                ((RewardPlayerAccess) serverPlayerEntity).setRewardDayCount(newRewardCount < 0 ? 0 : newRewardCount);
            } else if (mode == 3) {
                ((RewardPlayerAccess) serverPlayerEntity).setRewardDayCount(count < 0 ? 0 : count);
            } else if (mode == 4) {
                source.sendFeedback(() -> Text.translatable("command.rewardz.info", serverPlayerEntity.getName(), ((RewardPlayerAccess) serverPlayerEntity).getRewardDayCount(),
                        ((RewardPlayerAccess) serverPlayerEntity).getUsedRewardDays()), true);
            }
            RewardsServerPacket.writeS2CSyncRewardDayCountPacket(serverPlayerEntity);
            if (mode != 4) {
                source.sendFeedback(() -> Text.translatable("commands.rewardz.rewards_changed", serverPlayerEntity.getDisplayName()), true);
            }
        }

        return targets.size();
    }

}
