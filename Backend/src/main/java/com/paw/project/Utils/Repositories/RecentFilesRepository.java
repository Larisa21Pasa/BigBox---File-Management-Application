/**************************************************************************

 File:        RecentFilesRepository.java
 Copyright:   (c) 2023 NazImposter
 Description: Repository interface for managing RecentFile entities in the database.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure
 16.11.2023  Sebastian Pitica      Update user, integer
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Repositories;


import com.paw.project.Utils.Models.RecentFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentFilesRepository extends JpaRepository<RecentFile, Integer> {
    List<RecentFile> findAllByUserUserId(Integer userId);
}
