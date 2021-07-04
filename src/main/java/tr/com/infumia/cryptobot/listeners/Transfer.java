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
import tr.com.infumia.cryptobot.database.UserManager;

public class Transfer extends ListenerAdapter {


  @Override
  public final void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
    final var message = event.getMessage();
    final var user = event.getAuthor();
    final var guildId = event.getGuild().getId();
    final var args = event.getMessage().getContentRaw().split(" ");

    if (args[0].equalsIgnoreCase(ConfigManager.prefix + "transfer")) {
      if (args.length != 4) {
        message.replyEmbeds(Transfer.trueUsageMessage(message)).queue();
        return;
      }
      if (message.getMentionedMembers().size() == 1) {
       final double value;
       try {
         value = Double.parseDouble(args[2]);
       } catch (final NumberFormatException e) {
         message.replyEmbeds(Transfer.trueUsageMessage(message)).queue();
         return;
       }
       final Double coin = UserManager.getCoin(guildId, user.getId(), args[3]);
       if (coin.equals(0.0)) {
         message.replyEmbeds(Transfer.dontHaveCoinMessage(message, args[3])).queue();
       }
       else {
         if (coin >= value) {
           final var target = message.getMentionedMembers().get(0);
           final var targetBalance = UserManager.getCoin(guildId, target.getId(), args[3]);
           if (!target.getUser().equals(user)) {
             UserManager.setCoin(guildId, target.getId(), args[3], targetBalance + value);
             UserManager.setCoin(guildId, user.getId(), args[3], coin - value);
             message.replyEmbeds(Transfer.transferedMessage(message, args[3], value, target.getAsMention())).queue();
             target.getUser().openPrivateChannel().queue(c -> c.sendMessageEmbeds(Transfer
               .privateMessage(event.getGuild().getName(), user.getName(), message, args[3], value)).queue());
           }
           else {
             message.replyEmbeds(Transfer.trueUsageMessage(message)).queue();
           }
         }
         else {
           message.replyEmbeds(Transfer.needCoinMessage(message, args[3])).queue();
         }
       }
     }
     else {
       message.replyEmbeds(Transfer.wrongMentionUseMessage(message)).queue();
     }
    }
  }

  private static MessageEmbed wrongMentionUseMessage(@NotNull final Message message) {
    return new EmbedBuilder()
      .setTitle("Transaction Failed!")
      .setColor(Color.RED)
      .setDescription("You can't mention two or more users. You must mention only one user.")
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();
  }

  private static MessageEmbed dontHaveCoinMessage(@NotNull final Message message, @NotNull final String coin) {
    return new EmbedBuilder()
      .setTitle("Transaction Failed!")
      .setColor(Color.RED)
      .setDescription("You don't have " + coin + " or that coin does not exist in Crypto.")
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();
  }

  private static MessageEmbed trueUsageMessage(@NotNull final Message message) {
    return new EmbedBuilder()
      .setTitle("Wrong Usage")
      .setDescription("True usage of command: " + ConfigManager.prefix +
        "transfer **@MentionUser** **amount** **coinName**")
      .setColor(Color.ORANGE)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();
  }

  private static MessageEmbed transferedMessage(@NotNull final Message message, @NotNull final String coinName,
                                          final double value, @NotNull final String targetName) {
    return new EmbedBuilder()
      .setTitle("Transaction Successful!")
      .addField("Coin Type", coinName, false)
      .addField("Amount of Coin", String.valueOf(value), false)
      .addField("Transferer", message.getAuthor().getAsMention(), false)
      .addField("Recipient", targetName, false)
      .setColor(Color.GREEN)
      .setFooter(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")),
        message.getJDA().getSelfUser().getAvatarUrl())
      .build();
  }

  private static MessageEmbed privateMessage(@NotNull final String guildName, @NotNull final String userName,
                                             @NotNull final Message message, @NotNull final String coinType,
                                             @NotNull final Double amount) {
    return new EmbedBuilder()
      .setTitle("You received " + amount + " of " + coinType + " from " + userName)
      .setDescription("You received **" + amount + "** of " + coinType + " from **" + userName + "** in **" +
        guildName + "** guild. \n Have a nice day!")
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
}
