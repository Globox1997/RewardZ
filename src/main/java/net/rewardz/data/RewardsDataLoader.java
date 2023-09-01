package net.rewardz.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.rewardz.RewardzMain;

public class RewardsDataLoader implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger("RewardZ");

    @Override
    public Identifier getFabricId() {
        return new Identifier("rewards", "rewards_loader");
    }

    @Override
    public void reload(ResourceManager resourceManager) {

        resourceManager.findResources("rewards", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (int i = 1; i <= 12; i++) {
                    Map<Integer, List<Object>> monthMap = new HashMap<Integer, List<Object>>();
                    if (data.has("month_" + i)) {
                        JsonObject jsonMonthObject = data.get("month_" + i).getAsJsonObject();
                        for (int u = 1; u <= 31; u++) {

                            if (jsonMonthObject.has("day_" + u)) {
                                List<Object> dayList = new ArrayList<Object>();
                                JsonObject jsonDayObject = jsonMonthObject.get("day_" + u).getAsJsonObject();
                                boolean exact = false;
                                if (jsonDayObject.has("exact")) {
                                    exact = jsonDayObject.get("exact").getAsBoolean();
                                }
                                dayList.add(exact);
                                JsonObject jsonItemObject = jsonDayObject.get("item").getAsJsonObject();
                                ItemStack itemStack = Registries.ITEM.get(new Identifier(jsonItemObject.get("item").getAsString())).getDefaultStack();
                                itemStack.setCount(jsonItemObject.get("count").getAsInt());
                                if (jsonItemObject.has("nbt") && !jsonItemObject.get("nbt").isJsonNull()) {
                                    try {
                                        NbtCompound nbtCompound = StringNbtReader.parse(jsonItemObject.get("nbt").getAsString());
                                        itemStack.setNbt(nbtCompound);
                                    } catch (CommandSyntaxException commandSyntaxException) {
                                        throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
                                    }
                                }
                                dayList.add(itemStack);
                                if (jsonDayObject.has("tooltip")) {
                                    dayList.add(jsonDayObject.get("tooltip").getAsJsonArray().size());
                                    for (int j = 0; j < jsonDayObject.get("tooltip").getAsJsonArray().size(); j++) {
                                        dayList.add(jsonDayObject.get("tooltip").getAsJsonArray().get(j).getAsString());
                                    }
                                } else {
                                    dayList.add(0);
                                }
                                if (jsonDayObject.has("commands")) {
                                    dayList.add(jsonDayObject.get("commands").getAsJsonArray().size());
                                    for (int j = 0; j < jsonDayObject.get("commands").getAsJsonArray().size(); j++) {
                                        dayList.add(jsonDayObject.get("commands").getAsJsonArray().get(j).getAsString());
                                    }
                                } else {
                                    dayList.add(0);
                                }
                                monthMap.put(u, dayList);
                            }
                        }
                    }
                    RewardzMain.REWARD_MAP.put(i, monthMap);
                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}
