<template>
  <main class="container">
    <section class="max-width-600">
      <article>
            <hgroup>
              <h1>注册</h1>
              <h2>为每一次相遇欢呼</h2>
            </hgroup>
            <form>
              <label>
                <input
                  placeholder="邮箱"
                  v-model="RegisterForm.email"
                  autocomplete="on"
                  :aria-invalid="valid.email"
                  @blur="Focusout('email')"
                />
                <small class="form-tip" v-if="valid.email">这不是一个有效的邮箱！</small>
              </label>

              <label>
                <input
                  v-model="RegisterForm.passwd"
                  placeholder="密码"
                  type="password"
                  autocomplete="on"
                  :aria-invalid="valid.passwd"
                  @blur="Focusout('passwd')"
                />
                <small class="form-tip" v-if="valid.passwd">密码不得少于8位</small>
              </label>
              <label>
                <input
                  v-model="RegisterForm.repasswd"
                  placeholder="重复密码"
                  type="password"
                  autocomplete="on"
                  :aria-invalid="valid.repasswd"
                  @blur="Focusout('repasswd')"
                />
                <small class="form-tip" v-if="valid.repasswd">您输入的两次密码不同！</small>
              </label>
              <label>
                <div class="space-between">
                  <a @click="router.push('/login')">已有账号？点击登录</a>
                  <a @click="router.push('/forget')">忘记密码</a>
                </div>
              </label>
              <button type="button" @click="Regist">注册</button>
              <small @click="router.push('/active')">去激活账号</small>
            </form>
      </article>
    </section>
  </main>
</template>

<script setup lang="ts">
import { KMessage } from '@/methods/KMessage'
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { myRule, RuleFunc } from '@/types/ruletype'
import { debounce} from '@/methods/index'
import { regist } from '@/request/login';
const router = useRouter()
const RegisterForm = reactive({
  email: '',
  passwd: '',
  repasswd: '',
})
const valid:myRule = reactive({
  email: undefined,
  passwd: undefined,
  repasswd: undefined
})
const rule:RuleFunc = {
  email: (form: any,prop: string) => {
    valid[prop] = !(/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(form[prop]))
  },
  passwd: (form: any,prop: string) => {
    valid[prop] = form[prop].length < 8
  },
  repasswd: (form: any, prop: string) => {
    valid[prop] = form[prop] !== form['passwd']
  }
}
const Focusout = debounce((prop: string) => {
  rule[prop](RegisterForm, prop)
},100)

const Regist = () => {
  let check = true
  for(const key in rule) {
    Focusout(key)
    check&&=rule[key]
  }
  if (check) {
    regist(RegisterForm.email,RegisterForm.passwd).then(res=>{
      KMessage('注册成功','success')
      setTimeout(() => {
        router.push('/login')
      })
    })
  }else{
    KMessage('请检查您的输入是否正确','warning')
  }
}
</script>