/**************************************************************************

 File:        FileSystemManager.java
 Copyright:   (c) 2023 NazImposter
 Description: Service for managing the entries in the file system
 Designed by: Tudor Toporas

 Module-History:
 Date        Author                Reason
 13.11.2023  Tudor Toporas      Basic Functionality
 19.11.2023  Tudor Toporas      Update for builder
 19.11.2023  Tudor Toporas      Changed parameters to identify entries by id
 20.11.2023  Tudor Toporas      Finished CRUD on files/directories
 28.11.2023  Tudor Toporas      Added things, Reordered declarations for readability and fixed some issues
 29.11.2023  Tudor Toporas      Fixed PR issues
 12.12.2023  Tudor Toporas      Added code for testing and search of entries with start of name case-insensitive
 17.12.2023  Tudor Toporas      Added operations on the size of files
 25.12.2023  Tudor Toporas      New logic for authorization
 26.12.2023  Tudor Toporas      Added get all entries of user and properties of fse, made names of entries seem realistic
 12.01.2023  Tudor Toporas      Added createContextFor(User)
 15.01.2024  Tudor Toporas      Properly fixed rename, added global properties

 **************************************************************************/

package com.paw.project.Components.FileManager;

import com.paw.project.Utils.Exceptions.ConflictException;
import com.paw.project.Utils.Exceptions.NotFoundException;
import com.paw.project.Utils.Exceptions.StorageException;
import com.paw.project.Utils.Exceptions.UnprocessableException;
import com.paw.project.Utils.Models.*;
import com.paw.project.Utils.Models.Enums.DeleteStatus;
import com.paw.project.Utils.Models.Enums.RoleEnum;
import com.paw.project.Utils.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Collection;

import static com.paw.project.Utils.Models.Enums.DeleteStatus.*;

@Service
public class FileManagerService {
    private final FSEntriesRepository entryRepo;
    private final DrivesRepository driveRepo;
    private final RecentFilesRepository recentRepo;

    final UsersRepository userRepo;
    final PlansRepository planRepo;
    final SubscriptionsRepository subRepo;
    @Value("${nazimpostor.filesystem.root}")
    public String FILE_SYSTEM_ROOT;
    @Autowired
    public FileManagerService(FSEntriesRepository entryRepo,
                              DrivesRepository driveRepo,
                              RecentFilesRepository recentRepo,
                              UsersRepository userRepo,
                              PlansRepository planRepo,
                              SubscriptionsRepository subRepo){
        this.entryRepo = entryRepo;
        this.driveRepo = driveRepo;
        this.recentRepo = recentRepo;

        this.userRepo = userRepo;
        this.subRepo = subRepo;
        this.planRepo = planRepo;
        if(FILE_SYSTEM_ROOT == null)
            FILE_SYSTEM_ROOT = "D:\\FS";
    }

