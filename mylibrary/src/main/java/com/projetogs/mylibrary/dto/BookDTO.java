package com.projetogs.mylibrary.dto;

import com.projetogs.mylibrary.enums.ReadingStatus;

public record BookDTO(String title, String author, String publisher, String genre, ReadingStatus status) {
}
