package com.cloudcraftgaming.discal.internal.network.discordpw;

import com.cloudcraftgaming.discal.Main;
import com.cloudcraftgaming.discal.internal.data.BotSettings;
import com.cloudcraftgaming.discal.utils.ExceptionHandler;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

/**
 * Created by Nova Fox on 1/13/2017.
 * Website: www.cloudcraftgaming.com
 * For Project: DisCal
 */
public class UpdateListData {
    private static String token;

    /**
     * Initiates the data updater with a valid token.
     * @param settings BotSettings containing the API token.
     */
    public static void init(BotSettings settings) {
        token = settings.getBotsPwToken();
    }

    /**
     * Updates the site meta on bots.discord.pw
     */
    public static void updateSiteBotMeta() {
        try {
            Integer serverCount = Main.client.getGuilds().size();

            JSONObject json = new JSONObject()
					.put("shard_id", Main.client.getShards().get(0).getInfo()[0])
					.put("shard_count", Main.client.getShardCount())
					.put("server_count", serverCount);

            HttpResponse<JsonNode> response = Unirest.post("https://bots.discord.pw/api/bots/265523588918935552/stats").header("Authorization", token).header("Content-Type", "application/json").body(json).asJson();
        } catch (Exception e) {
            //Handle issue.
            System.out.println("Failed to update Discord PW list metadata!");
            ExceptionHandler.sendException(null, "Failed to update Discord PW list.", e, UpdateListData.class);
            e.printStackTrace();
        }
    }
}