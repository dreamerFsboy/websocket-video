import request from "../request";

export const adminLogin = (name:string,password:string,captcha:string,) => {
    return request('post','/login',{
        name,
        password,
        captcha,
    },'json')
}

export const systemLog = () => {
    return request('post','/log/findPage')
}
