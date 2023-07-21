import java.io.*;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;

public class WorkingHours {
    public static void main(
        String... args
    ) throws Exception {
        readJournalByDate()
            .forEach(WorkingHours::printWorkingHours);
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
        var dailyAccounting = sumJournalEntries(date, journalEntries);
        printWorkingHours(dailyAccounting);
    }

    private static DailyAccounting sumJournalEntries(
        String date,
        List<JournalEntry> journalEntries
    ) {
        var dailyAccounting = new DailyAccounting(date);
        journalEntries.stream()
            .sorted(comparing(JournalEntry::time))
            .forEach(dailyAccounting::add);
        return dailyAccounting;
    }

    private static void printWorkingHours(
        DailyAccounting dailyAccounting
    ) {
        var duration = dailyAccounting.getWorkingTime();
        System.out.printf(
            "%s %02d:%02d %s%n",
            dailyAccounting.date,
            duration.toHours(),
            duration.toMinutesPart(),
            dailyAccounting.pauseRegistered ? "" : "no pause"
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

    static class DailyAccounting {
        final String date;
        boolean active;
        boolean endOfDayRegistered;
        long numMinutesWorked;
        boolean pauseRegistered;
        LocalTime startOfLastActivePhase;

        DailyAccounting(
            String date
        ) {
            this.date = requireNonNull(date, "The date is missing.");
        }

        void add(
            JournalEntry journalEntry
        ) {
            var activity = journalEntry.activity().trim().toLowerCase();
            switch (activity) {
                case "feierabend":
                    setInactive(journalEntry.time);
                    endOfDayRegistered = true;
                    break;
                case "lunch":
                case "pause":
                    setInactive(journalEntry.time);
                    pauseRegistered = true;
                    break;
                default:
                    setActive(journalEntry.time);
            }
        }

        Duration getWorkingTime() {
            if (active)
                return Duration.ofMinutes(
                    numMinutesWorked + startOfLastActivePhase.until(now(), MINUTES));
            else
                return Duration.ofMinutes(numMinutesWorked);
        }

        private void setActive(
            LocalTime time
        ) {
            if (!active)
                startOfLastActivePhase = time;
            active = true;
        }

        private void setInactive(
            LocalTime time
        ) {
            if (active)
                numMinutesWorked += startOfLastActivePhase.until(time, MINUTES);
            active = false;
        }
    }
}