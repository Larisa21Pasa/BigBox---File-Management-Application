/**************************************************************************

 File:        RecentFile.java
 Copyright:   (c) 2023 NazImposter
 Description: Model class for RecentFile entity related operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 08.11.2023  Sebastian Pitica      Basic structure and fields with constraints
 19.11.2023  Sebastian Pitica      Remove @toString annotation, add @builder annotation
 19.11.2023  Toporas Tudor         Added option toBuilder = true to @Builder
 28.11.2023  Toporas Tudor         Fixed the "a user can have only one recent file" problem
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Models;

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
@Entity(name = "RecentFile")
@Table(name = "RecentFiles")
public class RecentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name = "entry_id")
    private Integer entryId;

    @OneToOne()
    @JoinColumn(name = "entry_id", referencedColumnName = "entry_id")
    private FileSystemEntry fileSystemEntry;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @NotNull
    @Column(name="expiry_date")
    private LocalDate expiryDate;

    @NotNull
    @Column(name="generate_date")
    private LocalDate generateDate;
}
