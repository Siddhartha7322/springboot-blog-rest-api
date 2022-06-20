package com.springboot.blog.controller;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
import com.springboot.blog.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;


@Validated
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // create Blog POST Rest endpoint
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto){
        System.out.println("Inside Controller");
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    // CREATE get ENDPOINT FOR BLOG POST
    @GetMapping
    public PostResponse getAllPosts(
            @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo,
            @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = AppConstants.DEFAULT_SORT_DIRECTION)   @Pattern(regexp = "ASC|DESC|asc|desc") String sortDir){
        System.out.println("Inside Controller");
        return postService.getAllPosts(pageNo,pageSize, sortBy, sortDir);

    }

    // Get Endpoint to return post by Id.
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostbyId(@PathVariable long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    public PostDto updatePost(@PathVariable long id, @RequestBody PostDto postDto){
        return postService.updatePost(id, postDto);
    }

    // Rest endpoint to delete post by id.
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable long id){
        postService.deletePostById(id);
        return "Post entity deleted successfully for id: " + id;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleConstraintViolationException(ConstraintViolationException e) {
        return "Validation error: " + "Valid values for sortDir are : ASC, DESC, asc, desc.";
    }
}
