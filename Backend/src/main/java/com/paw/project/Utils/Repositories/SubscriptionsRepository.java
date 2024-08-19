/**************************************************************************

 File:        SubscriptionsRepository.java
 Copyright:   (c) 2023 NazImposter
 Description: Repository interface for managing Subscription entities in the database.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure
 16.11.2023  Sebastian Pitica      Update user, integer
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Repositories;


import com.paw.project.Utils.Models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionsRepository extends JpaRepository<Subscription, Integer> {
    Subscription findFirstBySubscriptionId(Integer subscriptionId);
}
