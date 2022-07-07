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
import java.util.Objects;

import static java.util.Locale.*;

public class SimpleBot extends TelegramLongPollingBot {
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

            if (toWordStat || toMd2Html || toParse || toMovies) {
                messagesCounts++;
            }
        }
    }

    private void checkOnCommands(final Message message) {
        checkMovies(message);
        checkMd2Html(message);
        checkInfo(message);
        checkEvaluate(message);
        checkWordStat(message);
    }

    private void doMovies(final Message message) throws TelegramApiException {
        ans = new Movies().suggestMovies(message.getText().
                toLowerCase(ROOT).
                replace(" ", ""));

        if (ans == null) {
            printMessage(message, "Вы ввели неправильно, попробуйте еще раз.");
            return;
        }

        func(message);
    }

    private void doMd2Html(final Message message) throws IOException, TelegramApiException {
        final var in = new BufferedWriter(new FileWriter("in.txt"));

        in.write(message.getText());
        in.close();

        final var fileReader = new FileReader("in.txt", StandardCharsets.UTF_8);
        final var bufferedReader = new BufferedReader(fileReader);

        ans = new MDParser().parse(
                new ParagraphSource(
                        bufferedReader.lines().iterator()));

        func(message);
    }

    private void doCount(final Message message) throws IOException, TelegramApiException {
        final var writer = new BufferedWriter(new FileWriter("in.txt"));

        var sentence = message.getText().
                replace("!", "").
                replace("?", "");

        writer.write(sentence);
        writer.close();

        final var in = new Scanner2021("in.txt", "utf8");

        ans = WordStatInput.solve(new ArrayList<>(), in, "");

        in.close();
        func(message);
    }

    private void doParse(final Message message) throws TelegramApiException {
        Parser parser = new Parser(message.getText() + " ");

        for (BigInteger integer : parser.stack) {
            ans += integer + " ";
        }

        parser.stack.clear();
        func(message);
    }

    @SneakyThrows
    private void checkWordStat(final Message message) {
        if (Objects.equals(message.getText(), "/count_words")) {
            toWordStat = true;
            solve(message, "Введите сообщение, в котором хотите посчитать кол-во слов.");
        }
    }

    @SneakyThrows
    private void checkMovies(final Message message) {
        if (Objects.equals(message.getText(), "/movies")) {
            toMovies = true;
            messagesCounts++;

            StringBuilder c = new StringBuilder("Введите одним сообщением жанр, который вы хотите посмотреть: ");
            for (String s : Arrays.asList("\n -Ужасы",
                    "\n -Комедии",
                    "\n -Детективы",
                    "\n -Драмы")) {
                c.append(s);
            }

            printMessage(message, c.toString());
        }
    }

    @SneakyThrows
    private void checkMd2Html(final Message message) {
        if (Objects.equals(message.getText(), "/md2html")) {
            toMd2Html = true;
            solve(message, "Введите одним сообщением(MD) то, что хотите перевести в HTML");
        }
    }

    @SneakyThrows
    private void checkInfo(final Message message) {
        if (Objects.equals(message.getText(), "/info") || Objects.equals(message.getText(), "/start")) {
            clear();

            StringBuilder info = new StringBuilder("Was created by: \n   @medvezhonokok\n");
            for (String s : Arrays.asList("\n",
                    "Данный бот умеет: \n",
                    " Из развлекательного:\n",
                    "\t-советовать фильмы по жанрам\n",
                    "\t-советовать литературу на вечер\n")) {
                info.append(s);
            }
            for (String s : Arrays.asList("\n",
                    " Из более интеллектуального:\n",
                    "\t-считать выражения в ОПЗ\n",
                    "\t-считать кол-во слов в сообщении\n",
                    "\t-конвертировать файлы markdown to html\n")) {
                info.append(s);
            }

            printMessage(message, info.toString());
        }
    }

    private void printMessage(final Message message, final String c) throws TelegramApiException {
        execute(
                SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(c)
                        .build());
    }

    private void func(final Message message) throws TelegramApiException {
        printMessage(message, ans);
        clear();
    }

    private void solve(Message message, String c) throws TelegramApiException {
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
            solve(message, "Введите выражение в ОПЗ");
        }
    }

    @Override
    public String getBotUsername() {
        return "@mdvzhnk_bot";
    }

    @Override
    public String getBotToken() {
        return "5328607653:AAGuiGg0ivMrdzVrpk9AMM7LpTaHRxkX_70";
    }

    @SneakyThrows
    public static void main(String[] args) {
        SimpleBot bot = new SimpleBot();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
