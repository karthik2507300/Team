package com.certifypro.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Adds questions to a paper. Paper totalMarks is recomputed from all its questions. */
public record AddPaperQuestionsRequest(
        @NotEmpty @Valid List<Item> items
) {
    public record Item(
            @NotNull Long questionId,
            Integer sequenceOrder,
            @NotNull Integer marksAllocated
    ) {
    }
}
