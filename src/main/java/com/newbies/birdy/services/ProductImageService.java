package com.newbies.birdy.services;

import com.newbies.birdy.dto.FileImageDTO;
import com.newbies.birdy.dto.ProductImageDTO;

import java.util.List;

public interface ProductImageService {

    void saveImages(String[] images, Integer productId);

    List<ProductImageDTO> getAllImageByProductId(Integer productId);

    void deleteImages(Integer productId);

    void updateImagesProduct(List<FileImageDTO> files, List<Integer> ids, Integer productId);

    void updateImages(List<FileImageDTO> files, Integer productId);
}
