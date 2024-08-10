package com.pfe.elearning.Option.dto;

import com.pfe.elearning.common.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequest extends BaseEntity {
    @NotBlank(message = "Option text cannot be blank")
    private String text;
    @NotNull (message = "Option text cannot be null")
    private boolean correct;


}
