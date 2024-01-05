<script setup lang="ts">
import {useRoute} from "vue-router";
import {ref} from "vue";

const route = useRoute();

const { tag } = route.query;



const mockUser = {
  id: 1,
  username: 'wen',
  avatarUrl: 'https://img2.baidu.com/it/u=4260210395,2753506949&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1704474000&t=bc650d45794e38cbcc9b7ee86bcb6de4',
  userProfile: '大四学生，正在做项目together-now，在十五号回家，买的火车票 + 高铁票',
  gender: '男',
  userAccount: '666',
  phone: '12345678911',
  email: '123@qq.com',
  idCode: '1',
  tag: ['java', 'c++'],
  createTime: new Date(),
}

const userList = ref([mockUser])
</script>

<template>
  <van-card
      v-for="user in userList"
      :desc="user.userProfile"
      :title="`${user.username} (${user.idCode})`"
      :thumb="user.avatarUrl"
  >
    <template #tags>
      <van-tag plain type="primary" v-for="tag in user.tag" style="margin-right: 8px; margin-top: 8px">
        {{tag}}
      </van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系我</van-button>
    </template>
  </van-card>

</template>

<style scoped>

</style>