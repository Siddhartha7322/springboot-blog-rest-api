package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        // Mapping DTO to entity
        Post post = mapToEntity(postDto);

        // Saving the entity data in table
        Post newPost = postRepository.save(post);

        // Mapping resulting entity to DTO
        PostDto postResponse = mapToDTO(newPost);

        return postResponse;
    }

    private PostDto mapToDTO(Post newPost) {
        PostDto postResponse = new PostDto();
        postResponse.setId(newPost.getId());
        postResponse.setTitle(newPost.getTitle());
        postResponse.setDescription(newPost.getDescription());
        postResponse.setContent(newPost.getContent());
        return postResponse;
    }

    private Post mapToEntity(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        return post;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<Post> postPage = postRepository.findAll(pageable);
        List<Post> posts = postPage.getContent();
        List<PostDto> contents = posts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse(contents, postPage.getNumber(),
                postPage.getSize(), postPage.getNumberOfElements(),postPage.getTotalPages(),
                postPage.isLast());
        return postResponse;
    }
    @Override
    public PostDto getPostById(long id) {

        Post post =  postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", Long.toString(id)));
        return mapToDTO(post);
    }

    @Override
    public PostDto updatePost(long id, PostDto postDto) {

        Post post =  postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", Long.toString(id)));
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        Post updatedPost = postRepository.save(post);

        PostDto updatedPostDto = mapToDTO(updatedPost);

        return updatedPostDto;
    }

    @Override
    public void deletePostById(long id) {
        Post post =  postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", Long.toString(id)));

        postRepository.delete(post);
    }
}
