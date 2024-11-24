package me.TreeOfSelf.PandaDeathBan;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BanMessageUtil {
    public static Text createBanMessage(long unbanTime) {
        long currentTimeMillis = System.currentTimeMillis() / 1000L;
        long remainingTime = unbanTime - currentTimeMillis;

        long days = remainingTime / (24 * 60 * 60);
        remainingTime %= 24 * 60 * 60;

        long hours = remainingTime / (60 * 60);
        remainingTime %= 60 * 60;

        long minutes = remainingTime / 60;


        MutableText message = MutableText.of(Text.of("").getContent()).append(Text.of("☠ You are Dead ☠").copy().formatted(Formatting.RED))
                .append("\n")
                .append("\n")
                .append(Text.of("You can join in: ").copy().formatted(Formatting.WHITE));

        if (days > 0) {
            message.append(formatTimeUnit(days, "day")).formatted(Formatting.YELLOW);
        }
        if (hours > 0) {
            if (days > 0) message.append(Text.of(" "));
            message.append(formatTimeUnit(hours, "hour")).formatted(Formatting.YELLOW);
        }
        if (minutes > 0) {
            if (days > 0 || hours > 0) message.append(Text.of(" "));
            message.append(formatTimeUnit(minutes, "minute")).formatted(Formatting.YELLOW);
        }
        return message;
    }

    private static MutableText formatTimeUnit(long value, String unit) {
        MutableText text = MutableText.of(Text.of(String.format("%d %s", value, unit)).getContent());
        if (value != 1) {
            text.append("s");
        }
        return text;
    }
}