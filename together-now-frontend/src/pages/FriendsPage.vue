<script setup lang="ts">
import {ref, watchEffect} from "vue";
import myAxios from "../plugs/myAxios.ts"
import UserCardList from "../components/UserCardList.vue";
import {UserType} from "../models/user";

const userList = ref([])
const isMatchMode = ref<boolean>(false);

const loading = ref(true);

// 分页
const currentPage = ref(1);


const loadDate = async () => {
  let searchUserList;
  loading.value = true;

  if (isMatchMode.value) {
    // 心动模式
    searchUserList = await myAxios.get('/user/match', {
      params: {
        pageSize: 5,
        pageNum: currentPage.value,
      }
    })
        .then(function (response) {
          console.log('/user/match succeed', response);
          return response?.data;
        })
        .catch(function (error) {
          console.log('/user/match error', error);
        })
  } else {
    console.log('currentPage.value', currentPage.value)
    // 普通模式
    // Optionally the request above could also be done as
    searchUserList = await myAxios.get('/user/recommend', {
      params: {
        pageSize: 5,
        pageNum: currentPage.value,
      }
    })
        .then(function (response) {
          console.log('/user/recommend succeed', response);
          return response?.data;
        })
        .catch(function (error) {
          console.log('/user/recommend error', error);
        })
  }
  if (searchUserList) {
    searchUserList.forEach((user: UserType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = searchUserList;
  }
  loading.value = false;
}

watchEffect(() => {
  loadDate();
})

</script>

<template>
  <van-cell center>
    <template #title>
      <!-- 添加类名用于样式定位 -->
      <span class="heart-mode-title">心动模式</span>
    </template>
    <template #right-icon>
      <van-switch v-model="isMatchMode" active-color="#FF69B4" inactive-color="#dcdee0"/>
    </template>
  </van-cell>

  <user-card-list :user-list="userList" :loading="loading"/>

  <van-empty image="search" v-if="!userList || userList.length === 0" description="数据为空"/>

  <van-pagination
      v-model="currentPage"
      :total-items="100"
      :show-page-size="3"
      force-ellipses
  />

</template>

<style scoped>
.heart-mode-title {
  color: #FF69B4; /* 粉红色 */
  font-weight: bold; /* 加粗 */
  font-size: 18px; /* 字体大小 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2); /* 添加文字阴影，增加立体感 */
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; /* 使用更美观的字体，根据实际情况选择 */
  transition: all 0.3s ease; /* 添加平滑过渡效果 */
}

/* 可选：添加一个鼠标悬停效果，让文字在鼠标悬停时变化 */
.heart-mode-title:hover {
  color: #ff4081; /* 鼠标悬停时的颜色，稍微亮一点的粉红色 */
  text-shadow: 3px 3px 5px rgba(0, 0, 0, 0.3); /* 增强阴影效果 */
}
</style>