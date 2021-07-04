package tr.com.infumia.cryptobot.util;

import org.jetbrains.annotations.NotNull;

public class Formatter {

  public static String formatUSD(@NotNull final Double value) {
    return String.format("%,.2f", value);
  }
  public static String formatCoins(@NotNull final Double value) {
    return String.format("%,.4f", value);
  }

}
