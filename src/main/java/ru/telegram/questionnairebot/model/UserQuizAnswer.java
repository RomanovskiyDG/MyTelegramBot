package ru.telegram.questionnairebot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Entity(name = "userQuizAnswer")
@Data
public class UserQuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String userName;

    private String idQuiz;

    private Boolean isActiveQuiz;

    private List<String> answer;
}
