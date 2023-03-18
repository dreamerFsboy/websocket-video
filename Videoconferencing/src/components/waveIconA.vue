<template>
  <div class="audio-box2">
    <div
      v-for="i in 32"
      :style="{
        top: audioBox[i - 1] + 'px',
        left: 1 + i + 'px',
        backgroundColor: `rgb(${i * 8 + 126},${256 - i * 16},${i * 4 + 180})`,
        opacity: (i - 1) & 1 ? 1 : 0.9,
      }"
    ></div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps(["audioBox"]);
</script>

<style>
.audio-box2 {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: end;
  height: 36px;
  width: 36px;
  border-radius: 25%;
  padding: 2px;
  background-color: #000;
}
.audio-box2 > div {
  position: absolute;
  width: 1px;
  height: 1px;
  /* border-radius: 2px; */
  background-color: #aaa;
  /* border: 1px solid transparent;
      border-top: #aaa; */
  transition: all 0.01s;
}
</style>
