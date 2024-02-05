<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";

const searchText = ref('');
const originTagList = [
  {
    text: '性别',
    children: [
      {text: '男', id: '男'},
      {text: '女', id: '女'},
    ],
  },
  {
    text: '年级',
    children: [
      {text: '大一', id: '大一'},
      {text: '大二', id: '大二'},
      {text: '大三', id: '大三'},
      {text: '大四', id: '大四'},
    ],
  },
  {
    text: '学习方向',
    children: [
      {text: '前端开发', id: '前端开发'},
      {text: '后端开发', id: '后端开发'},
      {text: '全栈开发', id: '全栈开发'},
      {text: '人工智能', id: '人工智能'},
      {text: '深度学习', id: '深度学习'},
      {text: '机器学习', id: '机器学习'},
      {text: '图像识别', id: '图像识别'},
      {text: '算法', id: '算法'},
    ],
  },
  {
    text: '编程语言',
    children: [
      {text: 'Java', id: 'Java'},
      {text: 'Python', id: 'Python'},
      {text: 'C++', id: 'C++'},
      {text: 'C', id: 'C'},
      {text: 'C#', id: 'C#'},
      {text: 'JavaScript', id: 'JavaScript'},
      {text: 'Haskell', id: 'Haskell'},
      {text: 'Rust', id: 'Rust'},
      {text: 'Swift', id: 'Swift'},
      {text: 'matlab', id: 'matlab'},
      {text: 'PHP', id: 'PHP'},
    ],
  },
  {
    text: '年级',
    children: [
      {text: '大一', id: '大一'},
      {text: '大二', id: '大二'},
      {text: '大三', id: '大三'},
      {text: '大四', id: '大四'},
    ],
  },
];

let tagList = ref(originTagList);
/**
 * 搜索过滤
 * @param val
 */
const onSearch = (val) => {
  tagList.value = originTagList.map(parentTag => {
    const tempChildren = [...parentTag.children];
    const tempParentTag = {...parentTag};
    tempParentTag.children = tempChildren.filter(item => item.text.includes(searchText.value));
    return tempParentTag;
  })
}

const onCancel = () => {
  searchText.value = '';
  tagList.value = originTagList;
}

const activeIds = ref([]);
const activeIndex = ref(0);


const close = (tag) => {
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag;
  })
};

const router = useRouter();
const doSearchUsers = () => {
  router.push({
    path: '/user/list',
    query: {
      tags: activeIds.value,
    },
  })
}

</script>

<template>
  <form action="/public">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入搜索关键词"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>

  <van-divider content-position="left">已选标签</van-divider>
  <div v-if="activeIds.length === 0">请选择标签</div>
  <van-row :gutter="[18, 18]">
    <van-col v-for="tag in activeIds">
      <van-tag closeable size="medium" type="primary" @close="close(tag)">
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>
  <van-divider content-position="left">选择标签</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />

  <van-button block type="primary" @click="doSearchUsers" style="margin-bottom: auto" >搜索用户</van-button>
</template>

<style scoped>

</style>