package com.smart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.smart.entities.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
            logger.info(image);
            return image;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Image getDefault() {
        try {
            Map data = cloudinary.api().resourceByAssetID("43a98b32641557f9c573d4ee574d9b9b", ObjectUtils.emptyMap());
            Image image = new Image(data);
            logger.info(image);
            return image;
        }catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
