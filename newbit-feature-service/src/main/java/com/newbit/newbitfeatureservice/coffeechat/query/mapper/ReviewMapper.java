package com.newbit.newbitfeatureservice.coffeechat.query.mapper;

import com.newbit.newbitfeatureservice.coffeechat.query.dto.request.ReviewSearchServiceRequest;
import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ReviewListDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {

    List<ReviewListDto> selectReviews(ReviewSearchServiceRequest reviewSearchServiceRequest);

    long countReviews(ReviewSearchServiceRequest reviewSearchServiceRequest);
}
