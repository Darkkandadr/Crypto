package tr.com.infumia.cryptobot.listeners;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.cryptobot.Crypto;
import tr.com.infumia.cryptobot.database.ConfigManager;
import tr.com.infumia.cryptobot.database.Emotes;
import tr.com.infumia.cryptobot.util.Formatter;
import tr.com.infumia.cryptobot.util.PriceApi;
import tr.com.infumia.cryptobot.util.Sorter;

public class Leaderboard extends ListenerAdapter {


  @Override
  public final void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
    final var message = event.getMessage();
    final var guildId = event.getGuild().getId();
    if (message.getContentRaw().equalsIgnoreCase(ConfigManager.prefix + "leaderboard")) {
     final var embedMessage = new EmbedBuilder()
       .setTitle("Leaderboard (Top 10) \n Guild: " + event.getGuild().getName())
       .setColor(Color.CYAN)
       .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
         message.getJDA().getSelfUser().getAvatarUrl());
      final AtomicInteger size = new AtomicInteger(1);
      Leaderboard.getLeaderboard(guildId).forEach((s, aDouble) -> {
        if (size.get() <= 10) {
          final var user = event.getJDA().retrieveUserById(s).complete();
          embedMessage.addField("**" + size + ". **" + user.getName(),
            Formatter.formatUSD(aDouble) + " " + Emotes.getUSD(), false);
          size.getAndIncrement();
        }
      });
      message.replyEmbeds(embedMessage.build()).queue();
    }
  }

  private static HashMap<String, Double> getLeaderboard(@NotNull final String guildId) {
    final var userList = new HashMap<String, Double>();
    final var collection = Crypto.client.getDatabase("Guilds").getCollection(guildId);
    collection.find().forEach(user -> {
      final String[] userId = {"INFUMIA was here :)"};
      final double[] userBalance = {0.0};
      user.forEach((s, o) -> {
        if (s.equalsIgnoreCase("_id")) {
          userId[0] = String.valueOf(o);
        }
        else if (s.equalsIgnoreCase("USD")) {
          userBalance[0] = userBalance[0] + (Double) o;
        }
        else {
          userBalance[0] = userBalance[0] + (Double) o * PriceApi.getPrices().get(s);
        }
        userList.put(userId[0], userBalance[0]);
      });
    });
    return Sorter.sortByValueDouble(userList);
  }
}
