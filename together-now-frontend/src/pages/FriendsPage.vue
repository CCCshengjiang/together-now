<script setup lang="ts">
import {ref, watchEffect, watch, onMounted} from "vue";
import myAxios from "../plugs/myAxios.ts"
import UserCardList from "../components/UserCardList.vue";
import {UserType} from "../models/user";
import {showDialog} from "vant";


const userList = ref([])
const isMatchMode = ref<boolean>(false);

const loading = ref(true);
const currentPage = ref(1);
const totalItems = ref(0);
const pageSize = ref(5); // 定义每页的大小


const loadDate = async () => {
  loading.value = true;
  let searchUserList;
  // 根据isMatchMode的值调用不同的API
  if (isMatchMode.value) {
    searchUserList = await myAxios.get('/user/match', {
      params: {
        pageSize: pageSize.value,
        pageNum: currentPage.value,
      }
    })
        .then(function (response) {
          // console.log('/user/match succeed', response);
          return response?.data;
        })
        .catch(function (error) {
          // console.log('/user/match error', error);
        })
  } else {
    searchUserList = await myAxios.get('/user/recommend', {
      params: {
        pageSize: pageSize.value,
        pageNum: currentPage.value,
      }
    })
        .then(function (response) {
          // console.log('/user/recommend succeed', response);
          return response?.data;
        })
        .catch(function (error) {
          // console.log('/user/recommend error', error);
        })
  }

  if (searchUserList) {
    totalItems.value = searchUserList.totalUsers;
    searchUserList.safetyUsers.forEach((user: UserType) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = searchUserList.safetyUsers;
  }
  loading.value = false;
}

// 监听isMatchMode变化
watch(isMatchMode, () => {
  currentPage.value = 1; // 切换模式时重置为第一页
  // loadDate(); // 重新加载数据
});

watchEffect(() => {
  loadDate();
});

const images = [
  'https://img1.baidu.com/it/u=3028451851,2267400980&fm=253&fmt=auto&app=138&f=JPEG?w=533&h=300',
  'https://img2.baidu.com/it/u=3301550489,3406573973&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500',
  'https://img2.baidu.com/it/u=1964105560,4145185881&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500',
];

onMounted(async ()=> {
  if (!localStorage.getItem('methodExecuted')) {
    // 在页面加载完成之后执行的方法
    getTipsMessage();
    // 将执行状态保存到localStorage中
    localStorage.setItem('methodExecuted', String(true));
  }
})

const getTipsMessage = () => {
  showDialog({
    title: '用法提示',
    message: '独行快，众行远！' + '\n' + '\n' + '心动模式：匹配和自己标签相似用户' + '\n' + '\n' + '想要更多信息？ 点击头像试试吧' + '\n' + '\n' + '队伍页面右下角＋号按钮用于创建队伍',
  });
};

</script>

<template>
  <van-floating-bubble
      axis="xy"
      icon="fire-o"
      magnetic="x"
      @click="getTipsMessage"
  />

  <van-notice-bar
      color="#1989fa"
      background="#ecf9ff"
      left-icon="volume-o"
      text="独行快，众行远！并肩合作、共同克服困难，会走得更长远。搭子组队：一个帮助大家找到志同道合的伙伴的移动端网页"
  />

  <van-swipe :autoplay="3000" lazy-render>
    <van-swipe-item v-for="image in images" :key="image"
                    style="font-size: 30px; line-height: 20px; text-align: center;">
      <img :src="image" alt="" style="width: 100%; height: auto; max-height: 200px; display: inline-block;">
    </van-swipe-item>
    <van-swipe-item style="font-size: 20px; line-height: 150px; text-align: center;">
      志同道合才能走得更快更远🎈
    </van-swipe-item>
  </van-swipe>

  <van-cell center>
    <template #title>
      <span class="heart-mode-title2">搭子推荐</span>
    </template>
    <template #right-icon>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <span class="heart-mode-title" style="order: 1; margin-right: 8px;">心动模式</span>
        <van-switch v-model="isMatchMode" active-color="#FF69B4" inactive-color="#dcdee0" style="order: 2;"/>
      </div>
    </template>
  </van-cell>

  <user-card-list :user-list="userList" :loading="loading"/>

  <van-empty image="search" v-if="!loading && (!userList || userList.length === 0)" description="数据为空"/>

  <van-pagination
      v-model="currentPage"
      :total-items="totalItems"
      :items-per-page="pageSize"
      :show-page-size="5"
      force-ellipses
  >
    <template #prev-text>
      <van-icon name="arrow-left"/>
    </template>
    <template #next-text>
      <van-icon name="arrow"/>
    </template>
    <template #page="{ text }">{{ text }}</template>
  </van-pagination>
</template>

<style scoped>
.heart-mode-title {
  color: #FF69B4; /* 粉红色 */
  font-weight: bold; /* 加粗 */
  font-size: 18px; /* 字体大小 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2); /* 添加文字阴影，增加立体感 */
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; /* 使用更美观的字体，根据实际情况选择 */
  transition: all 0.3s ease; /* 添加平滑过渡效果 */
}

.heart-mode-title2 {
  color: #37363b; /* 粉红色 */
  font-weight: bold; /* 加粗 */
  font-size: 18px; /* 字体大小 */
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; /* 使用更美观的字体，根据实际情况选择 */
  transition: all 0.3s ease; /* 添加平滑过渡效果 */
}

/* 可选：添加一个鼠标悬停效果，让文字在鼠标悬停时变化 */
.heart-mode-title:hover {
  color: #ff4081; /* 鼠标悬停时的颜色，稍微亮一点的粉红色 */
  text-shadow: 3px 3px 5px rgba(0, 0, 0, 0.3); /* 增强阴影效果 */
}

.heart-mode-container {
  background-color: #FF69B4; /* 设置背景色为粉红色 */
}
</style>