package com.smart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.smart.entities.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@Service
public class ImageService {

    private final Logger logger = LogManager.getLogger(ImageService.class);
    @Autowired
    private Cloudinary cloudinary;

    public Image upload(MultipartFile file){
        try {
            Map data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());
            Image image = new Image(data);
//            logger.info(image);
            return image;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    public Image getDefault() {
        try {
            Map data = cloudinary.api().resourceByAssetID("85c1e82b140fb068250632cd812eafff", ObjectUtils.emptyMap());
            Image image = new Image(data);
//            logger.info(image);
            return image;
        }catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    @Async("asyncTaskExecutor")
    public void delete(String publicId) {
        try {
            ApiResponse apiResponse = null;
            if(!publicId.equals("ioy7gourm6mefsz1q3bd")) apiResponse = cloudinary.api().deleteResources(Collections.singletonList(publicId), ObjectUtils.asMap("type", "upload", "resource_type", "image"));
//            logger.info(apiResponse);
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
