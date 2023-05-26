package com.example.practice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "Photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // 길이가 255개 이상의 문자를 저장하고 싶을 때 지정
    @Column(length = 1000)
    private byte[] photoData;

    private String contentType;

    @Builder
    public Photo(byte[] photoData, String contentType){
        this.photoData = photoData;
        this.contentType = contentType;
    }
}
