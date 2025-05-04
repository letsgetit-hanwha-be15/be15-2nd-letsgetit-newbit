package com.newbit.newbituserservice.user.dto.response;

import com.newbit.newbituserservice.common.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorListResponseWrapper {
    private List<MentorListResponseDTO> mentors;
    private Pagination pagination;
}
