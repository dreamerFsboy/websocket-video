<template>
  <div class="audio-box">
    <div v-for="i in 15" 
      :style="{
        height:1+audioBox[16-i]+'px',
        backgroundColor: `rgb(${256-i*8},${128-i*8},${256-i*4})`,
        opacity: ((16-i)&1)?1:0.5,
      }"></div>
    <div v-for="i in 16" 
      :style="{
        height:1+audioBox[i-1]+'px',
        backgroundColor:`rgb(${i*8+126},${i*8},${i*4+180})`,
        opacity: ((i-1)&1)?1:0.5,
      }"></div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps(['audioBox'])
</script>

<style>
.audio-box {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 36px;
  width: 36px;
  border-radius: 25%;
  padding: 2px;
  background-color: #fff;
}
.audio-box > div {
  width: 1px;
  height: 1px;
  /* border-radius: 2px; */
  background-color: #aaa;
  transition: all 0.01s;
}
</style>