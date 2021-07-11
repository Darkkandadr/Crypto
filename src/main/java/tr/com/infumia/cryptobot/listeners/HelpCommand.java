package tr.com.infumia.cryptobot.listeners;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.cryptobot.database.ConfigManager;

public class HelpCommand extends ListenerAdapter {

  @Override
  public final void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
    final var prefix = ConfigManager.prefix;
    final var message = event.getMessage();
    if (event.getMessage().getContentRaw().equalsIgnoreCase(prefix + "help")) {
      message.replyEmbeds(HelpCommand.getHelpMessage(message)).queue();
    }
  }

  private static MessageEmbed getHelpMessage(@NotNull final Message message) {
    final var prefix = ConfigManager.prefix;
    return new EmbedBuilder()
      .setTitle(":bookmark: Crypto Commands")
      .addField("Prices Command", prefix+ "prices", false)
      .addField("Wallet Command", prefix+ "wallet", false)
      .addField("Mine Command", prefix+ "mine", false)
      .addField("Buy Command", prefix+ "buy **coinName** **amount**", false)
      .addField("Sell Command", prefix+ "sell **coinName** **amount**", false)
      .addField("Transfer Command", prefix+ "transfer **@MentionUser amount coinName**", false)
      .addField("Leaderboard of Guild", prefix+ "leaderboard", false)
      .addField("Invite Bot to Your Server", prefix+ "invite", false)
      .setFooter("Crypto | Developed by Infumia", message.getJDA().getSelfUser().getAvatarUrl())
      .setColor(Color.ORANGE)
      .build();
  }
}
