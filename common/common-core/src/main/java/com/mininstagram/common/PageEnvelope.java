package com.mininstagram.common;
import org.springframework.data.domain.Page;
import java.util.List;
public record PageEnvelope<T>(List<T> items,int page,int size,long totalElements,int totalPages) {
 public static <T> PageEnvelope<T> from(Page<T> result) {
   return new PageEnvelope<>(result.getContent(),result.getNumber(),result.getSize(),result.getTotalElements(),result.getTotalPages());
 }
}
