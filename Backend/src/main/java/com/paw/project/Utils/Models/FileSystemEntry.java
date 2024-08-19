/**************************************************************************

 File:        FileSystemEntry.java
 Copyright:   (c) 2023 NazImposter
 Description: Model class for FileSystemEntry entity related operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 08.11.2023  Sebastian Pitica      Basic structure and fields with constraints
 16.11.2023  Sebastian Pitica      Remove @min annotation from id field
 19.11.2023  Sebastian Pitica      Remove @toString annotation, add @builder annotation, added delete status field, added creation date field
 19.11.2023  Toporas Tudor         Added option toBuilder = true to @Builder
 28.11.2023  Toporas Tudor         Fixed the "a directory can have only one child" problem and added isNotFile
 29.11.2023  Sebastian Pitica      Added description
 17.12.2023  Tudor Toporas         Added size and data transfer object logic
 13.01.2024  Tudor Toporas         Changed validator for FS name

 **************************************************************************/


package com.paw.project.Utils.Models;

import com.paw.project.Utils.Models.Enums.DeleteStatus;
import com.paw.project.Utils.Others.Classes.EntryDTO;
import com.paw.project.Utils.Validators.ValidFSE;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "FileSystemEntry")
@Table(name = "FileSystemEntries")
public class FileSystemEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name = "entry_id")
    private Integer entryId;

    @ValidFSE
    @Basic
    @Column(name = "name")
    private String name;

    @NotNull
    @Basic
    @Column(name = "file_0_1")
    private Boolean isFile;

    @ManyToOne()
    @JoinColumn(name = "parent_id", referencedColumnName = "entry_id")
    private FileSystemEntry parent;

    @Basic
    @Nullable
    @Column(name="entry_information")
    private String entryInformation;

    @Basic
    @NotNull
    @Builder.Default
    @Column(name = "size_bytes")
    private Long size = 0L;

    @Builder.Default
    @Column(name = "creation_date")
    private LocalDate creationDate=LocalDate.now();


    @Builder.Default
    @Column(name = "delete_status")
    private DeleteStatus deleteStatus=DeleteStatus.NOT_DELETED;

    public Boolean isNotFile(){
        return !isFile;
    }

    public EntryDTO toDTO(){
        return EntryDTO.from(this);
    }
}
