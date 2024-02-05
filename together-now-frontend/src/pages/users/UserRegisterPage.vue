<script setup lang="ts">
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {showFailToast, showSuccessToast} from "vant";
import myAxios from "../../plugs/myAxios";

const router = useRouter();
const route = useRoute();
const userAccount = ref('');
const userPassword = ref('');
const checkPassword = ref('');
const idCode = ref('');

const onSubmit = async () => {
  if (userPassword.value !== checkPassword.value) {
    showFailToast('密码和确认密码不一致');
    return;
  }
  const res = await myAxios.post('/user/register', {
    userAccount: userAccount.value,
    userPassword: userPassword.value,
    checkPassword: checkPassword.value,
    idCode: idCode.value,
  })
  // console.log('登录', res);
  if (res.code === 20000 && res.data) {
    showSuccessToast('注册成功');
    window.location.href = route.query?.refirect as string ?? '/';
  } else {
    showFailToast('注册失败');
  }
};

const toUserLogin = () => {
  router.push({
    path: "/user/login",
  })
}

</script>

<template>
  <div class="user-login-index">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="userAccount"
            name="userAccount"
            label="账号"
            placeholder="请设置账号"
            :rules="[{ required: true, message: '请设置账号' }]"
        />
        <van-field
            v-model="userPassword"
            type="password"
            name="userPassword"
            label="密码"
            placeholder="请设置密码"
            :rules="[{ required: true, message: '请设置密码' }]"
        />
        <van-field
            v-model="checkPassword"
            type="password"
            name="checkPassword"
            label="确认密码"
            placeholder="确认密码"
            :rules="[{ required: true, message: '请确认密码' }]"
        />
        <van-field
            v-model="idCode"
            name="idCode"
            label="编号"
            placeholder="请设置编号"
            :rules="[{ required: true, message: '请设置编号' }]"
        />
      </van-cell-group>
      <div class="login-button">
        <van-button type="primary" native-type="submit">提交</van-button>
        <van-button type="success" @click="toUserLogin">返回登陆</van-button>
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