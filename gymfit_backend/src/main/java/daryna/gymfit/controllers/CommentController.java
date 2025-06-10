package daryna.gymfit.controllers;

import daryna.gymfit.dto.CommentCreationDto;
import daryna.gymfit.dto.ResponseCommentDto;
import daryna.gymfit.entities.Comment;
import daryna.gymfit.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/gymfit/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentCreationDto dto, Principal principal) {
        commentService.createComment(dto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }

}










