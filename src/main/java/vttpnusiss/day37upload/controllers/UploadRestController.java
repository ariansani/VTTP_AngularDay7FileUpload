package vttpnusiss.day37upload.controllers;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

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

    @Autowired
    private AmazonS3Client s3Client;

    @GetMapping(path = "{id}")
    public ResponseEntity<byte[]> getUpload(@PathVariable Integer id) {
        Optional<Upload> opt = uploadSvc.getPost(id);
        Upload up = opt.get();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(up.getMediaType()))
                .body(up.getContent());
    }

    @PostMapping(path = "/spaces", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postSpacesUpload(@RequestPart MultipartFile myfile, @RequestPart String title) {

        // my private metadata
        Map<String, String> myData = new HashMap<>();
        myData.put("title", title);
        myData.put("createdOn", (new Date()).toString());

        // metadata for the object
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(myfile.getContentType());
        metadata.setContentLength(myfile.getSize());
        metadata.setUserMetadata(myData);

        String hash = UUID.randomUUID().toString().substring(0, 8);

        try {

            PutObjectRequest putReq = new PutObjectRequest("arianBucket", "bear/%s".formatted(hash),
                    myfile.getInputStream(), metadata);
            putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult result = s3Client.putObject(putReq);

        } catch (IOException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonObject data = Json.createObjectBuilder()
                .add("content-type", myfile.getContentType())
                .add("name", hash)
                .add("original_name", myfile.getOriginalFilename())
                .add("size", myfile.getSize())
                .add("form_title", title)
                .build();
        return ResponseEntity.ok(data.toString());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postUpload(@RequestPart MultipartFile myfile, @RequestPart String title) {

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
                .build();
        return ResponseEntity.ok(data.toString());
    }
}
