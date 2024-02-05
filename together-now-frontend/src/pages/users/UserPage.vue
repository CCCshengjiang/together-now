<script setup lang="ts">
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../../services/userServices";
import myAxios from "../../plugs/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {useRoute} from "vue-router";

const user = ref();

onMounted(async ()=> {
  user.value = await getCurrentUser();
})

const route = useRoute();

const logout = async () => {
  const res = await myAxios.post('/user/logout');
  console.log(res);
  console.log(res.data);
  console.log(res.code);
  if (res.code === 20000 && res.data) {
    showSuccessToast('退出登录');
    window.location.href = route.query?.refirect as string ?? '/';
  }else {
    showFailToast('退出失败');
  }
}

</script>

<template>
  <van-cell title="头像">
    <img style="height: 47px" :src="user?.avatarUrl" alt=""/>
  </van-cell>
  <van-cell title="用户名" :value="user?.username"/>
  <van-cell title="修改个人信息" is-link to="/user/update" />
  <van-cell title="查看我管理的队伍" is-link to="/user/team/captain" />
  <van-cell title="查看我加入的队伍" is-link to="/user/team/join" />
  <van-button class="quit-login" block color="#ee0a24" @click="logout">退出登录</van-button>
</template>

<style scoped>
.quit-login {
  margin-top: 10px;
}
</style>