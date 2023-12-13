package com.smart.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "_image")
public class Image {

    @Id
    @GeneratedValue
    public Integer Id;
    public String signature;
    public String format;
    public String resource_type;
    public String secure_url;
    public Date created_at;
    public String asset_id;
    public String version_id;
    public String type;
    public int version;
    public String url;
    public String public_id;
    public String folder;
    public String original_filename;
    public String api_key;
    public int bytes;

    public int width;
    public String etag;
    public boolean placeholder;
    public int height;

    public Image(Map data) throws ParseException {
        if(data.containsKey("signature")) this.signature = (String) data.get("signature");
        if(data.containsKey("format")) this.format = (String) data.get("format");
        if(data.containsKey("resource_type")) this.resource_type = (String) data.get("resource_type");
        if(data.containsKey("secure_url")) this.secure_url = (String) data.get("secure_url");
        if(data.containsKey("created_at")) this.created_at = new SimpleDateFormat("yyyy-MM-dd").parse(((String) data.get("created_at")).substring(0, 10));
        if(data.containsKey("asset_id")) this.asset_id = (String) data.get("asset_id");
        if(data.containsKey("version_id")) this.version_id = (String) data.get("version_id");
        if(data.containsKey("type")) this.type = (String) data.get("type");
        if(data.containsKey("version")) this.version = (int) data.get("version");
        if(data.containsKey("url")) this.url = (String) data.get("url");
        if(data.containsKey("public_id")) this.public_id = (String) data.get("public_id");
        if(data.containsKey("folder")) this.folder = (String) data.get("folder");
        if(data.containsKey("original_filename")) this.original_filename = (String) data.get("original_filename");
        if(data.containsKey("api_key")) this.api_key = (String) data.get("api_key");
        if(data.containsKey("bytes")) this.bytes = (int) data.get("bytes");
        if(data.containsKey("width")) this.width = (int) data.get("width");
        if(data.containsKey("etag")) this.etag = (String) data.get("etag");
        if(data.containsKey("placeholder")) this.placeholder = (boolean) data.get("placeholder");
        if(data.containsKey("height")) this.height = (int) data.get("height");
    }
}