    /*
    ================================ PUBLIC METHODS ================================
     */
    //================================ CREATE
//todo: START OF: code for testing only shall be removed at the end
    private static final String[] fileNames = {
            "document", "report", "presentation", "spreadsheet", "image", "photo", "drawing", "code", "script", "data",
            "audio", "video", "archive", "readme", "notes", "contract", "schedule", "presentationDraft", "logo", "designDraft",
            "memo", "proposal", "diagram", "database", "sketch", "plan", "analysis", "template", "form", "model", "policy",
            "manual", "procedure", "survey", "manifesto", "blueprint", "manifest", "manifestation", "manifestationDraft",
            "manifestoDraft", "manifestCopy", "manifestationCopy", "manifestoCopy", "manifestoDraftCopy"
        };
    private static final String[] directoryNames = {
            "Documents", "Reports", "Presentations", "Spreadsheets", "Images", "Photos", "Drawings", "Code", "Scripts",
            "Data", "Audio", "Videos", "Archives", "Readme", "Notes", "Contracts", "Schedules", "PresentationDrafts",
            "Logos", "DesignDrafts", "Memos", "Proposals", "Diagrams", "Databases", "Sketches", "Plans", "Analyses",
            "Templates", "Forms", "Models", "Policies", "Manuals", "Procedures", "Surveys", "Manifestos", "Blueprints",
            "Manifestations", "ManifestoDrafts",
    };
    //the code for mapping numbers to words is thanks to https://www.rgagnon.com/javadetails/java-0426.html
    private static final String[] tensNames = {
            "", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    private static final String[] numNames = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve",
            "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20){
            soFar = numNames[number % 100];
            number /= 100;
        }
        else {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        return numNames[number] + "hundred" + soFar;
    }
    public void populateDB(int nrUsers, int nrFilesPerLevel, int nrDirPerLevel, int nrLevelsUnderRoot){
        List<Plan> plans = List.of(
                Plan.builder()
                        .maxSize(5L * 1_000_000_000)
                        .namePlan("Basic")
                        .cloudStorage("5 GB")
                        .planForClientType("New")
                        .fitFor("individuals with basic storage needs")
                        .description("Our entry-level plan for new users who have basic storage requirements." +
                                " Perfect for personal use and getting started with our services.").build(),
                Plan.builder()
                        .maxSize(20L * 1_000_000_000)
                        .namePlan("Standard")
                        .cloudStorage("20 GB")
                        .planForClientType("Regular")
                        .fitFor("users with moderate storage needs")
                        .description("A balanced plan for regular users with moderate storage needs. " +
                                "Ideal for individuals and small businesses looking for a reliable storage solution.").build(),
                Plan.builder()
                        .maxSize(100L * 1_000_000_000)
                        .namePlan("Premium")
                        .cloudStorage("100 GB")
                        .planForClientType("VIP")
                        .fitFor("power users with extensive storage requirements")
                        .description("Our top-tier plan for VIP users with extensive storage requirements. " +
                                "Enjoy premium features and a generous storage quota suitable for power users and businesses with high demands.").build()
        );
        planRepo.saveAll(plans);

        List<FileSystemEntry> roots = IntStream.range(1,nrUsers+1).boxed().map(FileManagerService::convertLessThanOneThousand)
                .map(i-> FileSystemEntry.builder()
                        .entryInformation("no entry info " + i)
                        .isFile(false)
                        .name("root" + i)
                        .build())
                .toList();

        List<User> users = IntStream.range(1,nrUsers+1).boxed().map(FileManagerService::convertLessThanOneThousand)
                .map(i -> User.builder()
                                .name("hello" + i)
                                .hashedPassword(new BCryptPasswordEncoder().encode(i))
                                .email("hello@" + i + ".com")
                                .isBlocked(false)
                                .roleEnum(RoleEnum.USER)
                                .subscription(
                                        subRepo.save(Subscription.builder()
                                                .plan(plans.get(new Random().nextInt(0,3)))
                                                .build()))
                                .build())
                .toList();

        List<Drive> drives = IntStream.range(0, roots.size())
                .boxed()
                .collect(Collectors.toMap(users::get, roots::get))
                .entrySet()
                .stream().map(it -> Drive.builder()
                        .driveInformation("drive info of " + it.getKey().getUsername())
                        .rootDirId(it.getValue())
                        .absolutePath(FILE_SYSTEM_ROOT + "\\" + it.getKey().getUserId().toString())
                        .occupiedSize(0L)
                        .user(it.getKey())
                        .build())
                .toList();

        userRepo.saveAll(users);
        entryRepo.saveAll(roots);
        driveRepo.saveAll(drives);

        List<FileSystemEntry> newEntries =  roots.stream()
                .flatMap(it -> genRootContents(it,nrLevelsUnderRoot, nrFilesPerLevel, nrDirPerLevel).stream())
                .toList();
        entryRepo.saveAll(newEntries);
        driveRepo.saveAll(drives.stream()
                .map(it -> it.toBuilder()
                        .occupiedSize(getAllEntriesOfUser(it.getUser().getUserId()).stream()
                                .map(FileSystemEntry::getSize)
                                .reduce(Long::sum)
                                .orElse(0L))
                        .build())
                .toList());
    }

