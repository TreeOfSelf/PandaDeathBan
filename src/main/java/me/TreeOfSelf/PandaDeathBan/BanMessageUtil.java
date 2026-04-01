package me.TreeOfSelf.PandaDeathBan;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;

public class BanMessageUtil {
    public static Component createBanMessage(MinecraftServer server, long unbanTime) {
        ConfigManager.Config config = ConfigManager.getConfig();

        long currentTimeMillis = System.currentTimeMillis() / 1000L;
        long remainingTime = unbanTime - currentTimeMillis;

        String formattedTime = formatTimeRemaining(remainingTime);
        return parseMessageLines(server, config.banMessage, formattedTime);
    }

    private static Component parseMessageLines(MinecraftServer server, java.util.List<String> lines, String timeRemaining) {
        MutableComponent message = Component.empty();
        ParserContext ctx = ServerPlaceholderContext.of(server).asParserContext();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replace("%death_time_remaining%", timeRemaining);
            message.append(Placeholders.SERVER_PLACEHOLDER_PARSER.parseComponent(line, ctx));

            if (i < lines.size() - 1) {
                message.append("\n");
            }
        }

        return message;
    }

    private static String formatTimeRemaining(long remainingTime) {
        long days = remainingTime / (24 * 60 * 60);
        remainingTime %= 24 * 60 * 60;

        long hours = remainingTime / (60 * 60);
        remainingTime %= 60 * 60;

        long minutes = remainingTime / 60;
        long seconds = remainingTime % 60;

        StringBuilder timeString = new StringBuilder();
        boolean hasTimeShown = false;

        if (days > 0) {
            timeString.append(days).append(" day");
            if (days != 1) timeString.append("s");
            hasTimeShown = true;
        }
        if (hours > 0) {
            if (hasTimeShown) timeString.append(" ");
            timeString.append(hours).append(" hour");
            if (hours != 1) timeString.append("s");
            hasTimeShown = true;
        }
        if (minutes > 0) {
            if (hasTimeShown) timeString.append(" ");
            timeString.append(minutes).append(" minute");
            if (minutes != 1) timeString.append("s");
            hasTimeShown = true;
        }
        if (seconds > 0 || !hasTimeShown) {
            if (hasTimeShown) timeString.append(" ");
            timeString.append(seconds).append(" second");
            if (seconds != 1) timeString.append("s");
        }

        return timeString.toString();
    }
}
