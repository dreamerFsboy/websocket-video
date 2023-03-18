export class ReBuildMap {
    keys:number[] = []
    values:{
        email: string,
        nickname?: string,
        avatar?:string,
        // active: boolean,
        num: number,
        roomid: number,
        owner:boolean,
        place?: null|'main'|'view1'|'view2'|'view3',
        // videoSource?: string,
        // audioSource?: string,
    }[] = []
    constructor(source? : [number,any][]) {
        if (source) {
            source.forEach(v=>{
                this.keys.push(v[0])
                this.values.push(v[1])
            })
            console.log(this)
        }
    }
    get(key: number) {
        return this.values[this.keys.indexOf(key)]
    }
    delete(key: number) {
        const index = this.keys.indexOf(key)
        if (index > -1) {
            this.keys.splice(index,index+1)
            this.values.splice(index,index+1)
        }
    }
    set(key: number,value: {
        email: string,
        nickname?: string,
        avatar?:string,
        // active: boolean,
        num: number,
        roomid: number,
        owner:boolean,
        // videoSource?: string,
        // audioSource?: string,
    }) {
        const index = this.keys.indexOf(key)
        if (index>-1) {
            return this.values[index] = value
        }
        this.keys.push(key)
        return this.values.push(value)
    }
}

export class SourceMap {
    keys: number[] =[]
    values: UserSource[] = []
    get(key: number) {
        let result = this.values[this.keys.indexOf(key)]
        if (!result) {
            result = {
                audio: new MateAudioSource(),
                video: new MateSource(),
            }
            this.set(key,result)
        }
        return result
    }
    delete(key: number) {
        const index = this.keys.indexOf(key)
        if (index > -1) {
            this.keys.splice(index,index+1)
            const tmp = this.values.splice(index,index+1)
            tmp.forEach(v=>{
                v.audio.unMount()
                v.video.unMount()
            })
        }
    }
    set(key: number,value: UserSource) {
        const index = this.keys.indexOf(key)
        if (index>-1) {
            return this.values[index] = value
        }
        this.keys.push(key)
        return this.values.push(value)
    }
}

interface UserSource {
    audio: MateAudioSource,
    video: MateSource,
}

export class MateSource {
    sourceBuffer: SourceBuffer | null = null
    Source: MediaSource = new MediaSource()
    TmpData: ArrayBuffer[] = []
    sourceUrl: string = ''
    constructor() {
        this.Source.onsourceopen = () => {
            if (!this.sourceBuffer) {
                this.sourceBuffer = this.Source.addSourceBuffer('video/webm;codecs=vp9')
                this.sourceBuffer.mode = 'sequence'
                this.sourceBuffer.onupdateend = () => {
                    if (this.sourceBuffer?.updating&&this.TmpData.length) {
                        console.log('in')
                        this.sourceBuffer.appendBuffer(this.TmpData.pop() as ArrayBuffer)
                    }
                }
            }
        }
        this.sourceUrl = URL.createObjectURL(this.Source)
    }
    append(data:ArrayBuffer) {
        console.log(this.Source.readyState + this.sourceBuffer)
        if (this.Source.readyState==='open'&&!this.sourceBuffer?.updating) {
            // console.log('in')
            this.sourceBuffer?.appendBuffer(data)
        } else {
            // console.log('wait')
            this.TmpData.unshift(data)
        }
    }
    forceRead() {
        if (this.TmpData.length&&this.Source.readyState==='open') {
            this.sourceBuffer?.appendBuffer(this.TmpData.pop() as ArrayBuffer)
            return true
        }
        return false
    }
    clearTmp() {
        this.TmpData = []
        if (this.sourceBuffer) {
            this.sourceBuffer.abort()
            this.Source.removeSourceBuffer(this.sourceBuffer)
            this.Source.endOfStream()
        }
        this.Source = new MediaSource()
        this.Source.onsourceopen = () => {
            if (!this.sourceBuffer) {
                this.sourceBuffer = this.Source.addSourceBuffer('video/webm;codecs=vp9')
                this.sourceBuffer.mode = 'sequence'
                this.sourceBuffer.onupdateend = () => {
                    if (this.sourceBuffer&&!this.sourceBuffer.updating&&this.TmpData.length) {
                        this.sourceBuffer.appendBuffer(this.TmpData.pop() as ArrayBuffer)
                    }
                }
            }
        }
        URL.revokeObjectURL(this.sourceUrl)
        this.sourceUrl = URL.createObjectURL(this.Source)
    }
    unMount() {
        if (this.sourceBuffer) {
            this.sourceBuffer.abort()
            this.Source.removeSourceBuffer(this.sourceBuffer)
            this.Source.endOfStream()
        }
        URL.revokeObjectURL(this.sourceUrl)
    }
}

export class MateAudioSource {
    sourceBuffer: SourceBuffer | null = null
    Source: MediaSource = new MediaSource()
    TmpData: ArrayBuffer[] = []
    sourceUrl: string = ''
    constructor() {
        this.Source.onsourceopen = () => {
            if (!this.sourceBuffer) {
                this.sourceBuffer = this.Source.addSourceBuffer('audio/webm;codecs=opus')
                this.sourceBuffer.mode = 'sequence'
                this.sourceBuffer.onupdateend = () => {
                    if (this.sourceBuffer?.updating&&this.TmpData.length) {
                        console.log('in')
                        this.sourceBuffer.appendBuffer(this.TmpData.pop() as ArrayBuffer)
                    }
                }
            }
        }
        this.sourceUrl = URL.createObjectURL(this.Source)
    }
    append(data:ArrayBuffer) {
        console.log(this.Source.readyState + this.sourceBuffer)
        if (this.Source.readyState==='open'&&!this.sourceBuffer?.updating) {
            // console.log('in')
            this.sourceBuffer?.appendBuffer(data)
        } else {
            // console.log('wait')
            this.TmpData.unshift(data)
        }
    }
    forceRead() {
        if (this.TmpData.length&&this.Source.readyState==='open') {
            this.sourceBuffer?.appendBuffer(this.TmpData.pop() as ArrayBuffer)
            return true
        }
        return false
    }
    clearTmp() {
        this.TmpData = []
        if (this.sourceBuffer) {
            this.sourceBuffer.abort()
            this.Source.removeSourceBuffer(this.sourceBuffer)
            this.Source.endOfStream()
        }
        this.Source = new MediaSource()
        this.Source.onsourceopen = () => {
            if (!this.sourceBuffer) {
                this.sourceBuffer = this.Source.addSourceBuffer('audio/webm;codecs=opus')
                this.sourceBuffer.mode = 'sequence'
                this.sourceBuffer.onupdateend = () => {
                    if (this.sourceBuffer&&!this.sourceBuffer.updating&&this.TmpData.length) {
                        this.sourceBuffer.appendBuffer(this.TmpData.pop() as ArrayBuffer)
                    }
                }
            }
        }
        URL.revokeObjectURL(this.sourceUrl)
        this.sourceUrl = URL.createObjectURL(this.Source)
    }
    unMount() {
        if (this.sourceBuffer) {
            this.sourceBuffer.abort()
            this.Source.removeSourceBuffer(this.sourceBuffer)
            this.Source.endOfStream()
        }
        URL.revokeObjectURL(this.sourceUrl)
    }
}