    List<FileSystemEntry> genRootContents(FileSystemEntry parent, Integer maxDepth, int nrFilesPerLevel, int nrDirPerLevel){
        if(maxDepth == 0)
            return List.of();
        return genContents(parent, nrFilesPerLevel, nrDirPerLevel).stream()
                .flatMap(it -> genRootContents(it, maxDepth - 1, nrFilesPerLevel, nrDirPerLevel).stream()).toList();
    }

    List<FileSystemEntry> genContents(FileSystemEntry parent, int nrFilesPerLevel, int nrDirPerLevel){
        if(parent.getIsFile())
            return List.of();
        return entryRepo.saveAll(IntStream.range(1,nrFilesPerLevel + nrDirPerLevel + 1).boxed().map(it -> FileSystemEntry.builder()
                        .name((it >= nrDirPerLevel + 1) ?
                                fileNames[new Random().nextInt(0, fileNames.length)]:
                                directoryNames[new Random().nextInt(0, directoryNames.length)]
                        )
                        .parent(parent)
                        .isFile(it >= nrDirPerLevel + 1)
                        .size(it >= nrDirPerLevel + 1 ? 1024L : 0L)
                        .build())
                .toList());
    }
//todo: END OF: code for testing only shall be removed at the end
    /**
     * Creates a new {@linkplain FileSystemEntry}, saves it in the database, and writes the content to a new {@link java.io.File} in the file system
     * @param parentId id of directory in which the new file will be present
     * @param name the name of the new file
     * @param content what will be written in the file
     * @return the newly created {@linkplain FileSystemEntry}
     * @throws UnprocessableException when trying to create a file as subordinated to another file,
     * or there is not enough space on the drive,
     * or the maxsize could not be found
     * @throws ConflictException when the name of the new file already exists
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId
     */
    public FileSystemEntry createFile(Integer parentId, String name, byte[] content){
        if(getEntry(parentId).getIsFile()){
            throw new UnprocessableException("Cannot create a file in a file");
        }
        if(entryRepo.findByParentEntryIdAndNameAndDeleteStatus(parentId, name, NOT_DELETED).isPresent()){
            throw new ConflictException("Filename already exists");
        }
        if(driveRepo.findByRootDirId(getRootEntry(getEntry(parentId)))
                .map(d -> d.getUser().getSubscription().getPlan().getMaxSize() < d.getOccupiedSize() + content.length)
                .orElseThrow(() -> new UnprocessableException("Could not find drive's max size"))
        )
        {
            throw new UnprocessableException("Not enough space on the drive");
        }
        FileSystemEntry newFile = uploadFile(
                entryRepo.save(
                        FileSystemEntry.builder()
                                .isFile(true)
                                .name(name)
                                .size((long) content.length)
                                .parent(FileSystemEntry.builder().entryId(parentId).build())
                                .build()
                ),
                content
        );
        addFileSizeToDrive(newFile);
        return newFile;
    }

    /**
     * Creates a copy of a file inside the given directory
     * @param parentId id of directory in which the file is to be copied
     * @param copyId file id that is to be copied
     * @return the newly created {@link FileSystemEntry}
     * @throws UnprocessableException when trying to copy a directory, or when trying to copy a file to another file
     * @throws ConflictException when trying to duplicate the file in the same directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId or copyId
     */
    public FileSystemEntry copyFile(Integer parentId, Integer copyId){
        FileSystemEntry parent = getEntry(parentId);
        FileSystemEntry copy = getEntry(copyId);
        if(copy.isNotFile()){
            throw new UnprocessableException("Cannot copy a directory");
        }
        if(copy.getParent().getEntryId().equals(parent.getEntryId())){
            throw new ConflictException("Cannot copy a file to the same directory");
        }
        if(parent.getIsFile()){
            throw new UnprocessableException("Cannot copy a file to a file");
        }
        return createFile(parentId, copy.getName(), downloadFile(copy));
    }

