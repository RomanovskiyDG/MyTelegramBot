package ru.telegram.questionnairebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.telegram.questionnairebot.config.BotConfig;
import ru.telegram.questionnairebot.model.Quiz;
import ru.telegram.questionnairebot.model.UserQuiz;
import ru.telegram.questionnairebot.model.UserQuizAnswer;
import ru.telegram.questionnairebot.repository.QuizRepository;
import ru.telegram.questionnairebot.repository.UserQuizAnswerRepository;
import ru.telegram.questionnairebot.repository.UserQuizRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserQuizRepository userQuizRepository;

    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;

    public TelegramBot(BotConfig config, QuizRepository quizRepository, UserQuizRepository userQuizRepository, UserQuizAnswerRepository userQuizAnswerRepository) {
        this.config = config;
        this.quizRepository = quizRepository;
        this.userQuizRepository = userQuizRepository;
        this.userQuizAnswerRepository = userQuizAnswerRepository;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Старт приложения"));
        listOfCommands.add(new BotCommand("/quiz", "Выберите опрос"));
        listOfCommands.add(new BotCommand("/help", "Информация о приложении"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            var userName = update.getMessage().getFrom().getUserName();

            switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());

                case "/quiz" -> {
                    var listQuiz = getAllQuizId();
                    if (!listQuiz.isEmpty()) {
                        addButtonAndSendMessage(listQuiz, chatId);
                        UserQuiz userQuiz = new UserQuiz();
                        userQuiz.setChatId(chatId);
                        userQuiz.setUserName(userName);
                        userQuizRepository.save(userQuiz);
                    }
                }
                default -> {
                    var userQuiz = userQuizRepository.findById(chatId);
                    if (userQuiz.isEmpty() || !userQuiz.get().getIsStartQuiz()) {
                        sendMessage(chatId, "Я не знаю такую команду!");
                    } else {
                        var question = userQuiz.get().getNumberQuestion();
                        var quiz = quizRepository.findById(userQuiz.get().getIdStartQuiz());
                        if (question == quiz.get().getQuestions().size()) {
                            var answer = userQuizAnswerRepository.getByUserNameAndIsActiveQuiz(userQuiz.get().getUserName(), true);
                            setAnswer(answer, question, update.getMessage().getText());
                            answer.setIsActiveQuiz(false);
                            userQuizAnswerRepository.save(answer);
                            userQuiz.get().setIsStartQuiz(false);
                            userQuizRepository.save(userQuiz.get());
                            SendMessage message = new SendMessage();
                            message.setChatId(chatId);
                            message.setText("Спасибо за прохождение опроса!");
                            send(message);
                        } else {
                            var answer = userQuizAnswerRepository.getByUserNameAndIsActiveQuiz(userQuiz.get().getUserName(), true);
                            setAnswer(answer, question, update.getMessage().getText());
                            userQuiz.get().setNumberQuestion(question + 1);
                            userQuizAnswerRepository.save(answer);
                            userQuizRepository.save(userQuiz.get());
                            SendMessage message = new SendMessage();
                            message.setChatId(chatId);
                            message.setText(quiz.get().getQuestions().get(question));
                            send(message);
                        }
                    }
                }

            }
        } else if (update.hasCallbackQuery()) {

            String callbackData = update.getCallbackQuery().getData();
            var quizId = quizRepository.findById(callbackData).get().getQuizId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(quizId)) {
                var quiz = quizRepository.findById(callbackData);
                var userStartQuiz = userQuizRepository.findById(chatId);
                userStartQuiz.get().setIsStartQuiz(true);
                userStartQuiz.get().setIdStartQuiz(quizId);
                userStartQuiz.get().setNumberQuestion(1);
                userQuizRepository.save(userStartQuiz.get());
                UserQuizAnswer answer = new UserQuizAnswer();
                answer.setIdQuiz(quizId);
                answer.setUserName(userStartQuiz.get().getUserName());
                answer.setIsActiveQuiz(true);
                userQuizAnswerRepository.save(answer);
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(quiz.get().getQuestions().get(0));
                send(message);
            }
        }
    }

    private void setAnswer(UserQuizAnswer answer, Integer question,String text) {
        if (question == 1) {
            answer.setAnswer(Collections.singletonList(text));
        }
        else {
            answer.getAnswer().add(text);
        }
    }


    private void addButtonAndSendMessage(List<Quiz> listQuiz, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите опрос");
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        listQuiz.forEach(quiz -> {
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            var inlinekeyboardButton = new InlineKeyboardButton();
            inlinekeyboardButton.setCallbackData(quiz.getQuizId());
            inlinekeyboardButton.setText(quiz.getName());
            rowInline.add(inlinekeyboardButton);
            if (rowInline.size() > 3) {
                rowsInline.add(rowInline);
            }
            markupInline.setKeyboard(rowsInline);
            message.setReplyMarkup(markupInline);
        });
        send(message);
    }

    private List<Quiz> getAllQuizId() {
        return (List<Quiz>) quizRepository.findAll();
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Добро пожаловать!";
        log.info("User " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        send(message);
    }

    private void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
