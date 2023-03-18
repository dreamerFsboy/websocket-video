<template>
  <main class="container">
    <section class="max-width-600">
      <article>
        <hgroup>
          <h1>激活账号</h1>
          <h2>欢迎来到wuhu视频</h2>
        </hgroup>
        <form>
          <label>
            <input v-model="code" placeholder="账号" autocomplete="on" />
          </label>

          <label>
            <div class="space-between">
              <a @click="router.push('/login')">激活了？登录</a>
              <a @click="router.push('/register')">没有账号？点击注册</a>
              <!-- <a href="/register">没有账号？点击注册</a> -->
            </div>
          </label>
          <button type="button" @click="activateAccount()">登录</button>
        </form>
      </article>
    </section>
  </main>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { active } from "@/request/login";
import { ref } from "vue";
import { KMessage } from "@/methods/KMessage";
const router = useRouter();
const code = ref("");
const activateAccount = () => {
    active(code.value).then(res => {
        KMessage('激活成功','success')
        router.push('/login')
    })
}
</script>

<style>
.space-between {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing);
}
</style>
