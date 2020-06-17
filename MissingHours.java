import java.io.*;
import java.time.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.DayOfWeek.*;

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
        var words = line.split(" ");
        var date = words[0];
        var time = words[1];
        var parts = time.split(":");
        var hours = Integer.parseInt(parts[0]);
        var minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes - requiredMinutes(date);
    }

    private static int requiredMinutes(
        String date
    ) {
        return isWeekend(date) ? 0 : 7 * 60;
    }

    private static boolean isWeekend(
        String dateAsString
    ) {
        var date = LocalDate.parse(dateAsString);
        var dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == SATURDAY || dayOfWeek == SUNDAY;
    }
}
