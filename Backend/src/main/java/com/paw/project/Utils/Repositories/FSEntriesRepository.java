/**************************************************************************

 File:        FSEntriesRepository.java
 Copyright:   (c) 2023 NazImposter
 Description: Repository interface for managing FileSystemEntry entities in the database.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure
 16.11.2023  Sebastian Pitica      Update user, integer
 19.11.2023  Tudor Toporas         Added findAllByParentEntryId method
 28.11.2023  Tudor Toporas         Added findByParentEntryIdAndNameAndDeleteStatus
 29.11.2023  Sebastian Pitica      Added description
 15.01.2024  Tudor Toporas         added global properties queries

 **************************************************************************/

package com.paw.project.Utils.Repositories;


import com.paw.project.Utils.Models.Enums.DeleteStatus;
import com.paw.project.Utils.Models.FileSystemEntry;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FSEntriesRepository extends JpaRepository<FileSystemEntry, Integer> {
    List<FileSystemEntry> findAllByParentEntryId(Integer entryId);
    Optional<FileSystemEntry> findByParentEntryIdAndNameAndDeleteStatus(Integer parentId, String name, DeleteStatus status);
    Long countAllByDeleteStatusNot(DeleteStatus deleteStatus);
    List<FileSystemEntry> findAllByIsFile(@NotNull Boolean isFile);
}
