<template>
  <nav>
    <div class="nav-left">
      <div class="icon-box">
        <svg
          t="1672642278918"
          viewBox="0 0 1024 1024"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          p-id="3160"
          width="100"
          height="100"
        >
          <path
            d="M352 458.2V678c0 9.4 7.6 17 17 17h73.1l3.2 1.5C466 706 488.6 711 512 711c23.4 0 46-5 66.7-14.5l3.2-1.5H655c9.4 0 17-7.6 17-17V460v-1.3-3.7c0-88.4-71.6-160-160-160s-160 71.6-160 160v3.2z m-32-3.2c0-106 86-192 192-192s192 86 192 192v223c0 27.1-21.9 49-49 49h-66.2c-24 10.5-50.1 16-76.8 16-26.8 0-52.8-5.5-76.8-16H369c-27.1 0-49-21.9-49-49V460v-1.7-3.3z m238.8-32.6"
            fill="#5AC1FD"
            p-id="3162"
          ></path>
          <circle cx="440" cy="450" r="30" fill="#5AC1FD"></circle>
          <circle cx="584" cy="450" r="30" fill="#5AC1FD"></circle>
        </svg>
      </div>
      <span>芜湖视频</span>
    </div>
    <div class="nav-right">
      <span>{{ timeStamp }}</span>
      <a v-if="!store.user" @click="router.push('/login')"> 登录 </a>
      <div v-else class="avatar">
        <img :src="store.user.avatar || '/default.svg'" />
      </div>
      <div v-if="store.user" class="email">
        {{ store.user.email }}
      </div>
    </div>
  </nav>
  <RouterView/>
</template>

<script setup lang="ts">
import {  onMounted, onUnmounted, ref } from "vue";
import usestore from "@/store";
import { useRouter } from "vue-router";
const router = useRouter();
const store = usestore();

const timeStamp = ref();

let stopInterval: number;
onMounted(() => {
  timeStamp.value = getNowTimeInChinese();
  stopInterval = setInterval(() => {
    timeStamp.value = getNowTimeInChinese();
  }, 1000);
});

onUnmounted(() => {
  clearInterval(stopInterval);
});
const getNowTimeInChinese = (): string => {
  const date = new Date();
  return (
    getNowHourTimeInChinese(date) +
    " - " +
    getNowMonthTimeInChinese(date) +
    " " +
    getNowDayInChinese(date)
  );
};

const getNowMonthTimeInChinese = (date: Date): string => {
  return `${date.getMonth()}月${date.getDate()}日`;
};
const getNowHourTimeInChinese = (date: Date): string => {
  let hour: string | number = date.getHours();
  let min: string | number = date.getMinutes();
  let am = hour < 12;
  hour %= 12;
  hour = hour < 10 ? "0" + hour : hour;
  min = min < 10 ? "0" + min : min;
  return `${am ? "上" : "下"}午${hour}:${min}`;
};
const getNowDayInChinese = (date: Date): string => {
  const Day = ["日", "一", "二", "三", "四", "五", "六"];
  return `周${Day[date.getDay()]}`;
};
</script>

<style>
.nav-left {
  display: flex;
  align-items: center;
  height: 60px;
  font-weight: 800;
  font-size: 20px;
}
.icon-box {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  width: 70px;
  height: 60px;
}
.nav-right {
  /* margin-left: auto; */
  display: flex;
  align-items: center;
  height: 60px;
  font-size: 20px;
  padding: 0 20px;
}
.nav-right > * {
  margin-left: 10px;
  max-height: 100%;
}
.avatar {
  height: 32px;
  width: 32px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background-color: #ddd;
  border: 2px solid #ccc;
  margin-left: 30px;
}
.avatar > * {
  transform: translate(-1px);
  max-height: 100%;
  max-width: 100%;
}
.email {
  font-size: 14px;
}
.main {
  margin-top: 40px;
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
</style>