    /**
     * Creates a new directory
     * @param parentId id of the directory where the new {@link FileSystemEntry} will be created
     * @param name the name of the new directory
     * @return the created {@link FileSystemEntry}
     * @throws ConflictException when the name of the new directory already exists
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId
     */
    public FileSystemEntry createDirectory(Integer parentId, String name){
        if(entryRepo.findByParentEntryIdAndNameAndDeleteStatus(parentId, name, NOT_DELETED).isPresent()){
            throw new ConflictException("Directory name already exists");
        }
        return entryRepo.save(FileSystemEntry
                .builder()
                .isFile(false)
                .parent(getEntry(parentId))
                .name(name)
                .build()
        );
    }

    public void createContextFor(User user){
        FileSystemEntry root = FileSystemEntry.builder()
                .isFile(false)
                .name(user.getName())
                .build();

        Drive drive = Drive.builder()
                .driveInformation("drive info of " + user.getUsername())
                .rootDirId(root)
                .absolutePath(FILE_SYSTEM_ROOT + "\\" + user.getUserId().toString())
                .occupiedSize(0L)
                .user(user)
                .build();
        createDriveFolderFor(drive);
        entryRepo.save(root);
        driveRepo.save(drive);
    }

    //================================ READ
    /**
     * Fetches the contents from the file
     * @param entryId id of target file
     * @return the content of the file
     * @throws UnprocessableException when entryId represents a directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    public byte[] getFileContents(Integer entryId){
        FileSystemEntry entry = getEntry(entryId);
        if(entry.getIsFile()){
            saveToRecent(entry);
            return downloadFile(entry);
        }else{
            throw new UnprocessableException("Cannot download a directory");
        }
    }

    /**
     * Finds all the child {@link FileSystemEntry}s of the specified directory
     * @param parentId id of the target directory
     * @return a {@link java.util.List} of the children
     * @throws UnprocessableException when target is not a directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified parentId
     */
    public List<FileSystemEntry> getDirectoryContents(Integer parentId){
        FileSystemEntry entry = getEntry(parentId);
        if(entry.getIsFile()){
            throw new UnprocessableException("Target is not a directory");
        }
        saveToRecent(entry);
        return getNotDeletedFiles(parentId);
    }

    /**
     * Finds the top deleted entries, not any that were recursively deleted
     * @param userId id of user who owns the trash bin
     * @return a {@link java.util.List} of the top deleted entries
     * @throws NotFoundException when the there is no {@link Drive} associated with the specified user id
     */
    public List<FileSystemEntry> getTrashBin(Integer userId){
        return mapDirTree(getRootEntry(userId).getEntryId(), e ->{
            if(e.getDeleteStatus().equals(DELETED)){
                if(e.getParent().getDeleteStatus().equals(NOT_DELETED)){
                    return List.of(e);
                }
            }
            return List.of();
        });
    }

    /**
     * Finds the entry with the specified id
     * @param id of target
     * @return a {@link FileSystemEntry} with the ID of id
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    public FileSystemEntry getEntry(Integer id){
        return entryRepo.findById(id).orElseThrow(()->new NotFoundException("File with the id " + id + " doesn't exist"));
    }

    /**
     * Finds the root directory of a user's personal drive
     * @param userId id of target user
     * @return {@link FileSystemEntry} of the root directory
     * @throws NotFoundException when the userId doesn't have a drive associated to it
     */
    public FileSystemEntry getRootEntry(Integer userId){
        return driveRepo.findByUserUserId(userId)
                .map(Drive::getRootDirId)
                .orElseThrow(()-> new NotFoundException("Drive of user id: " + userId + "doesn't exit"));
    }

    /**
     * Finds all {@link FileSystemEntry}s of the specified user that start with the given string case-insensitive
     * @param nameStart {@link String} that the name starts with
     * @param userId the user of witch entries are queried
     * @return a {@link java.util.List} of the matching {@link FileSystemEntry}s
     * @throws NotFoundException when user with the specified id doesn't exist
     */
    public List<FileSystemEntry> getEntriesOfUserWithNameStartsWith(String nameStart, Integer userId){
        return getAllEntriesOfUser(userId).stream()
                .filter(it -> it.getName().toLowerCase()
                        .startsWith(nameStart.toLowerCase()))
                .toList();
    }

