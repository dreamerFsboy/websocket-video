export interface UserMedia {
    name: string,
    video: string,
    audio: string,
    avatar: string,
}

export interface User {
    email: string,
    nickname?: string,
    avatar?: string,
}