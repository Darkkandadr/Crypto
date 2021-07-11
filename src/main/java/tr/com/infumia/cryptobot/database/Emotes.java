package tr.com.infumia.cryptobot.database;

public class Emotes {

  /**
   * Returns Bitcoin emote.
   * @return the emote of bitcoin
   */
  public static String getBitcoin() {
    return ConfigManager.emotes.get("Bitcoin").toString();
  }
  /**
   * Returns Ethereum emote.
   * @return the emote of ethereum
   */
  public static String getEthereum() {
    return ConfigManager.emotes.get("Ethereum").toString();
  }
  /**
   * Returns Ripple emote.
   * @return the emote of ripple
   */
  public static String getRipple() {
    return ConfigManager.emotes.get("Ripple").toString();
  }
  /**
   * Returns BinanceCoin emote.
   * @return the emote of binancecoin
   */
  public static String getBinanceCoin() {
    return ConfigManager.emotes.get("BinanceCoin").toString();
  }
  /**
   * Returns DogeCoin emote.
   * @return the emote of dogecoin
   */
  public static String getDogeCoin() {
    return ConfigManager.emotes.get("DogeCoin").toString();
  }
  /**
   * Returns Cardano emote.
   * @return the emote of cardano
   */
  public static String getCardano() {
    return ConfigManager.emotes.get("Cardano").toString();
  }
  /**
   * Returns USD emote.
   * @return the emote of usd
   */
  public static String getUSD() {
    return ConfigManager.emotes.get("USD").toString();
  }
}
