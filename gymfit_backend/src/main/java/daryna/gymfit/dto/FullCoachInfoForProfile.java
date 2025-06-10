package daryna.gymfit.dto;

import java.util.List;

public record FullCoachInfoForProfile(

        CoachProfileDto coachInfo,

        int coachRatingByClient,

        List<ResponseCommentDto> comments

) {
}
