<script setup lang="ts">

import {useRouter} from "vue-router";
import TeamCardList from "../../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import {showFailToast} from "vant";
import myAxios from "../../plugs/myAxios.ts";

const router = useRouter();
const doJoinTeam = () => {
  router.push({
    path: "/team/add",
  })
}

const teamList = ref([]);
// 加载时只触发一次
onMounted(async () =>{
  const res = await myAxios.get("/team/search");
  if (res?.code === 20000) {
    console.log('res.data' + res)
    teamList.value = res.data;
  }else {
    showFailToast('加载队伍失败，请刷新重试');
  }
})

</script>

<template>
  <div id="teamPage">
    <van-button type="primary" @click="doJoinTeam">创建队伍</van-button>
    <team-card-list :team-list="teamList"/>
  </div>

</template>

<style scoped>

</style>