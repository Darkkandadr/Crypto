package tr.com.infumia.cryptobot.listeners;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.cryptobot.database.ConfigManager;
import tr.com.infumia.cryptobot.database.Emotes;
import tr.com.infumia.cryptobot.util.PriceApi;

public final class CoinPrices extends ListenerAdapter {

  private static void pricesCommand(@NotNull final GuildMessageReceivedEvent event) {
    final var prices = PriceApi.getPrices();
    final var embedBuilder = new EmbedBuilder()
      .setTitle(":money_with_wings: Cryptocurrency Prices")
      .addField(Emotes.getBitcoin() + " Bitcoin", "**" + prices.get("Bitcoin").toString() + "**", true)
      .addField(Emotes.getEthereum() + " Ethereum", "**" + prices.get("Ethereum").toString() + "**", true)
      .addField(Emotes.getBinanceCoin() + " BinanceCoin", "**" + prices.get("BinanceCoin").toString() + "**", true)
      .addField(Emotes.getCardano() + " ADA (Cardano)", "**" + prices.get("Cardano").toString() + "**", true)
      .addField(Emotes.getRipple() + " Ripple", "**" + prices.get("Ripple").toString() + "**", true)
      .addField(Emotes.getDogeCoin() + " DOGE", "**" + prices.get("DogeCoin").toString() + "**", true)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        event.getJDA().getSelfUser().getAvatarUrl())
      .build();
    event.getMessage().replyEmbeds(embedBuilder).queue();
  }

  @Override
  public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
    final var args = event.getMessage().getContentRaw().split(" ");
    if (args[0].equalsIgnoreCase(ConfigManager.prefix + "prices")) {
      CoinPrices.pricesCommand(event);
    }
  }
}
