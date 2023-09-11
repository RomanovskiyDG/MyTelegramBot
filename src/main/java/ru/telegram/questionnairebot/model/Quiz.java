package ru.telegram.questionnairebot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Quiz {

    @Id
    private String quizId;

    private String name;

    private List<String> questions;
}
