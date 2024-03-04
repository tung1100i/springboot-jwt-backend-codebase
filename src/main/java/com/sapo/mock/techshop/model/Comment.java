package com.sapo.mock.techshop.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "comment")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String commentText;
}
