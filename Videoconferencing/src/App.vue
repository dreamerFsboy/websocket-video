<template>
  <RouterView />
</template>

<script setup lang="ts">
import usestore from '@/store';
const store = usestore()
store.SupportCheck()
store.SetUserAuto().then(res=>{
  // store.buildConnection()
})
store.getDevices()
</script>

