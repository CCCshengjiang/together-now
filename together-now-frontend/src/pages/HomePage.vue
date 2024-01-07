<script setup lang="ts">
import {onMounted, ref} from "vue";
import myAxios from "../plugs/myAxios.ts"
import UserCardList from "../components/UserCardList.vue";
const userList = ref([])

onMounted(async () => {
// Optionally the request above could also be done as
  const searchUserList = await myAxios.get('/user/recommend', {
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
  if (searchUserList) {
    searchUserList.forEach(user => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = searchUserList;
  }
})

</script>

<template>
  <user-card-list :user-list="userList"/>
  <van-empty image="search" v-if="!userList || userList.length === 0" description="数据为空"/>
</template>

<style scoped>

</style>