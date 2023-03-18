export class Recorder {
    sourceId:number = -1
    recorder:MediaRecorder|null=null
    constructor() {

    }

    start(stream:MediaStream,sourceId: number) {
        this.recorder = new MediaRecorder(stream,{})
    }

}