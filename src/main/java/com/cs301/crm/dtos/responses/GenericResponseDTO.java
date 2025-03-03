package com.cs301.crm.dtos.responses;

import java.time.ZonedDateTime;

public record GenericResponseDTO(
        boolean success,

        String message,

        ZonedDateTime timestamp
) {}