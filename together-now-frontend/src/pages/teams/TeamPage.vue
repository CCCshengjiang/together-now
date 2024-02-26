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
const searchTeam = async (val = '', teamStatus = 0) => {
  const res = await myAxios.get("/team/search", {
    params: {
      searchText: val,
      teamStatus,
    }
  });
  if (res?.code === 20000) {
    teamList.value = res.data;
  }else {
    // showFailToast('加载队伍失败，请刷新重试');
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

const active = ref('public');

const onChangeTab = (name) => {
  if (name == 'public') {
    searchTeam(searchText.value, 0);
  }else {
    searchTeam(searchText.value, 2);
  }
}

</script>

<template>
  <div id="teamPage">
    <van-search v-model="searchText" placeholder="搜索队伍" @search="onSearch"/>
    <van-tabs v-model:active="active" @change="onChangeTab">
      <van-tab title="公开队伍" name="public" />
      <van-tab title="加密队伍" name="secret"/>
    </van-tabs>
    <van-button class="add-button" type="primary" icon="plus" @click="doAddTeam"/>
    <team-card-list :team-list="teamList"/>
    <van-empty image="search" v-if="!teamList || teamList.length === 0" description="暂无符合要求队伍" />
  </div>

</template>

<style scoped>
.add-button {
  position: fixed;
  bottom: 60px;
  width: 50px;
  right: 12px;
  height: 50px;
  border-radius: 50%;
  z-index: 9999;
}
</style>