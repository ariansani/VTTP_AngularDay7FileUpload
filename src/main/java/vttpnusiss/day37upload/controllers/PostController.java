package vttpnusiss.day37upload.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vttpnusiss.day37upload.models.Upload;
import vttpnusiss.day37upload.services.UploadService;

@Controller
@RequestMapping(path="/post")
public class PostController {
    
    
    @Autowired
    private UploadService uploadSvc;

    @GetMapping(path="{postId}")
    public String getPost(@PathVariable Integer postId, Model model){

         Optional<Upload> opt =uploadSvc.getPost(postId);
        Upload up = opt.get();
        model.addAttribute("post", up);
        model.addAttribute("imageSrc","/upload/%d".formatted(up.getPostId()));
      return "post";
    }
}
