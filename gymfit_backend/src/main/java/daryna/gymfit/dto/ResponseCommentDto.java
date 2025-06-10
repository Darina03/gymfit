package daryna.gymfit.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResponseCommentDto(

        Long id,

        String content,

        LocalDateTime creationDate,

        String clientFullName)


{
}
