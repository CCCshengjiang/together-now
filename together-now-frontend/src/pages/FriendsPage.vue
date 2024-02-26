<script setup lang="ts">
import {ref, watchEffect, watch} from "vue";
import myAxios from "../plugs/myAxios.ts"
import UserCardList from "../components/UserCardList.vue";
import {UserType} from "../models/user";

const userList = ref([])
const isMatchMode = ref<boolean>(false);

const loading = ref(true);
const currentPage = ref(1);
const totalItems = ref(0);
const pageSize = ref(5); // 定义每页的大小


const loadDate = async () => {
  loading.value = true;
  let searchUserList;
  // 根据isMatchMode的值调用不同的API
  if (isMatchMode.value) {
    searchUserList = await myAxios.get('/user/match', {
      params: {
        pageSize: pageSize.value,
        pageNum: currentPage.value,
      }
    })
        .then(function (response) {
          // console.log('/user/match succeed', response);
          return response?.data;
        })
        .catch(function (error) {
          // console.log('/user/match error', error);
        })
  } else {
    searchUserList = await myAxios.get('/user/recommend', {
      params: {
        pageSize: pageSize.value,
        pageNum: currentPage.value,
      }
    })
        .then(function (response) {
          // console.log('/user/recommend succeed', response);
          return response?.data;
        })
        .catch(function (error) {
          // console.log('/user/recommend error', error);
        })
  }

  if (searchUserList) {
    totalItems.value = searchUserList.totalUsers;
    searchUserList.safetyUsers.forEach((user: UserType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = searchUserList.safetyUsers;
  }
  loading.value = false;
}

// 监听isMatchMode变化
watch(isMatchMode, () => {
  currentPage.value = 1; // 切换模式时重置为第一页
  loadDate(); // 重新加载数据
});

watchEffect(() => {
  loadDate();
});

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

  <van-empty image="search" v-if="!loading && (!userList || userList.length === 0)" description="数据为空"/>

  <van-pagination
      v-model="currentPage"
      :total-items="totalItems"
      :items-per-page="pageSize"
      :show-page-size="5"
      force-ellipses
  >
    <template #prev-text>
      <van-icon name="arrow-left" />
    </template>
    <template #next-text>
      <van-icon name="arrow" />
    </template>
    <template #page="{ text }">{{ text }}</template>
  </van-pagination>
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