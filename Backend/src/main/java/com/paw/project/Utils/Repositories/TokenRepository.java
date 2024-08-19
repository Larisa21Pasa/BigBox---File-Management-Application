/**************************************************************************

 File:        TokenRepository.java
 Copyright:   (c) 2023 NazImposter
 Description: Repository interface for managing roles entities in the database.
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 21.11.2023  Pasa Larisa           Added specific methods for token logic
 16.01.2024  Toporas Tudor         Fixed findAllByExpiredAndRevokedAndUserUserId function

 **************************************************************************/
package com.paw.project.Utils.Repositories;

import com.paw.project.Utils.Models.Token;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    List<Token> findAllByExpiredAndRevokedAndUserUserId(@NotNull Boolean expired, @NotNull Boolean revoked, Integer id);

    Optional<Token> findByToken(String token);
}