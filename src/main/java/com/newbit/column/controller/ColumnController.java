package com.newbit.column.controller;

import com.newbit.column.service.ColumnService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/columns")
@RequiredArgsConstructor
@Tag(name = "Column", description = "공개된 컬럼 조회 관련 API")
public class ColumnController {

    private final ColumnService columnService;
}
