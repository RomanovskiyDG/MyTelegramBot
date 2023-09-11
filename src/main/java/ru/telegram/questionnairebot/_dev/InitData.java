package ru.telegram.questionnairebot._dev;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.telegram.questionnairebot.model.Quiz;
import ru.telegram.questionnairebot.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@AllArgsConstructor
public class InitData {
    private QuizRepository quizRepository;

    @Autowired
    public void initData() {
        Quiz quiz1 = new Quiz();
        quiz1.setQuizId("quiz1");
        quiz1.setName("Кухня");
        List<String> questions1 = new ArrayList<>();
        questions1.add("У вас есть индивидульный проект?");
        questions1.add("Нужен замер?");
        questions1.add("Ваше ФИО?");
        questions1.add("Ваше номер телефона?");
        quiz1.setQuestions(questions1);

        Quiz quiz2 = new Quiz();
        quiz2.setQuizId("quiz2");
        quiz2.setName("Шкаф");
        List<String> questions2 = new ArrayList<>();
        questions2.add("У вас есть индивидульный проект?");
        questions2.add("Нужен замер?");
        questions2.add("Ваше ФИО?");
        questions2.add("Ваше номер телефона?");
        quiz2.setQuestions(questions2);

        Quiz quiz3 = new Quiz();
        quiz3.setQuizId("quiz3");
        quiz3.setName("Мебель для ванны");
        List<String> questions3 = new ArrayList<>();
        questions3.add("У вас есть индивидульный проект?");
        questions3.add("Нужен замер?");
        questions3.add("Ваше ФИО?");
        questions3.add("Ваше номер телефона?");
        quiz3.setQuestions(questions3);

        Quiz quiz4 = new Quiz();
        quiz4.setQuizId("quiz4");
        quiz4.setName("Гардеробная");
        List<String> questions4 = new ArrayList<>();
        questions4.add("У вас есть индивидульный проект?");
        questions4.add("Нужен замер?");
        questions4.add("Ваше ФИО?");
        questions4.add("Ваше номер телефона?");
        quiz4.setQuestions(questions4);
        quizRepository.save(quiz1);
        quizRepository.save(quiz2);
        quizRepository.save(quiz3);
        quizRepository.save(quiz4);
    }
}
