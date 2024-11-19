package com.seochang.quiteSeoul.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "placedata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dataId;

    @Column(columnDefinition = "json")
    private String placeInformation;

    private LocalDateTime collectedAt;

    @ManyToOne
    @JoinColumn(name = "place_id", referencedColumnName = "placeId", nullable = false)
    private Place place;

}
