<script setup lang="ts">
import TeamCardList from "../../components/TeamCardList.vue";
import {onMounted, ref} from "vue";
import {showFailToast} from "vant";
import myAxios from "../../plugs/myAxios.ts";

const loading = ref(true);

const teamList = ref([]);
const searchTeam = async (val = '') => {
  loading.value = true;
  const res = await myAxios.get("/team/search/captain", {
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
    <team-card-list :team-list="teamList" :loading="loading"/>
    <van-empty image="search" v-if="!loading && (!teamList || teamList.length === 0)" description="暂无符合要求队伍" />
  </div>

</template>

<style scoped>

</style>