    public Integer getParent(Integer entryId){
        return entryRepo.findById(entryId)
                .map(it -> Optional.ofNullable(it.getParent())
                        .map(FileSystemEntry::getEntryId)
                        .orElse(-1))
                .orElseThrow(()->new NotFoundException("Entry doesn't exist"));
    }

    public Long getUserCurrentSize(Integer userId){
        return getDrive(getRootEntry(userId)).getOccupiedSize();
    }

    public Long getUserMaxSize(Integer userId){
        return userRepo.findById(userId)
                .map(it -> it.getSubscription().getPlan().getMaxSize())
                .orElseThrow(()->new NotFoundException("User with id " + userId +" doesn't exist"));
    }

    public Integer getUserIdOf(Integer entryId){
        return getDrive(getEntry(entryId)).getUser().getUserId();
    }

    public List<FileSystemEntry> getAllEntriesOfUser(Integer userId){
        Integer rootId = getRootEntry(userId).getEntryId();
        List<FileSystemEntry> res = mapDirTree(rootId, it -> getNotDeletedFiles(it.getEntryId()));
        return Stream.of(res, entryRepo.findAllByParentEntryId(rootId)).flatMap(Collection::stream).toList();
    }

    public List<FileSystemEntry> getRecentFilesOfUser(Integer userId){
        return recentRepo.findAllByUserUserId(userId).stream()
                .filter(it -> it.getExpiryDate().isAfter(LocalDate.now()))
                .sorted((one, two)-> -one.getGenerateDate().compareTo(two.getGenerateDate()))
                .map(RecentFile::getFileSystemEntry)
                .toList();
    }

    public Long getNoUsers(){
        return userRepo.count();
    }

    public Long getNoFiles(){
        return entryRepo.countAllByDeleteStatusNot(PERMANENTLY_DELETED);
    }

    public Long getTotalOcupiedSize(){
        return entryRepo.findAllByIsFile(true).stream().map(FileSystemEntry::getSize).reduce(0L, Long::sum);
    }

    //================================ UPDATE
    /**
     * Moves a {@link FileSystemEntry} from one directory to another(different) directory
     * @param entryId id of {@link FileSystemEntry} that will be moved
     * @param destId id of destination directory
     * @return the updated entry
     * @throws UnprocessableException when destId represents a file
     * @throws ConflictException when the move happens to the same directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified entryId or destId
     */
    public FileSystemEntry move(Integer entryId, Integer destId){
        FileSystemEntry entry = getEntry(entryId);
        FileSystemEntry dest = getEntry(destId);
        if(dest.getIsFile()){
            throw new UnprocessableException("Destination must be a directory");
        }
        if(entry.getParent().getEntryId().equals(destId)){
            throw new ConflictException("Cannot move to the same directory");
        }
        return entryRepo.save(entry.toBuilder().parent(dest).build());
    }

    /**
     * Changes the name of the specified file
     * @param entryId id of target file
     * @param name the new name
     * @return the updated {@link FileSystemEntry}
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    public FileSystemEntry rename(Integer entryId, String name){
        FileSystemEntry entry = getEntry(entryId);
        if(entry.getIsFile()){
            renameFile(entry, name);
        }
        return entryRepo.save(
                entry.toBuilder().name(name).build());
    }

    /**
     * Updates the entry information for the specified entry
     * @param entryId id of target
     * @param newEntryInfo updated information
     * @return the updated entry
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    public FileSystemEntry updateEntryInfo(Integer entryId, String newEntryInfo){
        return entryRepo.save(
                getEntry(entryId).toBuilder()
                        .entryInformation(newEntryInfo)
                        .build()
        );
    }

    /**
     * Restores deleted files or directories
     * @param entryId id of target
     * @return the file, or top directory, that was restored
     * @throws NotFoundException when the entry is not deleted or permanently deleted or doesn't exist
     * @throws ConflictException when trying to restore an entry that already exists (having the same name)
     */
    public FileSystemEntry restoreFromTrashBin(Integer entryId){
        FileSystemEntry entry = getEntry(entryId);
        if(entry.getDeleteStatus().equals(NOT_DELETED)){
            throw new NotFoundException("Entry not in trash");
        } else if (entry.getDeleteStatus().equals(PERMANENTLY_DELETED)) {
            throw new NotFoundException("Entry doesn't exist");
        }
        if(entryRepo.findByParentEntryIdAndNameAndDeleteStatus(entry.getParent().getEntryId(), entry.getName(), NOT_DELETED).isPresent()){
            throw new ConflictException("Entry name already exists");
        }
        mapDirTree(entryId, e -> List.of(entryRepo.save(e.toBuilder().deleteStatus(NOT_DELETED).build())));
        return entryRepo.save(entry.toBuilder().deleteStatus(NOT_DELETED).build());
    }

