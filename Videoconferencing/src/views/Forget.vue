<template>
    <main class="container">
      <section class="max-width-600">
        <article>
              <hgroup>
                <h1>忘记密码</h1>
                <h2>为每一次相遇欢呼</h2>
              </hgroup>
              <form>
                <label>
                <input
                  placeholder="邮箱"
                  v-model="ForgetForm.email"
                  autocomplete="on"
                  :aria-invalid="valid.email"
                  @input="Focusout('email')"
                />
                <small class="form-tip" v-if="valid.email">这不是一个有效的邮箱！</small>
              </label>
              <label>
                <input
                  v-model="ForgetForm.passwd"
                  placeholder="密码"
                  type="password"
                  autocomplete="on"
                  :aria-invalid="valid.passwd"
                  @input="Focusout('passwd')"
                />
                <small class="form-tip" v-if="valid.passwd">密码不得少于8位</small>
              </label>
              <label>
                <input
                  v-model="ForgetForm.repasswd"
                  placeholder="重复密码"
                  type="password"
                  autocomplete="on"
                  :aria-invalid="valid.repasswd"
                  @input="Focusout('repasswd')"
                />
                <small class="form-tip" v-if="valid.repasswd">您输入的两次密码不同！</small>
              </label>
  
                <label>
                  <div class="space-between">
                    <a @click="router.push('/login')">想起来了？点击登录</a>
                    <a @click="router.push('/register')">没有账号，前往注册</a>
                  </div>
                </label>
                <button>重置密码</button>
              </form>
        </article>
      </section>
    </main>
  </template>
  
<script setup lang="ts">
import { KMessage } from '@/methods/KMessage'
import { computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { myRule, RuleFunc } from '@/types/ruletype'
import { debounce} from '@/methods/index'
const router = useRouter()
const ForgetForm = reactive({
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
  rule[prop](ForgetForm, prop)
},100)
</script>
