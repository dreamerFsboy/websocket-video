<template>
  <audio ref="audioPlayer" autoplay :src="src"></audio>
  <frequencyIcon
  @click="ChangeMuted"
   :audio-box="audioBox"
   v-if="Frequency"
  />
  <waveIconA
  @click="ChangeMuted"
   :audio-box="audioBox2"
   v-if="TimeDomain"
  />  
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { AudioWave } from "@/methods/AudioStream";
import frequencyIcon from "./frequencyIconA.vue";
import waveIconA from './waveIconA.vue'
const audioPlayer = ref();
const audioBox = reactive(new Array(16).fill(0));
const audioBox2 = reactive(new Array(32).fill(16));
const props = defineProps({
  src: String,
  audioContext: AudioContext,
  Frequency: {
    type: Boolean,
    default: () => true,
  },
  TimeDomain: {
    type: Boolean,
    default: () => false,
  },
  noVoice: {
    type: Boolean,
    default: () => false,
  },
});
onMounted(() => {
  audioPlayer.value.muted = props.noVoice;
  props.audioContext &&
    AudioWave(
      audioPlayer,
      props.audioContext,
      props.Frequency ? audioBox : null,
      props.TimeDomain ? audioBox2 : undefined
    );
});
const ChangeMuted = () => {
  audioPlayer.value.muted = !audioPlayer.value.muted
}
</script>
