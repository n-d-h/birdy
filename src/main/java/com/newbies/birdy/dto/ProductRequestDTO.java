package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

//    private ProductDTO productDTO;
    private String jsonProductDTO;

    private MultipartFile mainImage;

    private MultipartFile[] subImages;
}
