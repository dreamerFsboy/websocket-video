option<template>
  <div ref="videoList" id="video-list">
    <!-- <userVideoArea class="placemain" /> -->
    <component
      v-for="item in store.roomMate.values"
      :is="item.num === store.mineId? userVideoArea:VideoArea"
      :class="'place'+item.place"
      :srcVideo="item.num !== store.mineId&&store.roomMateStream.get(item.num).video.sourceUrl"
      :srcAudio="item.num !== store.mineId&&store.roomMateStream.get(item.num).audio.sourceUrl"
      :name="item.nickname||item.email"
      :avatar="item.avatar"
      :audioContext="store.audioContext"
    />
    
  </div>
  <div class="media-control-list">
    <details class="camera-button" role="list">
      <summary style="border-radius: 50%;position: static;" :style="{backgroundColor: videoOpen?'':'rgb(234,67,53)'}" aria-haspopup="listbox" role="button">  
        <VideoOpenIcon v-if="videoOpen" />
        <VideoCloseIcon v-else />
      </summary>
      <ul role="listbox">
        <li><a @click="ChangeCamera('')">关闭</a></li>
        <li v-for="(item ,index) in store.inputDevices?.video">
          <a @click="ChangeCamera(item.deviceId)">相机{{ index+1 }}</a>
        </li>
      </ul>
    </details>
    <!-- 音频开关 -->
    <button :style="{ backgroundColor: audioOpen?'':'rgb(234,67,53)'}" @click="OpenAudio">
      <AudioOpen v-if="audioOpen"/>
      <AudioClose v-else />
    </button>
    <!-- 会内成员列表 -->
    <button @click="OpenDialog(0)">
      <Members />
    </button>
    <!-- 会议聊天 -->
    <button @click="OpenDialog(2)">
      <ChatIcon />
    </button>
    <!-- 入会申请列表 -->
    <button @click="OpenDialog(1)" v-if="store.owner">
      <JoinAskIcon />
      <div class="prepare-num" v-show="store.PrepareMate.length" >{{ store.PrepareMate.length }}</div>
    </button>
    <!-- 挂断 -->
    <button style="background-color: rgb(234,67,53);" @click="Kick(store.mineId,store.user?.email)">
      <MeetShutIcon />
    </button>
  </div>
  <div @click="videoList.style.left = '0'" class="moble-page" style="left:0;">&#10094;</div>
  <div @click="videoList.style.left = '-100vw'" class="moble-page" style="right:0;">&#10095;</div>
  <Transition name="slide-fade">
    <div v-show="ControlDialogs[0]" class="hover-box">
      <label>
        <b>
          在会成员
        </b>
      </label>
      <input v-model="searchRoomMate" placeholder="搜索与会者" />
      <div class="flex-long-list">
        <div v-for="item in store.roomMate.values.filter((v)=>v.email?.includes(searchRoomMate)||v.nickname?.includes(searchRoomMate))" class="users-info-item">
          <div>
            <img :src="item.avatar&&item.avatar!=='' ? item['avatar'] : '/default.svg'" />
          </div>
          <div style="margin-right: auto;margin-left: 10px;">
            <small>{{ item['nickname'] || '未知用户' }}{{item.owner?' (主持人)':''}}</small>
            <small>{{ item['email'] || '未知账号' }}</small>
          </div>
          <button class="little-button"  @click="Kick(item.num,item.email)">
            <MeetShutIconB v-if="item.owner" />
            <KickIcon v-else />
          </button>
        </div>
      </div>
    </div>
  </Transition>
  <Transition name="slide-fade">
    <div v-show="ControlDialogs[1]" class="hover-box" v-if="store.owner">
      <label>
        <b>入会请求</b>
      </label>
      <input v-model="searchPrepare" placeholder="查询请求人" />
      <div class="flex-long-list">
        <div v-for="item in store.PrepareMate.filter((v)=>v.email?.includes(searchPrepare)||v.nickname?.includes(searchPrepare))" class="users-info-item">
          <div>
            <img :src="'avatar' in item? item['avatar'] as string : '/default.svg'" />
          </div>
          <div style="margin-right: auto;margin-left: 10px;">
            <small>{{ item['nickname'] || '未知用户' }}</small>
            <small>{{ item['email'] || '未知账号' }}</small>
          </div>
          <button class="little-button" @click="JoinAgree(item)">
            <JoinAgreeIcon />
          </button>
          <button class="little-button" @click="Refuse(item)">
            <JoinRefuseIcon />
          </button>
        </div>
      </div>
    </div>
  </Transition>
  <Transition name="slide-fade">
    <div v-show="ControlDialogs[2]" class="hover-box">
      <label>
        <b>会内聊天</b>
      </label>
      <p class="talk-area"></p>
      <div style="display: flex;">
        <select class="little-select">
          <option value="0">@所有人</option>
          <option v-for="(item,index) in store.roomMate.values" 
          :value="store.roomMate.keys[index]">@{{ item.nickname }}</option>
        </select>
        <input placeholder="说点什么？"/>
        <button class="little-button" style="border-radius: 10px; margin: 0 10px;height: 40px;min-width: 40px;">
          <SendMsgIcon />
        </button>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, reactive, ref, shallowRef, watch } from "vue";
