<script setup>
  import {ref} from "vue";
  import {showToast} from "vant";
  import Home from "../pages/Home.vue";
  import Team from "../pages/Team.vue";

  const onClickLeft = () => history.back();
  const onClickRight = () => alert('右边');
  const active = ref("home");
  const onChange = (index) => showToast(`标签 ${index}`);


</script>

<template>
  <van-nav-bar
      title="标题"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
  >
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>

  <div id="content">
    <template v-if="active === 'home'">
      <Home/>
    </template>
    <template v-if="active === 'team'">
      <Team/>
    </template>
  </div>

  <van-tabbar v-model="active" @change="onChange">
    <van-tabbar-item icon="home-o" name="home">主页</van-tabbar-item>
    <van-tabbar-item icon="friends-o" name="team">队伍</van-tabbar-item>
    <van-tabbar-item icon="setting-o" name="user">用户</van-tabbar-item>
  </van-tabbar>

</template>

<style scoped>

</style>