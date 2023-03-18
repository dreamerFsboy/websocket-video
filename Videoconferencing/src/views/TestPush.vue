<template>
    <input v-model="routerUrl" />
    <button @click="router.push(routerUrl)">前往</button>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter()
const routerUrl = ref('')
</script>