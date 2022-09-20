package vttpnusiss.day37upload.controllers;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Jdbc;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttpnusiss.day37upload.models.Upload;
import vttpnusiss.day37upload.services.UploadService;

@RestController
@RequestMapping(path = "/upload")
public class UploadRestController {

    public static final String SQL_INSERT_BLOB = "insert into post (title, pic,mediatype) values (?,?,?)";

    public static final String SQL_GET_UPLOAD = "select * from post where post_id= ?";

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private UploadService uploadSvc;

    @GetMapping(path = "{id}")
    public ResponseEntity<byte[]> getUpload(@PathVariable Integer id) {
        Optional<Upload> opt = uploadSvc.getPost(id);
        Upload up = opt.get();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(up.getMediaType()))
                .body(up.getContent());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postUpload(
            @RequestPart MultipartFile myfile, @RequestPart String title, @RequestPart String email) {

        try {
            int updated = template.update(SQL_INSERT_BLOB, title, myfile.getInputStream(), myfile.getContentType());
            System.out.printf("updated: %d\n", updated);
        } catch (DataAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonObject data = Json.createObjectBuilder()
                .add("content-type", myfile.getContentType())
                .add("name", myfile.getName())
                .add("original_name", myfile.getOriginalFilename())
                .add("size", myfile.getSize())
                .add("form_title", title)
                .add("form_email", email)
                .build();
        return ResponseEntity.ok(data.toString());
    }
}
