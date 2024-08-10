package com.pfe.elearning.token.repository;

import java.util.List;
import java.util.Optional;

import com.pfe.elearning.token.Token;
import com.pfe.elearning.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = """
      select t from Token t inner join User u
      on t.user.id = u.id
      where u.id = :id and (t.expired = false or t.revoked = false)
      """)
    List<Token> findAllValidTokenByUser(Integer id);

    Optional<Token> findByToken(String token);
    @Transactional
    void deleteByUserId(Integer userId);

    void deleteByUser(User user);
}
