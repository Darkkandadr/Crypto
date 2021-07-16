package tr.com.infumia.cryptobot.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.HashMap;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public final class ConfigManager {

  public static String prefix;
  public static int cooldown;
  public static int restartTime;
  public static HashMap<Object, Object> emotes = new HashMap<>();

  @NotNull
  private final MongoCollection<Document> configCollection;

  public ConfigManager(@NotNull final MongoClient client) {
    this.configCollection = client.getDatabase("CryptoDB")
      .getCollection("general_config");
  }

  public String getPrefix() {
    var first = this.configCollection
      .find(Filters.eq("_id", 2))
      .first();
    if (first == null) {
      first = new Document(new HashMap<>() {{
        this.put("_id", 2);
        this.put("prefix", "!");
      }});
      this.configCollection.insertOne(first);
    }
    ConfigManager.prefix = first.getString("prefix");
    if (ConfigManager.prefix == null) {
      first.append("prefix", "!");
      this.configCollection.updateOne(Filters.eq("_id", 2), first);
      ConfigManager.prefix = "!";
    }
    return ConfigManager.prefix;
  }

  public String getToken() {
    var first = this.configCollection
      .find(Filters.eq("_id", 1))
      .first();
    if (first == null) {
      first = new Document(new HashMap<>() {{
        this.put("_id", 1);
        this.put("token", "");
      }});
      this.configCollection.insertOne(first);
    }
    var token = first.getString("token");
    if (token == null) {
      first.append("token", "");
      this.configCollection.updateOne(Filters.eq("_id", 1), first);
      token = "";
    }
    return token;
  }

  public int getCooldown() {
    var first = this.configCollection
      .find(Filters.eq("_id", 3))
      .first();
    if (first == null) {
      first = new Document(new HashMap<>() {{
        this.put("_id", 3);
        this.put("cooldown", 60);
      }});
      this.configCollection.insertOne(first);
    }
    ConfigManager.cooldown = first.getInteger("cooldown", 60);
    return ConfigManager.cooldown;
  }

  public HashMap<Object, Object> getEmotes() {
    var first = this.configCollection
      .find(Filters.eq("_id", 4))
      .first();
    if (first == null) {
      first = new Document(new HashMap<>() {{
        this.put("_id", 4);
        this.put("Bitcoin", "<:bitcoin:847568647413170186>");
        this.put("Ethereum", "<:ethereum:847568647442792468>");
        this.put("Ripple", "<:ripple:847568647114981438>");
        this.put("BinanceCoin", "<:binancecoin:847568647446331401>");
        this.put("DogeCoin", "<:dogecoin:847568647488405514>");
        this.put("Cardano", "<:cardano:847582350379974707>");
        this.put("USD", ":dollar:");
      }});
      this.configCollection.insertOne(first);
    }
    final var center = first.getString("Ripple");
    if (center == null) {
      first.append("Bitcoin", "<:bitcoin:847568647413170186>");
      first.append("Ethereum", "<:ethereum:847568647442792468>");
      first.append("Ripple", "<:ripple:847568647114981438>");
      first.append("BinanceCoin", "<:binancecoin:847568647446331401>");
      first.append("DogeCoin", "<:dogecoin:847568647488405514>");
      first.append("Cardano", "<:cardano:847582350379974707>");
      first.append("USD", ":dollar:");
      this.configCollection.updateOne(Filters.eq("_id", 4), first);
    }
    first.forEach(ConfigManager.emotes::put);
    return ConfigManager.emotes;
  }

  public int getRestartTimeInMinutes() {
    var first = this.configCollection
      .find(Filters.eq("_id", 5))
      .first();
    if (first == null) {
      first = new Document(new HashMap<>() {{
        this.put("_id", 5);
        this.put("restartTime", 30);
      }});
      this.configCollection.insertOne(first);
    }
    ConfigManager.restartTime = first.getInteger("restartTime", 30);
    return ConfigManager.restartTime;
  }

}
