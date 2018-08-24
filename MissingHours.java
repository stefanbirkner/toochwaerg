import java.io.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;
import java.util.function.ToIntFunction;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MissingHours {

    public static void main(
        String... args
    ) throws Exception {
        try (
            var reader = new InputStreamReader(System.in, UTF_8);
        ) {
            var minutes = new BufferedReader(reader)
                .lines()
                .mapToInt(MissingHours::diffMinutes)
                .sum();
            System.out.println(Duration.ofMinutes(minutes));
        }
    }

    private static int diffMinutes(
        String line
    ) {
        var time = line.split(" ")[1];
        var parts = time.split(":");
        var hours = Integer.parseInt(parts[0]);
        var minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes - 7 * 60;
    }
}