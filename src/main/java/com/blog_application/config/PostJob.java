package com.blog_application.config;

import com.blog_application.model.post.Post;
import com.blog_application.repository.post.PostRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostJob implements Job{

    private final PostRepository postRepository;

    @Autowired
    public PostJob(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Post post = (Post) context.getMergedJobDataMap().get("post");
        postRepository.save(post);
        System.out.println("Scheduled post with title '" + post.getTitle() + "' has been saved successfully.");
    }

}
