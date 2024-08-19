/**************************************************************************

 File:        EntryDTO.java
 Copyright:   (c) 2023 NazImposter
 Description: Data Transfer Object for FileSystemEntries
 Designed by: Toporas Tudor

 Module-History:
 Date        Author                Reason
 17.12.2023  Tudor Toporas         Added data transfer object logic

 **************************************************************************/

package com.paw.project.Utils.Others.Classes;

import com.paw.project.Utils.Models.Enums.DeleteStatus;
import com.paw.project.Utils.Models.FileSystemEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EntryDTO {

    private Integer entryId;
    private String name;
    private Boolean isFile;
    private Integer parentId;
    private String entryInformation;
    private Long size;
    private LocalDate creationDate;
    private DeleteStatus deleteStatus;

    public static EntryDTO from(FileSystemEntry entry){
        return EntryDTO.builder()
                .entryId(entry.getEntryId())
                .name(entry.getName())
                .isFile(entry.getIsFile())
                .parentId(Optional.ofNullable(entry.getParent())
                        .map(FileSystemEntry::getEntryId)
                        .orElse(null))
                .entryInformation(entry.getEntryInformation())
                .size(entry.getSize())
                .creationDate(entry.getCreationDate())
                .deleteStatus(entry.getDeleteStatus())
                .build();
    }
}
