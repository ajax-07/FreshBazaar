package com.freshbazaar.identity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "created_at")
    @CreatedDate
    @JsonIgnore
    private Date createdAt;


    @Column(name = "created_by", nullable = false)
    @CreatedBy
    @JsonIgnore
    private String createdBy;

    @Column(name = "last_modified_at")
    @LastModifiedDate
    @JsonIgnore
    private Date lastModifiedAt;

    @Column(name = "last_modified_by")
    @LastModifiedBy
    @JsonIgnore
    private String lastModifiedBy;
}
