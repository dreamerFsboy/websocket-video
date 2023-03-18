<template>
    <div class="video-container">
        <video ref="videoPlayer" :src="srcVideo" autoplay controls></video>
        <div class="video-tip">
            <audioIcon
              :src="srcAudio"
              :audio-context="audioContext"
              :no-voice="muted"
              v-if="srcAudio"
            />
            <FrequencyIconA
              @click="ChangeMuted"
              :audio-box="audioBox"
              v-else/>
            <span>{{ name }}</span>
        </div>
        <!-- <div class="video-avatar" v-show="!videoPlayer||videoPlayer.paused">
            <img :src="avatar" />
        </div> -->
    </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { AudioWave } from "@/methods/AudioStream";
// import { VideoImformation } from '@/types/user';
import audioIcon from './audioIcon.vue';
import FrequencyIconA from './frequencyIconA.vue';
const videoPlayer = ref()
const audioBox = reactive(new Array(16).fill(0));
const muted = ref(false)
const ChangeMuted = () => {
    muted.value = !muted.value
    videoPlayer.value.muted = muted.value
}

const props=defineProps({
    audioContext:AudioContext,
    srcVideo: String,
    srcAudio: String,
    name: String,
    avatar: {
        type: String,
        default: () => '/default.svg'
    }

})

onMounted(() => {
    if(!props.srcAudio){
        AudioWave(
            videoPlayer,
            props.audioContext,
            audioBox)
    }else {
        videoPlayer.value.muted = true
    }
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
    /* opacity: .7; */
    opacity: 0;
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
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;

    box-sizing: border-box;
    height: 100px;
    width: 100px;

    background-color: #bbb;
    border: 2px solid #999;
}
.video-avatar>img{
    display: block;
    max-width: 100%;
    max-height: 100%;
    transform: translate(-2px);
}
</style>