package tr.com.infumia.cryptobot;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import tr.com.infumia.cryptobot.database.ConfigManager;
import tr.com.infumia.cryptobot.listeners.BuyCoin;
import tr.com.infumia.cryptobot.listeners.CoinPrices;
import tr.com.infumia.cryptobot.listeners.HelpCommand;
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
    Crypto.coinGeckoApiClient = new CoinGeckoApiClientImpl();
    Crypto.messages = new String[]{"Developed by Infumia", "www.infumia.com.tr", "Ethereum", "DogeCoin", "Bitcoin"};
    ConfigManager.prefix = configManager.getPrefix();
    ConfigManager.cooldown = configManager.getCooldown();
    ConfigManager.emotes = configManager.getEmotes();
    Crypto.log.info("§aDatabase connection successful.");
    final var token = configManager.getToken();
    if (token.isEmpty()) {
      throw new IllegalStateException("Bot token does not exist.");
    }
    if (Crypto.coinGeckoApiClient.ping().getGeckoSays().isEmpty()) {
      throw new IllegalAccessError("Bot couldn't connect Cryptocurrency Prices API");
    }
    Crypto.log.info("§aCryptocurrency Prices API status: " + Crypto.coinGeckoApiClient.ping().getGeckoSays());
    final var jda = JDABuilder.createDefault(token)
      .addEventListeners(new CoinPrices(), new BuyCoin(), new MineCoin(), new SellCoin(), new Wallet(),
        new InviteCommand(), new Transfer(), new Leaderboard(), new HelpCommand())
      .setActivity(Activity.watching("Crypto"))
      .setAutoReconnect(true)
      .build()
      .awaitReady();
    Crypto.log.info("§aJDA status: " + jda.getStatus());
    Executors.newScheduledThreadPool(2).scheduleAtFixedRate(() -> {
      PriceApi.refreshPrices();
      Crypto.log.info("§eCrypto Prices updated from API.");
    }, 0, 1, TimeUnit.MINUTES);
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      jda.getPresence().setActivity(Activity.playing(Crypto.messages[Crypto.currentIndex]));
      Crypto.currentIndex =(Crypto.currentIndex +1)% Crypto.messages.length;
    }, 0, 5, TimeUnit.SECONDS);
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      Crypto.coinGeckoApiClient = new CoinGeckoApiClientImpl();
      Crypto.log.info("§aCryptocurrency Prices API status: " + Crypto.coinGeckoApiClient.ping().getGeckoSays());
    }, 0, 10, TimeUnit.MINUTES);
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      try {
        Crypto.restartApplication();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }, 30, 30, TimeUnit.MINUTES);
  }

  private static void restartApplication() throws IOException {
    Runtime.
      getRuntime().
      exec("cmd /c start \"\" crypto.bat");
    System.exit(0);
  }

}
