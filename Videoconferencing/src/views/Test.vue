<template>
    <div>
        <VideoArea :audio-context="audioContext" 
        src-video="p6988424555630956551-106-170504.mp4"
        src-audio="2375606811.mp3"
        name="薛江源"
        ></VideoArea>
    </div>
</template>

<script setup lang="ts">
import VideoArea from '@/components/videoArea.vue';
import usestore from '@/store';
const store = usestore()
const audioContext = store.audioContext||new AudioContext()

</script>