    //================================ DELETE
    /**
     * Moves the entry and all of its children, if any, to the trash bin. Or ,if already in the trash bin, deletes them <b>permanently</b>
     * @param entryId id of entry to be deleted
     * @return the last representation of the first deleted entry
     * @throws UnprocessableException when trying to delete root directory
     * @throws NotFoundException when the there is no {@link FileSystemEntry} with the specified id
     */
    public FileSystemEntry deleteEntry(Integer entryId){
        FileSystemEntry entry = getEntry(entryId);
        if(getRootEntry(entry).getEntryId().equals(entryId)){
            throw new UnprocessableException("Cannot delete root directory");
        }
        if(entry.getIsFile()){
            return deleteSingleEntry(entry);
        }else{
            return deleteDirectory(entry);
        }
    }

    /*
    ================================ PRIVATE METHODS ================================
     */
    //================================ CREATE
    /**
     * Creates a new {@link java.io.File} and writes the content to it
     * @param entry {@linkplain FileSystemEntry}
     * @param content {@code byte[]} with the content to be written
     * @return the entry
     */
    private FileSystemEntry uploadFile(FileSystemEntry entry, byte[] content){
        File newFile = getFile(entry);
        try {
            newFile.createNewFile();
            Files.write(newFile.toPath(), content);
        } catch (IOException e) {
            throw new UnprocessableException("File could not be uploaded details: " + e.getMessage());
        }
        return entry;
    }

