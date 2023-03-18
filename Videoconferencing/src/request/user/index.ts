import request from "../request";

export const getUserInfo = () => {
    return request('post','/user/getMyInformation')
}

export const SavePic = (imgData:Blob,filename:string) => {
    return request('post','/picture/avator/upload?filename='+filename,imgData,'file')
}