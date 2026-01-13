package com.elmenus.fleet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medication")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "code", length = 50, nullable = false, unique = true)
  private String code;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "weight", nullable = false)
  private Double weight;

  @Column(name = "image_url")
  private String imageURL;

  public Medication(String code, String name, Double weight, String imageURL) {
    this.code = code;
    this.name = name;
    this.weight = weight;
    this.imageURL = imageURL;
  }

}
