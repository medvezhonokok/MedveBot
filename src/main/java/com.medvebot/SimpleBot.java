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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class SimpleBot extends TelegramLongPollingBot {
    private static int predCount = 0;

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

            if (toParse && predCount > 1) {
                doParse(message);
            }

            if (toWordStat && predCount > 1) {
                doCount(message);
            }

            if (toMd2Html && predCount > 1) {
                doMd2Html(message);
            }

            if (toMovies && predCount > 1) {
                doMovies(message);
            }

            if (toWordStat || toMd2Html || toParse || toMovies) {
                predCount++;
            }
        }
    }

    private void doMovies(final Message message) throws TelegramApiException {
        String type = message.getText().toLowerCase(Locale.ROOT);
        type = type.replace(" ", "");

        String ans = new Movies().suggestMovies(type);

        if (ans == null) {
            printMessage(message, "Вы ввели неправильно, попробуйте еще раз.");
            return;
        }

        printMessage(message, ans);
        clear();
    }

    private void doMd2Html(final Message message) throws IOException, TelegramApiException {
        BufferedWriter in = new BufferedWriter(new FileWriter("in.txt"));

        in.write(message.getText());
        in.close();

        final var fileReader = new FileReader("in.txt", StandardCharsets.UTF_8);
        final var bufferedReader = new BufferedReader(fileReader);

        String html = new MDParser().parse(new ParagraphSource(bufferedReader.lines().iterator()));

        printMessage(message, html);
        clear();
    }

    private void doCount(final Message message) throws IOException, TelegramApiException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("in.txt"));

        var sentence = message.getText();
        sentence = sentence.replace("!", "");
        sentence = sentence.replace("?", "");

        writer.write(sentence);
        writer.close();

        Scanner2021 in = new Scanner2021("in.txt", "utf8");

        ArrayList<String> words = new ArrayList<>();
        String ans = WordStatInput.solve(words, in, "");

        in.close();
        printMessage(message, ans);
        clear();
    }

    private void doParse(final Message message) throws TelegramApiException {
        Parser parser = new Parser(message.getText() + " ");
        parser.parse();

        StringBuilder ans = new StringBuilder();

        for (BigInteger integer : Parser.stack) {
            ans.append(integer).append(" ");
        }

        Parser.stack.clear();
        printMessage(message, String.valueOf(ans));
        clear();
    }

    private void checkOnCommands(final Message message) {
        checkMovies(message);
        checkMd2Html(message);
        checkInfo(message);
        checkEvaluate(message);
        checkWordStat(message);
    }

    @SneakyThrows
    private void checkWordStat(final Message message) {
        if (Objects.equals(message.getText(), "/count_words")) {
            toWordStat = true;
            predCount++;

            printMessage(message, "Введите сообщение, в котором хотите посчитать кол-во слов.");
        }
    }

    private void clear() {
        predCount = 0;
        toParse = toWordStat = toMovies = toMd2Html = false;
    }

    @SneakyThrows
    private void checkMovies(final Message message) {
        if (Objects.equals(message.getText(), "/movies")) {
            toMovies = true;
            predCount++;

            StringBuilder c = new StringBuilder("Введите одним сообщением жанр, который вы хотите посмотреть: ");
            for (String s : Arrays.asList("\n -Ужасы", "\n -Комедии", "\n -Детективы", "\n -Драмы")) {
                c.append(s);
            }

            printMessage(message, c.toString());
        }
    }

    private void printMessage(final Message message, final String c) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(c)
                        .build());
    }

    @SneakyThrows
    private void checkMd2Html(final Message message) {
        if (Objects.equals(message.getText(), "/md2html")) {
            toMd2Html = true;
            predCount++;
            printMessage(message, "Введите одним сообщением(MD) то, что хотите перевести в HTML");
        }
    }

    @SneakyThrows
    private void checkInfo(final Message message) {
        if (Objects.equals(message.getText(), "/info") || Objects.equals(message.getText(), "/start")) {
            clear();

            StringBuilder info = new StringBuilder("Was created by: \n   @medvezhonokok\n");
            for (String s : Arrays.asList("\n", "Данный бот умеет: \n", " Из развлекательного:\n", "\t-советовать фильмы по жанрам\n", "\t-советовать литературу на вечер\n")) {
                info.append(s);
            }
            for (String s : Arrays.asList("\n", " Из более интеллектуального:\n", "\t-считать выражения в ОПЗ\n", "\t-считать кол-во слов в сообщении\n", "\t-конвертировать файлы markdown to html\n")) {
                info.append(s);
            }

            printMessage(message, info.toString());
        }
    }

    @SneakyThrows
    private void checkEvaluate(final Message message) {
        if (Objects.equals(message.getText(), "/evaluate")) {
            toParse = true;
            predCount++;
            printMessage(message, "Введите выражение в ОПЗ");
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
        SimpleBot bot = new SimpleBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