import AudioOpen from "@/components/icons/audioOpen.vue";
import AudioClose from "@/components/icons/audioClose.vue"
import Members from "@/components/icons/members.vue"
import ChatIcon from "@/components/icons/chat.vue"
import JoinAskIcon from "@/components/icons/joinAsk.vue"
import MeetShutIcon from "@/components/icons/meetingShut.vue"
import VideoOpenIcon from "@/components/icons/videoOpen.vue"
import VideoCloseIcon from "@/components/icons/videoClose.vue"
import MeetShutIconB from "@/components/icons/meetingShutB.vue"
import KickIcon from "@/components/icons/kick.vue"
import JoinAgreeIcon from "@/components/icons/joinAgree.vue"
import JoinRefuseIcon from "@/components/icons/joinRefuse.vue"
import SendMsgIcon from "@/components/icons/sendMessage.vue"
import usestore from "@/store";
import { useRoute, useRouter } from "vue-router";
import userVideoArea from "@/components/userVideoArea.vue";
import VideoArea from "@/components/videoArea.vue";
// import { joinAsk } from "@/methods/StreamControl";
import { debounce } from '@/methods/index'
import { joinArgeeOne,joinRefuse,kick } from "@/methods/StreamControl";
const router = useRouter();

const searchRoomMate = shallowRef('')
const searchPrepare = shallowRef('')

const route = useRoute()
const store = usestore();
// const meetId = route.params['meetId'] as any - 0

const ControlDialogs = reactive([false,false,false])

const OpenDialog = (num:number) => {
  if (ControlDialogs[num]) {
    ControlDialogs[num] = false
    return
  } else {
    ControlDialogs.forEach((v,i)=>{
      ControlDialogs[i]=false
    })
    ControlDialogs[num] = true
  }
}

const audioOpen = ref(false);
const videoOpen = ref(false)
const videoList = ref()
const ChangeCamera = (deviceId: string) => {
  store.closeVideoStream()
  store.closeRecorder('video')
  if (deviceId===''){
    videoOpen.value=false
    return
  }
  videoOpen.value=true
  const option = {
    deviceId
  }
  navigator.mediaDevices.getUserMedia({ video:option}).then((res)=> {
    store.videoStream = res
    store.openRecorder('video')
    console.log(store.wsConnection?.readyState)
  })
}
const OpenAudio = () => {
  audioOpen.value = !audioOpen.value
  if (audioOpen.value) {
    store.SetAudioContext()
    navigator.mediaDevices
      .getUserMedia({
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
        },
      })
      .then((res) => {
        store.audioStream = res;
        //   let audio = document.querySelector('audio')
        //   audio&&(audio.srcObject = res)
        if(store.sourceId) {
          store.openRecorder('audio')
        }
      });
  } else {
    store.closeRecorder('audio')
    store.audioStream?.getTracks().forEach((res) => {
      res.stop();
    });
    store.audioStream = null;
  }
};

// store.buildConnection()

const JoinAgree = (item:{email:string,nickname?:string,avatar?:string}) => {
  const index = store.PrepareMate.indexOf(item)
  store.PrepareMate.splice(index,index+1)
  store.Send(new Blob([joinArgeeOne(store.mineRoom,store.mineId),item.email]))
}

const Refuse = (item:{email:string,nickname?:string,avatar?:string}) =>{
  const index = store.PrepareMate.indexOf(item)
  store.PrepareMate.splice(index,index+1)
  store.Send(new Blob([joinRefuse(store.mineRoom,store.mineId),item.email]))
}

const Kick = (num:number,email:string|undefined) => {
  if (email) {
    if (num===store.mineId&&store.roomMate.get(num).owner) {
      num = 0
    }
    store.Send(new Blob([ kick(store.mineRoom,store.mineId,num), email ]))
  }
}



