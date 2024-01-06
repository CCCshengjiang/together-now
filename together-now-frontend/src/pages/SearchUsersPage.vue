<script setup lang="ts">
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugs/myAxios.ts"
import qs from 'qs';
import UserCardList from "../components/UserCardList.vue";

const route = useRoute();

const {tags} = route.query;

const userList = ref([])

onMounted(async() => {
// Optionally the request above could also be done as
  const searchUserList = await myAxios.get('/user/search/tags', {
    params: {
      tagNameList: tags,
    },
    paramsSerializer: params => {
      return qs.stringify(params, { indices: false })
    }
  })
      .then(function (response) {
        console.log('/user/search/tags succeed', response);
        return response?.data;
      })
      .catch(function (error) {
        console.log('/user/search/tags error', error);
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
  <van-empty image="search" v-if="!userList || userList.length === 0" description="暂无符合要求用户" />
</template>

<style scoped>

</style>