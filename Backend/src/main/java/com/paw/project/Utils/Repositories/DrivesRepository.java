/**************************************************************************

 File:        DrivesRepository.java
 Copyright:   (c) 2023 NazImposter
 Description: Repository interface for managing Drive entities in the database.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure
 16.11.2023  Sebastian Pitica      Update user, integer
 19.11.2023  Tudor Toporas         Update findByRootDirId
 20.11.2023  Tudor Toporas         Added findByUserUserId
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Repositories;


import com.paw.project.Utils.Models.Drive;
import com.paw.project.Utils.Models.FileSystemEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrivesRepository extends JpaRepository<Drive, Integer> {
    Optional<Drive> findByRootDirId(FileSystemEntry entry);
    Optional<Drive> findByUserUserId(Integer userId);
}
