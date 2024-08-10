package com.pfe.elearning.Option.servise.Impl;

import com.pfe.elearning.Option.dto.OptionMapper;
import com.pfe.elearning.Option.dto.OptionRequest;
import com.pfe.elearning.Option.dto.OptionResponse;
import com.pfe.elearning.Option.entity.Option;
import com.pfe.elearning.Option.repository.OptionRepository;
import com.pfe.elearning.Option.servise.OptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

//    @Override
//    @Transactional
//    public OptionResponse createOption(OptionRequest optionRequest) {
//        Option option = optionMapper.toOption(optionRequest);
//        option = optionRepository.save(option);
//        return optionMapper.toOptionResponse(option);
//    }

    @Override
    public OptionResponse createOption(OptionRequest optionRequest) {
        return null;
    }

    @Override
    @Transactional
    public OptionResponse updateOption(Long optionId, OptionRequest optionRequest) {
        Option option = optionRepository.findById(Math.toIntExact(optionId))
                .orElseThrow(() -> new RuntimeException("Option not found"));
        option.setText(optionRequest.getText());
        option.setCorrect(optionRequest.isCorrect());
        option = optionRepository.save(option);
        return optionMapper.toOptionResponse(option);
    }

    @Override

    public OptionResponse getOptionById(Long optionId) {
        Option option = optionRepository.findById(Math.toIntExact(optionId))
                .orElseThrow(() -> new RuntimeException("Option not found"));
        return optionMapper.toOptionResponse(option);
    }

    @Override

    public List<OptionResponse> getAllOptions() {
        return optionRepository.findAll().stream()
                .map(optionMapper::toOptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOption(Long optionId) {
        Option option = optionRepository.findById(Math.toIntExact(optionId))
                .orElseThrow(() -> new RuntimeException("Option not found"));
        optionRepository.delete(option);
    }
    @Override
    public List<Option> findCorrectOptionsByQuestionId(Integer questionId) {
        return optionRepository.findByQuestionIdAndCorrect(questionId, true);
    }
}