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

@Table("memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipEntity {

    @Id
    @Column("membership_id")
    private UUID membershipId;

    @Column("club_id")
    private UUID clubId;

    @Column("user_id")
    private UUID userId;

    @Column("role")
    private String role;

    @Column("status")
    private String status;

    @Column("join_date")
    private Instant joinDate;

    @Column("end_date")
    private Instant endDate;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
