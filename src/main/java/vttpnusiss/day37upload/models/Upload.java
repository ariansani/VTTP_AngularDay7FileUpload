package vttpnusiss.day37upload.models;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Upload {

    private Integer postId;
    private String title;
    private String mediaType;
    private byte[] content;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public static Upload create(ResultSet rs) throws SQLException{
        Upload up = new Upload();
        up.setPostId(rs.getInt("post_id"));
        up.setTitle(rs.getString("title"));
        up.setMediaType(rs.getString("mediaType"));
        up.setContent(rs.getBytes("pic"));
        return up;
    }

}
