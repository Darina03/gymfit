package daryna.gymfit.services;

import daryna.gymfit.authentification.JwtService;
import daryna.gymfit.dao.CommentRepository;
import daryna.gymfit.dto.CommentCreationDto;
import daryna.gymfit.dto.ResponseCommentDto;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.Comment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ClientService clientService;
    private final CoachService coachService;

    @Transactional
    public void createComment(CommentCreationDto dto, String email) {
        Client client =clientService.findByEmail(email);
        Coach coach = coachService.getCoachById(dto.coachId());


        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setCreationDate(LocalDateTime.now());
        comment.setClient(client);
        comment.setCoach(coach);

        commentRepository.save(comment);
    }

    public List<ResponseCommentDto> getCommentsForCoach(Long coachId) {
        return commentRepository.findByCoachIdOrderByCreationDateDesc(coachId).stream()
                .map(comment -> {
                    return new ResponseCommentDto(comment.getId(),comment.getContent(), comment.getCreationDate(),
                            comment.getClient().getName() + " " + comment.getClient().getSurname() );
                }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
