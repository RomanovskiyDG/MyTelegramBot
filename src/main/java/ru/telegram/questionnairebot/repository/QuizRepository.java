package ru.telegram.questionnairebot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.telegram.questionnairebot.model.Quiz;

public interface QuizRepository extends CrudRepository<Quiz, String> {
}