// watch(store,() => {
//   if (store.wsConnection&&store.audioStream&&store.sourceId&&store.roomMate.keys.values.length!==1) {
    
//   }
// })
store.openInterval()

window.onresize = debounce(() => {
  if (window.innerWidth>700) {
    videoList.value.style.left = ''
  }
},100)
</script>

<style>
.media-control-list{
  --button-size: 40px;
  --button-pad: 11px;
  background-color: #555;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  height: 56px;
  width: 100%;
  position: fixed;
  bottom: 0;
  z-index: 50;
}
/* .media-control-list>*{
  height: 56px;
  width: 56px;
  margin: 5px;
} */
.media-control-list>button {
  position: relative;
  height: var(--button-size);
  width: var(--button-size);
  margin: 5px;
  border-radius: 50%;
  background-color: #aaa;
  border: none;
  /* overflow: hidden; */

  display: flex;
  justify-content: center;
  align-items: center;
  padding: var(--button-pad);
}
.media-control-list>button>svg{
  width: 90%;
  height: 90%;
}
.camera-button{
  height: var(--button-size);
  width: var(--button-size);
  margin: 5px;
}
.camera-button>summary{
  position: static;
  padding: var(--button-pad);
  border: none;
  border-radius: 50%;
  width: var(--button-size);
  height: var(--button-size);
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #aaa;

}
/* .camera-button>summary>svg{
  
} */
.camera-button>summary::after{
  position: absolute;
  right: -7%;
  bottom: 8px;
}
.camera-button[open]>summary::after {
  transform: rotate(-90deg);
}
.camera-button>ul{
  top:none;
  bottom: var(--button-size);
  width: 100px;
}

#video-list {
  position: relative;
  background-color: #555;
  display:grid;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 56px;
  grid-template-areas:
    'main upper'
    'main view1'
    'main view2'
    'main view3'
    'main down'
    ;
  grid-template-rows: 1fr 250px 250px 250px 1fr;
  grid-template-columns: 1fr 350px ;
}
.moble-page{
  display: none;
  position: fixed;
}

.hover-box{
  position: fixed;
  background-color: #fff;
  right: 5px;
  bottom: 60px;
  border-radius: 10px;
  height: 90vh;
  width: 340px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.hover-box input {
  font-size: 16px;
  height: 40px;
  margin: 0;
}

.flex-long-list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.users-info-item {
  display: flex;
  height: 50px;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
}
.users-info-item>div:nth-child(1) {
  display: flex;
  justify-content: center;
  align-items: center;
  /* overflow: hidden; */
  height: 30px;
  width: 30px;
  /* border-radius: 50%; */
  /* border: 2px solid #999;
  background-color: #aaa; */
}
.users-info-item small {
  font-size: 12px;
  display: block;
}
.users-info-item img {
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
.little-button {
  background-color: #fff;
  overflow: hidden;
  border-radius: 50%;
  border: none;
  margin: 0;
  padding: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 36px;
  width: 36px;
}
.little-button:hover {
  background-color: #eee;
}

.talk-area {
  overflow-y: auto;
  word-wrap: break-word;
  white-space: pre-wrap;
  margin: 10px 0;
  flex: 1;
  border: 1px solid #aaa;
  border-radius: 5px;
  font-size: small;
}

.little-select{
  height: 40px;
  margin: 0;
  margin-right: 10px;
  padding: 0 10px;
  width: 115px;
}
.prepare-num{
  position: absolute;
  right: 0;
  top: 0;
  width: 18px;
  height: 18px;
  display: flex;
  justify-content: center;
  align-items: center;
  transform: translate(25%,-25%);
  background-color: #f50;
  font-size: 12px;
  border-radius: 9999px;
}

.placemain{
  grid-area: main;
}

.placeview1{
  grid-area: view1
}
.placeview2{
  grid-area: view2
}
.placeview3{
  grid-area: view3
}

@media screen and (max-width: 700px) {
  #video-list{
    grid-template-columns: 100vw 100vw ;
    grid-template-rows: 1fr 250px 250px 1fr ;
    grid-template-areas:
    'main upper'
    'main view1'
    'main view2'
    'main down'
    ;
  }
  .moble-page{
    display: flex;
    position: fixed;
    justify-content: center;
    align-items: center;
    background-color: rgba(0, 0, 0, .3);
    border-radius: 5px;
    height: 50px;
    width: 30px;
    bottom: 50%;
  }
  .hover-box{
    left: 5px;
    right: 5px;
    width: calc(100vw - 10px);
    height: 80vh;
  }
}


.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>