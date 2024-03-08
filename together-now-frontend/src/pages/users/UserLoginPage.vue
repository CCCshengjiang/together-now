<script setup lang="ts">
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {showFailToast, showSuccessToast} from "vant";
import myAxios from "../../plugs/myAxios";

const router = useRouter();
const route = useRoute();
const userAccount = ref('');
const userPassword = ref('');
const onSubmit = async () => {
  const res = await myAxios.post('/user/login', {
    userAccount: userAccount.value,
    userPassword: userPassword.value,
  })
  // console.log('登录', res);
  if (res.code === 20000 && res.data) {
    showSuccessToast('登陆成功');
    window.location.href = route.query?.refirect as string ?? '/';
  } else {
    showFailToast('登陆失败');
  }
};

const toUserRegister = () => {
  router.push({
    path: "/user/register",
  })
}

</script>

<template>
  <van-notice-bar
      color="#1989fa"
      background="#ecf9ff"
      left-icon="info-o"
      wrapable
      :scrollable="false"
      >为了更好的使用本系统，现提供体验账号如下,
    账号:admin 密码:admin888</van-notice-bar>
  <div class="user-login-index">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="userAccount"
            name="userAccount"
            label="账号"
            placeholder="请输入账号"
            :rules="[{ required: true, message: '请输入账号' }]"
        />
        <van-field
            v-model="userPassword"
            type="password"
            name="userPassword"
            label="密码"
            placeholder="密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div class="login-button">
        <van-button type="primary" native-type="submit">登录</van-button>
        <van-button type="success" @click="toUserRegister">注册账号</van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
.user-login-index {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 70vh; /* 可根据需要调整高度 */
}
.login-button {
  margin: 16px;
  display: flex;
  justify-content: center;
  gap: 20px; /* 设置按钮之间的间距 */
}
</style>