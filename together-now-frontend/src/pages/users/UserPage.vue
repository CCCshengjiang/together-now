<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../../services/userServices";

const user = ref();
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

onMounted(async ()=> {
  user.value = await getCurrentUser();
})

</script>

<template>
  <div v-if="user">
    <van-cell title="编号" :value="user.idCode" />
    <van-cell title="头像" is-link to="/user/edit" @click="toEdit('avatarUrl', '头像', user.avatarUrl)">
      <img style="height: 47px" :src="user.avatarUrl" alt=""/>
    </van-cell>
    <van-cell title="用户名" is-link to="/user/edit" :value="user.username" @click="toEdit('username', '用户名', user.username)" />
    <van-cell title="账号" is-link to="/user/edit" :value="user.userAccount" @click="toEdit('userAccount', '账号', user.userAccount)" />
    <van-cell title="性别" is-link to="/user/edit" :value="user.gender" @click="toEdit('gender', '性别', user.gender)" />
    <van-cell title="电话" is-link to="/user/edit" :value="user.phone" @click="toEdit('phone', '电话', user.phone)" />
    <van-cell title="邮箱" is-link to="/user/edit" :value="user.email" @click="toEdit('email', '邮箱', user.email)" />
    <van-cell title="创建" :value="user.createTime" />
  </div>


</template>

<style scoped>

</style>