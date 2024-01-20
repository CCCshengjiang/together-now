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
const searchTeam = async (val = '') => {
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
}


// 加载时只触发一次
onMounted( () =>{
  searchTeam();
})

const searchText = ref('');
const onSearch = (val) => {
  searchTeam(val);
};

</script>

<template>
  <div id="teamPage">
    <team-card-list :team-list="teamList"/>
    <van-empty image="search" v-if="!teamList || teamList.length === 0" description="暂无符合要求队伍" />
  </div>

</template>

<style scoped>

</style>