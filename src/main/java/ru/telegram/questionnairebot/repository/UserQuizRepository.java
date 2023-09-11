package ru.telegram.questionnairebot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.telegram.questionnairebot.model.UserQuiz;

public interface UserQuizRepository extends CrudRepository<UserQuiz, Long> {

}
