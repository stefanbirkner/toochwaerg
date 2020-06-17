import java.io.*;
import java.nio.charset.*;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

public class WorkingHours {
    public static void main(
        String... args
    ) throws Exception {
        var byDate = new TreeMap<String, List<String>>();
        try (
            var reader = new InputStreamReader(System.in, UTF_8);
        ) {
            new BufferedReader(reader)
                .lines()
                .filter(WorkingHours::isTopicLine)
                .forEach(line -> collect(line, byDate));
        }
        byDate.entrySet()
            .stream()
            .forEach(entry -> printWorkingHours(entry.getKey(), entry.getValue()));
    }

    private static boolean isTopicLine(
        String line
    ) {
        return Pattern.matches(
            "\\[(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})\\] (.*)",
            line
        );
    }

    private static void collect(
        String line,
        Map<String, List<String>> map
    ) {
        var parts = splitOnFirstWhitespace(line);
        var date = parts[0].substring(1);
        var topics = map.computeIfAbsent(
            date,
            (key) -> new ArrayList<String>()
        );
        topics.add(parts[1]);
    }

    private static void printWorkingHours(
        String date,
        List<String> topics
    ) {
        Collections.sort(topics);
        var active = false;
        var numMinutes = 0L;
        LocalTime start = null;
        var withPause = false;
        for (var topic: topics) {
            var parts = splitOnFirstWhitespace(topic);
            var action = parts[1].toLowerCase();
            var time = LocalTime.parse(
                removeLastChar(parts[0]));
            if (active) {
                var pause = action.startsWith("lunch") || action.startsWith("pause");
                withPause |= pause;
                if (action.startsWith("feierabend") || pause) {
                    active = false;
                    numMinutes += start.until(time, MINUTES);
                }
            } else {
                active = true;
                start = time;
            }
        }
        if (active) {
            numMinutes += start.until(now(), MINUTES);
        }
        var duration = Duration.ofMinutes(numMinutes);
        System.out.printf(
            "%s %02d:%02d %s%n",
            date,
            duration.toHours(),
            duration.toMinutesPart(),
            withPause ? "" : "no pause"
        );
    }

    private static String[] splitOnFirstWhitespace(String text) {
        var indexWhitespace = text.indexOf(' ');
        return new String[] {
            text.substring(0, indexWhitespace),
            text.substring(indexWhitespace + 1)
        };
    }

    private static String removeLastChar(String text) {
        return text.substring(
            0,
            text.length() - 1);
    }
}