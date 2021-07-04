package tr.com.infumia.cryptobot;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import tr.com.infumia.cryptobot.database.ConfigManager;
import tr.com.infumia.cryptobot.listeners.BuyCoin;
import tr.com.infumia.cryptobot.listeners.CoinPrices;
import tr.com.infumia.cryptobot.listeners.InviteCommand;
import tr.com.infumia.cryptobot.listeners.Leaderboard;
import tr.com.infumia.cryptobot.listeners.MineCoin;
import tr.com.infumia.cryptobot.listeners.SellCoin;
import tr.com.infumia.cryptobot.listeners.Transfer;
import tr.com.infumia.cryptobot.listeners.Wallet;
import tr.com.infumia.cryptobot.util.PriceApi;

@Log4j2
public final class Crypto {

  public static MongoClient client;
  public static CoinGeckoApiClient coinGeckoApiClient;
  private static String[] messages = null;
  private static int currentIndex=0;

  public static void main(final String[] args) throws LoginException, InterruptedException {
    final var mongoClient = MongoClients.create("mongodb://localhost:27017");
    final var configManager = new ConfigManager(mongoClient);
    Crypto.client = mongoClient;
    Crypto.messages = new String[]{"Developed by Infumia", "www.infumia.com.tr", "Ethereum", "DogeCoin", "Bitcoin"};
    ConfigManager.prefix = configManager.getPrefix();
    ConfigManager.cooldown = configManager.getCooldown();
    ConfigManager.emotes = configManager.getEmotes();
    Crypto.log.info("§aDatabase connection successful.");
    final var token = configManager.getToken();
    Crypto.coinGeckoApiClient = new CoinGeckoApiClientImpl();
    if (token.isEmpty()) {
      throw new IllegalStateException("Bot token does not exist.");
    }
    if (Crypto.coinGeckoApiClient.ping().getGeckoSays().isEmpty()) {
      throw new IllegalAccessError("Bot couldn't connect Cryptocurrency Prices API");
    }
    Crypto.log.info("§aCryptocurrency Prices API status: " + Crypto.coinGeckoApiClient.ping().getGeckoSays());
    final var jda = JDABuilder.createDefault(token)
      .addEventListeners(new CoinPrices(), new BuyCoin(), new MineCoin(), new SellCoin(), new Wallet(),
        new InviteCommand(), new Transfer(), new Leaderboard())
      .setActivity(Activity.watching("Crypto"))
      .setAutoReconnect(true)
      .build()
      .awaitReady();
    Crypto.log.info("§aJDA status: " + jda.getStatus());
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      PriceApi.refreshPrices();
      Crypto.log.info("§eCrypto Prices updated from API.");
    }, 0, 60, TimeUnit.SECONDS);
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      jda.getPresence().setActivity(Activity.playing(Crypto.messages[Crypto.currentIndex]));
      Crypto.currentIndex =(Crypto.currentIndex +1)% Crypto.messages.length;
    }, 0, 5, TimeUnit.SECONDS);

  }


}
