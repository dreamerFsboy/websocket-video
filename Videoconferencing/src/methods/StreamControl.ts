import {
    readAsVideoRPCControl,
    readAsVideoRPCStream,
    readAsVideoRPCMessage,
} from './Stream'
import { ControlHead } from './StreamHeads'

export const createRoom = () => {
    return readAsVideoRPCControl(ControlHead.createroom,0,0,0)
}
export const joinAsk = (roomId: number) => {
    return readAsVideoRPCControl(ControlHead.joinAsk,roomId,0,0)
}
export const joinArgeeOne = (roomId: number,senderId: number) => {
    return readAsVideoRPCControl(ControlHead.joinArgee,roomId,senderId,0)
}
export const joinArgeeAll = (roomId: number,senderId: number) => {
    return readAsVideoRPCControl(ControlHead.joinArgee,roomId,senderId,0)
}
export const joinRefuse = (rommId: number,senderId: number,) => {
    return readAsVideoRPCControl(ControlHead.joinRefuse,rommId,senderId,0)
}
export const changeSource = (roomId: number,senderId:number) => {
    return readAsVideoRPCControl(ControlHead.changeSource,roomId,senderId,0)
}
export const getInformation = (roomId: number,senderId:number) => {
    return readAsVideoRPCControl(ControlHead.getInformation,roomId,senderId,0)
}
export const kick = (roomId:number,senderId: number,reciver:number) => {
    return readAsVideoRPCControl(ControlHead.kick,roomId,senderId,reciver)
}
export const videoStream = (roomId: number,sender:number,data: Blob,mediaSign: number) => {
    return readAsVideoRPCStream(ControlHead.videoStream,roomId,sender,0,data,mediaSign)
}
export const audioStream = (roomId: number,sender:number,data: Blob,mediaSign: number) => {
    return readAsVideoRPCStream(ControlHead.audioStream,roomId,sender,0,data,mediaSign)
}
export const message = (roomId: number,reciverId: number,message: Blob) => {
    return readAsVideoRPCMessage(ControlHead.message,roomId,reciverId,message)
}
