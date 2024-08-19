/**************************************************************************

 File:        DemoController.java
 Copyright:   (c) 2023 NazImposter
 Description: Dummy controller for testing authentication and authorization with tokens.
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 11.11.2023  Pasa Larisa          Initial creation of the Demo resource controller.
 21.11.2023  Pasa Larisa          Create dummy controller to simulate file_manager access scenario.
 27.11.2023  Matei Rares          Update dummy path to simulate file_manager access scenario.
 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file_manager_test")
//@PreAuthorize("hasRole('USER')")
public class DemoController {
    @GetMapping
    public ResponseEntity<String> getFiles()
    {
        return ResponseEntity.ok("Saliut from filemanager");

    }
}
