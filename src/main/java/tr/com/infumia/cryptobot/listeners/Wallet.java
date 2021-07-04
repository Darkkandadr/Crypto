package tr.com.infumia.cryptobot.listeners;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.cryptobot.database.ConfigManager;
import tr.com.infumia.cryptobot.database.UserManager;
import tr.com.infumia.cryptobot.util.Formatter;
import tr.com.infumia.cryptobot.util.Sorter;

public class Wallet extends ListenerAdapter {

  @Override
  public final void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
    final var args = event.getMessage().getContentRaw().split(" ");
    if (args[0].equalsIgnoreCase(ConfigManager.prefix + "wallet")) {
      Wallet.getWallet(event.getGuild().getId(), event.getAuthor().getId());
      final var wallet = Wallet.getWallet(event.getGuild().getId(), event.getAuthor().getId());
      final var sortedWallet = Sorter.sortByValue(wallet);
      final var embedMessage = new EmbedBuilder()
        .setTitle(":briefcase: Your Wallet")
        .setColor(Color.decode("#EC780B"))
        .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
          event.getJDA().getSelfUser().getAvatarUrl());
      sortedWallet.forEach((s, o) -> embedMessage.addField(ConfigManager.emotes.getOrDefault(s, "").toString() + " " + s, Formatter.formatCoins((Double) o), true));
      event.getMessage().replyEmbeds(embedMessage.build()).queue();
    }
  }

  private static HashMap<String, Object> getWallet(@NotNull final String guildId, @NotNull final String userId) {
    final var walletMap = new HashMap<String, Object>();
    walletMap.put("Ethereum", 0.0);
    walletMap.put("Bitcoin", 0.0);
    walletMap.put("Ripple", 0.0);
    walletMap.put("BinanceCoin", 0.0);
    walletMap.put("DogeCoin", 0.0);
    walletMap.put("Cardano", 0.0);
    walletMap.put("USD", 0.0);
    final Map<String, Object> allData = UserManager.getAllData(guildId, userId);
    if (allData != null) {
      allData.forEach(walletMap::replace);
    }
    walletMap.remove("_id");
    return walletMap;
  }
}
