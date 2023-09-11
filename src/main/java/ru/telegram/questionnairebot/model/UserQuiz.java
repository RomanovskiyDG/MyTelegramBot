package ru.telegram.questionnairebot.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "usersDataTable")
@Data
public class UserQuiz {

    @Id
    private Long chatId;

    private String userName;

    private Boolean isStartQuiz;

    private String idStartQuiz;

    private Integer numberQuestion;

}
