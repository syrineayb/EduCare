package com.pfe.elearning.question.repository;

import com.pfe.elearning.question.dto.CandidateResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CandidateResponseRepository extends CrudRepository<CandidateResponse, Long> {
}
