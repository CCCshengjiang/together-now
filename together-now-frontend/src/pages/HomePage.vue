<script setup lang="ts">
import {ref} from "vue";

const images = [
  'https://gitee.com/CCCshengjiang/blog-img/raw/master/image/202402060011679.jpg',
  'https://gitee.com/CCCshengjiang/blog-img/raw/master/image/202402060011340.jpg',
];

const defaultList = [
  { id: 100, text: '搭子' },
  { id: 100, text: '搭子' },
  { id: 101, text: '志同道合' },
  { id: 101, text: '志同道合' },
  { id: 102, text: '移动端' },
  { id: 103, text: '组队' },
  { id: 103, text: '组队' },
  { id: 104, text: '朋友' },
  { id: 105, text: '联系搭子' },
  { id: 106, text: '创建队伍' },
  { id: 107, text: '加入队伍' },
  { id: 108, text: '退出队伍' },
  { id: 109, text: '更新队伍' },
  { id: 110, text: '解散队伍' },
  { id: 111, text: '独行快' },
  { id: 112, text: '众行远' },
  { id: 113, text: '众行远' },
  { id: 114, text: '众行远' },
  { id: 115, text: '互相帮助' },
  { id: 116, text: '目标一致' },
  { id: 117, text: '克服困难' },
  { id: 118, text: '更长远' },
  { id: 119, text: 'wen' },
  { id: 120, text: 'wen' },
];

const list = ref([...defaultList]);
const add = () => {
  list.value.push({ id: Math.random(), text: 'Barrage' });
};
</script>

<template>
  <van-notice-bar
      color="#1989fa"
      background="#ecf9ff"
      left-icon="volume-o"
      text="独行快，众行远！并肩合作、共同克服困难，会走得更长远。搭子组队：一个帮助大家找到志同道合的伙伴的移动端H5网页"
  />

  <van-swipe :autoplay="3000" lazy-render>
    <van-swipe-item v-for="image in images" :key="image" style="font-size: 20px; line-height: 200px; text-align: center;">
      <img :src="image" alt="" style="width: 100%; height: auto; max-height: 200px; display: inline-block;">
    </van-swipe-item>
    <van-swipe-item style="font-size: 20px; line-height: 150px; text-align: center;">
      广告位😜
    </van-swipe-item>
  </van-swipe>

  <van-barrage v-model="list">
    <div class="video" style="width: 100%; height: 150px"></div>
  </van-barrage>
  <van-space style="margin-top: 10px">
    <van-button @click="add" type="primary" size="normal"> 弹幕 </van-button>
  </van-space>

</template>

<style scoped>
.my-swipe .van-swipe-item {
  margin-top: 10px;
  color: #fff;
  font-size: 20px;
  line-height: 150px;
  text-align: center;
  background-color: #39a9ed;
}
</style>