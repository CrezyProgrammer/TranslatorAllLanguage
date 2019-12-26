package com.quiz.translatoraalllanguage;

import java.util.Locale;

/**
 * Created by almaz on 12.04.17.
 */

public class Word {
    private String word;
    private String sourcePosition = "en";
    private String targetPosition = "bn";
    private String sourceLanguage;
    private String targetLanguage;
    private String translation;

    Word(String word, String translation, String sourcePosition, String targetPosition) {
        this.word = word;
        this.translation = translation;
        this.sourcePosition = sourcePosition;
        this.targetPosition = targetPosition;
        if(Locale.getDefault().getLanguage().equals("en"))
            sourceLanguage = sourcePosition;
            targetLanguage = targetPosition;

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }

    public String getSourcePosition() {
        return sourcePosition;
    }

    public void setSourcePosition(String sourcePosition) {
        this.sourcePosition = sourcePosition;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public String getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(String targetPosition) {
        this.targetPosition = targetPosition;
    }

    public boolean isEmpty() {
        if (word == null && targetPosition.equals("en") && sourcePosition.equals("bn"))
            return true;
        else
            return false;
    }
}
