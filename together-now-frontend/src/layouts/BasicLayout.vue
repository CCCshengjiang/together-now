<script setup lang="ts">
import {useRouter} from "vue-router";
import {ref} from "vue";
import routes from "../config/route.ts";
import {getCurrentUser} from "../services/userServices.ts";

const router = useRouter();
const DEFAULT_TITLE = '伙伴匹配';
const title = ref(DEFAULT_TITLE);

router.beforeEach((to) => {
  const toPath = to.path;
  const route = routes.find((route) => {
    return toPath == route.path;
  });
  title.value = route?.title ?? DEFAULT_TITLE;
});

const onClickBack = () => history.back();
const onClickSearch = () => {
  const res = getCurrentUser();
  console.log('res', res);
  router.push('/search')
}
</script>

<template>
  <van-nav-bar
      :title="title"
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
  <van-tabbar route >
    <van-tabbar-item to="/" icon="https://fastly.jsdelivr.net/npm/@vant/assets/icon-demo.png" name="friends">搭子</van-tabbar-item>
    <van-tabbar-item to="/team" icon="friends-o" name="team">队伍</van-tabbar-item>
    <van-tabbar-item to="/user" icon="setting-o" name="user">个人</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
#content {
  padding-bottom: 25px;
}
</style>