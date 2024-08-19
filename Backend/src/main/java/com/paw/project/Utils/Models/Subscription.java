/**************************************************************************

 File:        Subscription.java
 Copyright:   (c) 2023 NazImposter
 Description: Model class for Subscription entity related operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 08.11.2023  Sebastian Pitica      Basic structure and fields with constraints
 16.11.2023  Sebastian Pitica      Remove @min annotation from id field
 19.11.2023  Sebastian Pitica      Remove @toString annotation, add @builder annotation
 19.11.2023  Toporas Tudor         Added option toBuilder = true to @Builder
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Models;

import jakarta.persistence.*;

import lombok.*;

@Data
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Subscription")
@Table(name = "Subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Basic
    @Column(name = "subscription_id")
    private Integer subscriptionId;

    @ManyToOne()
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id")
    private Plan plan;

}
