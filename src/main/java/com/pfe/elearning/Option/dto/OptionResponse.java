package com.pfe.elearning.Option.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponse {
    private Integer id;
    private String text;
    private boolean correct;
}