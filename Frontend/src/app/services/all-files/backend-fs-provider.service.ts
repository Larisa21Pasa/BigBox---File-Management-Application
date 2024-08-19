/**************************************************************************

 File:        backend-fs-provider.service.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 01.12.2023  Sebastian Pitica      Created basic structure for the service, added methods for all the endpoints available at the moment
 14.12.2023  Sebastian Pitica      Added methods for getting current user
 16.12.2023  Sebastian Pitica      Added methods for getting the recent files and folders for the current user, and minor refactoring
 16.12.2023  Matei Rares           Added search method
 **************************************************************************/


import {Injectable} from "@angular/core";
import {RequesterService} from "../requester/requester.service";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {AuthService} from "../auth_service/auth.service";
import {MyAccountService} from "../my_account_service/my-account.service";

@Injectable({
  providedIn: 'root'
})
export class BackendFSProvider {

  fsBackendUrl = "http://localhost:8080/api/file_manager/entries";
  detailsUrl = "http://localhost:8080/api/file_manager/details";


  headers=new HttpHeaders({
      'Authorization':`Bearer ${localStorage.getItem('access_token')}`,
      'Access-Control-Allow-Origin': 'http://localhost:4200',
    })
  constructor(private http:HttpClient, private accountService: MyAccountService, private requester: RequesterService, private authService: AuthService) {
    localStorage.getItem('refresh_token')
  }


  getCurrentUser() {
    let email = localStorage.getItem("email");
    return this.accountService.GetUser(email);
  }

  getRootFsEntryId(userId: number) {

    return this.requester.get(this.fsBackendUrl+`/rootEntry/userId/${userId}`,this.headers);
  }

  getFsEntries(parentId: number) {
    return this.requester.get(this.fsBackendUrl+`/parentId/${parentId}`,this.headers );
  }

  getTrashFsEntries(userId: number) {
    return this.requester.get(this.fsBackendUrl+`/trash/userId/${userId}`);
  }

  getRecentsFsEntries(userId: number) {
    return this.requester.get(this.fsBackendUrl+`/recents/userId/${userId}`,this.headers);
  }

  downloadEntry(entryId: number) {
    return this.requester.get(this.fsBackendUrl+`/download/${entryId}`);
  }

  deleteEntry(entryId: number) {
    return this.requester.delete(this.fsBackendUrl+`/delete/${entryId}`);
  }

  restoreEntry(entryId: number) {
    return this.requester.post(this.fsBackendUrl+`/restore/${entryId}`,null,this.headers);
  }

  updateEntryInfo(entryId: number, updateInfo: string) {
    return this.requester.post(this.fsBackendUrl+`/updateInfo/${entryId}`, updateInfo);
  }

  renameEntry(entryId: number, newName: string) {
    return this.requester.post(this.fsBackendUrl+`/rename/${entryId}`, newName);
  }

  moveEntry(entryId: number, newParentId: number) {
    return this.requester.post(this.fsBackendUrl+`/move/${entryId}/parentId/${newParentId}`,null,this.headers);
  }

  copyEntry(entryId: number, newParentId: number) {
    return this.requester.post(this.fsBackendUrl+`/copy/${entryId}/parentId/${newParentId}`, "");
  }

  createFolder(parentId: number, folderName: string) {
    return this.requester.post(this.fsBackendUrl+`/create/parentId/${parentId}`, folderName);
  }

  uploadFile(parentId: number, file: File) {
      const formData=new FormData()
    formData.append("file",file)
    return this.requester.post(this.fsBackendUrl+`/upload/parentId/${parentId}`, formData);
  }

  getAllEntries(userid:number){
    return this.requester.get(this.fsBackendUrl+`/userId/${userid}`,this.headers);
  }

  getUserStorage(userid:number){
    return this.requester.get(this.detailsUrl+`/size/userId/${userid}`,this.headers);
  }



  searchFiles(fileName:string,userid:number){
    return this.requester.get(this.fsBackendUrl+`/search/${fileName}/userId/${userid}`,this.headers);
  }

  getFileProps(id:number){
    return this.requester.get(this.fsBackendUrl+`/${id}/properties`,this.headers);
  }


}
