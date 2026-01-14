package com.ashish.clubs.common.models.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * A generic response wrapper for paginated lists of data.
 * Useful for consistent API responses across services when fetching collections.
 *
 * @param <T> The type of content in the list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> content;        // The actual list of items
    private int page;               // Current page number (0-indexed or 1-indexed, define consistently)
    private int size;               // Number of items per page
    private long totalElements;     // Total number of items across all pages
    private int totalPages;         // Total number of pages
    private boolean last;           // True if this is the last page
    private boolean first;          // True if this is the first page
}