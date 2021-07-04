package tr.com.infumia.cryptobot.database;

public class Emotes {

  public static String getBitcoin() {
    return ConfigManager.emotes.get("Bitcoin").toString();
  }
  public static String getEthereum() {
    return ConfigManager.emotes.get("Ethereum").toString();
  }
  public static String getRipple() {
    return ConfigManager.emotes.get("Ripple").toString();
  }
  public static String getBinanceCoin() {
    return ConfigManager.emotes.get("BinanceCoin").toString();
  }
  public static String getDogeCoin() {
    return ConfigManager.emotes.get("DogeCoin").toString();
  }
  public static String getCardano() {
    return ConfigManager.emotes.get("Cardano").toString();
  }
  public static String getUSD() {
    return ConfigManager.emotes.get("USD").toString();
  }
}
