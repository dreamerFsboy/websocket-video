import {getDevices, InputDevice} from "@/methods/Device";
import { KMessage } from "@/methods/KMessage";
import { audioStream, videoStream } from "@/methods/StreamControl";
import { ControlHead } from "@/methods/StreamHeads";
import StreamMessage from "@/methods/StreamMessage";
import { getUserInfo } from "@/request/user";
import { ReBuildMap, SourceMap } from "@/types/ReBuildMap";
import { User } from "@/types/userMedia";
import { defineStore } from "pinia";


const lsSaveKey = "fhiewnovmweijvj"
const support = [
    'video/webm;codecs=vp9',
    'audio/webm;codecs=opus'
]

const placeKey = ['main','view1','view2','view3']

export default defineStore('global',{
    state() {
        return {
            // user: {email:'920868587@qq.com'} as null|User,
            user: null as null|User,
            // 作为音频数据检测使用，装饰作用
            audioContext: null as null|AudioContext,
            // 用户音视频流导入
            audioStream: null as null|MediaStream,
            videoStream: null as null|MediaStream,
            recorderInterval: 0 as number,
            AudioRecorder: null as null|MediaRecorder,
            VideoRecorder: null as null|MediaRecorder,
            // 用户设备
            inputDevices: null as null|InputDevice,
            // 房间和预备入房客户
            roomMate: new ReBuildMap(),
            roomMateStream: new SourceMap(),
            
            PrepareMate: [] as { email: string, nickname?: string, avatar?: string}[],
            // ws连接
            wsConnection: null as null|WebSocket,

            //
            mineId: 0 as number,
            mineRoom: 0 as number,
            sourceId: -1 as number,
            oldSourceId: -1 as number,
            owner: false as boolean,

            place: [0,0,0,0] as [number,number,number,number],


            textDecoder: new TextDecoder('utf-8'),

            homeErr: null as {title: string,context: string,push?: string}|null
        }
    },
    getters:{
        support: () :boolean => {
            const tmp = JSON.parse(localStorage.getItem(lsSaveKey)||'false')
            if (!tmp) {
                const arr = [MediaRecorder,MediaSource]
                let result = true
                for(const key in arr) {
                    for(const i in support){
                        console.log(support[i]+arr[key]?.isTypeSupported(support[i]))
                        result&&=arr[key]?.isTypeSupported(support[i])
                    }
                }
                localStorage.setItem(lsSaveKey,result.toString())
                return result
            }
            return tmp
        }
    },
    actions: {
        SupportCheck() {
            const result = this.support
            if (!result) {
                KMessage('您的浏览器不支持本产品的功能，请更换高版本的谷歌或者火狐浏览器来体验完整功能',
                'warning',5000
                )
            }
        },
        SetAudioContext() {
            if (!this.audioContext) this.audioContext = new AudioContext()
        },
        SetUser(email:string) {
            this.user = {
                email
            }
        },
        SetUserAuto() {
            return getUserInfo().then((res:any)=> {
                this.user = {
                    email: res['email'],
                    avatar: res['picture']||null
                }
                this.user.avatar&&(this.user.avatar='/api/'+this.user.avatar)
            })
        },
        getDevices() {
            getDevices().then((res)=> {
                this.inputDevices=res as unknown as InputDevice
            })
        },
        closeVideoStream() {
            this.videoStream?.getTracks().forEach(res=>{
                res.stop()
            })
            this.videoStream=null
        },
        openRecorder(type: 'audio'|'video') {
            if (type === 'audio') {
                if (this.audioStream) {
                    this.AudioRecorder = new MediaRecorder(this.audioStream,{
                        mimeType: support[1]
                    })
                    this.AudioRecorder.ondataavailable = (e) => {
                        console.log(e.data)
                        this.Send(
                            audioStream(
                                this.mineRoom,
                                this.mineId,
                                e.data,
                                this.sourceId
                            )
                        )
                    }
                    this.AudioRecorder.start()
                }
            } else if (type === 'video') {
                if (this.videoStream) {
                    this.VideoRecorder = new MediaRecorder(this.videoStream,{
                        mimeType: support[0]
                    })
                    this.VideoRecorder.ondataavailable = (e) => {
                        console.log(e.data)
                        this.Send(
                            videoStream(
                                this.mineRoom,
                                this.mineId,
                                e.data,
                                this.sourceId
                            )
                        )
                    }
                    this.VideoRecorder.start()
                }
            }
        },
        closeRecorder(type: 'audio'|'video') {
            switch (type) {
                case 'audio':
                    if (this.AudioRecorder?.state === 'recording') {
                        this.AudioRecorder.stop()
                    }
                    this.AudioRecorder = null
                break
                case 'video':
                    if (this.VideoRecorder?.state === 'recording') {
                        this.VideoRecorder.stop()
                    }
                    this.VideoRecorder = null
                break
            }
        },
        RecorderInterval() {
            if (this.AudioRecorder){
                console.log(this.AudioRecorder.state)
                this.AudioRecorder.state==='recording'&&this.AudioRecorder.requestData()
            }
            if (this.VideoRecorder) {
                this.VideoRecorder.requestData()
                console.log(this.wsConnection?.readyState)
            }
        },
        openInterval() {
            this.recorderInterval = setInterval(this.RecorderInterval,100)
        },
        closeInterval() {
            clearInterval(this.recorderInterval)
        },
        buildConnection() {
            if (this.wsConnection) return this.wsConnection
            const token =  document.cookie.split(';').find(v=>v.indexOf('satoken=')!==-1)?.replace('satoken=','').replace(' ','')
            console.log(token)
            if (token) {
                this.wsConnection = new WebSocket('wss://videotest.lgh/websocket/'+token)
                // this.wsConnection = new WebSocket('wss://videotest.lgh:443/websocket')
                this.wsConnection.binaryType = 'arraybuffer'
                this.wsConnection.onmessage = (e) => {
                    const result = StreamMessage(e.data)
                    console.log(result)
                    switch(result.ctl) {
                        case ControlHead.createroom:
                            this.mineRoom = result.room
                            this.mineId = result.reciver
                            this.owner = true
                            console.log(this.mineRoom+' '+this.mineId)
                        break;
                        case ControlHead.joinAsk: 
                            {let source
                            result.data&&(source = this.textDecoder.decode(result.data))
                            if (source) {
                                const data = JSON.parse(source)
                                data.avatar&&(data.avatar='/api/'+data.avatar)
                                this.PrepareMate.push(data as {
                                    email:string,
                                    nickname?: string,
                                    avatar?:string,
                                })
                                console.log(data)
                            }}
                        break;
                        case ControlHead.joinArgee: 
                            this.mineRoom = result.room
                            this.mineId = result.reciver
                            this.homeErr = {
                                title: '进入会议',
                                context: '请求已通过，正在进入会议',
                                push: '/meeting/' + result.room
                            }
                        break;
                        case ControlHead.joinUnExist:
                            this.homeErr = {
                                title: '进入会议',
                                context: '会议不存在',
                            }
                        break;
                        case ControlHead.joinRefuse:
                            this.homeErr = {
                                title: '进入会议',
                                context: '会议主持人拒绝了你的请求',
                            }
                        break;
                        case ControlHead.getInformation:
                            {let source
                            result.data&&(source = this.textDecoder.decode(result.data))
                            if (source) {
                                const data:{email:string,roomid:number,owner:boolean,nickname:string,num:number,avatar?:string}[] = JSON.parse(source)
                                // this.roomMate.values.forEach(v=>v.active=false)
                                console.log(data)
                                const activeMate:number[] = []
                                const unactiveMate:number[] = []
                                data.forEach(v=>{
                                    activeMate.push(v.num)
                                    v.avatar&&(v.avatar='/api/'+v.avatar)
                                    this.roomMate.set(v.num,v)
                                })
                                this.roomMate.keys.forEach(v=>{
                                    if (!activeMate.includes(v)) unactiveMate.push(v)
                                })
                                unactiveMate.forEach(v=>{
                                    this.roomMate.delete(v)
                                })
                                // if (window.innerWidth>700) {
                                this.place.forEach((v,i)=>{
                                    const mate = this.roomMate.get(v)
                                    if (mate) mate.place = placeKey[i] as 'main'|'view1'|'view2'|'view3'
                                    else this.place[i] = 0
                                })
                                const arr = this.roomMate.values
                                this.place.forEach((v,j)=>{
                                    if (v===0) {
                                        for(let i=0;i<arr.length;i++) {
                                            if(!arr[i].place){
                                                arr[i].place=placeKey[j] as 'main'|'view1'|'view2'|'view3',
                                                this.place[j] = arr[i].num
                                                break                                           
                                            }
                                        }
                                    }
                                })
                                console.log(this.place)
                                // }
                            }}
                        break;
                        case ControlHead.kick:
                            console.log('you have been kicked')
                            this.wsConnection?.close()
                            this.wsConnection = null
                            this.roomMate = new ReBuildMap()
                            this.PrepareMate =  [] as { email: string, nickname?: string, avatar?: string}[]
                            this.mineId = 0
                            this.mineRoom = 0
                            this.sourceId = 0
                        break;
                        case ControlHead.changeSource:
                            if (result.data) {
                                const oldSource = this.sourceId
                                this.sourceId = new DataView(result.data).getUint8(0) 
                                if (oldSource===this.sourceId) {
                                    this.sourceId++
                                }
                                this.closeRecorder('audio')
                                this.closeRecorder('video')
                                this.roomMateStream.values.forEach(v=>{
                                    v.audio.clearTmp()
                                    v.video.clearTmp()
                                })
                                this.openRecorder('audio')
                                this.openRecorder('video')
                                console.log(this.sourceId)
                            }
                        break;
                        case ControlHead.audioStream:
                            if (result.data) {
                                if (new DataView(result.data.slice(0,1)).getUint8(0)===this.sourceId) {
                                    const audio = this.roomMateStream.get(result.send).audio
                                    audio.append(result.data.slice(1))
                                }
                            } 
                        break;
                        case ControlHead.videoStream:
                            if (result.data) {
                                if (new DataView(result.data.slice(0,1)).getUint8(0)===this.sourceId) {
                                    const video = this.roomMateStream.get(result.send).video
                                    video.append(result.data.slice(1))
                                }
                            }
                        break;
                    }
                }
                return this.wsConnection
            } else {
                KMessage('您未登录，请登录！','danger')
                return false
            }
        },
        Send(data:Blob) {
            // console.log(data)
            if (this.wsConnection!=null) {
                // console.log(1)
              if (this.wsConnection.readyState === this.wsConnection.CONNECTING) {
                // console.log(2)
                this.wsConnection.onopen = () => {
                    console.log(this.wsConnection)
                    this.wsConnection?.send(data)
                }
              } else {
                this.wsConnection.send(data)
              }
            }
        }
    }
})