<script setup lang="ts">
import {showToast} from "vant";
import {useRouter} from "vue-router";

const router = useRouter();
const onClickBack = () => history.back();
const onClickSearch = () => {
  router.push('/search')
}
const onChange = (index) => showToast(`标签 ${index}`);
</script>

<template>
  <van-nav-bar
      title="标题"
      left-text="返回"
      right-text="按钮"
      left-arrow
      @click-left="onClickBack"
      @click-right="onClickSearch"
  >
    <template #right>
      <van-icon name="search" size="18"/>
    </template>
  </van-nav-bar>

  <div id="content">
    <router-view/>
  </div>
  <van-tabbar route @change="onChange">
    <van-tabbar-item to="/" icon="home-o" name="home">主页</van-tabbar-item>
    <van-tabbar-item to="/team" icon="friends-o" name="team">队伍</van-tabbar-item>
    <van-tabbar-item to="/user" icon="setting-o" name="user">个人</van-tabbar-item>
  </van-tabbar>


</template>

<style scoped>
#content {
  padding-bottom: 50px;
}
</style>