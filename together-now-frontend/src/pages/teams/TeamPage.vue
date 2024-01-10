<script setup lang="ts">

import {useRouter} from "vue-router";

const router = useRouter();
const doJoinTeam = () => {
  router.push({
    path: "/team/add",
  })
}

</script>

<template>
  <div id="teamPage">
    <van-button type="primary" @click="doJoinTeam">主要按钮</van-button>
  </div>

</template>

<style scoped>

</style>