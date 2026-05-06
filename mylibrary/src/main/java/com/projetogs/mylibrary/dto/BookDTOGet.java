package com.projetogs.mylibrary.dto;

import com.projetogs.mylibrary.enums.ReadingStatus;

public record BookDTOGet(String id, String title, String author, String publisher, String genre, ReadingStatus status) {
}