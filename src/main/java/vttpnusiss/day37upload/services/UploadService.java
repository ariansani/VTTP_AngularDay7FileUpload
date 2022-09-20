package vttpnusiss.day37upload.services;

import java.sql.ResultSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import vttpnusiss.day37upload.models.Upload;

@Service
public class UploadService {
    
    public static final String SQL_INSERT_BLOB = "insert into post (title, pic,mediatype) values (?,?,?)";

    public static final String SQL_GET_UPLOAD = "select * from post where post_id= ?";

    @Autowired
    private JdbcTemplate template;


    public Optional<Upload> getPost(Integer postId){
        
        Optional<Upload> opt = template.query(SQL_GET_UPLOAD, (ResultSet rs) -> {
            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(Upload.create(rs));

        }, postId);
        Upload up = opt.get();
        return Optional.of(up);
    
    }
}
