
const headone = 4
const heads = ['ctl', 'room', 'send', 'reciver', 'size']
export default (data: ArrayBuffer) => {
    const length = data.byteLength
    const result: HeaderObject = { 
        data: null,
        ctl: 0,
        room: 0,
        send: 0,
        reciver: 0,
        size: 0,
    }
    heads.forEach((element, i) => {
        result[element as 'ctl'|'room'|'send'|'reciver'| 'size'] = new DataView(data.slice(i * headone, i * headone + headone)).getInt32(0)
    })
    result.data = data.slice(20)
    return result
}

interface HeaderObject {
    data: ArrayBuffer | null,
    ctl: number,
    room: number,
    send: number,
    reciver: number,
    size: number,
}