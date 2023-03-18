<template>
    <div class="video-container">
        <video ref="videoPlayer" :srcObject="store.videoStream" autoplay></video>
        <!-- <audio ref="audioplayer" :srcObject="store.audioStream" autoplay></audio> -->
        <div
        class="video-tip"
        >
            <FrequencyIconA :audio-box="audioBox"/>
            <span>{{ store.user?.nickname||store.user?.email }}</span>
            <!-- <audio ref="audioplayer" :srcObject="store.audioStream" autoplay></audio> -->
        </div>
        <div class="video-avatar" v-show="!store.videoStream">
            <img :src="store.user?.avatar||'/default.svg'" />
        </div>
    </div>
</template>

<script setup lang="ts">
import { onUnmounted, reactive, ref, watch } from 'vue';
// import { VideoImformation } from '@/types/user';
import FrequencyIconA from './frequencyIconA.vue';
import usestore from '@/store';
// import { AudioWave } from '@/methods/AudioStream';
// const audioplayer = ref()
const store = usestore()
const videoPlayer = ref()
const audioBox = reactive(new Array(16).fill(0));
let showNum = 0
watch(store,() => {
    // console.log(store)
    if ((!!store.audioContext)&&(!!store.audioStream)) {
        // console.log(audioplayer.value)
        // AudioWave(
        //     audioplayer,
        //     store.audioContext,
        //     audioBox
        // )
        const ff = new Uint8Array(16)
        const source = store.audioContext.createMediaStreamSource(store.audioStream)
        // console.log(store.audioStream.active)
        // source.connect(store.audioContext.destination)
        const analyser = store.audioContext.createAnalyser()
        // const result = store.audioContext.createMediaStreamDestination()
        const result = store.audioContext.createMediaStreamDestination()
        analyser.fftSize = 32;
        source.connect(analyser)
        analyser.connect(result)
        showNum = setInterval(() => {
            analyser.getByteFrequencyData(ff);
            ff.forEach((v,i)=>{
                audioBox[i] = v/16
            })
        }, 100);
        // AudioWave(audioplayer,store.audioContext,audioBox)
    }else {
        clearInterval(showNum)
        audioBox.forEach((v,i)=>audioBox[i]=0)
    }
})

onUnmounted(() => {
    clearInterval(showNum)
})
</script>

<style>
.video-container{
    position: relative;
    overflow: hidden;
}
.video-container>video{
    width: 100%;
    height: 100%;
    display: block;
    background-color: #444;
}
.video-tip{
    position: absolute;
    bottom: 0px;
    z-index: 10;
    background-color: #000;
    opacity: .7;
    display: flex;
    justify-content: start;
    width: 100%;
    height: 60px;
    align-items: center;
    color: #fff;
    font-size: 18px;
    font-weight: 800;
}
.video-tip>*{
    margin: 0 10px;
}
.video-avatar{
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    /* border-radius: 50%; */
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
/* 
    box-sizing: border-box; */
    height: 100px;
    width: 100px;
/* 
    background-color: #bbb;
    border: 2px solid #999; */
}
.video-avatar>img{
    /* display: block; */
    /* max-width: 100%;
    max-height: 100%; */
    /* transform: translate(-2px); */
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    border-radius: 100px;
    background-color: #e6e6e6;
    border:  2px solid #bbb;
    box-sizing: border-box;
    overflow: hidden;
}
</style>