/**************************************************************************

 File:        Plan.java
 Copyright:   (c) 2023 NazImposter
 Description: Model class for Plan entity related operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 08.11.2023  Sebastian Pitica      Basic structure and fields with constraints
 16.11.2023  Sebastian Pitica      Remove @min annotation from id field
 19.11.2023  Sebastian Pitica      Remove @toString annotation, add @builder annotation
 19.11.2023  Toporas Tudor         Added option toBuilder = true to @Builder
 29.11.2023  Sebastian Pitica      Added description
 10.12.2023  Larisa Pasa           Added fields for description plan
 **************************************************************************/

package com.paw.project.Utils.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Plan")
@Table(name = "Plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name = "plan_id")
    private Integer planId;

    @Basic
    @NotNull
    @Column(name="max_size")
    private Long maxSize;

    @Basic
    @NotNull
    @Column(name = "name_plan")  // Adăugați această anotare pentru a corespunde numelui câmpului din TypeScript
    private String namePlan;

    @Basic
    @NotNull
    @Column(name = "cloud_storage")
    private String cloudStorage;

    @Basic
    @NotNull
    @Column(name = "plan_for_client_type")
    private String planForClientType;

    @Basic
    @NotNull
    @Column(name = "fit_for")
    private String fitFor;

    @Basic
    @NotNull
    @Column(name = "description")
    private String description;
}
