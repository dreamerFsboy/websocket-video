import request from "../request";

export const logout = () => {
    return request('get','/logout')
}

export const regist = (email:string,password: string) => {
    return request('post','/regist',{
        email,
        password
    },'json')
}

export const login = (email:string,password:string,captcha:string,) => {
    return request('post','/user/login',{
        email,
        password,
        captcha,
    },'json')
}

export const active = (token: string) => {
    return request('get','/user/activate',{token})
}