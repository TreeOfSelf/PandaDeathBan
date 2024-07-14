package me.sebastian420.PandaDeathBan;

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


        MutableText message = MutableText.of(Text.of("").getContent()).append(Text.of("You are Death Banned").copy().formatted(Formatting.RED))
                .append("\n")
                .append(Text.of("You will be unbanned in: ").copy().formatted(Formatting.WHITE));

        if (days > 0) message.append(Text.of(String.format("%d days ", days)).copy().formatted(Formatting.YELLOW));
        if (hours > 0) message.append(Text.of(String.format("%d hours ", hours)).copy().formatted(Formatting.YELLOW));
        message.append(Text.of(String.format("%d minutes", minutes)).copy().formatted(Formatting.YELLOW));

        return message;
    }
}