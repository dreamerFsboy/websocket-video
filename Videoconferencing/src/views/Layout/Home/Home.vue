<template>
    <main class="main">
    <section>
      <div class="grid">
        <div class="in-box">
          <h1>随时随地使用视频会议</h1>
          <div>致力于高可用低延时的在线流媒体解决方案</div>
          <div class="in-meeting" v-if="store.user">
            <input placeholder="输入会议号" v-model="meetId" />
            <!-- <button @click="(store.SetAudioContext(),router.push('/meeting/'+meetId))">进入会议</button> -->
            <details role="list">
              <summary @click="store.buildConnection()" aria-haspopup="listbox" role="button">
                开始会议
              </summary>
              <ul role="listbox">
                <li><a @click="Join">加入会议</a></li>
                <li><a @click="creatMeeting">新建会议</a></li>
              </ul>
            </details>
          </div>
          <div class="in-login" v-else>
            <button @click="router.push('/login')">登录</button>
            <button class="secondary" @click="router.push('/register')">
              注册
            </button>
          </div>
          <small>了解更多资讯<a>WuHu Meeting</a></small>
        </div>
        <div class="banner-img">
          <img src="file(6)(1).png" />
          <img
            style="animation: show 20s linear infinite alternate"
            src="file(5)(1).png"
          />
        </div>
      </div>
    </section>
  </main>
  <dialog :[messageOpen]="''">
    <article>
      <header><b>{{ messageObj.title }}</b></header>
      <main :aria-busy="waiting">
        {{ messageObj.context }}
      </main>
    </article>
  </dialog>
</template>

<script setup lang="ts">
import { reactive, shallowRef, watch } from "vue";
import usestore from "@/store";
import { useRouter } from "vue-router";
import { KMessage } from "@/methods/KMessage";
import { createRoom, joinAsk } from "@/methods/StreamControl";
const router = useRouter();
const store = usestore();

const meetId = shallowRef('')

const messageOpen = shallowRef('')
const waiting = shallowRef(true)
const messageObj = reactive({
  title: '' as string,
  context: '' as string
})

const Join = () => {
  store.SetAudioContext()
  if (meetId.value!=='') {
    // router.push('/meeting/'+meetId.value)
    store.Send(joinAsk(meetId.value as any - 0))
    messageOpen.value = 'open'
    messageObj.title = '进入会议'
    messageObj.context = '正在进入会议'
  } else {
    KMessage('请输入您的会议号！','warning')
  }
}

const creatMeeting = () => {
  store.buildConnection()
  store.Send(createRoom())
  messageOpen.value = 'open'
  messageObj.title = '创建会议'
  messageObj.context = '正在创建会议'
}

watch(store,() => {
  if (store.mineRoom) {
    router.push('/meeting/'+store.mineRoom)
  }
  if (store.homeErr) {
    messageOpen.value = 'open'
    waiting.value=false
    messageObj.title = store.homeErr.title
    messageObj.context = store.homeErr.context
    store.homeErr.push&&router.push(store.homeErr.push)
  }
})
</script>

<style>
.in-box {
  padding: 150px 50px;
  display: flex;
  flex-direction: column;
}
.in-box h1 {
  margin-bottom: 15px;
}
.in-box > * {
  margin-bottom: 30px;
}
.in-meeting {
  display: flex;
}
.in-meeting > * {
  max-width: 350px;
}
.in-meeting > *:nth-child(2) {
  margin-bottom: var(--spacing);
  width: 200px;
  margin-left: 20px;
}
.in-login {
  display: flex;
}
.in-login > button {
  margin-right: 20px;
  margin-bottom: var(--spacing);
  width: 120px;
}
.banner-img {
  position: relative;
  /* height: 600px; */
}
.banner-img > img {
  position: absolute;
  height: 90%;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}
@media screen and (min-width: 1400px) {
  .main {
    padding: 0 calc(50vw - 700px);
  }
}
@media screen and (max-width: 650px) {
  .nav-left {
    display: none;
  }
  .nav-right {
    width: 100%;
    justify-content: space-between;
  }
  .email {
    display: none;
  }
  .main {
    margin: 0;
  }
}
@keyframes show {
  0% {
    opacity: 0;
  }
  40% {
    opacity: 0;
  }
  60% {
    opacity: 1;
  }
  100% {
    opacity: 1;
  }
}

</style>