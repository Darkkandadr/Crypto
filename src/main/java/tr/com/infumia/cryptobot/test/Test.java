package tr.com.infumia.cryptobot.test;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class Test extends ListenerAdapter {

  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    if(event.getAuthor().getId().equalsIgnoreCase("217236906189127680") && event.getMessage().getContentRaw().equalsIgnoreCase("test")) {
      event.getMessage().reply("Click to say hello")
        .setActionRow(
          Button.primary("hello", "test"),
          Button.success("emoji", Emoji.fromMarkdown("<:minn:245267426227388416>")))
        .queue();
    }
  }

  @Override
  public void onButtonClick(ButtonClickEvent event) {
    if (event.getComponentId().equals("hello")) {
      event.reply("Hello :)").queue(); // send a message in the channel
    } else if (event.getComponentId().equals("emoji")) {
      event.editMessage("That button didn't say click me").queue(); // update the message
    }
  }

}
