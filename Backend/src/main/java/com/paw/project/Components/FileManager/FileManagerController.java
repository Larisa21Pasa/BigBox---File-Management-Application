/**************************************************************************

 File:        FileManagerController.java
 Copyright:   (c) 2023 NazImposter
 Description: Controller class for the file manager module to handle requests regarding the file system.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 19.11.2023  Sebastian Pitica      Basic structure and controller methods
 20.11.2023  Tudor Toporas         Did some of the todos
 20.11.2023  Sebastian Pitica      Added some todos, and some to-be-implemented methods
 28.11.2023  Tudor Toporas         Finished todos, added createDir, updateInfo, restoreFromBin, getBin
 29.11.2023  Sebastian Pitica      Added documentation comments for all methods
 12.12.2023  Tudor Toporas         Added code for testing
 17.12.2023  Tudor Toporas         Added operations on the size of files and fixed recursive parent in FSEntries
 26.12.2023  Tudor Toporas         Added get all entries of user and properties of fse
 12.01.2023  Tudor Toporas         Fixed download
 15.01.2024  Tudor Toporas         fixed copy, added global properties

 **************************************************************************/


package com.paw.project.Components.FileManager;

import com.paw.project.Utils.Exceptions.ConflictException;
import com.paw.project.Utils.Exceptions.NotFoundException;
import com.paw.project.Utils.Exceptions.UnprocessableException;
import com.paw.project.Utils.Models.Drive;
import com.paw.project.Utils.Models.FileSystemEntry;
import com.paw.project.Utils.Others.Classes.EntryDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/file_manager")

public class FileManagerController {

    private final FileManagerService fileManagerService;

    public FileManagerController(FileManagerService fileManagerService) {
        this.fileManagerService = fileManagerService;
    }
    //todo: START OF: code for testing only shall be removed at the end
    @PostMapping("/populateDB")
    public ResponseEntity<?> PopulateDataBase(@RequestParam(value = "nrUsers", required = false)Integer nrUsers,
                                              @RequestParam(value = "nrFilesPerLevel", required = false)Integer nrFilesPerLevel,
                                              @RequestParam(value = "nrDirPerLevel", required = false)Integer nrDirPerLevel,
                                              @RequestParam(value = "nrLevelsUnderRoot", required = false)Integer nrLevelsUnderRoot)
    {
        nrUsers = Optional.ofNullable(nrUsers).orElse(5);
        nrFilesPerLevel = Optional.ofNullable(nrFilesPerLevel).orElse(2);
        nrDirPerLevel = Optional.ofNullable(nrDirPerLevel).orElse(3);
        nrLevelsUnderRoot = Optional.ofNullable(nrLevelsUnderRoot).orElse(5);

        fileManagerService.populateDB(nrUsers, nrFilesPerLevel, nrDirPerLevel, nrLevelsUnderRoot);
        return new ResponseEntity<>("done", HttpStatus.OK);
    }
    //todo: END OF: code for testing only shall be removed at the end

