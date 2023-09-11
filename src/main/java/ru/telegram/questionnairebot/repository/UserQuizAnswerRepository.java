package ru.telegram.questionnairebot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.telegram.questionnairebot.model.UserQuizAnswer;

public interface UserQuizAnswerRepository extends CrudRepository<UserQuizAnswer, Long> {
    UserQuizAnswer getByUserNameAndIsActiveQuiz (String userName, Boolean isActiveQuiz);
}
