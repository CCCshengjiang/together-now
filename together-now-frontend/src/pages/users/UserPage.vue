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
  if (res.code === 20000 && res.data) {
    showSuccessToast('退出登录');
    window.location.href = route.query?.refirect as string ?? '/';
  }else {
    showFailToast('退出失败');
  }
}

</script>

<template>
  <div class="centered-avatar">
  <van-image
      round
      width="5rem"
      height="5rem"
      :src="user?.avatarUrl"
  />
  </div>
  <van-divider class="bold-text">{{ user?.username }}</van-divider>
  <van-cell title="修改个人信息" is-link to="/user/update" />
  <van-cell title="查看我管理的队伍" is-link to="/user/team/captain" />
  <van-cell title="查看我加入的队伍" is-link to="/user/team/join" />
  <van-button class="user-logout" block color="#ee0a24" @click="logout">退出登录</van-button>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Great+Vibes&display=swap');


.user-logout {
  margin-top: 10px;
}

.centered-avatar {
  display: flex;
  justify-content: center; /* 横向居中 */
  margin-top: 1rem;
  margin-bottom: 1rem; /* 添加底部距离 */
}

.bold-text {
  font-weight: bold; /* 使文本变粗 */
  color: #000000; /* 设置文本颜色为纯黑色 */
  font-family: 'Raleway ', cursive; /* 应用书法体字体 */
  font-size: 18px; /* 设置字体大小为 24 像素 */
}
</style>