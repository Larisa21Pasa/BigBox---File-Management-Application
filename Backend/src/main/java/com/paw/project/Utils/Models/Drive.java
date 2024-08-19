/**************************************************************************

 File:        Drive.java
 Copyright:   (c) 2023 NazImposter
 Description: Model class for Drive entity related operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 08.11.2023  Sebastian Pitica      Basic structure and fields with constraints
 16.11.2023  Sebastian Pitica      Remove @min annotation from id field
 19.11.2023  Sebastian Pitica      Remove @toString annotation and add @builder annotation
 19.11.2023  Toporas Tudor         Added option toBuilder = true to @Builder
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Drive")
@Table(name = "Drives")
public class Drive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drive_id")
    @Basic
    private Integer driveId;

    @NotNull
    @Basic
    @Column(name = "absolute_path")
    private String absolutePath;

    @Basic
    @NotNull
    @Min(0)
    @Column(name="occupied_size")
    private Long occupiedSize;

    @OneToOne()
    @JoinColumn(name = "entry_id", referencedColumnName = "entry_id")
    private FileSystemEntry rootDirId;

    @Basic
    @Column(name="drive_information")
    private String driveInformation;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
