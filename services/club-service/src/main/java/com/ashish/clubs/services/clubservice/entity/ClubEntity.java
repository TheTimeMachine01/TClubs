package com.ashish.clubs.services.clubservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("clubs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubEntity {

    @Id
    @Column("club_id")
    private UUID clubId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("owner_id")
    private UUID ownerId;

    @Column("image_url")
    private String imageUrl;

    @Column("location")
    private String location;

    @Column("status")
    private String status;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
