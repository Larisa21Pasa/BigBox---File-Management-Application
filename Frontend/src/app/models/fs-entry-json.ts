/**************************************************************************

   File:       fs-entry-json.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date         Author                Reason
   30.11.2023   Sebastian Pitica      Basic structure
   01.12.2023   Sebastian Pitica      Added isInTrash and ownerId fields
   15.12.2023   Sebastian Pitica      Modified isInTrash field to be a number instead of a boolean
   16.12.2023   Sebastian Pitica      Added entryDescription field

 **************************************************************************/

export interface FsEntryJson {
  name:string,
  createdDate:Date,
  isFile:boolean,
  size:number,
  entryId:number,
  parentId:number,
  isInTrash:number, // 0 - false, 1 - true, -1 - permanent delete should be ignored by the frontend
  entryDescription:string
}