    private void saveToRecent(FileSystemEntry entry){
        recentRepo.save(RecentFile.builder()
                .entryId(entry.getEntryId())
                .user(getDrive(entry).getUser())
                .generateDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(1)).build()
        );
    }

    private void createDriveFolderFor(Drive drive){
        if(new File(drive.getAbsolutePath()).mkdirs()){
        }else{
            throw new StorageException("Root Drive folder could not be created");
        }
    }

    //================================ READ
    /**
     * @param entry {@link FileSystemEntry} that represents the file with the content needed
     * @return {@code byte[]} with the content of the file
     */
    private byte[] downloadFile(FileSystemEntry entry){
        File newFile = getFile(entry);
        byte[] output;
        try {
            output = Files.readAllBytes(newFile.toPath());
        } catch (IOException e) {
            throw new UnprocessableException("File could not be downloaded details: " + e.getMessage());
        }
        return output;
    }

    /**
     * Maps {@linkplain FileSystemEntry} to actual file name
     * @param entry {@linkplain FileSystemEntry}
     * @return name of the file that represents the {@linkplain FileSystemEntry}
     */
    private String getFileName(FileSystemEntry entry){
        return entry.getEntryId().toString() + "_" + entry.getName();
    }

    /**
     * @param entry {@linkplain FileSystemEntry}
     * @return absolute path to the drive that this {@linkplain FileSystemEntry} is in
     */
    private String getDriveRootPath(FileSystemEntry entry){
        String absPath = getDriveAbsolutePath(entry);
        if(absPath.endsWith("\\")){
            return absPath;
        }else{
            return absPath + "\\";
        }
    }

    /**
     * @param entry {@linkplain FileSystemEntry}
     * @return {@link java.io.File} that represents the {@linkplain FileSystemEntry}
     */
    private File getFile(FileSystemEntry entry){
        return new File(getDriveRootPath(entry) + getFileName(entry));
    }

    /**
     * Recursive function that finds the root directory
     * @param entry the file/directory that we want the root of
     * @return {@linkplain FileSystemEntry} that represents the root directory
     */
    private FileSystemEntry getRootEntry(FileSystemEntry entry){
        return entryRepo
                .findById(entry.getParent() == null ? -1 : entry.getParent().getEntryId())
                .map(this::getRootEntry)
                .orElse(entry);
    }

    private Drive getDrive(FileSystemEntry entry){
        FileSystemEntry root = getRootEntry(entry);
        return driveRepo.findByRootDirId(root).orElseThrow(() -> new NotFoundException("RootDir " + root + " is not connected to a drive"));
    }

    /**
     * Finds the Drive id for the input file/directory
     * @param entry file/directory that we need the drive id of
     * @return Drive id of the drive the entry is in
     */
    private String getDriveAbsolutePath(FileSystemEntry entry){
        return getDrive(entry).getAbsolutePath();
    }

    private List<FileSystemEntry> getNotDeletedFiles(Integer dirId){
        return entryRepo.findAllByParentEntryId(dirId).stream().filter(e -> e.getDeleteStatus().equals(NOT_DELETED)).toList();
    }

    //================================ UPDATE

    private void addFileSizeToDrive(FileSystemEntry entry){
        alterSizeOfDrive(entry, 1);
    }

    private void removeFileSizeFromDrive(FileSystemEntry entry){
        alterSizeOfDrive(entry, -1);
    }

    private void alterSizeOfDrive(FileSystemEntry entry, Integer operation){
        Drive d = getDrive(entry);
        driveRepo.save(d.toBuilder()
                .occupiedSize(d.getOccupiedSize() + operation * entry.getSize())
                .build());
    }

    private void renameFile(FileSystemEntry entry, String newName) {
        File old = getFile(entry);
        File newFile = getFile(entry.toBuilder().name(newName).build());
        if(old.renameTo(newFile)){
        }else{
            throw new StorageException("Rename failed");
        }
    }

    //================================ DELETE
    private FileSystemEntry deleteDirectory(FileSystemEntry entry){
        mapDirTree(entry.getEntryId(), e -> List.of(deleteSingleEntry(e)));
        return deleteSingleEntry(entry);
    }

    private FileSystemEntry deleteSingleEntry(FileSystemEntry entry){
        if(entry.getDeleteStatus().equals(PERMANENTLY_DELETED)){
            throw new NotFoundException("The file/directory doesn't exist");
        }
        DeleteStatus deleteStatus = nextDeleteStatus(entry);
        if(deleteStatus.equals(PERMANENTLY_DELETED)){
            if(getFile(entry).delete() == false){
                //throw new StorageException("Could not delete entry");//todo uncomment for real content
            }
            removeFileSizeFromDrive(entry);
        }
        return entryRepo.save(entry.toBuilder().deleteStatus(deleteStatus).build());
    }

    //================================ UTILS
    private DeleteStatus nextDeleteStatus(FileSystemEntry entry){
        return switch (entry.getDeleteStatus()){
            case NOT_DELETED -> DELETED;
            case DELETED -> PERMANENTLY_DELETED;
            default -> entry.getDeleteStatus();
        };
    }

    /**
     * Traverses all the FileSystemEntries contained in the directory parameter recursively then applies the function, starting from leaves
     * @param dirId the directory id that will be traversed recursively
     * @param f function that will be applied
     * @return an aggregated {@link java.util.List} of all the {@link FileSystemEntry}'s that resulted from the function
     */
    private List<FileSystemEntry> mapDirTree(Integer dirId, Function<FileSystemEntry, List<FileSystemEntry>> f){
        List<FileSystemEntry> next = entryRepo.findAllByParentEntryId(dirId);
        return next.stream().flatMap(l -> {
            List<FileSystemEntry> res = new ArrayList<>();
            if(l.isNotFile()){
                res.addAll(mapDirTree(l.getEntryId(),f));
            }
            res.addAll(f.apply(l));
            return res.stream();
        }).toList();
    }
}
