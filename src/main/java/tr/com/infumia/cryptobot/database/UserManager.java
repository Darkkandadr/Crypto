package tr.com.infumia.cryptobot.database;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.cryptobot.Crypto;
import tr.com.infumia.cryptobot.util.PriceApi;

public final class UserManager {

  @NotNull
  private static final MongoDatabase database = Crypto.client.getDatabase("Guilds");

  private UserManager() {
  }

  public static void setCoin(@NotNull final String guildId, @NotNull final String userid, @NotNull final String coin,
                             final double value) {
    final var collection = UserManager.database.getCollection(guildId);
    final var filter = new Document("_id", userid);
    if (collection.findOneAndUpdate(filter, Updates.set(coin, value)) == null) {
      collection.insertOne(filter.append(coin, value));
    }
  }

  public static Double getCoin(@NotNull final String guildId, @NotNull final String userid,
                                         @NotNull final String coin) {
    final var collection = UserManager.database.getCollection(guildId);
    final var filter = new Document("_id", userid);
    if (collection.find(filter).first() != null) {
      final var aDouble = Objects.requireNonNull(collection.find(filter).first()).getDouble(coin);
      return Objects.requireNonNullElse(aDouble, 0.0);
    }
    else{
      return 0.0;
    }
  }

  public static @Nullable Map<String, Object> getAllData(@NotNull final String guildID, @NotNull final String userId) {

    final var dataMap = new HashMap<String, Object>();
    final var collection = UserManager.database.getCollection(guildID);
    final var filter = new Document("_id", userId);

    if (collection.find(filter).first() != null) {
      Objects.requireNonNull(collection.find(filter).first()).forEach(dataMap::put);
      return dataMap;
    }
    else {
      return null;
    }
  }

  public static Map.Entry<Boolean, Double> hasMoney(@NotNull final String guildId, @NotNull final String userId,
                                                    @NotNull final String coinName, final double amount){
    final var prices = PriceApi.getPrices();
    final var usdBalance = UserManager.getCoin(guildId, userId, "USD");
    // 0.1 eth (amount) = 0.1 * eth price (need money)
    final var needMoney = amount * prices.get(coinName);
    if (usdBalance != null && usdBalance >= needMoney){
      return Map.entry(true, needMoney);
    }
    else {
      return Map.entry(false, needMoney);
    }
  }
}
