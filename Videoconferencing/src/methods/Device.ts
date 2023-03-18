export const getDevices = () => {
    return navigator.mediaDevices.enumerateDevices().then(Devices=>{
        const result:InputDevice = {
            audio:false,
            video: [],
        }
        Devices.forEach(res=>{
            if (res.kind==='audioinput')  (result.audio = true)
            else if (res.kind==='videoinput') {
                result.video.push(res)
                // res.deviceId
            }
        })
        return result
    })
}

export interface InputDevice {
    audio: boolean,
    video: InputDeviceInfo[]
}