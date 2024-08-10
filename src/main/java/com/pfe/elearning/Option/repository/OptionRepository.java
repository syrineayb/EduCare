package com.pfe.elearning.Option.repository;

import com.pfe.elearning.Option.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option,Integer> {

    List<Option> findByQuestionIdAndCorrect(Integer questionId, boolean b);
}
