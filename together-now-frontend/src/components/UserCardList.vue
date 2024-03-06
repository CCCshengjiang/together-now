<script setup lang="ts">
import {UserType} from "../models/user";
import {showDialog} from "vant";

interface UserCardListProps {
  loading: boolean;
  userList: UserType[];
}

const props = withDefaults(defineProps<UserCardListProps>(), {
      loading: true,
      // @ts-ignore
      userList: [] as UserType[],
    }
);

const showEmail = (email: string) => showDialog({
  title: 'Email',
  message: email ? email : '该用户未设置Email',
});

const showProfile = (userProfile: string) => showDialog({
  title: '用户介绍',
  message: userProfile ? userProfile : '该用户暂无介绍',
});

</script>

<template>
  <div v-if="loading">
    <!-- 当数据正在加载时，显示3个骨架屏占位 -->
    <van-skeleton title avatar :row="3" v-for="n in 5" :key="n"></van-skeleton>
  </div>
  <div v-else>
    <!-- 数据加载完成后，显示实际的用户卡片 -->
    <van-card
        v-for="user in props.userList"
        :key="user.id"
        :desc="user.userProfile"
        :title="`${user.username} (编号：${user.idCode})`"
        :thumb="user.avatarUrl"
        @click-thumb="showProfile(user.userProfile)"
    >
      <template #tags>
        <van-tag plain type="primary" v-for="tag in user.tags" :key="tag" style="margin-right: 8px; margin-top: 8px">
          {{ tag }}
        </van-tag>
      </template>
      <template #footer>
        <van-button size="mini" @click="showEmail(user.email)">联系我</van-button>
      </template>
    </van-card>
  </div>
</template>

<style scoped>

</style>