package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommentServiceimpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;

    CommentServiceimpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    /* Save comments for corresponding postId passed in request */
    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {

        Comment comment = mapToEntity(commentDto);

        // find the post by supplied postId if not found then throw @ResourceNotFoundException
        Post post = postRepository.findById(postId).
                orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Long.toString(postId)));

        // set returned post object to comment entity.
        comment.setPost(post);
        // Save Comment Entity to DB.
        Comment savedComment = commentRepository.save(comment);

        return mapToDto(savedComment);
    }

    /* Fetch comments for corresponding postid. */
    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {

        List<Comment> comments = commentRepository.findByPostId(postId).
                orElseThrow(() -> new ResourceNotFoundException("Get", "postId", Long.toString(postId)));

        return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentsByCommentId(long postId, long commentId) {

        // retrieve Post by postId
        Post post = postRepository.findById(Long.valueOf(postId)).
                orElseThrow(() -> new ResourceNotFoundException("Post", "postId", Long.toString(postId)));
        // retrieve Comment by Comment id
        Comment comment = commentRepository.findById(commentId).
                orElseThrow(() -> new ResourceNotFoundException
                        ("Comment", "commentId", "" + commentId));
        if(!comment.getPost().getId().equals(post.getId()))
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post.");
        return mapToDto(comment);
    }

    @Override
    public CommentDto updateCommentByCommentId(long postId, long commentId, CommentDto commentDto) {
        // retrieve Post by postId
        Post post = postRepository.findById(postId).orElseThrow(() ->new ResourceNotFoundException("Post", "postId", "" + postId));
        System.out.println(post);
        // retrieve Comment by Comment id
        Comment comment = commentRepository.findById(commentId).
                orElseThrow(() -> new ResourceNotFoundException
                        ("Comment", "commentId", "" + commentId));

        System.out.println(comment);

        if(!comment.getPost().getId().equals(post.getId()))
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post.");

        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        Comment updatedComment = commentRepository.save(comment);

        /* return mapToDto(updatedComment);*/



        return mapToDto(updatedComment);
    }

    @Override
    public void deleteCommentByCommentId(long postId, long commentId) {

        // retrieve Post by postId
        Post post = postRepository.findById(postId).orElseThrow(() ->new ResourceNotFoundException("Post", "postId", "" + postId));

        // retrieve Comment by Comment id
        Comment comment = commentRepository.findById(commentId).
                orElseThrow(() -> new ResourceNotFoundException
                        ("Comment", "commentId", "" + commentId));

        if(!comment.getPost().getId().equals(post.getId()))
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to the post.");

        commentRepository.delete(comment);
        return;
    }

    // Mapping DTO to Entity
    private Comment mapToEntity(CommentDto commentDto){
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        return comment;
    }


    // Mapping Entity to DTO
    private CommentDto mapToDto(Comment comment){
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setName(comment.getName());
        commentDto.setEmail(comment.getEmail());
        commentDto.setBody(comment.getBody());

        return commentDto;
    }

}
