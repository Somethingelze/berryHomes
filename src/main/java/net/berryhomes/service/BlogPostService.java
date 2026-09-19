package net.berryhomes.service;

import net.berryhomes.model.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface BlogPostService {
    public Page<BlogPost> published(Pageable pageable);

    public Page<BlogPost> active(Pageable pageable);

    public BlogPost get(UUID id);

    public BlogPost save(BlogPost post, MultipartFile image);

    public BlogPost updatePublished(UUID id, boolean published);

    public void archive(UUID id);

}
