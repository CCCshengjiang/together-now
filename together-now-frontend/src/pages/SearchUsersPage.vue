<script setup lang="ts">
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugs/myAxios.ts"
import qs from 'qs';

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
        return response.data?.data;
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
  <van-card
      v-for="user in userList"
      :desc="user.userProfile"
      :title="`${user.username} (编号：${user.idCode})`"
      :thumb="user.avatarUrl"
  >
    <template #tags>
      <van-tag plain type="primary" v-for="tag in user.tag" style="margin-right: 8px; margin-top: 8px">
        {{ tag }}
      </van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系我</van-button>
    </template>
  </van-card>
  <van-empty image="search" v-if="!userList || userList.length === 0" description="暂无符合要求用户" />
</template>

<style scoped>

</style>