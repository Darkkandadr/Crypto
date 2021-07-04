package tr.com.infumia.cryptobot.util;

import com.litesoftwares.coingecko.constant.Currency;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Synchronized;
import org.jetbrains.annotations.NotNull;

public final class PriceApi {

  private static final Map<String, Double> PRICES = new ConcurrentHashMap<>();

  private PriceApi() {
  }

  @Synchronized("PRICES")
  @NotNull
  public static Map<String, Double> getPrices() {
    return Collections.unmodifiableMap(PriceApi.PRICES);
  }

  @Synchronized("PRICES")
  public static void refreshPrices() {
    final var client = new CoinGeckoApiClientImpl();
    client.getPrice("bitcoin", Currency.USD).values().stream().findFirst()
      .flatMap(map -> map.values().stream().findFirst())
      .ifPresent(currency ->
        PriceApi.PRICES.put("Bitcoin", currency));
    client.getPrice("ethereum", Currency.USD).values().stream()
      .findFirst()
      .flatMap(map -> map.values().stream().findFirst())
      .ifPresent(currency ->
        PriceApi.PRICES.put("Ethereum", currency));
    client.getPrice("binancecoin", Currency.USD).values().stream()
      .findFirst()
      .flatMap(map -> map.values().stream().findFirst())
      .ifPresent(currency ->
        PriceApi.PRICES.put("BinanceCoin", currency));
    client.getPrice("ripple", Currency.USD).values().stream()
      .findFirst()
      .flatMap(map -> map.values().stream().findFirst())
      .ifPresent(currency ->
        PriceApi.PRICES.put("Ripple", currency));
    client.getPrice("dogecoin", Currency.USD).values().stream()
      .findFirst()
      .flatMap(map -> map.values().stream().findFirst())
      .ifPresent(currency ->
        PriceApi.PRICES.put("DogeCoin", currency));
    client.getPrice("cardano", Currency.USD).values().stream()
      .findFirst()
      .flatMap(map -> map.values().stream().findFirst())
      .ifPresent(currency ->
        PriceApi.PRICES.put("Cardano", currency));
  }
}
