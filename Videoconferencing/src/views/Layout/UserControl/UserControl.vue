<template>
    <main class="main">
        <section>
            <form>
                <div class="img-box">
                    <img :src="avatarUrl" />
                </div>
                <label>
                    <input ref="avatar" type="file" />
                </label>
                <button @click="Submit" type="button">提交修改</button>
            </form>
        </section>
    </main>
</template>

<script setup lang="ts">
import usestore from '@/store';
import { computed, ref } from 'vue';
import {SavePic} from '@/request/user'
const store = usestore()

const avatar = ref()
const avatarUrl = computed(() => {
    let defaulturl = store.user?.avatar
    if (defaulturl) {
        return avatar.value?.files[0] || defaulturl
    }
    return avatar.value?.files[0] || '/default.svg'
})

const Submit = () => {
    console.log(avatar.value.files)
    if (avatar.value.files.length) {
        SavePic(avatar.value.files[0],Date.now()+avatar.value.files[0].name)
    }
}

</script>

<style>
.img-box{
    background-color: #aaa;
}
</style>