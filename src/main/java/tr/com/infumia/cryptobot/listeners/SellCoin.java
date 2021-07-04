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

public class SellCoin extends ListenerAdapter {

  private static void sellCommand(@NotNull final GuildMessageReceivedEvent event, @NotNull final String[] args) {
    final var message = event.getMessage();
    if (args.length != 3){
      message.replyEmbeds(SellCoin.trueUsageMessage(message)).queue();
      return;
    }
    final double value;
    try {
      value = Double.parseDouble(args[2]);
    } catch (final NumberFormatException e) {
      message.replyEmbeds(SellCoin.trueUsageMessage(message)).queue();
      return;
    }
    if (args[1].equalsIgnoreCase("ETH") || args[1].equalsIgnoreCase("Ethereum")) {
      if (SellCoin.sellCoin(event, value, "Ethereum")){
        message.replyEmbeds(SellCoin.soldMessage(message, "Ethereum", value)).queue();
      }
      else {
        message.replyEmbeds(SellCoin.needCoinMessage(message, "Ethereum")).queue();
      }
    } else if (args[1].equalsIgnoreCase("BTC") || args[1].equalsIgnoreCase("Bitcoin")) {
      if (SellCoin.sellCoin(event, value, "Bitcoin")){
        message.replyEmbeds(SellCoin.soldMessage(message, "Bitcoin", value)).queue();
      }
      else {
        message.replyEmbeds(SellCoin.needCoinMessage(message, "Bitcoin")).queue();
      }
    } else if (args[1].equalsIgnoreCase("XRP") || args[1].equalsIgnoreCase("Ripple")) {
      if (SellCoin.sellCoin(event, value, "Ripple")){
        message.replyEmbeds(SellCoin.soldMessage(message, "Ripple", value)).queue();
      }
      else {
        message.replyEmbeds(SellCoin.needCoinMessage(message, "Ripple")).queue();
      }
    } else if (args[1].equalsIgnoreCase("BNB") || args[1].equalsIgnoreCase("BinanceCoin")) {
      if (SellCoin.sellCoin(event, value, "BinanceCoin")){
        message.replyEmbeds(SellCoin.soldMessage(message, "BinanceCoin", value)).queue();
      }
      else {
        message.replyEmbeds(SellCoin.needCoinMessage(message, "BinanceCoin")).queue();
      }
    } else if (args[1].equalsIgnoreCase("DOGE") || args[1].equalsIgnoreCase("DogeCoin")) {
      if (SellCoin.sellCoin(event, value, "DogeCoin")){
        message.replyEmbeds(SellCoin.soldMessage(message, "DogeCoin", value)).queue();
      }
      else {
        message.replyEmbeds(SellCoin.needCoinMessage(message, "DogeCoin")).queue();
      }
    } else if (args[1].equalsIgnoreCase("ADA") || args[1].equalsIgnoreCase("Cardano")) {
      if (SellCoin.sellCoin(event, value, "Cardano")){
        message.replyEmbeds(SellCoin.soldMessage(message, "Cardano", value)).queue();
      }
      else {
        message.replyEmbeds(SellCoin.needCoinMessage(message, "Cardano")).queue();
      }
    } else {
      message.replyEmbeds(SellCoin.wrongCoinMessage(message)).queue();
    }
  }

  @Override
  public final void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
    final var args = event.getMessage().getContentRaw().split(" ");
    if (args[0].equalsIgnoreCase(ConfigManager.prefix + "sell")) {
      SellCoin.sellCommand(event, args);
    }
  }

  private static boolean sellCoin(@NotNull final GuildMessageReceivedEvent event, final double value, @NotNull final String coinName){
    final var guildId = event.getGuild().getId();
    final var userId = event.getAuthor().getId();
    final var coinAmount = UserManager.getCoin(guildId, userId, coinName);
    final var usdBalance = UserManager.getCoin(guildId, userId, "USD");
    final var usdAmount = PriceApi.getPrices().get(coinName) * value;
    if (coinAmount >= value) {
      UserManager.setCoin(guildId, userId, coinName, coinAmount - value);
      UserManager.setCoin(guildId, userId, "USD", usdBalance + usdAmount);
      return true;
    }
    else {
      return false;
    }
  }

  private static MessageEmbed soldMessage(@NotNull final Message message, @NotNull final String coinName,
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

  private static MessageEmbed needCoinMessage(@NotNull final Message message, @NotNull final String coinName) {

    final var coinBalance = UserManager.getCoin(message.getGuild().getId(),
      message.getAuthor().getId(), coinName);
    return new EmbedBuilder()
      .setTitle("Transaction Failed!")
      .setDescription("You don't have enought " + coinName + ". You have only " + coinBalance + " " + coinName + ".")
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
      .setDescription("True usage of command: " + ConfigManager.prefix + "sell **coinName** **amount**")
      .setColor(Color.ORANGE)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();

  }

}
