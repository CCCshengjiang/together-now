<script setup lang="ts">
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../../services/userServices";

const user = ref();

onMounted(async ()=> {
  user.value = await getCurrentUser();
})

</script>

<template>
  <van-cell title="头像">
    <img style="height: 47px" :src="user?.avatarUrl" alt=""/>
  </van-cell>
  <van-cell title="用户名" :value="user?.username"/>
  <van-cell title="修改个人信息" is-link to="/user/update" />
  <van-cell title="查看我管理的队伍" is-link to="/user/team/captain" />
  <van-cell title="查看我加入的队伍" is-link to="/user/team/join" />
</template>

<style scoped>

</style>