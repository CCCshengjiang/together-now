<script setup lang="ts">
import {ref, watchEffect} from "vue";
import myAxios from "../plugs/myAxios.ts"
import UserCardList from "../components/UserCardList.vue";
import {UserType} from "../models/user";

const userList = ref([])
const isMatchMode = ref<boolean>(false);

const loading = ref(true);

const loadDate = async () => {
  let searchUserList;
  loading.value = true;
  if (isMatchMode.value) {
    // 心动模式
    const num = 10;
    searchUserList = await myAxios.get('/user/match', {
      params: {
        num,
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
    // 普通模式
    // Optionally the request above could also be done as
    searchUserList = await myAxios.get('/user/recommend', {
      params: {
        pageSize: 10,
        pageNum: 1,
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
  <van-cell center title="心动模式">
    <template #right-icon>
      <van-switch v-model="isMatchMode" active-color="#ee0a24" inactive-color="#dcdee0"/>
    </template>
  </van-cell>
  <user-card-list :user-list="userList" :loading="loading"/>
  <van-empty image="search" v-if="!userList || userList.length === 0" description="数据为空"/>
</template>

<style scoped>

</style>