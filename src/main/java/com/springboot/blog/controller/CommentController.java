package com.springboot.blog.controller;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/posts/{postId}/comments")
public class CommentController {

    CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable long postId, @RequestBody CommentDto commentDto){

    return new ResponseEntity<>(commentService.createComment(postId, commentDto), HttpStatus.CREATED);
    }


    @GetMapping
    public List<CommentDto> getAllComments(@PathVariable long postId){
        return commentService.getCommentsByPostId(postId);

    }

    @GetMapping("/{id}")
    public CommentDto getAllCommentsById(@PathVariable long postId, @PathVariable long id){
        return commentService.getCommentsByCommentId(postId, id);
    }

    @PutMapping("/{id}")
    public CommentDto updateComment(@PathVariable long postId, @PathVariable(name = "id") long commentId,@RequestBody CommentDto commentDto){
        return commentService.updateCommentByCommentId(postId, commentId, commentDto);
    }

    @DeleteMapping("/{id}")
    public String deleteCommentByCommentId(@PathVariable long postId, @PathVariable(name = "id") long commentId){
        commentService.deleteCommentByCommentId(postId, commentId);
        return "Comment entity deleted successfully for commentId: " + commentId + " belonging to postId: " + postId;
    }
}
