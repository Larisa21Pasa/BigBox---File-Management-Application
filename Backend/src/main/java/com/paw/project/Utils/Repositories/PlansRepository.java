/**************************************************************************

 File:        PlansRepository.java
 Copyright:   (c) 2023 NazImposter
 Description: Repository interface for managing Plan entities in the database.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Structure
 16.11.2023  Sebastian Pitica      Update user, integer
 29.11.2023  Sebastian Pitica      Added description

 **************************************************************************/

package com.paw.project.Utils.Repositories;


import com.paw.project.Utils.Models.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlansRepository extends JpaRepository<Plan, Integer> {
    Plan findByPlanId(Integer planId);
}
