<script setup lang="ts">
import {showToast} from "vant";
import {ref} from 'vue';

const onClickLeft = () => history.back();
const onClickRight = () => showToast('按钮');
const active = ref(0);
const onChange = (index) => showToast(`标签 ${index}`);
</script>

<template>
  <van-nav-bar
      title="标题"
      left-text="返回"
      right-text="按钮"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
  >
    <template #right>
      <van-icon name="search" size="18"/>
    </template>
  </van-nav-bar>

  <div>
    <router-view />
  </div>
  <router-link to="/">Go to Home</router-link>
  <router-link to="/team">Go to Team</router-link>
  <router-link to="/user">Go to User</router-link>

  <van-tabbar route @change="onChange">
    <van-tabbar-item to="/" icon="home-o" name="home">主页</van-tabbar-item>
    <van-tabbar-item to="/team" icon="friends-o" name="team">队伍</van-tabbar-item>
    <van-tabbar-item to="/user" icon="setting-o" name="user">个人</van-tabbar-item>
  </van-tabbar>


</template>

<style scoped>

</style>