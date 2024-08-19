/**************************************************************************

 File:        UsersRepository.java
 Copyright:   (c) 2023 NazImposter
 Description: Repository interface for managing User entities in the database.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 11.11.2023  Pasa Larisa           Added filters for JWTAuthenticationFilter
 07.11.2023  Sebastian Pitica      Structure
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Repositories;

import com.paw.project.Utils.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
   User findByUserId(Integer userId);
   Optional<User> findByEmail(String email);
   User findUserByEmail(String email);

}
