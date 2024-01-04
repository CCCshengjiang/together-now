<script setup lang="ts">
import {useRouter} from "vue-router";

const user = {
  id: 1,
  username: 'wen',
  avatarUrl: 'https://img2.baidu.com/it/u=4260210395,2753506949&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1704474000&t=bc650d45794e38cbcc9b7ee86bcb6de4',
  gender: '男',
  userAccount: '666',
  phone: '12345678911',
  email: '123@qq.com',
  idCode: '1',
  createTime: new Date(),
}
const router = useRouter();
const toEdit = (editKey: string, editName: string, currentValue: string) => {
  router.push({
    path: '/user/edit',
    query: {
      editKey,
      editName,
      currentValue,
    }
  })
}

</script>

<template>
  <van-cell title="编号" :value="user.idCode" />
  <van-cell title="头像" is-link to="/user/edit" @click="toEdit('avatarUrl', '头像', user.avatarUrl)">
    <img style="height: 47px" :src="user.avatarUrl" alt=""/>
  </van-cell>
  <van-cell title="用户名" is-link to="/user/edit" :value="user.username" @click="toEdit('username', '用户名', user.username)" />
  <van-cell title="账号" is-link to="/user/edit" :value="user.userAccount" @click="toEdit('userAccount', '账号', user.userAccount)" />
  <van-cell title="性别" is-link to="/user/edit" :value="user.gender" @click="toEdit('gender', '性别', user.gender)" />
  <van-cell title="电话" is-link to="/user/edit" :value="user.phone" @click="toEdit('phone', '电话', user.phone)" />
  <van-cell title="邮箱" is-link to="/user/edit" :value="user.email" @click="toEdit('email', '邮箱', user.email)" />
  <van-cell title="创建" :value="user.createTime.toISOString()" />


</template>

<style scoped>

</style>