    //================================ CREATE ====================================//
    /**
     * Uploads (writes) the {@link MultipartFile} as {@link java.io.File} in the file system
     * @param parentId id of directory in which the new file will be present
     * @param file the {@link MultipartFile} that will be uploaded
     * @return the newly created {@link EntryDTO} as {@link ResponseEntity<EntryDTO>}
     * @throws UnprocessableException when trying to create a file as subordinated to another file,
     * or there is not enough space on the drive,
     * or the maxsize could not be found,
     * or there is an error with the file at the controller level
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId
     * @throws ConflictException when the name of the new file already exists
     */
    @PostMapping("/entries/upload/parentId/{parentId}")
    public ResponseEntity<EntryDTO> uploadFileSystemEntry(@PathVariable Integer parentId, @RequestBody MultipartFile file) {
        try {
            return new ResponseEntity<>(fileManagerService.createFile(parentId, file.getOriginalFilename(), file.getBytes()).toDTO(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new UnprocessableException("File could not be uploaded details: " + e.getMessage());
        }
    }

    /**
     * Creates a new directory
     * @param parentId id of the directory where the new {@link FileSystemEntry} will be created
     * @param name the name of the new directory
     * @return the newly created {@link EntryDTO} as {@link ResponseEntity<EntryDTO>}
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId
     * @throws ConflictException when the name of the new directory already exists
     */
    @PostMapping("/entries/create/parentId/{parentId}")
    public ResponseEntity<EntryDTO> createDirectory(@PathVariable Integer parentId, @RequestBody String name){
        return new ResponseEntity<>(fileManagerService.createDirectory(parentId, name).toDTO(), HttpStatus.OK);
    }

    /**
     * Creates a copy of a file inside the given directoryg
     * @param parentId id of directory in which the file is to be copied
     * @param entryId file id that is to be copied
     * @return the newly created {@link EntryDTO} as {@link ResponseEntity<EntryDTO>}
     * @throws UnprocessableException when trying to copy a directory, or when trying to copy a file to another file
     * @throws ConflictException when trying to duplicate the file in the same directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId or entryId
     */
    @PostMapping("/entries/copy/{entryId}/parentId/{parentId}")
    public ResponseEntity<EntryDTO> copyFileSystemEntry(@PathVariable Integer entryId, @PathVariable Integer parentId) {
        return new ResponseEntity<>(fileManagerService.copyFile(parentId, entryId).toDTO(), HttpStatus.CREATED);
    }


    //================================ UPDATE ====================================//
    /**
     * Moves a {@link FileSystemEntry} from one directory to another(different) directory
     * @param entryId id of {@link FileSystemEntry} that will be moved
     * @param parentId id of destination directory
     * @return the updated {@link EntryDTO} as {@link ResponseEntity<EntryDTO>}
     * @throws UnprocessableException when destId represents a file
     * @throws ConflictException when the move happens to the same directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId or entryId
     */
    @PostMapping("/entries/move/{entryId}/parentId/{parentId}")
    public ResponseEntity<EntryDTO> moveFileSystemEntry(@PathVariable Integer entryId, @PathVariable Integer parentId) {
        return new ResponseEntity<>(fileManagerService.move(entryId, parentId).toDTO(), HttpStatus.OK);
    }

    /**
     * Changes the name of the specified file
     * @param entryId id of target file
     * @param newName the new name
     * @return the updated {@link EntryDTO} as {@link ResponseEntity<EntryDTO>}
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    @PostMapping("/entries/rename/{entryId}")
    public ResponseEntity<EntryDTO> renameFileSystemEntry(@PathVariable Integer entryId, @RequestBody String newName) {
        return new ResponseEntity<>(fileManagerService.rename(entryId, newName).toDTO(), HttpStatus.OK);
    }

    /**
     * Updates the entry information for the specified entry
     * @param entryId id of target
     * @param newInfo updated information
     * @return the updated {@link EntryDTO} as {@link ResponseEntity<EntryDTO>}
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    @PostMapping("/entries/updateInfo/{entryId}")
    public ResponseEntity<EntryDTO> updateEntryInfo(@PathVariable Integer entryId, @RequestBody String newInfo){
        return new ResponseEntity<>(fileManagerService.updateEntryInfo(entryId, newInfo).toDTO(), HttpStatus.OK);
    }

    /**
     * Restores deleted files or directories
     * @param entryId id of target
     * @return the restored {@link EntryDTO} as {@link ResponseEntity<EntryDTO>}
     * @throws NotFoundException when the entry is not deleted or permanently deleted
     * @throws ConflictException when trying to restore an entry that already exists (having the same name)
     */
    @PostMapping("/entries/restore/{entryId}")
    public ResponseEntity<EntryDTO> restoreDeletedEntry(@PathVariable Integer entryId){
        return new ResponseEntity<>(fileManagerService.restoreFromTrashBin(entryId).toDTO(), HttpStatus.OK);
    }

    //================================ READ ====================================//
    /**
     * Fetches the content of the file with the specified id as {@link Resource}
     * @param entryId id of target file
     * @return the content of the file as {@link Resource} converted from {@link ByteArrayResource} of the {@link java.io.File}
     * @throws UnprocessableException when entryId represents a directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    @GetMapping("/entries/download/{entryId}")
    public ResponseEntity<Map<String, byte[]>> downloadFileSystemEntry(@PathVariable Integer entryId) {
        Map<String, byte[]> map = new HashMap<>();
        map.put("content", fileManagerService.getFileContents(entryId));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * Finds all the child {@link FileSystemEntry}s of the specified directory
     * @param parentId id of the target directory
     * @return a {@link java.util.List} of the children as {@link ResponseEntity<List>}
     * @throws UnprocessableException when target is not a directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId
     */
    @GetMapping("/entries/parentId/{parentId}")
    public ResponseEntity<List<EntryDTO>> getFileSystemEntries(@PathVariable Integer parentId) {
        return new ResponseEntity<>(fileManagerService.getDirectoryContents(parentId).stream().map(FileSystemEntry::toDTO).toList(), HttpStatus.OK);
    }

    /**
     * Finds the top deleted entries, not any that were recursively deleted
     * @param userId id of user who owns the trash bin
     * @return a {@link java.util.List} of the top deleted entries as {@link ResponseEntity<List>}
     * @throws NotFoundException when the there is no {@link Drive} associated with the specified user id
     */
    @GetMapping("/entries/trash/userId/{userId}")
    public ResponseEntity<List<EntryDTO>> getTrashBin(@PathVariable Integer userId){
        return new ResponseEntity<>(fileManagerService.getTrashBin(userId).stream().map(FileSystemEntry::toDTO).toList(), HttpStatus.OK);
    }

    /**
     * Finds the root directory of a user's personal drive
     * @param userId id of target user
     * @return {@link EntryDTO} of the root directory as {@link ResponseEntity<>}
     * @throws NotFoundException when the userId doesn't have a drive associated to it
     */
    @GetMapping("/entries/rootEntry/userId/{userId}")
    public ResponseEntity<EntryDTO> getRootFileSystemEntry(@PathVariable Integer userId) {
        return new ResponseEntity<>(fileManagerService.getRootEntry(userId).toDTO(), HttpStatus.OK);
    }

    /**
     * Finds all the {@link FileSystemEntry}s belonging to a user that start with a string of characters case-insensitive
     * @param userId owner of the searched {@link FileSystemEntry}s
     * @param name string that all found matches start with
     * @return a {@link List} of the matched {@link EntryDTO}s as a {@link ResponseEntity<> }
     */
    @GetMapping("/entries/search/{name}/userId/{userId}")
    public ResponseEntity<List<EntryDTO>> getEntryByName(@PathVariable Integer userId,
                                                         @PathVariable  String name){
        return new ResponseEntity<>(fileManagerService.getEntriesOfUserWithNameStartsWith(name, userId).stream().map(FileSystemEntry::toDTO).toList(), HttpStatus.OK);
    }

    /**
     * Finds the parent id of the specified entry
     * @param entryId id of entry
     * @return an {@link Integer} that represents the id of the parent
     */
    @GetMapping("/entries/{entryId}/parent")
    public ResponseEntity<Integer> getParent(@PathVariable Integer entryId){
        return new ResponseEntity<>(fileManagerService.getParent(entryId), HttpStatus.OK);
    }

    @GetMapping("/entries/{entryId}/properties")
    public ResponseEntity<EntryDTO> getFseProperties(@PathVariable Integer entryId){
        return new ResponseEntity<>(fileManagerService.getEntry(entryId).toDTO(), HttpStatus.OK);
    }

    /**
     * Finds the size details of the user
     * @param userId id of the user
     * @return a {@link Map<>} of the size details of the user
     */
    @GetMapping("/details/size/userId/{userId}")
    public ResponseEntity<Map<String, Long>> getUserSizeDetails(@PathVariable Integer userId){
        Map<String, Long> details = new HashMap<>();
        details.put("max", fileManagerService.getUserMaxSize(userId));
        details.put("current", fileManagerService.getUserCurrentSize(userId));
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping("/entries/userId/{userId}")
    public ResponseEntity<List<EntryDTO>> getAllUserEntries(@PathVariable Integer userId){
        return new ResponseEntity<>(fileManagerService.getAllEntriesOfUser(userId).stream().map(FileSystemEntry::toDTO).toList(), HttpStatus.OK);
    }

    @GetMapping("/entries/recents/userId/{userId}")
    public ResponseEntity<List<EntryDTO>> getRecentFiles(@PathVariable Integer userId){
        return new ResponseEntity<>(fileManagerService.getRecentFilesOfUser(userId).stream().map(FileSystemEntry::toDTO).toList(), HttpStatus.OK);
    }

    @GetMapping("/global/details")
    public ResponseEntity<?> getGlobalDetails(){
        Map<String, Long> details = new HashMap<>();
        details.put("noUsers", fileManagerService.getNoUsers());
        details.put("noFiles", fileManagerService.getNoFiles());
        details.put("totalOcupiedSize", fileManagerService.getTotalOcupiedSize());
        return new ResponseEntity<>(details, HttpStatus.OK);
    }


    //================================ DELETE ====================================//
    /**
     * Moves the entry and all of its children, if any, to the trash bin. Or, if already in the trash bin, deletes them permanently
     * @param entryId id of entry to be deleted
     * @return the last representation of the first deleted entry
     * @throws UnprocessableException when trying to delete root directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    @DeleteMapping("/entries/delete/{entryId}")
    public ResponseEntity<EntryDTO> deleteFileSystemEntry(@PathVariable Integer entryId) {
        return new ResponseEntity<>(fileManagerService.deleteEntry(entryId).toDTO(), HttpStatus.OK);
    }


}
