import java.io.*;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

public class WorkingHours {
    public static void main(
        String... args
    ) throws Exception {
        readJournalByDate()
            .entrySet()
            .stream()
            .forEach(entry -> printWorkingHours(entry.getKey(), entry.getValue()));
    }

    private static Map<String, List<JournalEntry>> readJournalByDate(
    ) throws IOException {
        try (
            var reader = new InputStreamReader(System.in, UTF_8);
        ) {
            return new BufferedReader(reader)
                .lines()
                .filter(WorkingHours::isTopicLine)
                .map(JournalEntry::parse)
                .collect(groupingBy(JournalEntry::date));
        }
    }

    private static boolean isTopicLine(
        String line
    ) {
        return Pattern.matches(
            "\\[(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2})\\] (.*)",
            line
        );
    }

    private static void printWorkingHours(
        String date,
        List<JournalEntry> journalEntries
    ) {
        journalEntries.sort(comparing(JournalEntry::time));
        var active = false;
        var numMinutes = 0L;
        LocalTime start = null;
        var withPause = false;
        for (var journalEntry: journalEntries) {
            var activity = journalEntry.activity().toLowerCase();
            if (active) {
                var pause = activity.startsWith("lunch") || activity.startsWith("pause");
                withPause |= pause;
                if (activity.startsWith("feierabend") || pause) {
                    active = false;
                    numMinutes += start.until(journalEntry.time(), MINUTES);
                }
            } else {
                active = true;
                start = journalEntry.time();
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

    static record JournalEntry (
        String date,
        LocalTime time,
        String activity
    ) {
        static JournalEntry parse(
            String line
        ) {
            var dateAndRest = splitOnFirstWhitespace(line);
            var date = dateAndRest[0].substring(1);
            var timeAndActivity = splitOnFirstWhitespace(dateAndRest[1]);
            var time = LocalTime.parse(
                removeLastChar(timeAndActivity[0]));
            var activity = timeAndActivity[1];
            return new JournalEntry(date, time, activity);
        }

        private static String[] splitOnFirstWhitespace(
            String text
        ) {
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
}