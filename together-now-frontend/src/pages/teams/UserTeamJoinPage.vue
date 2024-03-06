<script setup lang="ts">

import {useRouter} from "vue-router";
import TeamCardList from "../../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import {showFailToast} from "vant";
import myAxios from "../../plugs/myAxios.ts";

const router = useRouter();
const doAddTeam = () => {
  router.push({
    path: "/team/add",
  })
}
const teamList = ref([]);

const loading = ref(true);

const searchTeam = async (val = '') => {
  loading.value = true;
  const res = await myAxios.get("/team/search/join", {
    params: {
      searchText: val,
    }
  });
  if (res?.code === 20000) {
    teamList.value = res.data;
  }else {
    showFailToast('加载队伍失败，请刷新重试');
  }
  loading.value = false;
}


// 加载时只触发一次
onMounted( () =>{
  searchTeam();
})


</script>

<template>
  <div id="teamPage">
    <van-button type="primary" @click="doAddTeam">创建队伍</van-button>
    <team-card-list :team-list="teamList" :loading="loading"/>
    <van-empty image="search" v-if="!loading && (!teamList || teamList.length === 0)" description="暂无符合要求队伍" />
  </div>

</template>

<style scoped>

</style>