package tr.com.infumia.cryptobot.listeners;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.cryptobot.database.ConfigManager;
import tr.com.infumia.cryptobot.database.Emotes;
import tr.com.infumia.cryptobot.database.UserManager;
import tr.com.infumia.cryptobot.util.Formatter;
import tr.com.infumia.cryptobot.util.PriceApi;

public final class BuyCoin extends ListenerAdapter {

  private static void buyCommand(@NotNull final GuildMessageReceivedEvent event, @NotNull final String[] args) {
    final var message = event.getMessage();
    if (args.length != 3){
      message.replyEmbeds(BuyCoin.trueUsageMessage(message)).queue();
      return;
    }
    final double value;
    try {
      value = Double.parseDouble(args[2]);
    } catch (final NumberFormatException e) {
      message.replyEmbeds(BuyCoin.trueUsageMessage(message)).queue();
      return;
    }
    if (args[1].equalsIgnoreCase("ETH") || args[1].equalsIgnoreCase("Ethereum")) {
      if (BuyCoin.buyCoin(event, value, "Ethereum")){
        message.replyEmbeds(BuyCoin.boughtMessage(message, "Ethereum", value)).queue();
      }
      else {
        message.replyEmbeds(BuyCoin.needMoneyMessage(message, "Ethereum", value)).queue();
      }
    } else if (args[1].equalsIgnoreCase("BTC") || args[1].equalsIgnoreCase("Bitcoin")) {
      if (BuyCoin.buyCoin(event, value, "Bitcoin")){
        message.replyEmbeds(BuyCoin.boughtMessage(message, "Bitcoin", value)).queue();
      }
      else {
        message.replyEmbeds(BuyCoin.needMoneyMessage(message, "Bitcoin", value)).queue();
      }
    } else if (args[1].equalsIgnoreCase("XRP") || args[1].equalsIgnoreCase("Ripple")) {
      if (BuyCoin.buyCoin(event, value, "Ripple")){
        message.replyEmbeds(BuyCoin.boughtMessage(message, "Ripple", value)).queue();
      }
      else {
        message.replyEmbeds(BuyCoin.needMoneyMessage(message, "Ripple", value)).queue();
      }
    } else if (args[1].equalsIgnoreCase("BNB") || args[1].equalsIgnoreCase("BinanceCoin")) {
      if (BuyCoin.buyCoin(event, value, "BinanceCoin")){
        message.replyEmbeds(BuyCoin.boughtMessage(message, "BinanceCoin", value)).queue();
      }
      else {
        message.replyEmbeds(BuyCoin.needMoneyMessage(message, "BinanceCoin", value)).queue();
      }
    } else if (args[1].equalsIgnoreCase("DOGE") || args[1].equalsIgnoreCase("DogeCoin")) {
      if (BuyCoin.buyCoin(event, value, "DogeCoin")){
        message.replyEmbeds(BuyCoin.boughtMessage(message, "DogeCoin", value)).queue();
      }
      else {
        message.replyEmbeds(BuyCoin.needMoneyMessage(message, "DogeCoin", value)).queue();
      }
    } else if (args[1].equalsIgnoreCase("ADA") || args[1].equalsIgnoreCase("Cardano")) {
      if (BuyCoin.buyCoin(event, value, "Cardano")){
        message.replyEmbeds(BuyCoin.boughtMessage(message, "Cardano", value)).queue();
      }
      else {
        message.replyEmbeds(BuyCoin.needMoneyMessage(message, "Cardano", value)).queue();
      }
    } else {
      message.replyEmbeds(BuyCoin.wrongCoinMessage(message)).queue();
    }
  }

  @Override
  public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
    final var args = event.getMessage().getContentRaw().split(" ");
    if (args[0].equalsIgnoreCase(ConfigManager.prefix + "buy")) {
      BuyCoin.buyCommand(event, args);
    }
  }

  private static boolean buyCoin(@NotNull final GuildMessageReceivedEvent event, final double value, @NotNull final String coinName){
    final var guildId = event.getGuild().getId();
    final var userId = event.getAuthor().getId();
    final var hasMoney = UserManager.hasMoney(guildId, userId, coinName, value);
      if(hasMoney.getKey()){
        final var usdBalance = UserManager.getCoin(guildId, userId, "USD");
        final var coinBalance = UserManager.getCoin(guildId, userId, coinName);
        UserManager.setCoin(guildId, userId, "USD", usdBalance - hasMoney.getValue());
        UserManager.setCoin(guildId, userId, coinName, coinBalance + value);
        return true;
      }
      else{
        return false;
      }
  }

  private static MessageEmbed boughtMessage(@NotNull final Message message, @NotNull final String coinName,
                                            final double value) {
    return new EmbedBuilder()
      .setTitle("Transaction Successful!")
      .addField("Coin Type", coinName, false)
      .addField("Amount of Coin", String.valueOf(value), false)
      .addField("Current price for USD",
        Formatter.formatUSD(value * PriceApi.getPrices().get(coinName)), false)
      .setColor(Color.GREEN)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();

  }

  private static MessageEmbed needMoneyMessage(@NotNull final Message message, @NotNull final String coinName,
                                               final double value) {

    final var needMoney = Formatter.formatUSD(value * PriceApi.getPrices().get(coinName));
    final var usdBalance = UserManager.getCoin(message.getGuild().getId(), message.getAuthor().getId(), "USD");
    return new EmbedBuilder()
      .setTitle("Transaction Failed!")
      .addField("Coin Type", coinName, false)
      .addField("Amount of Coin", String.valueOf(value), false)
      .addField("Current price for USD", needMoney, false)
      .setDescription("You need **" + needMoney + " USD** to buy " + value + " " + coinName +
        ", but you have only **" + Formatter.formatUSD(usdBalance) + " USD**.")
      .setColor(Color.RED)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();

  }

  private static MessageEmbed wrongCoinMessage(@NotNull final Message message) {

    return new EmbedBuilder()
      .setTitle("Wrong Coin Name!")
      .setDescription("You can use only these coins: " +
        "\n " + Emotes.getBitcoin() + " **Bitcoin (BTC)** " +
        "\n " + Emotes.getEthereum() + " **Ethereum (ETH)**" +
        "\n " + Emotes.getRipple() + " **Ripple (XRP)** " +
        "\n " + Emotes.getBinanceCoin() + " **BinanceCoin (BNB)** " +
        "\n " + Emotes.getDogeCoin() + " **DogeCoin (DOGE)** " +
        "\n " + Emotes.getCardano() + " **Cardano (ADA)**")
      .setColor(Color.ORANGE)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();

  }

  private static MessageEmbed trueUsageMessage(@NotNull final Message message) {

    return new EmbedBuilder()
      .setTitle("Wrong Usage")
      .setDescription("True usage of command: " + ConfigManager.prefix + "buy **coinName** **amount**")
      .setColor(Color.ORANGE)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();

  }
}
