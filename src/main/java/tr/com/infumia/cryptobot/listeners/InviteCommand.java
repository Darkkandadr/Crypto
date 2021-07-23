package tr.com.infumia.cryptobot.listeners;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.cryptobot.database.ConfigManager;

public class InviteCommand extends ListenerAdapter {

  @Override
  public final void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
    final var message = event.getMessage();
    final var jda = event.getJDA();
    if (message.getContentRaw().equalsIgnoreCase(ConfigManager.prefix + "invite")) {
      final var inviteLink = event.getJDA().getInviteUrl(Permission.ADMINISTRATOR);
      final var embedMessage = new EmbedBuilder()
        .setTitle("Invite Me!")
        .setDescription("[Click Here to Invite]("+ inviteLink +")" + "\n [Vote the Bot](https://top.gg/bot/847455891485360179)")
        .setColor(Color.GREEN)
        .setFooter("Crypto | Developed by Infumia", jda.getSelfUser().getAvatarUrl())
        .build();
      message.replyEmbeds(embedMessage).queue();
    }
    if (message.getContentRaw().equalsIgnoreCase(ConfigManager.prefix + "amountofguild")) {
      final var size = jda.getGuilds().size();
      message.reply(String.valueOf(size)).queue();
    }
  }


}
