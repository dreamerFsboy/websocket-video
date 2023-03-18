<template>
  <main class="container">
    <section class="max-width-600">
      <article>
            <hgroup>
              <h1>登录</h1>
              <h2>欢迎来到wuhu视频</h2>
            </hgroup>
            <form>
              <label>
                <input
                  v-model="LoginForm.email"
                  placeholder="账号"
                  autocomplete="on"
                  :aria-invalid="valid.email"
                  @blur="Focusout('email')"
                />
                <small class="form-tip" v-if="valid.email">邮箱格式错误</small>
              </label>
              <label>
                <input
                  v-model="LoginForm.passwd"
                  placeholder="密码"
                  type="password"
                  autocomplete="on"
                  :aria-invalid="valid.passwd"
                  @blur="Focusout('passwd')"
                />
                <small class="form-tip" v-if="valid.passwd">密码不符合格式</small>
              </label>
              <label>
              <div class="verification">
                <input
                  v-model="LoginForm.code"
                  placeholder="验证码"
                  :aria-invalid="valid.code"
                  @blur="Focusout('code')"
                />
                <div @click="nowtime=Date.now()">
                  <img :src="'/api/captcha.jpg?check=false&d='+nowtime" />
                </div>
              </div>
              <small class="form-tip" v-if="valid.code">验证码格式错误</small>
              </label>

              <label>
                <div class="space-between">
                  <a @click="router.push('/register')">没有账号？点击注册</a>
                  <!-- <a href="/register">没有账号？点击注册</a> -->
                  <a @click="router.push('/forget')">忘记密码</a>
                </div>
              </label>
              <button type="button" @click="Login">登录</button>
              <small @click="router.push('/active')">去激活账号</small>
            </form>
      </article>
    </section>
  </main>
</template>

<script setup lang="ts">
import { KMessage } from '@/methods/KMessage'
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { myRule, RuleFunc } from '@/types/ruletype'
import { debounce} from '@/methods/index'
import {login} from '@/request/login'
import usestore from '@/store';
const store = usestore()
const router = useRouter()
const nowtime = ref(0)
const LoginForm = reactive({
  email: '',
  passwd: '',
  code: '',
})
const valid:myRule = reactive({
  email: undefined,
  passwd: undefined,
  code: undefined
})
const rule:RuleFunc = {
  email: (form: any,prop: string) => {
    valid[prop] = !(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(form[prop]))
  },
  passwd: (form: any,prop: string) => {
    valid[prop] = form[prop].length < 6
  },
  code: (form: any, prop: string) => {
    valid[prop] = form[prop].length !== 4
  }
}
const Focusout = debounce((prop: string) => {
  rule[prop](LoginForm, prop)
},100)

const Login = () => {
  let check = true
  for(const key in rule) {
    Focusout(key)
    check&&=rule[key]
  }
  if (check) {
    login(LoginForm.email,LoginForm.passwd,LoginForm.code).then(res=>{
      KMessage('登录成功','success')
      store.buildConnection()
      // store.SetUser(email)
      store.SetUserAuto().then(()=>{
        router.push('/')
      })
    })
  }else{
    KMessage('请检查您的输入是否正确','warning')
  }
}
</script>

<style>
.space-between{
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing);
}
.verification{
  display: flex;
  cursor: pointer;
}
.verification+small{
  display: block;
  width: 100%;
  margin-top: calc(var(--spacing) * -.75);
  margin-bottom: var(--spacing);
  color: var(--muted-color);
}
.verification>input{
  flex: 1;
  width: 60%;
}
.verification>div{
  height: 60px;
  margin-left: 20px;
  margin-bottom: var(--spacing);
}
.verification>div>img{
  max-height: 100%;
}
</style>
