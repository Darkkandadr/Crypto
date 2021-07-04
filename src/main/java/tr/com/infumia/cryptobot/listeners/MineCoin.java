package tr.com.infumia.cryptobot.listeners;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.cryptobot.database.ConfigManager;
import tr.com.infumia.cryptobot.database.UserManager;

public class MineCoin extends ListenerAdapter {

  private final HashMap<String, LocalDateTime> cooldown = new HashMap<>();

  @Override
  public final void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event){
      final var guildId = event.getGuild().getId();
      final var userId = event.getAuthor().getId();
      final var args = event.getMessage().getContentRaw().split(" ");
      if (args[0].equalsIgnoreCase(ConfigManager.prefix + "mine")) {
        Duration duration = null;
        if(this.cooldown.containsKey(userId)){
          duration = Duration.between(this.cooldown.get(userId), LocalDateTime.now());
        }
        if ((duration != null ? duration.toSeconds() : 0) > ConfigManager.cooldown || this.cooldown.get(userId) == null){
          final var rangeMin = 0.0;
          final var rangeMax = 60.0;
          final var r = new Random();
          final var randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
          final var roundedValue = Math.round(randomValue * 100.0) / 100.0;
          final var usd = UserManager.getCoin(guildId, userId, "USD");
          UserManager.setCoin(guildId, userId, "USD", usd + roundedValue);
          this.cooldown.put(userId, LocalDateTime.now());
          event.getMessage().reply("Succesfully! You mined " + roundedValue + " USD!").queue();
        }
        else{
          assert duration != null;
          event.getMessage().reply("You must wait " + (ConfigManager.cooldown - duration.toSeconds()) + " seconds for mine again.").queue();
        }
      }
  }

}
