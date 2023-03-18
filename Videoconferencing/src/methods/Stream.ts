export const readAsVideoRPCControl = (
    control: number,
    room: number,
    sender: number,
    reciver: number,
) => {
    const result = new ArrayBuffer(20)
    const TypeArr = new Uint8Array(result)
    ReadAsUIntBuffer(control, TypeArr)
    ReadAsUIntBuffer(room, TypeArr, 4)
    ReadAsUIntBuffer(sender, TypeArr, 8)
    ReadAsUIntBuffer(reciver, TypeArr, 12)
    return new Blob([result])
}
export const readAsVideoRPCStream = (
        control: number,
        room: number,
        sender: number,
        reciver: number,
        data: Blob,
        mediaSign: number
    ) => {
    const length = data.size
    const result = new ArrayBuffer(21)
    const controlNum = new Uint8Array(result)
    if (mediaSign) {
        controlNum[20] = mediaSign
    }
    ReadAsUIntBuffer(control, controlNum)
    ReadAsUIntBuffer(room, controlNum, 4)
    ReadAsUIntBuffer(sender,controlNum,8)
    ReadAsUIntBuffer(reciver, controlNum, 12)
    ReadAsUIntBuffer(length, controlNum, 16)
    return new Blob([result, data])
}

export const readAsVideoRPCMessage = (
    control: number,
    room: number,
    reciver: number,
    data: Blob,
) => {
const length = data.size
const result = new ArrayBuffer(20)
const controlNum = new Uint8Array(result)

ReadAsUIntBuffer(control, controlNum)
ReadAsUIntBuffer(room, controlNum, 4)
ReadAsUIntBuffer(reciver, controlNum, 12)
ReadAsUIntBuffer(length, controlNum, 16)
return new Blob([result, data])
}


export const ReadAsUIntBuffer = (num: number, uintArr: Uint8Array, p:number = 0) => {
    if (num===0)return
    for (let i = 0; i < 4; i++) {
        uintArr[i + p] = (num >>> ((3 - i) * 8)) % (256)
    }
}