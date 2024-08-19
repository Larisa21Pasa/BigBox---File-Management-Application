/**************************************************************************

 File:        SharedFSEntry.java
 Copyright:   (c) 2023 NazImposter
 Description: Model class for SharedFSEntry entity related operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 08.11.2023  Sebastian Pitica      Basic structure and fields with constraints
 16.11.2023  Sebastian Pitica      Remove @min annotation from id field
 19.11.2023  Sebastian Pitica      Remove @toString annotation, add @builder annotation
 19.11.2023  Toporas Tudor         Added option toBuilder = true to @Builder
 28.11.2023  Toporas Tudor         Added default value with @Builder and made user be able to have multiple SharedFSEntries
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "SharedFSEntry")
@Table(name = "SharedFSEntries")
public class SharedFSEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name = "share_id")
    private Integer shareId;

    @OneToOne()
    @JoinColumn(name = "entry_id", referencedColumnName = "entry_id")
    private FileSystemEntry fileSystemEntry;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Pattern(regexp = "(r|w)")
    @Basic
    @Builder.Default
    @Column(name="type_r_w")
    private String typeRW = "r";
}
