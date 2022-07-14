package com.medvebot;

import com.medvebot.features.Movies;
import com.medvebot.features.Scanner2021;
import com.medvebot.features.WordStatInput;
import com.medvebot.md2html.MDParser;
import com.medvebot.md2html.ParagraphSource;
import com.medvebot.parser.Parser;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MedveBot extends TelegramLongPollingBot {

    private static int messagesCounts = 0;
    private String ans = "";

    private static boolean toWordStat = false;
    private static boolean toMovies = false;
    private static boolean toParse = false;
    private static boolean toMd2Html = false;

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            checkOnCommands(message);
            doCommand(message);
            getUpdate();
        }
    }

    private void getUpdate() {
        if (toWordStat || toMd2Html || toParse || toMovies) {
            messagesCounts++;
        }
    }

    private void doCommand(Message message) {
        if (toParse && messagesCounts > 1) {
            doParse(message);
        }

        if (toWordStat && messagesCounts > 1) {
            doCount(message);
        }

        if (toMd2Html && messagesCounts > 1) {
            doMd2Html(message);
        }

        if (toMovies && messagesCounts > 1) {
            doMovies(message);
        }
    }

    private void checkOnCommands(final Message message) {
        checkMovies(message);
        checkMd2Html(message);
        checkInfo(message);
        checkEvaluate(message);
        checkWordStat(message);
    }

    @SneakyThrows
    private void doMovies(final Message message) {
        ans = new Movies().suggestMovies(message.getText());

        if (ans == null) {
            printMessage(message, "Вы ввели неправильно, попробуйте еще раз. \n\t Попробуйте: \n\t\t '-Ужасы' или 'Ужасы'");
            return;
        }

        finalFunc(message);
    }

    @SneakyThrows
    private void doMd2Html(final Message message) {
        final var in = new BufferedWriter(new FileWriter("in.txt"));

        in.write(message.getText());
        in.close();

        final var fileReader = new FileReader("in.txt", StandardCharsets.UTF_8);
        final var bufferedReader = new BufferedReader(fileReader);

        ans = new MDParser().parse(
                new ParagraphSource(
                        bufferedReader.lines().iterator()));

        finalFunc(message);
    }

    @SneakyThrows
    private void doCount(final Message message) {
        final var writer = new BufferedWriter(new FileWriter("in.txt"));

        var sentence = message.getText().
                replace("!", "").
                replace("?", "");

        writer.write(sentence);
        writer.close();

        final var in = new Scanner2021("in.txt", "utf8");

        ans = WordStatInput.solve(new ArrayList<>(), in, "");

        in.close();
        finalFunc(message);
    }

    @SneakyThrows
    private void doParse(final Message message) {
        final var parser = new Parser(message.getText() + " ");

        ans = parser.parse();
        finalFunc(message);
    }

    @SneakyThrows
    private void checkWordStat(final Message message) {
        if (Objects.equals(message.getText(), "/count_words")) {
            toWordStat = true;
            increase(message, "Введите сообщение, в котором хотите посчитать кол-во слов.");
        }
    }

    @SneakyThrows
    private void checkMovies(final Message message) {
        if (Objects.equals(message.getText(), "/movies")) {
            toMovies = true;
            messagesCounts++;

            StringBuilder c = new StringBuilder("Введите одним сообщением жанр, который вы хотите посмотреть: ");
            for (String s : Arrays.asList(
                    "\n -Ужасы",
                    "\n -Комедии",
                    "\n -Детективы",
                    "\n -Боевики",
                    "\n -Драмы",
                    "\n -Рандомный фильм")) {
                c.append(s);
            }

            printMessage(message, c.toString());
        }
    }

    @SneakyThrows
    private void checkMd2Html(final Message message) {
        if (Objects.equals(message.getText(), "/md2html")) {
            toMd2Html = true;
            increase(message, "Введите одним сообщением(MD) то, что хотите перевести в HTML");
        }
    }

    @SneakyThrows
    private void checkInfo(final Message message) {
        if (Objects.equals(message.getText(), "/info")
                || Objects.equals(message.getText(), "/start")) {
            clear();
            StringBuilder info = new StringBuilder();

            for (String s : Arrays.asList(
                    "Was created by: \n   @medvezhonokok\n",
                    "\n",
                    "Данный бот умеет: \n",
                    " Из развлекательного:\n",
                    "\t-советовать фильмы по жанрам\n",
                    "\n",
                    " Из более интеллектуального:\n",
                    "\t-считать выражения в ОПЗ\n",
                    "\t-считать кол-во слов в сообщении\n",
                    "\t-конвертировать файлы MarkDown to HTML\n")) {
                info.append(s);
            }

            printMessage(message, info.toString());
        }
    }

    @SneakyThrows
    private void printMessage(final Message message, final String c) {
        execute(SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(c)
                .build());
    }

    @SneakyThrows
    private void finalFunc(final Message message) {
        printMessage(message, ans);
        clear();
    }

    @SneakyThrows
    private void increase(Message message, String c) {
        messagesCounts++;
        printMessage(message, c);
    }

    private void clear() {
        messagesCounts = 0;
        toParse = toWordStat = toMovies = toMd2Html = false;
    }

    @SneakyThrows
    private void checkEvaluate(final Message message) {
        if (Objects.equals(message.getText(), "/evaluate")) {
            toParse = true;
            increase(message, "Введите выражение в Обратной Польской записи:" +
                    "\n\nСписок операций: " +
                    "\n\t\t\t'/' - Деление" +
                    "\n\t\t\t'*' - Умножение" +
                    "\n\t\t\t'+' - Сложение" +
                    "\n\t\t\t'~' - Корень" +
                    "\n\t\t\t'%' - Деление по модулю" +
                    "\n\t\t\t'-' - Вычитание" +
                    "\n\n\t " +
                    "\uD835\uDC0D\uD835\uDC0E\uD835\uDC13\uD835\uDC04: " +
                    "\n\t\tБот умеет работать с очень большими числами…");
        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @SneakyThrows
    public static void main(String[] args) {
        MedveBot bot = new MedveBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
