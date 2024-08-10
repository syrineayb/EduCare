package com.pfe.elearning.Option.servise;

import com.pfe.elearning.Option.dto.OptionRequest;
import com.pfe.elearning.Option.dto.OptionResponse;
import com.pfe.elearning.Option.entity.Option;

import java.util.List;

public interface OptionService {
    OptionResponse createOption(OptionRequest optionRequest);
    OptionResponse updateOption(Long optionId, OptionRequest optionRequest);
    OptionResponse getOptionById(Long optionId);
    List<OptionResponse> getAllOptions();
    void deleteOption(Long optionId);

    List<Option> findCorrectOptionsByQuestionId(Integer questionId);
}